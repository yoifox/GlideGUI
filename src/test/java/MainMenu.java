import org.lwjgl.glfw.GLFW;
import core.Scene;
import core.body.ColorValue;
import core.body.ui.*;

public class MainMenu extends Scene
{
    Component bc;
    HowToPlay howToPlayComp = new HowToPlay();
    @Override
    public void onCreate() {
        super.onCreate();
        GLFW.glfwSetWindowSizeLimits(window.getWindowId(), 750, 800, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
        window.setTitle("Set");
        bc = new Component(700, 0.9f, new Color(new ColorValue(1, 1, 1, 1), 0, null, new float[] {32, 32, 32, 32}));
        bc.boxShadow(48, 0, 0);
        bc.center(); bc.isHeightPercentage = true;
        addBody(bc);

        Component logo = new Component(432, 240, new Color(objectLoader.loadTexture(getClass(), "/set.png")));
        logo.center();
        logo.y = 0.2f;
        bc.addChild(logo);

        Button singlePlayer = new Button(500, 0.1f, new Color(new ColorValue(1, 1, 1, 1), 4, new ColorValue(0.5f, 0.5f, 0.5f, 1), new float[] {48, 48, 48, 48}));
        singlePlayer.center();
        singlePlayer.isHeightPercentage = true;
        singlePlayer.y = 0.5f;
        singlePlayer.visibleOutsideParentBounds = true;
        bc.addChild(singlePlayer);

        Button multiPlayer = new Button(500, 0.1f, new Color(new ColorValue(1, 1, 1, 1), 4, new ColorValue(0.5f, 0.5f, 0.5f, 1), new float[] {48, 48, 48, 48}));
        multiPlayer.center();
        multiPlayer.isHeightPercentage = true;
        multiPlayer.y = 0.65f;
        multiPlayer.visibleOutsideParentBounds = true;
        bc.addChild(multiPlayer);

        Button howToPlay = new Button(500, 0.1f, new Color(new ColorValue(1, 1, 1, 1), 4, new ColorValue(0.5f, 0.5f, 0.5f, 1), new float[] {48, 48, 48, 48}));
        howToPlay.center();
        howToPlay.isHeightPercentage = true;
        howToPlay.y = 0.8f;
        howToPlay.visibleOutsideParentBounds = true;
        bc.addChild(howToPlay);

        Text singlePlayerText = new Text("Single-player", 42, fontArial);
        Text howToPlayText = new Text("How to play", 42, fontArial);
        Text multiPlayerText = new Text("Multi-player", 42, fontArial);
        singlePlayer.addChild(singlePlayerText);
        singlePlayerText.center();
        singlePlayerText.setTextColor(new ColorValue(0.5f, 0.5f, 0.5f, 1));
        multiPlayer.addChild(multiPlayerText);
        multiPlayerText.center();
        multiPlayerText.setTextColor(new ColorValue(0.5f, 0.5f, 0.5f, 1));
        howToPlay.addChild(howToPlayText);
        howToPlayText.center();
        howToPlayText.setTextColor(new ColorValue(0.5f, 0.5f, 0.5f, 1));

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(Button button) {
                if(howToPlayComp.visible) return;
                if(button == singlePlayer) changeScene(new SinglePlayer());
                else if(button == multiPlayer) changeScene(new MultiPlayer());
                else if(button == howToPlay)
                {
                    howToPlayComp.visible = true;
                }
            }
        };

        Button.OnHoverListener onHoverListener = new Button.OnHoverListener() {
            @Override
            public void onEnter(Button button) {
                if(!howToPlayComp.visible)
                    button.bc.color = new ColorValue(0.8f, 0.8f, 0.8f, 1);
            }

            @Override
            public void onLeave(Button button) {
                button.bc.color = new ColorValue(1, 1, 1, 1);
            }
        };

        singlePlayer.onClickListener = onClickListener;
        //multiPlayer.onClickListener = onClickListener;
        howToPlay.onClickListener = onClickListener;

        singlePlayer.onHoverListener = onHoverListener;
        multiPlayer.onHoverListener = onHoverListener;
        howToPlay.onHoverListener = onHoverListener;

        howToPlayComp.visible = false;
        bc.addChild(howToPlayComp);
    }
}
