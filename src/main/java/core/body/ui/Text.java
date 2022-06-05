package core.body.ui;

import org.joml.Vector2f;
import core.body.Body;
import core.body.ColorValue;
import core.util.Const;
import core.util.Transformation;

import java.util.Map;

public class Text extends Layout
{
    private String text;
    private int textSize = 72;
    private ColorValue textColor = ColorValue.COLOR_BLACK;
    private int lineSpacing = 12;
    private int textDirection = LEFT_TO_RIGHT;
    public static int LEFT_TO_RIGHT = 0, RIGHT_TO_LEFT = 1, CENTER = 2;
    Font font;

    private String previousText;
    private int previousTextSize;
    private int previousLineSpacing;
    private Font previousFont;
    private float previousWidth = 0, previousHeight = 0;

    private float lastPosX = 0;

    public Text(String text, int textSize, Font font)
    {
        this.text = text;
        this.textSize = textSize;
        this.font = font;
        adjustWidth = true;
        adjustHeight = true;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        setText(text);
    }

    public Text append(String text)
    {
        this.text = this.text + text;
        float ax = lastPosX;
        for(char c : text.toCharArray())
        {
            TextCharacter textCharacter = new TextCharacter(font, String.valueOf(c), textSize);
            textCharacter.x = ax;
            ax += textCharacter.width;
            if(textColor != null)
                textCharacter.textColor = textColor;
            textCharacter.margin[MARGIN_BOTTOM] = lineSpacing;
            addChild(textCharacter);
        }
        lastPosX = ax;
        return this;
    }

    public Text setText(String text)
    {
        this.text = text;
        children.clear();
        float ax = 0;
        for(char c : text.toCharArray())
        {
            TextCharacter textCharacter = new TextCharacter(font, String.valueOf(c), textSize);
            textCharacter.x = ax;
            ax += textCharacter.width;
            if(textColor != null)
                textCharacter.textColor = textColor;
            textCharacter.margin[MARGIN_BOTTOM] = lineSpacing;

            addChild(textCharacter);
        }
        lastPosX = ax;
        return this;
    }

    public Text setTextSize(int textSize)
    {
        this.textSize = textSize;
        setText(text);
        return this;
    }

    public Text setTextColor(ColorValue textColor)
    {
        this.textColor = textColor;
        setText(text);
        return this;
    }

    public Text setLineSpacing(int lineSpacing)
    {
        this.lineSpacing = lineSpacing;
        setText(text);
        return this;
    }

    public Text setFont(Font font)
    {
        this.font = font;
        setText(text);
        return this;
    }

    public void setTextDirection(int textDirection)
    {
        this.textDirection = textDirection;
    }

    public int getTextSize()
    {
        return textSize;
    }

    public String getText()
    {
        return text;
    }

    public ColorValue getTextColor() {
        return textColor;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public Font getFont() {
        return font;
    }

    public int getTextDirection() {
        return textDirection;
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        float posX = getX();
        float posY = getY();
        float maxHeightInLine = 0;
        float maxWidth = 0;

        if(adjustWidth) width = 0;
        if(adjustHeight) height = 0;

        int counter = 0;
        for(Map.Entry<String, Body> entry : children.entrySet())
        {
            if(entry.getValue() instanceof TextCharacter textCharacter)
            {
                if(textDirection == LEFT_TO_RIGHT || textDirection == CENTER)
                    posX += textCharacter.margin[MARGIN_LEFT];
                else
                    posX -= (textCharacter.width + textCharacter.margin[Component.MARGIN_RIGHT]);

                textCharacter.x = posX;
                textCharacter.y = posY + textCharacter.descent;

                if(textDirection == LEFT_TO_RIGHT || textDirection == CENTER)
                    posX += textCharacter.width + textCharacter.margin[Component.MARGIN_RIGHT];
                else
                    posX -= textCharacter.margin[MARGIN_LEFT];

                if(textCharacter.height + textCharacter.margin[Component.MARGIN_TOP] + textCharacter.margin[Component.MARGIN_BOTTOM] > maxHeightInLine)
                {
                    maxHeightInLine = textCharacter.height + textCharacter.margin[Component.MARGIN_TOP] + textCharacter.margin[Component.MARGIN_BOTTOM];
                }

                if(textCharacter.text.equals(Const.lineSeparator))
                {
                    textCharacter.visible = false;
                    posY += maxHeightInLine;
                    maxHeightInLine = 0;
                    if(posX > maxWidth) maxWidth = posX;
                    posX = getX();
                }

            }
            counter++;
        }

        if(posX > maxWidth) maxWidth = posX;
        posY += maxHeightInLine;

        contentWidth = maxWidth - getX();
        contentHeight = posY - getY();

        if(adjustWidth) width = contentWidth;
        if(adjustHeight) height = contentHeight;

        Vector2f origin = Transformation.getTopLeftCorner(this);
        for(Map.Entry<String, Body> entry : children.entrySet())
        {
            if (entry.getValue() instanceof TextCharacter textCharacter)
            {
                textCharacter.x -= getX() - origin.x;
                textCharacter.y -= getY() - origin.y;
            }
        }

        previousWidth = width;
        previousHeight = height;
        previousText = text;
        previousTextSize = textSize;
        previousLineSpacing = lineSpacing;
        previousFont = font;
    }

    public TextCharacter getCharAt(int index)
    {
        int i = 0;
        for(Map.Entry<String, Body> entry : children.entrySet())
        {
            if(entry.getValue() instanceof TextCharacter textCharacter)
            {
                if(i == index)
                    return textCharacter;
                i++;
            }
        }
        return null;
    }
}
