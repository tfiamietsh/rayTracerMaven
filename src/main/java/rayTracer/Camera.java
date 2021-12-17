package rayTracer;

public class Camera {
	Camera() {}
	
	Camera(Vec3f position, Vec3f orientation, int fovyInDegrees) {
		setPosition(position);
		setOrientation(orientation);
		setFOVy(fovyInDegrees);
	}
	
	Vec3f calculateRayDirection2(int pixelX, int pixelY, int width, int height) {
		float normalized_i = ((float)(pixelX)/width - .5f)*width/height;
		float normalized_j = (float)(height - pixelY)/height - .5f;
		Vec3f image_point = right.mul(normalized_i).add(up.mul(normalized_j))
				.add(position.add(orientation));
	                        ;
		Vec3f direction = image_point.sub(position);
		return direction;
	}
	
	//	!!! выполняет какую-то хрень, нужно исправить
	Vec3f calculateRayDirection(int pixelX, int pixelY, int width, int height) {
		//	вычисляем относительно точки (0, 0, 0) = начала координат
		float x = (2*(pixelX + .5f)/width - 1)*_halfTanFovy*width/height;
		float y = (2*((height - pixelY) + .5f)/height - 1)*_halfTanFovy;
		
		Vec3f dir = new Vec3f(x, y, _initialOrientation.getZ());
		dir.normalize();
		
		//	вращение относительно точки (0, 0, -1) = _initialOrientation
		dir = dir.rotateByX(_angleX).rotateByY(_angleY).rotateByZ(_angleZ);
		
		//	перемещаем камеру на ее позицию
		return dir.add(position);
	}
	
	void setFOVy(int fovyInDegrees) {
		float fovy = (float)(fovyInDegrees*Math.PI/180);
		this._halfTanFovy = (float)(Math.tan(fovy/2));
	}
	
	Vec3f getPosition() {
		return position;
	}

	void setPosition(Vec3f position) {
		this.position = position;
	}

	Vec3f getOrientation() {
		return orientation;
	}

	void setOrientation(Vec3f orientation) {
		orientation.normalize();
		this.orientation = orientation;
		
		Vec3f tempYZ = new Vec3f(_initialOrientation.getX(), orientation.getY(), orientation.getZ());
		_angleX = (float)(Math.acos(tempYZ.dot(_initialOrientation)));
		Vec3f tempXZ = new Vec3f(orientation.getX(), _initialOrientation.getY(), orientation.getZ());
		_angleY = (float)(Math.acos(tempXZ.dot(_initialOrientation)));
		Vec3f tempXY = new Vec3f(orientation.getX(), orientation.getY(), _initialOrientation.getZ());
		_angleZ = (float)(Math.acos(tempXY.dot(_initialOrientation)));
	}
	
	void setUp(Vec3f up) {
		up.normalize();
		this.up = up;
		this.right = this.orientation.mul(up);
	}

	private Vec3f position, orientation;
	private float _halfTanFovy;
	private Vec3f _initialOrientation = new Vec3f(0.f, 0.f, -1.f);
	private float _angleX, _angleY, _angleZ;
	
	private Vec3f right, up;
}
