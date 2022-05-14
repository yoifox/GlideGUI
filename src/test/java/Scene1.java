import core.Scene;
import core.body.ColorValue;
import core.body.ui.*;

public class Scene1 extends Scene
{
    @Override
    public void onCreate()
    {
        HorizontalList horizontalList = new HorizontalList();
        horizontalList.space = 4;
        horizontalList.center();

        for(int i = 0; i < 3; i++)
        {
            Button component = new Button(64, 48, new Color(0.8f, 0.8f, 0.8f, 1));
            component.bc.roundness = new float[] {8, 8, 8, 8};
            component.bc.borderWidth = 1;
            component.bc.borderColor = new ColorValue(0.65f, 0.65f, 0.65f, 1);
            component.boxShadow(8, 0, 0);

            Text text = new Text("Button", 18, fontRoboto);
            text.positionEachFrame(0.5f, 0.56f);
            text.setTextColor(new ColorValue(0.1f, 0.1f, 0.1f, 1));
            component.addChild(text);

            component.onHoverListener = new Button.OnHoverListener() {
                @Override
                public void onEnter(Button button) {
                    button.bc.color = new ColorValue(0.65f, 0.65f, 0.65f, 1);
                    button.boxShadow(18, 0, 0, new ColorValue(0, 0, 0, 0.5f));
                }

                @Override
                public void onLeave(Button button) {
                    button.bc.color = new ColorValue(0.8f, 0.8f, 0.8f, 1);
                    button.boxShadow(8, 0, 0);
                }
            };

            horizontalList.components.add(component);
        }

        addBody(horizontalList);

        super.onCreate();
    }
}