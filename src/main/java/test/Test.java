package test;

import core.Scene;
import core.body.ColorValue;
import core.body.ui.TextArea;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        TextArea textArea = new TextArea(100, 100, 32, fontRoboto);
        textArea.setBorder(1, ColorValue.COLOR_BLACK);
        textArea.center();
        addBody(textArea);
    }
}
