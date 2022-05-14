package core.body.ui;

import core.Window;
import core.input.Keyboard;

public class TextArea extends Text
{
    public TextArea(String text, int textSize, Font font)
    {
        super(text, textSize, font);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        scene.keyInput.addCharCallback(new Keyboard.CharCallback() {
            @Override
            public void onCharTyped(Window window, char key) {
                append(key + "");
            }
        });

        scene.keyInput.addActionCallback(new Keyboard.ActionCallback() {
            @Override
            public void onAction(Window window, int action) {
                if(action == Keyboard.ACTION_BACKSPACE)
                {
                    if(!getText().equals(""))
                        setText(getText().substring(0, getText().length() - 1));
                }
                else if(action == Keyboard.ACTION_ENTER)
                {
                    if(!getText().equals(""))
                        append("\n");
                }
            }
        });
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
    }
}
