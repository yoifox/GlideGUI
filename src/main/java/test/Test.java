package test;

import core.Scene;
import core.util.Transformation;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        window.setTitle(1f / delta + "");
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
