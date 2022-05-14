package core.body.ui;

public class Layout extends Component
{
    protected float contentWidth, contentHeight;
    public int gravity = GRAVITY_TOP_LEFT;
    public boolean updateEveryFrame = true;
    public static final int GRAVITY_TOP_LEFT = 0, GRAVITY_TOP_RIGHT = 1, GRAVITY_BOTTOM_LEFT = 2, GRAVITY_BOTTOM_RIGHT = 3,
            GRAVITY_LEFT_MIDDLE = 4, GRAVITY_RIGHT_MIDDLE = 5, GRAVITY_TOP_MIDDLE = 6, GRAVITY_BOTTOM_MIDDLE = 7, GRAVITY_CENTER = 8;

    public boolean adjustWidth = true;
    public boolean adjustHeight = true;

    public float getContentWidth() {
        return contentWidth;
    }

    public float getContentHeight() {
        return contentHeight;
    }

    public Layout(float width, float height) {
        super(width, height);
    }

    public Layout(float width, float height, Color bc) {
        super(width, height, bc);
    }

    public Layout() {
    }
}
