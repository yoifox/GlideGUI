package core.body.ui;

import core.Window;
import core.input.Keyboard;
import core.util.MathUtil;

public class TextArea extends Button
{
    boolean isFocused = false;
    public Text text;
    public boolean adjustWidth = true, adjustHeight = true;

    public TextArea(float width, float height, int textSize, Font font)
    {
        super(width, height);
        text = new Text("", textSize, font);
        addChild(text);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        scene.keyInput.addCharCallback(new Keyboard.CharCallback() {
            @Override
            public void onCharTyped(Window window, char key) {
                if(isFocused)
                    text.append(key + "");
            }
        });

        scene.keyInput.addActionCallback(new Keyboard.ActionCallback() {
            @Override
            public void onAction(Window window, int action) {
                if(!isFocused) return;
                if(action == Keyboard.ACTION_BACKSPACE)
                {
                    if(!text.getText().equals(""))
                        text.setText(text.getText().substring(0, text.getText().length() - 1));
                }
                else if(action == Keyboard.ACTION_ENTER)
                {
                    if(!text.getText().equals(""))
                        text.append("\n");
                }
            }
        });

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(Button button) {
                isFocused = true;
            }
        };
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(scene.mouseInput.isLeftButtonJustPressed())
        {
            if(!MathUtil.pointInQuad(this, (float) scene.mouseInput.getX(), (float) scene.mouseInput.getY()))
            {
                isFocused = false;
            }
        }
        if(adjustWidth)
        {
            if(text.getWidth() > getWidth())
            {
                width = text.getWidth();
                isWidthPercentage = false;
            }
        }
        if(adjustHeight)
        {
            if(text.getHeight() > getHeight())
            {
                height = text.getHeight();
                isHeightPercentage = false;
            }
        }
    }
}
