package rayTracer;

public class Plane extends RTObject{
	public Plane() {}
	
	public Plane(Vec3f center, Vec3f normal, Material material) {
		this.center = center;
		this.normal = normal;
		this.material = material;
	}

	@Override
	float rayIntersection(Vec3f origin, Vec3f dir) {
		this.normal.normalize();

		float dist = (center.dot(normal) - origin.dot(normal))/dir.dot(normal);
		
		if (dist > 0)
			return dist;
		return RayTracer.INFINITY;
		
		/*
		float dist = -(D + center.dot(normal))/dir.dot(normal);
		if (dist > 0)
			return dist;
		return RayTracer.INFINITY;
		*/
		
		/*
		float denom = this.normal.dot(dir);
		if (Math.abs(denom) > 1e-4f) {
			float dist = this.center.sub(origin).dot(this.normal)/denom;
			if (dist > 0)
				return dist;
		}
		return RayTracer.INFINITY;
		*/
	}

	@Override
	Vec3f calculateNormal(Vec3f point) {
		return getNormal();
	}

	public Vec3f getCenter() {
		return center;
	}

	public void setCenter(Vec3f center) {
		this.center = center;
	}

	public Vec3f getNormal() {
		normal.normalize();
		return normal;
	}

	public void setNormal(Vec3f normal) {
		this.normal = normal;
	}

	private Vec3f center, normal;
}
