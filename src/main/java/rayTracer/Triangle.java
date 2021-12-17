package rayTracer;

public class Triangle extends RTObject {
	Triangle() {}
	
	Triangle(Vec3f vertex1, Vec3f vertex2, Vec3f vertex3, Material material) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.vertex3 = vertex3;
		this.material = material;
	}

	@Override
	float rayIntersection(Vec3f origin, Vec3f dir) {
		float inf = RayTracer.INFINITY;

		Vec3f v0v1 = vertex2.sub(vertex1);
		Vec3f v0v2 = vertex3.sub(vertex1);
		Vec3f pVec = dir.mul(v0v2);
		float det = v0v1.dot(pVec);

		if (Math.abs(det) < 1e-3f)
			return inf;

		float invDet = 1 / det;

		Vec3f tVec = origin.sub(vertex1);
		float u = tVec.dot(pVec)*invDet;
		if (u < 0 || u > 1)
			return inf;

		Vec3f qVec = tVec.mul(v0v1);
		float v = dir.dot(qVec)*invDet;
		if (v < 0 || u + v > 1)
			return inf;

		return v0v2.dot(qVec)*invDet;
	}

	@Override
	Vec3f calculateNormal(Vec3f point) {
		Vec3f normal = this.vertex2.sub(this.vertex1).mul(
				this.vertex3.sub(this.vertex1));
		normal.normalize();
		return normal;
	}

	public Vec3f getVertex1() {
		return vertex1;
	}

	public void setVertex1(Vec3f vertex) {
		this.vertex1 = vertex;
	}

	public Vec3f getVertex2() {
		return vertex2;
	}

	public void setVertex2(Vec3f vertex) {
		this.vertex2 = vertex;
	}

	public Vec3f getVertex3() {
		return vertex3;
	}

	public void setVertex3(Vec3f vertex) {
		this.vertex3 = vertex;
	}

	private Vec3f vertex1, vertex2, vertex3;
	private Vec3f _normal;
}

//	пересечение https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/moller-trumbore-ray-triangle-intersection