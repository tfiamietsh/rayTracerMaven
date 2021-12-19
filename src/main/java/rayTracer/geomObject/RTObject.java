package rayTracer.geomObject;

import rayTracer.util.Vec3f;

//	абстрактный трассируемый объект
//	содержит основные методы нужные для трассировки
public abstract class RTObject {
	//	функция возвращает ближайшую точку на поверхности объекта по лучу
	//	с началом в origin и направлением dir, если существует; иначе - "бесконечность"
	public abstract float rayIntersection(Vec3f origin, Vec3f dir);
	
	//	вычисляет нормаль в точке на поверхности объекта
	public abstract Vec3f calculateNormal(Vec3f point);

	public void setMaterial(Material mat) {
		this.material = mat;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	protected Material material;
}
