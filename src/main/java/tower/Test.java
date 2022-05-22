package tower;

import core.Scene;
import core.body.ColorValue;
import core.body.ui.Button;
import core.body.ui.Text;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        Button button = new Button(200, 100);
        button.setBackground(new ColorValue(0.8f, 0.8f, 0.8f, 1))
                .setBorder(2, new ColorValue(0.5f, 0.5f, 0.5f, 1))
                .addChild(new Text("Restart", 32, fontRoboto).center());
        button.onHover(new ColorValue(0.5f, 0.5f, 0.5f, 1));
        button.onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(Button button) {
                changeScene(new TowerOfHanoi());
            }
        };
        button.center();
        button.y = 0.9f;
        addBody(button);
    }
}
