package core.body.ui;

import core.Window;
import core.body.Body;
import core.body.ColorValue;
import core.input.Keyboard;
import core.util.MathUtil;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class TextArea extends Button
{
    boolean isFocused = false;
    private final Text text;
    public boolean adjustWidth = true, adjustHeight = true;
    public boolean multiLine = true;
    public int maxLength = 150;
    private int textSize;
    Component cursor;
    int cursorPos = -1;

    public TextArea(float width, float height, int textSize, Font font)
    {
        super(width, height);
        text = new Text("", textSize, font);
        addChild(text);
        cursor = new Component(2, textSize, Color.COLOR_BLACK);
        cursor.setPosition(getX(), getY());
        cursor.visible = false;
        addChild(cursor);
        this.textSize = textSize;
    }

    private void updateCursorPosition()
    {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(cursorPos == -1)
                {
                    if(text.getText().length() == 0)
                    {
                        if(getTextDirection() == Text.LEFT_TO_RIGHT)
                            cursor.setPosition(getTopLeftCorner().x, getTopLeftCorner().y);
                        else if(getTextDirection() == Text.RIGHT_TO_LEFT)
                            cursor.setPosition(getTopLeftCorner().x + getWidth(), getTopLeftCorner().y);
                        else if(getTextDirection() == Text.CENTER)
                            cursor.setPosition(getTopLeftCorner().x + getWidth() / 2f, getTopLeftCorner().y + getHeight() / 2f - textSize / 2f);
                        return;
                    }
                    TextCharacter firstChar = text.getCharAt(0);
                    if(firstChar == null) return;
                    cursor.setPosition(firstChar.getX(), firstChar.getY() - firstChar.descent);
                    return;
                }
                TextCharacter lastChar = text.getCharAt(cursorPos);
                if(lastChar == null) return;
                if(!lastChar.isCreated())
                {
                    lastChar.doOnCreate(new Runnable() {
                        @Override
                        public void run() {
                            doNextFrame(new Runnable() {
                                @Override
                                public void run() {
                                    cursor.setPosition(lastChar.getX() + lastChar.getWidth() + 1, lastChar.getY() - lastChar.descent);
                                    cursor.height = textSize - 4;
                                }
                            });
                        }
                    });
                }
                else
                {
                    doNextFrame(new Runnable() {
                        @Override
                        public void run() {
                            cursor.setPosition(lastChar.getX() + lastChar.getWidth() + 1, lastChar.getY() - lastChar.descent);
                            cursor.height = textSize - 4;
                        }
                    });
                }
            }
        };

        if(cursor.isCreated())
            r.run();
        else
        {
            cursor.doOnCreate(r);
        }
    }

    public TextArea setTextSize(int textSize)
    {
        text.setTextSize(textSize);
        updateCursorPosition();
        return this;
    }

    public TextArea setFont(Font font)
    {
        text.setFont(font);
        updateCursorPosition();
        return this;
    }

    public TextArea setText(String text)
    {
        this.text.setText(text);
        doNextFrame(() -> {
            cursorPos = text.length() - 1;
            updateCursorPosition();
        });
        return this;
    }

    public TextArea append(String text)
    {
        this.text.append(text);
        doNextFrame(() -> {
            cursorPos = text.length() - 1;
            updateCursorPosition();
        });
        return this;
    }

    public TextArea setTextColor(ColorValue color)
    {
        text.setTextColor(color);
        cursor.setBackground(color);
        return this;
    }

    public TextArea setTextDirection(int textDirection)
    {
        text.setTextDirection(textDirection);
        updateCursorPosition();
        return this;
    }

    public int getTextDirection()
    {
        return text.getTextDirection();
    }

    public String getText()
    {
        return text.getText();
    }

    public boolean isFocused() {
        return isFocused;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        scene.keyInput.addCharCallback(new Keyboard.CharCallback() {
            @Override
            public void onCharTyped(Window window, char key) {
                if(isFocused && text.getText().length() != maxLength)
                {
                    addChar(key);
                    cursorPos++;
                    updateCursorPosition();
                }
            }
        });

        scene.keyInput.addActionCallback(new Keyboard.ActionCallback() {
            @Override
            public void onAction(Window window, int action) {
                if(!isFocused) return;
                if(action == GLFW.GLFW_KEY_BACKSPACE)
                {
                    if(cursorPos != -1)
                    {
                        removeChar();
                        cursorPos--;
                    }
                }
                else if(action == GLFW.GLFW_KEY_ENTER && text.getText().length() != maxLength)
                {
                    if(multiLine)
                    {
                        addChar('\n');
                        cursorPos++;
                    }
                }
                else if(action == GLFW.GLFW_KEY_LEFT)
                {
                    if(cursorPos > - 1)
                        cursorPos--;
                }
                else if(action == GLFW.GLFW_KEY_RIGHT)
                {
                    if(cursorPos < text.getText().length() - 1)
                        cursorPos++;
                }
                updateCursorPosition();
            }
        });

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(Button button) {
                isFocused = true;
            }
        };
        updateCursorPosition();
    }

    float time = 0;
    @Override
    public void update(float delta)
    {
        super.update(delta);
        Vector2f topLeft = getTopLeftCorner();
        text.isXPercentage = false;
        text.isYPercentage = false;
        if(text.getTextDirection() == Text.RIGHT_TO_LEFT)
            text.setPosition(topLeft.x + getWidth(), topLeft.y);
        else if(text.getTextDirection() == Text.LEFT_TO_RIGHT)
            text.setPosition(topLeft.x, topLeft.y);
        else if(text.getTextDirection() == Text.CENTER)
            text.center();

        time += delta;
        if(time > 0.2f)
        {
            time = 0;
            if(isFocused)
                cursor.visible = !cursor.visible;
        }
        if(scene.mouseInput.isLeftButtonJustPressed())
        {
            if(!MathUtil.pointInQuad(this, (float) scene.mouseInput.getX(), (float) scene.mouseInput.getY()))
            {
                isFocused = false;
                updateCursorPosition();
                cursor.visible = false;
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
        if(scene.mouseInput.isLeftButtonJustPressed() && isFocused)
        {
            int i = 0;
            int min = 0;
            float minDistance = -1;
            for(Map.Entry<String, Body> entry : text.children.entrySet())
            {
                if(entry.getValue() instanceof TextCharacter textCharacter)
                {
                    float d = MathUtil.distance(textCharacter, new Vector2f((float)scene.mouseInput.getX(), (float) scene.mouseInput.getY()));
                    if(d < minDistance || minDistance == -1)
                    {
                        min = i;
                        minDistance = d;
                    }
                    i++;
                }
            }
            if(min == text.getText().length() - 1)
            {
                cursorPos = min;
                updateCursorPosition();
            }
            else if(text.getTextDirection() == Text.LEFT_TO_RIGHT)
            {
                cursorPos = min - 1;
                updateCursorPosition();
            }
            else if(text.getTextDirection() == Text.CENTER)
            {
                cursorPos = min - 1;
                updateCursorPosition();
            }
            else
            {
                cursorPos = min + 1;
                updateCursorPosition();
            }
        }
    }

    private void addChar(char c) {
        String str = text.getText();
        if(cursorPos == str.length() - 1)
        {
            text.append(c + "");
            return;
        }
        String newString = str.substring(0, cursorPos + 1) + c + str.substring(cursorPos + 1);
        text.setText(newString);
    }

    private void removeChar()
    {
        String str = text.getText();
        String newString = str.substring(0, cursorPos) + str.substring(cursorPos + 1);
        text.setText(newString);
    }
}
