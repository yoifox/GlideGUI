package core.body.ui;

import org.joml.Vector2f;
import core.util.Transformation;

import java.util.ArrayList;
import java.util.List;

public class HorizontalList extends Layout
{
    public List<Component> components = new ArrayList<>();
    public float space = 0;

    public HorizontalList()
    {
        super();
    }

    public HorizontalList(float width, float height)
    {
        super(width, height);
    }

    public HorizontalList(float width, float height, float space, List<Component> components)
    {
        super(width, height);
        this.components = components;
        this.space = space;
    }

    public HorizontalList(float width, float height, Color bc, float space, List<Component> components)
    {
        super(width, height, bc);
        this.components = components;
        this.space = space;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        if(scrollbar != null)
        {
            addChild(scrollbar);
        }
        notifyDataChange();
        initContent();
    }

    private void initContent()
    {
        for(Component component : components)
        {
            component.parent = this;
            component.scene = scene;
            if(!component.isCreated())
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
                float position = getWidth() - lastScroll;
                float x = position * ((contentWidth - getWidth()) / getWidth());
                notifyDataChange(x);
            }
        }
    }

    public void notifyDataChange()
    {
        notifyDataChange(0);
    }

    public void notifyDataChange(float startPos)
    {
        float pos = -startPos;
        for(Component component : components)
        {
            if(!component.isCreated())
            {
                component.parent = this;
                component.scene = scene;
                component.onCreate();
            }
            pos += component.margin[Component.MARGIN_LEFT];
            component.setPosition(pos, getY());
            if(component instanceof Layout layout)
                pos += space + component.margin[Component.MARGIN_RIGHT] + getLayoutWidth(layout);
            else
                pos += space + component.margin[Component.MARGIN_RIGHT] + component.getWidth();
        }

        contentWidth = getWidthSum();
        contentHeight = getMaxHeight();

        if(adjustWidth)
        {
            if(contentWidth > getWidth())
                width = contentWidth;
        }
        if(adjustHeight)
        {
            if(contentHeight > getHeight())
                height = contentHeight;
        }

        Vector2f origin = Transformation.getContentTopLeftCornerLayout(this);

        pos = origin.x - startPos;
        for(Component component : components)
        {
            pos += component.margin[Component.MARGIN_LEFT];
            component.setPosition(pos, origin.y);
            //component.setPosition(origin.x + (component.x - getX()), origin.y);
            if(component instanceof Layout layout)
                pos += space + component.margin[Component.MARGIN_RIGHT] + getLayoutWidth(layout);
            else
                pos += space + component.margin[Component.MARGIN_RIGHT] + component.getWidth();
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
        position = getWidth() - position;
        if(contentWidth > getWidth() && position != lastScroll)
        {
            float x = position * ((contentWidth - getWidth()) / getWidth());
            notifyDataChange(x);
        }
        lastScroll = position;
    }

    private float getWidthSum()
    {
        float sum = 0;
        for(Component component : components)
        {
            if(component instanceof Layout layout)
                sum += layout.getContentWidth() + component.margin[Component.MARGIN_LEFT] + component.margin[Component.MARGIN_RIGHT] + space;
            else
                sum += component.getWidth() + component.margin[Component.MARGIN_LEFT] + component.margin[Component.MARGIN_RIGHT] + space;
        }
        return sum;
    }

    private float getMaxHeight()
    {
        float max = 0;
        for(Component component : components)
        {
            float x;
            if(component instanceof Layout layout)
                x = layout.getContentHeight();
            else
                x = component.getHeight();
            if(x > max) max = x;
        }
        return max;
    }

    HorizontalScrollbar scrollbar;
    public void setScrollbar(HorizontalScrollbar scrollbar)
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

    private static float getLayoutWidth(Layout layout)
    {
        return layout.getContentWidth();
        //return Math.max(layout.getContentWidth(), layout.getWidth());
    }
}
