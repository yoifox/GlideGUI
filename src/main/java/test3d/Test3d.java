package test3d;

import core.Scene;
import core.body.*;
import core.body.ui.Color;
import core.body.ui.Component;
import core.utils.Transformation;

public class Test3d extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        Mesh cubeMesh = objectLoader.loadMesh(getClass(), "/shapes/cube.fbx");
        Entity cube = new Entity(cubeMesh, new Material(ColorValue.COLOR_GREEN, null, null));
        Texture grass = objectLoader.loadTexture("C:\\Users\\User\\Desktop\\test\\grass.jpg");
        Texture texture = objectLoader.loadTexture("C:\\Users\\User\\Desktop\\test\\texture.jpg");
        Texture height = objectLoader.loadTexture("C:\\Users\\User\\Desktop\\test\\height.png");
        cube.material.color = new AnimatedTexture(new Texture[] {texture, grass, height}, 1);
        addBody(cube);

        Component component = new Component(100, 100, new Color(new AnimatedTexture(new Texture[] {texture, grass, height}, 0.2f)));
        component.center();
        addBody(component);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
