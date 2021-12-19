package rayTracer;

import rayTracer.geomObject.Material;
import rayTracer.geomObject.RTObject;
import rayTracer.traceObject.PointLight;
import rayTracer.util.Vec3f;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class RayTracer {
    public static final float INFINITY = 10000.f;
    final Vec3f BACKGROUND_COLOR = new Vec3f(.2f, .7f, .8f);    // .55 .06 .0
    int outputImageWidth = 0, outputImageHeight = 0;
    float fovy = 0.f;

    //	устанавливает разрешение выходного изображения
    public void configure(Dimension resolution) {
        outputImageWidth = resolution.width;
        outputImageHeight = resolution.height;
    }

    //	вычисляет цвет в точке куда упал луч с началом origin и направлением dir,
    //	вычисление происходит для всех объектов и источников света в сцене (ambient тоже)
    private Vec3f castRay(Vec3f origin, Vec3f dir, ArrayList<RTObject> objects,
                          float ambientIntensity, Vec3f ambientColor, ArrayList<PointLight> lights) {
        //	ближайшая точка пересечения луча с некоторым объектом, номаль в этой точке
        Vec3f hitPoint = new Vec3f(), N = new Vec3f();
        //	изначально материалом объекта будет материал фона,
        //	так если не будет пересечения, вернем материал фона
        Material objMat = new Material(BACKGROUND_COLOR, 0.f);

        //	пробегаемся по всем объектам в сцене и вычисляем hitPoint и нормаль N,
        //	изначально считаем, что расстояние до объекта "бесконечно" большое
        float objectDist = INFINITY;
        for (int i = 0; i < objects.size(); i++) {
            float iDist = objects.get(i).rayIntersection(origin, dir);
            if (0.f < iDist && iDist < objectDist) {
                objectDist = iDist;
                hitPoint = origin.add(dir.mul(iDist));

                N = objects.get(i).calculateNormal(hitPoint);
                N.normalize();
                objMat = objects.get(i).getMaterial();
            }
        }
        //	если луч не столкнулся ни с одним
        //	объектом возвращаем фоновой цвет
        if (objectDist == INFINITY)
            return objMat.getDiffuseColor();

        //	пробегаемся по всем источникам света и вычисляем
        //	окончательный цвет в точке
        Vec3f color = new Vec3f();
        for (int i = 0; i < lights.size(); i++) {
            //	вычисляем направление от источника к точке
            Vec3f lightDir = lights.get(i).getPosition().sub(hitPoint);
            float lightDist = lightDir.length();
            lightDir.normalize();

            //	поскольку hitPoint лежит на поверхности объекта, мы слегка его смещаем
            Vec3f shadowOrigin = lightDir.dot(N) < 0.f ?
                    hitPoint.sub(N.mul(1e-3f)) : hitPoint.add(N.mul(1e-3f));
            Vec3f shadowPoint = new Vec3f(), shadowN;

            //	снова пробегаемся по сцене
            float shadowEndDist = INFINITY;
            for (int j = 0; j < objects.size(); j++) {
                float iDist = objects.get(j).rayIntersection(shadowOrigin, lightDir);
                if (0.f < iDist && iDist < shadowEndDist) {
                    shadowEndDist = iDist;
                    shadowPoint = shadowOrigin.add(lightDir.mul(iDist));

                    shadowN = objects.get(j).calculateNormal(shadowPoint);
                    shadowN.normalize();
                    //objMat = objects.get(j).getMaterial();
                }
            }

            //	если shadowEndDist конечен, то это значит что точка hitPoint
            //	лежит в тени, проверяем также, что если точка в тени, то источник
            //	света не должен быть "в тени"
            Vec3f shadowVec = shadowPoint.sub(shadowOrigin);
            if (shadowEndDist != INFINITY && shadowVec.length() < lightDist)
                continue;

            //	добавляем долю влияния источника на окончательный цвет
            color = color.add(lights.get(i).calculateColorAtPoint(hitPoint,
                    N, dir, objMat, ambientIntensity, ambientColor));
        }

        return color;
    }

    //	!!! нужно добавить камеру
    public void render(String filename, Vec3f cameraPosition, float fovy,
                       ArrayList<RTObject> objects, float ambientIntensity, Vec3f ambientColor,
                       ArrayList<PointLight> lights, JProgressBar progressBar) {
        BufferedImage image = new BufferedImage(outputImageWidth,
                outputImageHeight, BufferedImage.TYPE_INT_RGB);

        //	будет считать сколько пикселей прошли (нужно для прогрессбара)
        int setPixelsCount = 0;
        float progressBarValueFactor = 100.f / (outputImageWidth * outputImageHeight);

        //	пробегаемся по пикселям изображения
        for (int pixelY = 0; pixelY < outputImageHeight; pixelY++)
            for (int pixelX = 0; pixelX < outputImageWidth; pixelX++, setPixelsCount++) {
                //	вычисляем направление луча трассировки
                float x = (2 * (pixelX + .5f) / outputImageWidth - 1) * (float)
                        (Math.tan(fovy / 2)) * outputImageWidth / outputImageHeight;
                float y = (2 * ((outputImageHeight - pixelY) + .5f) /
                        outputImageHeight - 1) * (float) (Math.tan(fovy / 2));

                Vec3f direction = new Vec3f(x, y, -1.f);
                direction = direction.sub(cameraPosition);
                direction.normalize();

                //	вычисляем цвет который вернет результат трассировки
                //	и конвертируем его из Vec3f в Int
                Vec3f Vec3fColor =
                        castRay(cameraPosition, direction, objects,
                                ambientIntensity, ambientColor, lights);
                int pixelRGB = Material.Vec3fToInt(Vec3fColor);

                //	устанавливаем цвет пикселя в изображении
                image.setRGB(pixelX, pixelY, pixelRGB);

                //	если передан прогрессбар обновляем его данные
                if (progressBar != null)
                    progressBar.setValue((int) (setPixelsCount * progressBarValueFactor));
            }

        //	получаем расширение файла и сохраняем картинку с визуализацией
        String fileExtention = filename.substring(filename.lastIndexOf('.') + 1);
        try {
            ImageIO.write(image, fileExtention, new File(filename));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Не удалось сохранить файл",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}