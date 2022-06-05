package core.body.ui;

import core.body.ColorValue;
import core.body.Texture;
import cz.vutbr.web.css.MediaSpec;
import cz.vutbr.web.css.MediaSpecAll;
import org.fit.cssbox.awt.GraphicsEngine;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.layout.Dimension;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

public class Color
{
    public static final Color COLOR_BLACK = new Color(0, 0, 0, 1);
    public static final Color COLOR_WHITE = new Color(1, 1, 1, 1);
    public static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
    public static final Color COLOR_RED = new Color(1, 0, 0, 1);
    public static final Color COLOR_GREEN = new Color(0, 1, 0, 1);
    public static final Color COLOR_BLUE = new Color(0, 0, 1, 1);
    public static final Color COLOR_YELLOW = new Color(1, 1, 0, 1);
    public static final Color COLOR_PURPLE = new Color(1, 0, 1, 1);
    public static final Color COLOR_GRAY = new Color(0.5f, 0.5f, 0.5f, 1);
    public static final Color COLOR_LIGHT_GRAY = new Color(0.8f, 0.8f, 0.8f, 1);

    public Texture texture;
    public ColorValue color = new ColorValue(0, 0, 0, 0);
    public ColorValue addColor = new ColorValue(0, 0, 0, 0);
    public ColorValue addColorTransparent = new ColorValue(0, 0, 0, 0);
    public ColorValue mulColor = new ColorValue(1, 1, 1, 1);
    public float borderWidth = 0;
    public ColorValue borderColor = ColorValue.COLOR_TRANSPARENT;
    public float[] roundness = new float[] {0, 0, 0, 0}; //only percentage
    public boolean[] isRoundnessPercentage = new boolean[] {false, false, false, false};
    public static final int ROUNDNESS_TOP_LEFT = 0, ROUNDNESS_TOP_RIGHT = 1, ROUNDNESS_BOTTOM_LEFT = 2, ROUNDNESS_BOTTOM_RIGHT = 3;

    public Color() {}
    public Color(Texture texture) {this.texture = texture;}
    public Color(ColorValue color) {this.color = color;}
    public Color(float r, float g, float b, float a) {this.color = new ColorValue(r, g, b, a);}

    public Color(ColorValue color, float borderWidth, ColorValue borderColor, float[] roundness)
    {
        this.color = color == null ? new ColorValue() : color;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor == null ? ColorValue.COLOR_TRANSPARENT : borderColor;
        this.roundness = roundness == null ? new float[4] : roundness;
    }

    public Color(Texture texture, float borderWidth, ColorValue borderColor, float[] roundness)
    {
        this.texture = texture;
        this.color = new ColorValue();
        this.borderWidth = borderWidth;
        this.borderColor = borderColor == null ? ColorValue.COLOR_TRANSPARENT : borderColor;
        this.roundness = roundness == null ? new float[4] : roundness;
    }
}
