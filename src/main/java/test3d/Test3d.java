package test3d;

import core.Looper;
import core.Scene;
import core.Window;
import core.body.*;
import core.body.light.DirectionalLight;
import core.body.ui.Button;
import core.body.ui.Text;
import core.utils.Transformation;
import org.joml.Vector3f;

public class Test3d extends Scene
{
    RigidBody3d body;
    @Override
    public void onCreate() {
        super.onCreate();
        Mesh cylinder = objectLoader.loadMesh(getClass(), "/shapes/cylinder.fbx");
        Entity cylinderEntity = new Entity(cylinder, new Material(ColorValue.COLOR_GREEN, null, null));
        addBody(cylinderEntity);
        Mesh cone = objectLoader.loadMesh(getClass(), "/shapes/cone.fbx");
        Entity coneEntity = new Entity(cone, new Material(ColorValue.COLOR_GREEN, null, null));
        coneEntity.y = cylinderEntity.getBoundingBox().height;
        addBody(coneEntity);

        CollisionShape3d collisionShape3d = new CollisionShape3d(cylinder, true);
        collisionShape3d.addToParent(cylinderEntity);
        collisionShape3d.masks.add("a");

        CollisionShape3d cameraCollision = new CollisionShape3d(new Vector3f(camera.x, camera.y, camera.z), 0);
        cameraCollision.masks.add("a");
        body = new RigidBody3d(cameraCollision);
        addBody(body);

        cameraCollision.collisionShapeListener = new CollisionShape3d.CollisionShapeListener() {
            @Override
            public void onCollisionEnter(CollisionShape3d shape, Vector3f normal) {
                System.out.println(normal);
            }

            @Override
            public void onCollisionLeave(CollisionShape3d shape) {
                System.out.println("Leave");
            }
        };

        directionalLight = new DirectionalLight(20, 20, 20, 1, ColorValue.COLOR_WHITE);
        setWorldColor(0.3f, 0.3f, 0.3f, 1);

        Button button = ((Button) new Button(200, 200)
                .setBackground(new ColorValue(0.8f, 0.8f, 0.8f, 1))
                .setBorder(2, new ColorValue(0.5f, 0.5f, 0.5f, 1))
                .center()
                .addChild(new Text("Button", 42, fontRoboto).center()))
                .onHover(new ColorValue(0.5f, 0.5f, 0.5f, 1));
        button.onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(Button button) {
                Looper.addWindow(new Window(false, 1000, 1000, new Test3d()));
            }
        };
        addBody(button);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
        body.setPosition(camera.x, camera.y, camera.z);
    }
}
