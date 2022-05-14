package core.body.ui;

import org.joml.Vector2f;
import core.utils.Transformation;

public class VerticalScrollbar extends Component
{
    Button scrollbar;
    public float scrollPosition = 0;
    VerticalList verticalList;
    Color btnBc;
    float scrollbarWidth = 0;

    public VerticalScrollbar(float width, Color bc, VerticalList parent)
    {
        this.height = 1;
        this.width = width;
        scrollbarWidth = width;
        this.btnBc = bc;
        this.verticalList = parent;
        isHeightPercentage = true;
        visibleOutsideParentBounds = true;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        isWidthPercentage = true;

        Vector2f pos = Transformation.getTopLeftCorner(verticalList);
        setPosition(pos.x + verticalList.getWidth(), pos.y);

        scrollbar = new Button();
        scrollbar.x = getX();
        scrollbar.y = getY();
        scrollbar.height = 64;
        scrollbar.width = scrollbarWidth;
        scrollbar.bc = btnBc;
        scrollbar.visibleOutsideParentBounds = true;
        addChild(scrollbar);
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        Vector2f pos = Transformation.getTopLeftCorner(verticalList);
        setPosition(pos.x + verticalList.getWidth(), pos.y);

        if (scrollbar.isPressed())
        {
            if(scene.mouseInput.getY() <= verticalList.getTopLeftCorner().y + verticalList.getHeight() - scrollbar.getHeight() &&
                    scene.mouseInput.getY() >= verticalList.getTopLeftCorner().y)
            {
                scrollbar.setPosition(scrollbar.x, (float) scene.mouseInput.getY());
                scrollPosition = (float) (verticalList.getTopLeftCorner().y + verticalList.getHeight() - scene.mouseInput.getY());
            }
            else if(scene.mouseInput.getY() > verticalList.getTopLeftCorner().y + verticalList.getHeight() - scrollbar.getHeight())
            {
                scrollbar.setPosition(scrollbar.x, verticalList.getTopLeftCorner().y + verticalList.getHeight() - scrollbar.getHeight());
                scrollPosition = verticalList.getHeight();
            }
            else if(scene.mouseInput.getY() < verticalList.getTopLeftCorner().y)
            {
                scrollbar.setPosition(scrollbar.x, verticalList.getTopLeftCorner().y);
                scrollPosition = 0;
            }
            verticalList.scroll(scrollPosition);
        }
    }
}
