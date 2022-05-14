package core.body.ui;

import core.body.ColorValue;

public class TextCharacter extends Component
{
    private Font font;
    public String text;
    public int textSize = 32;
    public ColorValue textColor;
    public int descent = 0;
    public int ascent = 0;
    public boolean initialized = false;
    private String string;

    public TextCharacter(Font font, String text, int size)
    {
        this.font = font;
        this.text = text;
        this.textSize = size;
        this.textColor = ColorValue.COLOR_BLACK;
    }

    public String toString()
    {
        if(string == null)
        {
            string = font.getName() + "," + textSize + "," + text;
        }
        return string;
    }

    public Font getFont()
    {
        return font;
    }

    public void setFont(Font font)
    {
        this.font = font;
    }
}
