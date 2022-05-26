package core.body.ui;

import org.joml.Vector2f;
import core.Window;
import core.util.Transformation;

import java.util.ArrayList;
import java.util.List;

public class VerticalList extends Layout
{
    Window window;
    public List<Component> components = new ArrayList<>();
    public float space = 0;

    public VerticalList()
    {
        super();
    }

    public VerticalList(float width, float height)
    {
        super(width, height);
    }

    public VerticalList(float width, float height, float space, List<Component> components)
    {
        super(width, height);
        this.components = components;
        this.space = space;
    }

    public VerticalList(float width, float height, Color bc, float space, List<Component> components)
    {
        super(width, height, bc);
        this.components = components;
        this.space = space;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        notifyDataChange();
        initContent();
    }

    private void initContent()
    {
        for(Component component : components)
        {
            component.parent = this;
            component.scene = scene;
            component.onCreate();
        }
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(updateEveryFrame)
        {
            if(lastScroll == 0)
            {
                notifyDataChange();
            }
            else
            {
                float position = getHeight() - lastScroll;
                float y = position * ((contentHeight - getHeight()) / getHeight());
                notifyDataChange(y);
            }
        }
    }

    public void notifyDataChange()
    {
        notifyDataChange(0);
    }

    private void notifyDataChange(float startPos)
    {
        float pos = startPos;
        for(Component component : components)
        {
            if(!component.isCreated())
            {
                component.parent = this;
                component.scene = scene;
                component.onCreate();
            }
            pos += component.margin[Component.MARGIN_TOP];
            component.setPosition(getX(), pos);
            if(component instanceof Layout layout)
                pos += space + component.margin[Component.MARGIN_BOTTOM] + getLayoutHeight(layout);
            else
                pos += space + component.margin[Component.MARGIN_BOTTOM] + component.getHeight();
        }

        contentWidth = getMaxWidth();
        contentHeight = getHeightSum();

        if(adjustWidth)
        {
            if(contentWidth > width)
                width = contentWidth;
        }
        if(adjustHeight)
        {
            if(contentHeight > height)
                height = contentHeight;
        }

        Vector2f origin = Transformation.getContentTopLeftCornerLayout(this);

        pos = origin.y - startPos;
        for(Component component : components)
        {
            pos += component.margin[Component.MARGIN_TOP];
            component.setPosition(origin.x, pos);
            if(component instanceof Layout layout)
                pos += space + component.margin[Component.MARGIN_BOTTOM] + getLayoutHeight(layout);
            else
                pos += space + component.margin[Component.MARGIN_BOTTOM] + component.getHeight();
        }

        //gravity
        if(gravity == GRAVITY_TOP_LEFT);
        else if(gravity == GRAVITY_TOP_RIGHT)
        {
            for(Component component : components)
                component.move(getWidth() - getContentWidth(), 0);
        }
        else if(gravity == GRAVITY_BOTTOM_LEFT)
        {
            for(Component component : components)
                component.move(0, getHeight() - getContentHeight());
        }
        else if(gravity == GRAVITY_BOTTOM_RIGHT)
        {
            for(Component component : components)
                component.move(getWidth() - getContentWidth(), getHeight() - getContentHeight());
        }
        else if(gravity == GRAVITY_LEFT_MIDDLE)
        {
            for(Component component : components)
                component.move(0, getHeight() / 2f - getContentHeight() / 2f);
        }
        else if(gravity == GRAVITY_RIGHT_MIDDLE)
        {
            for(Component component : components)
                component.move(getWidth() - getContentWidth(), getHeight() / 2f - getContentHeight() / 2f);
        }
        else if(gravity == GRAVITY_TOP_MIDDLE)
        {
            for(Component component : components)
                component.move(getWidth() / 2f - getContentWidth() / 2f, 0);
        }
        else if(gravity == GRAVITY_BOTTOM_MIDDLE)
        {
            for(Component component : components)
                component.move(getWidth() / 2f - getContentWidth() / 2f, getHeight() - getContentHeight());
        }
        else if(gravity == GRAVITY_CENTER)
        {
            for(Component component : components)
                component.move(getWidth() / 2f - getContentWidth() / 2f, getHeight() / 2f - getContentHeight() / 2f);
        }
    }

    private float lastScroll = 0;
    public void scroll(float position)
    {
        position = getHeight() - position;
        if(contentHeight > getHeight() && position != lastScroll)
        {
            float y = position * ((contentHeight - getHeight()) / getHeight());
            notifyDataChange(y);
        }
        lastScroll = position;
    }

    VerticalScrollbar scrollbar;
    public void setScrollbar(VerticalScrollbar scrollbar)
    {
        if(isCreated())
            removeScrollbar();
        this.scrollbar = scrollbar;
        addChild(scrollbar);
    }


    public void removeScrollbar()
    {
        if(scrollbar != null)
            removeChild(scrollbar);
    }

    private float getHeightSum()
    {
        float sum = 0;
        for(Component component : components)
        {
            if(component instanceof Layout layout)
                sum += getLayoutHeight(layout) + component.margin[Component.MARGIN_TOP] + component.margin[Component.MARGIN_BOTTOM] + space;
            else
                sum += component.getHeight() + component.margin[Component.MARGIN_TOP] + component.margin[Component.MARGIN_BOTTOM] + space;
        }
        return sum;
    }

    private float getMaxWidth()
    {
        float max = 0;
        for(Component component : components)
        {
            float x;
            if(component instanceof Layout layout)
                x = layout.getContentWidth();
            else
                x = component.getWidth();
            if(x > max) max = x;
        }
        return max;
    }

    private static float getLayoutHeight(Layout layout)
    {
        return layout.getContentHeight();
        //return Math.max(layout.getContentWidth(), layout.getWidth());
    }
}
