package test;

import core.Scene;
import core.body.*;
import core.body.light.DirectionalLight;
import core.util.Transformation;

public class Test extends Scene
{
    Entity cube;

    @Override
    public void onCreate() {
        super.onCreate();
        String root = "C:\\Users\\User\\Desktop\\hdr\\";
        skyBox = objectLoader.loadCubeMap(root + "px.png", root + "nx.png", root + "py.png",
                root + "ny.png", root + "pz.png", root + "nz.png");

        Mesh sphere = objectLoader.loadMesh(getClass(), "/shapes/sphere.fbx");
        Mesh cylinder = objectLoader.loadMesh(getClass(), "/shapes/cylinder.fbx");
        Mesh other = objectLoader.loadMesh("C:\\Users\\User\\Desktop\\test\\ring.fbx");
        cube = new Entity(other, new Material(new ColorValue(0.3f, 0.34f, 0.4f, 1)));
        cube.material.metallicValue = 0.45f;
        cube.rotate(-90, 0, 0);
        addBody(cube);
        directionalLight = new DirectionalLight(0, 0, 0, 2, new ColorValue(0.3f, 0.3f, 0.3f, 1));
        setWorldColor(0.5f, 0.5f, 0.55f, 1);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
        cube.rotate(1, 0, 0);
    }
}
