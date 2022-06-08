package test;

import core.Scene;
import core.body.*;
import core.body.ui.Color;
import core.body.ui.Component;
import core.body.ui.Text;
import core.loader.ModelLoader;
import core.util.Transformation;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        Model model = ModelLoader.loadModel("C:\\Users\\User\\Desktop\\mess\\medieval_city_pack.fbx", objectLoader);
        assert model != null;
        for(Mesh mesh : model.meshes)
        {
            Entity entity = new Entity(mesh, new Material(ColorValue.COLOR_GRAY));
            addBody(entity);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        window.setTitle(1f / delta + "");
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
