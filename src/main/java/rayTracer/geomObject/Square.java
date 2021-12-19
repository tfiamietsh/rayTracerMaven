package rayTracer.geomObject;

import rayTracer.RayTracer;
import rayTracer.util.Vec3f;

public class Square extends RTObject {
    private Vec3f center, normal;
    private float side;
    private Vec3f vertex1, widthVec, heightVec;

    public Square() {
    }

    Square(Vec3f center, Vec3f normal, float side, Material material) {
        setCenter(center);
        setNormal(normal);
        setSide(side);
        setMaterial(material);

        calculateTriangles();
    }

    @Override
    public float rayIntersection(Vec3f origin, Vec3f dir) {
        float dist = (vertex1.sub(origin)).dot(getNormal()) /
                dir.dot(normal);

        Vec3f intesectPoint = origin.add(dir.mul(dist));
        Vec3f v = intesectPoint.sub(vertex1);

        float width = widthVec.length();
        heightVec = getNormal().mul(widthVec);
        float height = heightVec.length();

        float proj1 = v.dot(widthVec) / width;
        float proj2 = v.dot(heightVec) / height;

        if ((proj1 < width && proj1 > 0) && (proj2 < height && proj2 > 0))
            return dist;

        return RayTracer.INFINITY;
    }

    @Override
    public Vec3f calculateNormal(Vec3f point) {
        this.normal.normalize();
        return this.normal;
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

    public float getSide() {
        return side;
    }

    public void setSide(float side) {
        this.side = side;

        calculateTriangles();
    }

    private void calculateTriangles() {
        Vec3f binormal = new Vec3f(0.f, 1.f, 0.f);
        Vec3f tangent = binormal.mul(this.normal);
        tangent.normalize();
        float sideHalf = this.side / 2;

        vertex1 = this.center.add(tangent.mul(-sideHalf).add(
                binormal.mul(sideHalf)));
        Vec3f vertex2 = this.center.add(tangent.mul(sideHalf).add(
                binormal.mul(sideHalf)));
        Vec3f vertex3 = this.center.add(tangent.mul(-sideHalf).add(
                binormal.mul(-sideHalf)));
        Vec3f vertex4 = this.center.add(tangent.mul(sideHalf).sub(
                binormal.mul(-sideHalf)));

        widthVec = vertex2.sub(vertex1);
        heightVec = vertex4.sub(vertex1);
        vertex1 = vertex3;
    }
}
//	link	https://computergraphics.stackexchange.com/questions/8418/get-intersection-ray-with-square