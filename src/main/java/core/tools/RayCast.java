package core.tools;

import core.Scene;
import core.body.Body;
import core.body.Camera;
import core.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RayCast
{
    private Scene scene;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private final Camera camera;

    public RayCast(Camera camera)
    {
        this.camera = camera;
        viewMatrix = Transformation.createViewMatrix(camera);
    }

    public Vector3f getRay(Scene scene, float x, float y)
    {
        this.scene = scene;
        projectionMatrix = scene.window.projectionMatrix;
        viewMatrix = Transformation.createViewMatrix(camera);
        return calculateRay(new Vector2f(x, y));
    }

    private Vector3f calculateRay(Vector2f point)
    {
        point.y = scene.window.getHeight() - point.y;
        point.x = (2f * point.x) / scene.window.getWidth() - 1f;
        point.y = (2f * point.y) / scene.window.getHeight() - 1f;
        Vector4f clipCoordinates = new Vector4f(point.x, point.y, -1f, 1f);

        Matrix4f invertedProjection = new Matrix4f(projectionMatrix).invert();

        Vector4f eyeCoordinates = new Matrix4f(invertedProjection).transform(clipCoordinates);
        eyeCoordinates.z = -1f;
        eyeCoordinates.w = 0f;

        Matrix4f invertedView = new Matrix4f(viewMatrix).invert();

        Vector4f worldCoordinates = new Matrix4f(invertedView).transform(eyeCoordinates);
        Vector3f rayCoordinates = new Vector3f(worldCoordinates.x, worldCoordinates.y, worldCoordinates.z);
        rayCoordinates = new Vector3f(rayCoordinates).normalize();
        return rayCoordinates;
    }
}
