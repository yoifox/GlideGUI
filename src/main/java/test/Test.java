package test;

import core.Scene;
import core.body.*;
import core.body.ui.TextArea;
import core.utils.Transformation;
import org.joml.Vector3f;

public class Test extends Scene
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Mesh cubeM = objectLoader.loadMesh(getClass(), "/shapes/cube.fbx");
        cubeM.boundingBox.visible = true;
        Entity cube = new Entity(cubeM, new Material(ColorValue.COLOR_GREEN));
        //cube.scale(2, 0, 2);
        CollisionShape3d collisionShape3d = cube.addBoxCollision();
        collisionShape3d.setOnClickListener(new CollisionShape3d.OnClickListener() {
            @Override
            public void onClick(CollisionShape3d shape, Vector3f normal) {
                cube.material.colorValue = ColorValue.COLOR_RED;
            }

            @Override
            public void onRelease(CollisionShape3d shape) {
                cube.material.colorValue = ColorValue.COLOR_GREEN;
            }
        });
        addBody(cube);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
