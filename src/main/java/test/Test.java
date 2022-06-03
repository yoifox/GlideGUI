package test;

import core.Scene;
import core.body.*;
import core.util.Transformation;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Test extends Scene
{
    RigidBody3d cubeBody;
    Entity cube;
    boolean isOnFloor = false;
    @Override
    public void onCreate() {
        super.onCreate();
        String root = "C:\\Users\\User\\Desktop\\hdr\\";
        skyBox = objectLoader.loadCubeMap(root + "px.png", root + "nx.png", root + "py.png",
                root + "ny.png", root + "pz.png", root + "nz.png");
        Mesh cubeMesh = objectLoader.loadMesh(getClass(), "/shapes/cube.fbx");
        cube = new Entity(cubeMesh, new Material(ColorValue.COLOR_RED));
        CollisionShape3d cubeCollision = cube.addBoxCollision("floor");
        cubeCollision.setOnClickListener(new CollisionShape3d.OnClickListener() {
            @Override
            public void onClick(CollisionShape3d shape, Vector3f normal) {
                cube.material.colorValue = ColorValue.COLOR_GREEN;
            }

            @Override
            public void onRelease(CollisionShape3d shape) {
                cube.material.colorValue = ColorValue.COLOR_RED;
            }
        });
        cubeBody = new RigidBody3d(cubeCollision);
        cubeBody.addChild(cube);
        cubeBody.move(0, 10, 0);
        addBody(cubeBody);
        cubeCollision.collisionShapeListener = new CollisionShape3d.CollisionShapeListener() {
            @Override
            public void onCollisionEnter(CollisionShape3d shape, Vector3f normal) {
                cubeBody.speedY = 0;
                isOnFloor = true;
            }

            @Override
            public void onCollisionLeave(CollisionShape3d shape) {
                isOnFloor = false;
            }
        };

        Entity floor = new Entity(cubeMesh, new Material(ColorValue.COLOR_GRAY));
        CollisionShape3d floorCollision = floor.addBoxCollision("floor");
        floor.setScale(10, 1, 10);
        addBody(floor);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
        if(keyInput.isKeyJustPressed(GLFW.GLFW_KEY_SPACE))
        {
            cubeBody.speedY = 10;
        }
        if(keyInput.isKeyJustPressed(GLFW.GLFW_KEY_LEFT))
        {
            cubeBody.move(-cube.getBoundingBox().width, 0, 0);
        }
        if(keyInput.isKeyJustPressed(GLFW.GLFW_KEY_RIGHT))
        {
            cubeBody.move(cube.getBoundingBox().width, 0, 0);
        }
        if(keyInput.isKeyJustPressed(GLFW.GLFW_KEY_UP))
        {
            cubeBody.move(0, 0, -cube.getBoundingBox().depth);
        }
        if(keyInput.isKeyJustPressed(GLFW.GLFW_KEY_DOWN))
        {
            cubeBody.move(0, 0, cube.getBoundingBox().depth);
        }
    }

    @Override
    public synchronized void updatePhysics(float delta) {
        super.updatePhysics(delta);
        if(!isOnFloor)
            cubeBody.speedY -= 9.8f*delta;
    }
}
