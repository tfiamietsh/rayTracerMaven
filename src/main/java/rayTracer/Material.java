package rayTracer;

import java.awt.*;

//	класс материала, хранит цвет и показатель глянцевости
public class Material {
	Material() {}
	
	Material(Vec3f color) {
		diffuseColor = color;
	}
	
	Material(Vec3f color, float spec) {
		diffuseColor = color;
		shineness = spec;
	}
	
	void setDiffuseColor(Vec3f color) {
		diffuseColor = color;
	}
	
	Vec3f getDiffuseColor() {
		return diffuseColor;
	}
	
	void setShineness(float spec) {
		shineness = spec;
	}
	
	float getShineness() {
		return shineness;
	}
	
	//	ограничение компонент в диапазоне [0; 1]
	static Vec3f restrict(Vec3f color) {
		float x = (float)(Math.max(0.f, Math.min(1.f, color.getX())));
		float y = (float)(Math.max(0.f, Math.min(1.f, color.getY())));
		float z = (float)(Math.max(0.f, Math.min(1.f, color.getZ())));
		return new Vec3f(x, y, z);
	}

	//	конвертируем цвет из представления Vec3f в понятный swing'у класс Color
	static Color Vec3fToColor(Vec3f col) {
		Vec3f color = restrict(col);
		Color result = new Color((int)(255*color.getX()), 
			(int)(255*color.getY()), (int)(255*color.getZ()));
		return result;
	}
	
	//	конвертируем цвет из Color в представление Vec3f
	static Vec3f ColorToVec3f(Color color) {
		Vec3f result = new Vec3f((float)(color.getRed())/255,
			(float)(color.getGreen())/255, (float)(color.getBlue())/255);
		return result;
	}
	
	//	конвертируем цвет из представления Vec3f в int (8 бит на каждый канал цвета)
	static int Vec3fToInt(Vec3f color) {
		Color c = Vec3fToColor(color);
		return (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
	}
	
	private Vec3f diffuseColor;
	private float shineness;
}
