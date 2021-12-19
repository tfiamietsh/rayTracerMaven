package rayTracer.traceObject;

import rayTracer.geomObject.Material;
import rayTracer.util.Vec3f;

//	класс точечного источника света
public class PointLight {
    private Vec3f position;
    private float intensity;
    private Vec3f color;

    public PointLight() {
    }

    PointLight(Vec3f point, float intensity) {
        this.position = point;
        this.intensity = intensity;
        //	по умолчанию белого цвета
        this.color = new Vec3f(1.f, 1.f, 1.f);
    }

    PointLight(Vec3f point, float intensity, Vec3f color) {
        this.position = point;
        this.intensity = intensity;
        this.color = color;
    }

    //	функция вычисляет освещения от источника света в точке point
    //	с нормалью normal, направлением взгляда viewDir, материала material,
    //	с интенсивностью освещения окрежения ambientIntensity и цветом окружения ambientColor
    public Vec3f calculateColorAtPoint(Vec3f point, Vec3f normal, Vec3f viewDir,
                                       Material material, float ambientIntensity, Vec3f ambientColor) {
        Vec3f lightDir = this.position.sub(point);

        //	используем ненормализованное направление света в точку lightDir
        //	для вычисления коэффициента затухания от расстояния до источника света
        float dist = lightDir.dot(lightDir);
        float A = 1.f, B = .01f, C = .00001f;
        float attenuation = 1.f / (A + B * dist + C * dist * dist);

        //	теперь нормализуем
        lightDir.normalize();
        //	вычисляем коэффициент интенсивности диффузной компоненты
        float diff = Math.max(normal.dot(lightDir), 0.f);
        //	вычисляем направление отраженного от точки вектора
        Vec3f reflectDir = Vec3f.reflect(lightDir.mul(-1.f), normal).mul(-1.f);
        float spec = (float) (Math.pow(Math.max(viewDir.dot(reflectDir), 0.f),
                Math.max(material.getShineness(), 0.1f)));
        //	вычисляем комноненты освещения: окружения, диффузную и бликовую
        Vec3f ambient = ambientColor.mul(ambientIntensity).mulCW(material.getDiffuseColor());
        Vec3f diffuse = this.color.mul(this.intensity * diff).mulCW(material.getDiffuseColor());
        Vec3f specular = this.color.mul(this.intensity * spec).mulCW(material.getDiffuseColor());
        diffuse = diffuse.mul(attenuation);

        return ambient.add(diffuse).add(specular);
    }

    public Vec3f getPosition() {
        return position;
    }

    public void setPosition(Vec3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vec3f getColor() {
        return color;
    }

    public void setColor(Vec3f color) {
        this.color = color;
    }
}
