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

        Entity c = new Entity(cubeMesh, new Material(ColorValue.COLOR_RED));
        CollisionShape3d cubeCollision1 = c.addBoxCollision("floor");
        cubeCollision1.setOnClickListener(new CollisionShape3d.OnClickListener() {
            @Override
            public void onClick(CollisionShape3d shape, Vector3f normal) {
                c.material.colorValue = ColorValue.COLOR_GREEN;
            }

            @Override
            public void onRelease(CollisionShape3d shape) {
                c.material.colorValue = ColorValue.COLOR_RED;
            }
        });
        c.move(0, 10, 0);
        addBody(c);

        cubeBody = new RigidBody3d(cubeCollision);
        cubeBody.addChild(cube);
        cubeBody.move(0, 10, 0);
        addBody(cubeBody);

        Entity floor = new Entity(cubeMesh, new Material(ColorValue.COLOR_GRAY));
        CollisionShape3d floorCollision = floor.addBoxCollision("floor");
        floor.setScale(10, 1, 10);
        addBody(floor);

        Entity floor2 = new Entity(cubeMesh, new Material(ColorValue.COLOR_GRAY));
        CollisionShape3d floorCollision2 = floor2.addBoxCollision("floor");
        floor2.setScale(10, 1, 10);
        floor2.move(25, 0, 0);
        addBody(floor2);

        Entity floor3 = new Entity(cubeMesh, new Material(ColorValue.COLOR_GRAY));
        CollisionShape3d floorCollision3 = floor3.addBoxCollision("floor");
        floor3.setScale(10, 1, 10);
        floor3.move(-25, 0, 0);
        addBody(floor3);

        cubeCollision.collisionShapeListener = new CollisionShape3d.CollisionShapeListener() {
            @Override
            public void onCollisionEnter(CollisionShape3d shape, Vector3f normal) {
                //if(shape == floorCollision)
                //{
                    cubeBody.speedY = 0;
                    isOnFloor = true;
                //}
            }

            @Override
            public void onCollisionLeave(CollisionShape3d shape) {
                //if(shape == floorCollision)
                    isOnFloor = false;
            }
        };
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        window.setTitle(1f / delta + "");
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
        if(keyInput.isKeyJustPressed(GLFW.GLFW_KEY_RIGHT_SHIFT))
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
            cubeBody.speedY -= 14f*delta;
    }
}
