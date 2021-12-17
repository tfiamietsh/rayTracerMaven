package rayTracer;

//	класс вектора из трех компонент типа float
public class Vec3f {
	Vec3f() {
		this.x = this.y = this.z = 0.f;
	}
	
	Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	//	сумма векторов
	Vec3f add(Vec3f other) {
		return new Vec3f(this.x + other.x, this.y + other.y, this.z + other.z);
	}

	//	умножение вектора на число
	Vec3f mul(float a) {
		return new Vec3f(this.x*a, this.y*a, this.z*a);
	}

	//	разность векторов
	Vec3f sub(Vec3f other) {
		return add(other.mul(-1.f));
	}

	//	скалярное произведение векторов
	float dot(Vec3f other) {
		return this.x*other.x + this.y*other.y + this.z*other.z;
	}

	//	векторное умножение векторов
	Vec3f mul(Vec3f other) {
		float tmpX = y*other.z - z*other.y;
		float tmpY = z*other.x - x*other.z;
		float tmpZ = x*other.y - y*other.x;
	    return new Vec3f(tmpX, tmpY, tmpZ);
	}

	//	покомпонентное умножение векторов, результат - вектор, компоненты которого
	//	являются результатом умножения соответствующих компонент исходных векторов
	Vec3f mulCW(Vec3f other) {
	    return new Vec3f(x*other.x, y*other.y, z*other.z);
	}

	//	нормализация текущего вектора
	void normalize() {
		float len = (float)(Math.sqrt(this.dot(this)));
		if (len != 0.) {
			this.x /= len;
			this.y /= len;
			this.z /= len;	
		}
	}

	//	вычисление длины вектора
	float length() {
		return (float)(Math.sqrt(this.dot(this)));
	}

	//	поворот вектора относительно оси Х
	Vec3f rotateByX(float angle) {
		Vec3f rotated = new Vec3f(x, y, z);
		rotated.y = (float)(Math.cos(angle)*y - Math.sin(angle)*z);
		rotated.z = (float)(Math.sin(angle)*y + Math.cos(angle)*z);
		return rotated;
	}
	
	Vec3f rotateByY(float angle) {
		Vec3f rotated = new Vec3f(x, y, z);
		rotated.x = (float)(Math.cos(angle)*x + Math.sin(angle)*z);
		rotated.z = (float)(-Math.sin(angle)*x + Math.cos(angle)*z);
		return rotated;
	}
	
	Vec3f rotateByZ(float angle) {
		Vec3f rotated = new Vec3f(x, y, z);
		rotated.x = (float)(Math.cos(angle)*x - Math.sin(angle)*y);
		rotated.y = (float)(Math.sin(angle)*x + Math.cos(angle)*y);
		return rotated;
	}

	//	поворот вектора по всем осям, значения углов в angles
	Vec3f rotate(Vec3f angles) {
		Vec3f rotated = new Vec3f(this.x, this.y, this.z);
		return rotated.rotateByX(angles.x).rotateByY(angles.y).rotateByZ(angles.z);
	}
	
	void setX(float x) { 
		this.x = x; 
	}
	
	float getX() { 
		return x; 
	}
	
	void setY(float x) { 
		this.x = x; 
	}
	
	float getY() { 
		return y; 
	}
	
	void setZ(float x) { 
		this.x = x; 
	}
	
	float getZ() { 
		return z; 
	}

	//	для направления луча падающего в направлении I,
	//	вычисляет отраженный от точки с нормалью N
	static Vec3f reflect(Vec3f I, Vec3f N) {
		return I.sub(N.mul(2.f*I.dot(N)));
	}
	
	private float x, y, z;
}