package test;

import core.Scene;
import core.body.ui.HTML;
import core.util.Transformation;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        HTML html = new HTML(1000, 1000);
        InputStream is = new ByteArrayInputStream("aaaaa".getBytes());
        html.open(is);
        addBody(html);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        window.setTitle(1f / delta + "");
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }

    @Override
    public synchronized void updatePhysics(float delta) {
        super.updatePhysics(delta);
    }
}
