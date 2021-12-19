package rayTracer;

import rayTracer.ui.Workspace;

public class Main {
	public static void main(String[] args) {
		Workspace workspace = new Workspace();

		/* далее идет блок теста без графического интерфейса
		
		//	создаем новый объект трассировщика
		RayTracer tracer = new RayTracer();
		tracer.configure(new Dimension(1920, 1080));
		
		//	задаем материалы: цвет и коэффициент глянца
		Material ivory = new Material(new Vec3f(.4f, .4f, .3f), 32.f);
		Material red_rubber = new Material(new Vec3f(.3f, .1f, .1f), 8.f);
		
		//	добавляем объекты в сцену
		ArrayList<RTObject> objects = new ArrayList<RTObject>();
		objects.add(new Sphere(new Vec3f(-3.f, 0.f, -16.f), 4.f, ivory));
		objects.add(new Sphere(new Vec3f(-1.f, -1.5f, -12.f), 4.f, red_rubber));
		objects.add(new Sphere(new Vec3f(1.5f, -0.5f, -18.f), 6.f, red_rubber));
		objects.add(new Sphere(new Vec3f(7.f, 5.f, -18.f), 8.f, ivory));
		
		//	устанавливаем источники света
		ArrayList<PointLight> pointLights = new ArrayList<PointLight>();
		pointLights.add(new PointLight(new Vec3f(-3.f, 1.f, -1.f), .7f,
				new Vec3f(1.f, 0.f, 0.f)));
		pointLights.add(new PointLight(new Vec3f(0.f, 7.f, -9.f), .8f,
				new Vec3f(0.f, 1.f, 0.f)));
		pointLights.add(new PointLight(new Vec3f(5.f, 1.5f, -2.5f), .9f,
				new Vec3f(0.f, 0.f, 1.f)));
		
		//	освещение окружения
		float ambientLightIntensity = .2f;
		Vec3f ambientLightColor = new Vec3f(1.f, 1.f, 1.f);
		
		//	ставим камеру в начало координат
		Vec3f cameraPosition = new Vec3f(0.f, 0.f, 0.f);
		//	угол поля зрения по вертикали в градусах
		int fovyInDegrees = 60;
		
		//	визуализация с сохранением
		tracer.render("TestScene1.png", cameraPosition, 
			(float)(Math.PI*fovyInDegrees/180), objects, ambientLightIntensity,
			ambientLightColor, pointLights, null);
		*/
	}
}
