public class Card
{
    public static final int COLOR_RED = 1, COLOR_GREEN = 2, COLOR_PURPLE = 3;
    public static final int SHAPE_DIAMOND = 1, SHAPE_ELLIPSE = 2, SHAPE_WAVE = 3;
    public static final int COUNT_ONE = 1, COUNT_TWO = 2, COUNT_THREE = 3;
    public static final int FILLING_HOLLOW = 1, FILLING_STRIPES= 2, FILLING_SOLID = 3;

    public int color, shape, count, filling;
    public int startX, startY, endX, endY;
    public Card(int color, int shape, int count, int filling, int[] coordinates)
    {
        this.color = color;
        this.shape = shape;
        this.count = count;
        this.filling = filling;
        startX = coordinates[0];
        startY = coordinates[1];
        endX = coordinates[2];
        endY = coordinates[3];
    }
}
