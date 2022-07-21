package test;

import core.Scene;
import core.body.*;
import core.util.Transformation;
import org.joml.Vector3f;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        Mesh mesh = objectLoader.loadMesh(getClass(), "/shapes/cube.fbx");
        Entity entity = new Entity(mesh, new Material(ColorValue.COLOR_LIGHT_GRAY));
        CollisionShape3d shape = entity.addBoxCollision();
        shape.setOnClickListener(new CollisionShape3d.OnClickListener() {
            @Override
            public void onClick(CollisionShape3d shape, Vector3f normal) {
                System.out.println("Hi!");
            }

            @Override
            public void onRelease(CollisionShape3d shape) {
                System.out.println("Bye!");
            }
        });
        addBody(entity);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        window.setTitle(1f / delta + "");
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
