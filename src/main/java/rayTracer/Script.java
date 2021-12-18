package rayTracer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Script {
	//	функция попытается выполнить предложенный сценарий,
	//	в случае успеха возвращает пустую строку, иначе - ошибки
	static String tryToExecuteScript(String script, Dimension resolution,
			JProgressBar progressBar) {
		String errors = checkForErrors(script);
		if (errors == "")
			executeScript(script, resolution, progressBar);
		return errors;
	}
	
	//	функция выполняет сценарий, который не содержит ошибок
	private static void executeScript(String script, Dimension resolution, 
			JProgressBar progressBar) {
		Vec3f ambientColor = new Vec3f();
		float ambientIntensity = 0.f;
		
		Vec3f cameraPosition = new Vec3f();
		float fovy = 0.f;
		
		ArrayList<PointLight> lights = new ArrayList<PointLight>();
		ArrayList<RTObject> objects = new ArrayList<RTObject>();

		String[] lines = script.split("\r\n|\r|\n");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].length() == 0)
				continue;
			
			String[] line = lines[i].split(" +");
			//	освещение окружения
			if (lines[i].startsWith("A ")) {
				ambientIntensity = extractFloat(line[1]);
				ambientColor = Material.ColorToVec3f(
					extractColor(line[2]));
			} else
			//	камера
			if (lines[i].startsWith("C ")) {
				//	!!! игнорирует некоторые компоненты
				cameraPosition = extractVec3f(line[1]);
				fovy = (float)(Math.PI*extractInt(line[3])/180);
			} else
			//	точечный источник света
			if (lines[i].startsWith(ONLY_ONE_POINT_LIGHT_CONSTRAINT ? "L " : "l ")) {
				PointLight light = new PointLight();
				light.setPosition(extractVec3f(line[1]));
				light.setIntensity(extractFloat(line[2]));
				light.setColor(Material.ColorToVec3f(extractColor(line[3])));
				
				lights.add(light);
			} else
			//	сфера
			if (lines[i].startsWith("sp ")) {
				Sphere sphere = new Sphere();
				sphere.setCenter(extractVec3f(line[1]));
				sphere.setRadius(extractFloat(line[2]));
				sphere.setMaterial(new Material(
						Material.ColorToVec3f(extractColor(line[3])),
						extractFloat(line[4])));
				
				objects.add(sphere);
			} else
			//	плоскость
			if (lines[i].startsWith("pl ")) {
				Plane plane = new Plane();
				plane.setCenter(extractVec3f(line[1]));
				plane.setNormal(extractVec3f(line[2]));
				plane.setMaterial(new Material(
						Material.ColorToVec3f(extractColor(line[3])),
						extractFloat(line[4])));
				
				objects.add(plane);
			} else
			//	треугольник
			if (lines[i].startsWith("tr ")) {
				Triangle triangle = new Triangle();
				triangle.setVertex1(extractVec3f(line[1]));
				triangle.setVertex2(extractVec3f(line[2]));
				triangle.setVertex3(extractVec3f(line[3]));
				triangle.setMaterial(new Material(
						Material.ColorToVec3f(extractColor(line[4])),
						extractFloat(line[5])));
				
				objects.add(triangle);
			} else
			//	квадрат
			if (lines[i].startsWith("sq ")) {
				Square square = new Square();
				square.setCenter(extractVec3f(line[1]));
				square.setNormal(extractVec3f(line[2]));
				square.setSide(extractFloat(line[3]));
				square.setMaterial(new Material(
						Material.ColorToVec3f(extractColor(line[4])),
						extractFloat(line[5])));
				
				objects.add(square);
			} else
			//	циллиндр
			if (lines[i].startsWith("cy ")) {
				Cylinder cylinder = new Cylinder();
				Vec3f center1 = extractVec3f(line[1]); 
				cylinder.setCenter1(center1);
				cylinder.setRadius(extractFloat(line[3]));
				Vec3f orientation = extractVec3f(line[2]);
				float height = extractFloat(line[4]);
				orientation.normalize();
				Vec3f center2 = center1.add(orientation.mul(height));
				cylinder.setCenter2(center2);
				cylinder.setMaterial(new Material(
						Material.ColorToVec3f(extractColor(line[5])),
						extractFloat(line[6])));
				
				objects.add(cylinder);
			}
		}
		
		//	создаем трассировщик, настраиваем и визуализируем
		RayTracer tracer = new RayTracer();
		tracer.configure(resolution);
		
		tracer.render("src/main/resources/RenderedScene.png", cameraPosition, fovy,
			objects, ambientIntensity, ambientColor, lights, progressBar);
	}
	
	//	функция которая проверяет сценарий на наличие ошибок
	static String checkForErrors(String script) {
		String errors = "";
		String[] lines = script.split("\r\n|\r|\n");
		int ambientLightCount = 0, pointLightCount = 0,
			cameraCount = 0;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].length() == 0)
				continue;
			try {
				String[] line = lines[i].split(" +");
				//	ambient light
				if (lines[i].startsWith("A ")) {
					if (!isIntensity(line[1]))
						errors += errorAtLine(i, ERR_INTENSITY);
					if (!isColor(line[2]))
						errors += errorAtLine(i, ERR_COLOR);
					ambientLightCount += 1;
					if (ambientLightCount > 1)
						errors += errorAtLine(i, ERR_EXCESS);
				} else
				//	camera 
				if (lines[i].startsWith("C ")) {
					isVec3f(line[1]);
					if (!isNormal(line[2]))
						errors += errorAtLine(i, ERR_NORMAL);
					int fovy = extractInt(line[3]);
					if (!(0 < fovy && fovy < 180))
						errors += errorAtLine(i, ERR_FOVY);
					cameraCount += 1;
					if (cameraCount > 1)
						errors += errorAtLine(i, ERR_EXCESS);
				} else
				//	point light
				if (lines[i].startsWith(ONLY_ONE_POINT_LIGHT_CONSTRAINT ? "L " : "l ")) {
					isVec3f(line[1]);
					if (!isIntensity(line[2]))
						errors += errorAtLine(i, ERR_INTENSITY);
					if (!isColor(line[3]))
						errors += errorAtLine(i, ERR_COLOR);
					pointLightCount += 1;
					if (ONLY_ONE_POINT_LIGHT_CONSTRAINT && pointLightCount > 1)
						errors += errorAtLine(i, ERR_EXCESS);
				} else
				//	sphere
				if (lines[i].startsWith("sp ")) {
					isVec3f(line[1]);
					if (!isPositiveFloat(line[2]))
						errors += errorAtLine(i, ERR_DIAMETER);
					if (!isColor(line[3]))
						errors += errorAtLine(i, ERR_COLOR);
					if (!isPositiveFloat(line[4]))
						errors += errorAtLine(i, ERR_SHININESS);
				} else
				//	plane
				if (lines[i].startsWith("pl ")) {
					isVec3f(line[1]);
					if (!isNormal(line[2]))
						errors += errorAtLine(i, ERR_NORMAL);
					if (!isColor(line[3]))
						errors += errorAtLine(i, ERR_COLOR);
					if (!isPositiveFloat(line[4]))
						errors += errorAtLine(i, ERR_SHININESS);
				} else
				//	triangle
				if (lines[i].startsWith("tr ")) {
					isVec3f(line[1]);
					isVec3f(line[2]);
					isVec3f(line[3]);
					if (!isColor(line[4]))
						errors += errorAtLine(i, ERR_COLOR);
					if (!isPositiveFloat(line[5]))
						errors += errorAtLine(i, ERR_SHININESS);
				} else
				//	square
				if (lines[i].startsWith("sq ")) {
					isVec3f(line[1]);
					if (!isNormal(line[2]))
						errors += errorAtLine(i, ERR_NORMAL);
					if (!isPositiveFloat(line[3]))
						errors += errorAtLine(i, ERR_SIDE);
					if (!isColor(line[4]))
						errors += errorAtLine(i, ERR_COLOR);
					if (!isPositiveFloat(line[5]))
						errors += errorAtLine(i, ERR_SHININESS);
				} else
				//	cylinder
				if (lines[i].startsWith("cy ")) {
					isVec3f(line[1]);
					if (!isNormal(line[2]))
						errors += errorAtLine(i, ERR_NORMAL);
					if (!isPositiveFloat(line[3]))
						errors += errorAtLine(i, ERR_DIAMETER);
					if (!isPositiveFloat(line[4]))
						errors += errorAtLine(i, ERR_HEIGHT);
					if (!isColor(line[5]))
						errors += errorAtLine(i, ERR_COLOR);
					if (!isPositiveFloat(line[6]))
						errors += errorAtLine(i, ERR_SHININESS);
				} else if (!lines[i].startsWith("//")) {
					errors += errorAtLine(i, ERR_IDENTIFIER);
				}
			} catch (Exception ex) {
				errors += errorAtLine(i, ERR_SYNTAX);
			}
			
		}
		if (ambientLightCount == 0) 
			errors += errorAtLine(lines.length - 1, ERR_AMBIENT);
		if (cameraCount == 0)
			errors += errorAtLine(lines.length - 1, ERR_CAMERA);
		if (errors.length() > 0)
			errors = errors.substring(0, errors.length() - 1);
		return errors;
	}
	// 0 <= |t| <= 255, для каждой компоненты цвета
	private static boolean isColor(String s) {
		String[] rgb = s.split(",");
		int red = Integer.parseInt(rgb[0]);
		int green = Integer.parseInt(rgb[1]);
		int blue = Integer.parseInt(rgb[2]);
		return 0 <= red && red <= 255 && 0 <= green && green <= 255
				&& 0 <= blue && blue <= 255;
	}
	
	private static Color extractColor(String s) {
		String[] rgb = s.split(",");
		return new Color(extractInt(rgb[0]), extractInt(rgb[1]), 
				extractInt(rgb[2]));
	}
	// 0 <= |t| <= 1, для каждой компоненты нормали t
	private static boolean isNormal(String s) {
		String[] xyz = s.split(",");
		float x = extractFloat(xyz[0]);
		float y = extractFloat(xyz[1]);
		float z = extractFloat(xyz[2]);
		return Math.abs(x) <= 1 && Math.abs(y) <= 1 
				&& Math.abs(z) <= 1;
	}
	//	нужно, чтобы ловить исключения
	private static boolean isVec3f(String s) {
		String[] xyz = s.split(",");
		isFloat(xyz[0]);
		isFloat(xyz[1]);
		isFloat(xyz[2]);
		return true;
	}
	// float,float,float
	private static Vec3f extractVec3f(String s) {
		String[] xyz = s.split(",");
		return new Vec3f(extractFloat(xyz[0]), extractFloat(xyz[1]), 
				extractFloat(xyz[2]));
	}
	//	0 <= |intensity| <= 1
	private static boolean isIntensity(String s) {
		float intensity = extractFloat(s);
		return 0.f <= intensity && intensity <= 1.f;
	}
	//	нужно, чтобы ловить исключения
	private static boolean isFloat(String s) {
		extractFloat(s);
		return true;
	}
	
	private static boolean isPositiveFloat(String s) {
		return extractFloat(s) > 0;
	}
	
	private static float extractFloat(String s) {
		return Float.parseFloat(s);
	}
	
	private static int extractInt(String s) {
		return Integer.parseInt(s);
	}
	//	возвращает ошибку с номером строки, line начинается с 0
	private static String errorAtLine(int line, String msg) {
		return "Строка " + (line + 1) + ": " + msg + "\n";
	}
	//	ограничение на единственность источника точечного света, true = 1; false >= 1
	//	если true - то в сценарии идентификатором будет L, иначе - l
	private final static boolean ONLY_ONE_POINT_LIGHT_CONSTRAINT = false;
	//	список возможных ошибок
	private final static String ERR_INTENSITY =
			"Интенсивность должна находиться в отрезке [0; 1]";
	private final static String ERR_NORMAL =
			"Компоненты нормали должны находиться в отрезке [0; 1]";
	private final static String ERR_COLOR =
			"Компоненты цвета должны находиться в отрезке [0; 255]";
	private final static String ERR_EXCESS =
			"Превышено допустимое количество идентификаторов";
	private final static String ERR_FOVY =
			"Угол поля зрения должен находиться в диапазоне (0; 180)";
	private final static String ERR_DIAMETER =
			"Диаметр должен быть положительным";
	private final static String ERR_HEIGHT =
			"Высота должна быть положительной";
	private final static String ERR_SIDE =
			"Сторона должна быть положительной";
	private final static String ERR_IDENTIFIER =
			"Недопустимый идентификатор";
	private final static String ERR_SYNTAX =
			"Синтаксическая ошибка - неверный формат параметра идентификатора";
	private final static String ERR_CAMERA =
			"Отсутствует идентификатор камеры";
	private final static String ERR_AMBIENT =
			"Отсутствует идентификатор окружающего освещения";
	private final static String ERR_SHININESS =
			"Коэффициент глянца должен быть не меньше 1";
}
