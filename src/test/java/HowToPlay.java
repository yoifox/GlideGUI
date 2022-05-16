import core.body.ui.*;

public class HowToPlay extends Button
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        width = 500;
        height = 500;
        center();
        bc = Color.COLOR_WHITE;
        shadow(1000, 0.5f, 0.5f);

        Component game1 = new Component(200, 155);
        game1.bc = new Color(scene.objectLoader.loadTexture(getClass(), "/game1.jpg"));

        Text text = new Text("The object of the game is to identify a set of 3 cards from\n12 cards placed face-up on the table.\nEach card has four features, which can vary as follows:", 18, scene.fontArial);
        text.isXPercentage = true; text.x = 0.1f;
        text.isYPercentage = true; text.y = 0.1f;
        text.origin = Component.ORIGIN_TOP_LEFT;
        text.setLineSpacing(6);
        addChild(text);

        game1.center();
        addChild(game1);

        Text text1 = new Text("A set consists of 3 cards in which each of the cards'\nfeatures, looked at one-by-one, are the same on each card,\nor, are different on each card. All of the features must\nseparately satisfy this rule.", 18, scene.fontArial);
        text1.isXPercentage = true; text1.x = 0.1f;
        text1.isYPercentage = true; text1.y = 0.9f;
        text1.origin = Component.ORIGIN_BOTTOM_LEFT;
        text1.setLineSpacing(6);
        addChild(text1);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(scene.mouseInput.isLeftButtonJustPressed() && !isPressed())
            visible = false;
    }
}
