package test;

import core.Scene;
import core.body.ColorValue;
import core.body.Entity;
import core.body.Material;
import core.body.Mesh;
import core.body.ui.Color;
import core.body.ui.Component;
import core.util.Transformation;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        String root = "C:\\Users\\User\\Desktop\\hdr\\";
        skyBox = objectLoader.loadCubeMap(root + "px.png", root + "nx.png", root + "py.png",
                root + "ny.png", root + "pz.png", root + "nz.png");
        Mesh sphere = objectLoader.loadMesh(getClass(), "/shapes/sphere.fbx");
        Mesh cube = objectLoader.loadMesh("C:\\Users\\User\\Desktop\\test\\cube.fbx");
        Mesh other = objectLoader.loadMesh("C:\\Users\\User\\Desktop\\test\\ring.fbx");
        Entity entity = new Entity(sphere);
        entity.cubeMapReflection = skyBox;
        entity.material.metallicValue = 1;
        addBody(entity);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
