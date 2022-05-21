package test3d;

import core.Scene;
import core.body.*;
import core.tools.RayCast;
import core.utils.Transformation;
import org.joml.Vector3f;

public class Test3d extends Scene
{
    RayCast rayCast;
    RigidBody3d ray;
    Entity cube1;
    @Override
    public void onCreate() {
        super.onCreate();
        Mesh cubeMesh = objectLoader.loadMesh(getClass(), "/shapes/cube.fbx");
        Entity cube = new Entity(cubeMesh, new Material(ColorValue.COLOR_GREEN, null, null));
        cube.y = 40;
        cube.addBoxCollision("test");
        rayCast = new RayCast(camera);
        CollisionShape3d collisionShape3d = new CollisionShape3d(0);
        collisionShape3d.masks.add("test");
        ray = new RigidBody3d(collisionShape3d);
        collisionShape3d.collisionShapeListener = new CollisionShape3d.CollisionShapeListener() {
            @Override
            public void onCollisionEnter(CollisionShape3d shape, Vector3f normal) {
                System.out.println("hi");
            }

            @Override
            public void onCollisionLeave(CollisionShape3d shape) {
                System.out.println("bye");
            }
        };

        cube1 = new Entity(cubeMesh, new Material(ColorValue.COLOR_GREEN, null, null));
        cube1.setScale(0.1f, 0.1f, 0.1f);

        addBody(ray);
        addBody(rayCast);
        addBody(cube);
        addBody(cube1);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
        ray.setPosition(camera.x, camera.y, camera.z);
    }

    @Override
    public synchronized void updatePhysics(float delta) {
        super.updatePhysics(delta);
        Vector3f normal = rayCast.getRay(this, (float) mouseInput.getX(), (float) mouseInput.getY());
        normal.normalize();
        //System.out.println(ray.x + "," + ray.y + "," + ray.z);
        ray.move(normal.x / 100f, normal.y / 100f, normal.z / 100f);
        cube1.setPosition(ray.x, ray.y, ray.z);
    }
}
