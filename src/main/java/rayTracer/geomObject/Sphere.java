package rayTracer.geomObject;

import rayTracer.RayTracer;
import rayTracer.util.Vec3f;

public class Sphere extends RTObject {
    private Vec3f center;
    private float radius;

    public Sphere() {
    }

    Sphere(Vec3f center, float diameter, Material material) {
        this.center = center;
        this.radius = diameter / 2;
        this.material = material;
    }

    @Override
    public float rayIntersection(Vec3f origin, Vec3f dir) {
        Vec3f OC = origin.sub(this.center);
        float A = dir.dot(dir);
        float B = 2 * OC.dot(dir);
        float C = OC.dot(OC) - this.radius * this.radius;
        float D = B * B - 4 * A * C;
        if (D >= 0) {
            //	вычисляем меньший из корней,
            //	это и будет наименьшее расстояние до сферы
            float dist = (-B - (float) (Math.sqrt(D))) / (2 * A);
            if (dist > 0)
                return dist;
        }
        return RayTracer.INFINITY;
    }

    @Override
    public Vec3f calculateNormal(Vec3f pointOnSurface) {
        Vec3f normal = pointOnSurface.sub(this.center);
        normal.normalize();
        return normal;
    }

    public Vec3f getCenter() {
        return center;
    }

    public void setCenter(Vec3f center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
