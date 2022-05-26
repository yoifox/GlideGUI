package core.body;

import org.joml.Vector4f;
import core.util.Util;

public class ColorValue
{
    public static final ColorValue COLOR_BLACK = new ColorValue(0, 0, 0, 1);
    public static final ColorValue COLOR_WHITE = new ColorValue(1, 1, 1, 1);
    public static final ColorValue COLOR_TRANSPARENT = new ColorValue(0, 0, 0, 0);
    public static final ColorValue COLOR_RED = new ColorValue(1, 0, 0, 1);
    public static final ColorValue COLOR_GREEN = new ColorValue(0, 1, 0, 1);
    public static final ColorValue COLOR_BLUE = new ColorValue(0, 0, 1, 1);
    public static final ColorValue COLOR_YELLOW = new ColorValue(1, 1, 0, 1);
    public static final ColorValue COLOR_PURPLE = new ColorValue(1, 0, 1, 1);

    private final Vector4f color = new Vector4f();

    public ColorValue() {
    }

    public ColorValue(int color)
    {
        int[] bytes = Util.intToBytes(color);
        int[] ints = new int[] {bytes[0], bytes[1], bytes[2], bytes[3]};
        this.color.x = ints[0] / 256f;
        this.color.y = ints[1] / 256f;
        this.color.z = ints[2] / 256f;
        this.color.w = ints[3] / 256f;
    }

    public ColorValue(float r, float g, float b, float a)
    {
        color.x = r;
        color.y = g;
        color.z = b;
        color.w = a;
    }

    public void setColor(int color)
    {
        int[] bytes = Util.intToBytes(color);
        int[] ints = new int[] {bytes[0], bytes[1], bytes[2], bytes[3]};
        this.color.x = ints[0] / 256f;
        this.color.y = ints[1] / 256f;
        this.color.z = ints[2] / 256f;
        this.color.w = ints[3] / 256f;
    }

    public void setColor(float r, float g, float b, float a)
    {
        color.x = r;
        color.y = g;
        color.z = b;
        color.w = a;
    }

    public Vector4f vec4()
    {
        return color;
    }
}
