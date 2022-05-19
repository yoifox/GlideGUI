package test3d;

import core.Scene;
import core.body.ColorValue;
import core.body.Entity;
import core.body.Material;
import core.body.Mesh;
import core.body.light.DirectionalLight;
import core.utils.Transformation;

public class Test3d extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        Mesh mesh = objectLoader.loadMesh(getClass(), "/shapes/torus.fbx");
        Entity entity = new Entity(mesh, new Material(ColorValue.COLOR_GREEN, null, null));
        addBody(entity);
        directionalLight = new DirectionalLight(20, 20, 20, 1, ColorValue.COLOR_WHITE);
        setWorldColor(0.5f, 0.5f, 0.5f, 1);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
