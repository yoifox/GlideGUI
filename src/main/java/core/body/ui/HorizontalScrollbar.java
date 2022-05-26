package core.body.ui;

import org.joml.Vector2f;
import core.util.Transformation;

public class HorizontalScrollbar extends Component
{
    Button scrollbar;
    public float scrollPosition = 0;
    HorizontalList horizontalList;
    Color btnBc;
    float scrollBarHeight = 0;

    public HorizontalScrollbar(float height, Color bc, HorizontalList parent)
    {
        this.height = height;
        this.width = 1;
        scrollBarHeight = height;
        isWidthPercentage = true;
        this.btnBc = bc;
        this.horizontalList = parent;
        visibleOutsideParentBounds = true;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        isWidthPercentage = true;

        Vector2f pos = Transformation.getTopLeftCorner(horizontalList);
        setPosition(pos.x, pos.y + horizontalList.getHeight());

        scrollbar = new Button();
        scrollbar.x = getX();
        scrollbar.y = getY();
        scrollbar.height = scrollBarHeight;
        scrollbar.width = 64;
        scrollbar.bc = btnBc;
        scrollbar.visibleOutsideParentBounds = true;
        addChild(scrollbar);
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        Vector2f pos = Transformation.getTopLeftCorner(horizontalList);
        setPosition(pos.x, pos.y + horizontalList.getHeight());

        if (scrollbar.isPressed())
        {
            if(scene.mouseInput.getX() <= horizontalList.getTopLeftCorner().x + horizontalList.getWidth() - scrollbar.getWidth() &&
                    scene.mouseInput.getX() >= horizontalList.getTopLeftCorner().x)
            {
                scrollbar.setPosition((float) scene.mouseInput.getX(), scrollbar.y);
                scrollPosition = (float) (horizontalList.getTopLeftCorner().x + horizontalList.getWidth() - scene.mouseInput.getX());
            }

            else if(scene.mouseInput.getX() > horizontalList.getTopLeftCorner().x + horizontalList.getWidth() - scrollbar.getWidth())
            {
                scrollbar.setPosition(horizontalList.getTopLeftCorner().x + horizontalList.getWidth() - scrollbar.getWidth(), scrollbar.y);
                //scrollPosition = horizontalList.getWidth();
                scrollPosition = 0;
            }

            else if(scene.mouseInput.getX() < horizontalList.getTopLeftCorner().x)
            {
                scrollbar.setPosition(horizontalList.getTopLeftCorner().x, scrollbar.y);
                scrollPosition = 0;
                scrollPosition = horizontalList.getWidth();
            }

            horizontalList.scroll(scrollPosition);
        }
    }
}
