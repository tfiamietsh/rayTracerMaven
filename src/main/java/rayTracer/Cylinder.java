package rayTracer;

public class Cylinder extends RTObject {
	Cylinder() {}
	
	Cylinder(Vec3f center1, Vec3f center2, float diameter, Material material) {
		setCenter1(center1);
		setCenter2(center2);
		setRadius(diameter/2);
		setMaterial(material);
	}
	
	@Override
	float rayIntersection(Vec3f p, Vec3f v) {
		Vec3f pA = this.center1;
		Vec3f vA = this.center2.sub(this.center1);
		vA.normalize();
		Vec3f dP = p.sub(pA);
		Vec3f vec1 = v.sub(vA.mul(v.dot(vA)));
		Vec3f vec2 = dP.sub(vA.mul(dP.dot(vA)));
		float A = vec1.dot(vec1);
		float B = 2*vec1.dot(vec2);
		float C = vec2.dot(vec2) - this.radius*this.radius;
		float D = B*B - 4*A*C;
		
		float[] distances = new float[4];
		for (int i = 0; i < 4; i++)
			distances[i] = RayTracer.INFINITY;
		
		int distancesIndex = 1;
		//	calculating t1 and t2
		if (D >= 0f) {
			float t1 = (-B - (float)(Math.sqrt(D)))/(2*A);
			float t2 = (-B + (float)(Math.sqrt(D)))/(2*A);
			if (t1 > 0) {
				Vec3f q1 = p.add(v.mul(t1));
				float dot1 = vA.dot(q1.sub(this.center1));
				float dot2 = vA.dot(q1.sub(this.center2));
				if (dot1 > 0 && dot2 < 0) {
					//	we have intersection
					distances[distancesIndex] = t1;
					distancesIndex++;
				}
			}
			if (t2 > 0) {
				Vec3f q2 = p.add(v.mul(t2));
				float dot1 = vA.dot(q2.sub(this.center1));
				float dot2 = vA.dot(q2.sub(this.center2));
				if (dot1 > 0 && dot2 < 0) {
					//	we have intersection
					distances[distancesIndex] = t2;
					distancesIndex++;
				}
			}
		}
		
		//	calculating t3 and t4
		float t3 = this._cap1Plane.rayIntersection(p, v);
		float t4 = this._cap2Plane.rayIntersection(p, v);
		Vec3f q3 = p.add(v.mul(t3));
		Vec3f q4 = p.add(v.mul(t4));
		if (q3.dot(q3) < this.radius*this.radius) {
			distances[distancesIndex] = t3;
			distancesIndex++;
		}
		if (q4.dot(q4) < this.radius*this.radius) {
			distances[distancesIndex] = t4;
			distancesIndex++;
		}
		
		float minDist = distances[0];
		for (int i = 1; i < 4; i++)
			if (distances[i] < minDist)
				minDist = distances[i];
		
		return minDist;
	}

	@Override
	Vec3f calculateNormal(Vec3f pointOnSurface) {
		Vec3f vA = this.center1.sub(this.center2);
		vA.normalize();

		//	if point on cap1
		Vec3f cap1Normal = this._cap1Plane.getNormal();
		Vec3f pointCenter1 = pointOnSurface.sub(center1);
		if (cap1Normal.dot(pointCenter1) < 1e-4f)
			return cap1Normal;
		
		//	if point on cap2
		Vec3f cap2Normal = this._cap2Plane.getNormal();
		Vec3f pointCenter2 = pointOnSurface.sub(center2);
		if (cap2Normal.dot(pointCenter2) < 1e-4f)
			return cap2Normal;

		//	else normal on cylinder sides
		Vec3f pCen2 = pointOnSurface.sub(this.center2);
		Vec3f Cen1Cen2 = this.center1.sub(this.center2);
		Cen1Cen2.normalize();
		float shifted = pCen2.dot(Cen1Cen2);
		Vec3f shiftedVec = this.center2.add(Cen1Cen2.mul(shifted));
		
		Vec3f normal = pointOnSurface.sub(shiftedVec);
		normal.normalize();
		return normal;
	}

	public Vec3f getCenter1() {
		return center1;
	}

	public void setCenter1(Vec3f center) {
		this.center1 = center;
	}

	public Vec3f getCenter2() {
		return center2;
	}

	public void setCenter2(Vec3f center) {
		this.center2 = center;

		Vec3f vA = this.center2.sub(this.center1);
		vA.normalize();
		this._cap1Plane = new Plane(this.center1, vA, this.material);
		this._cap2Plane = new Plane(this.center2, vA.mul(-1.f), this.material);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	private Vec3f center1, center2;
	private float radius;
	private Plane _cap1Plane, _cap2Plane;
}
