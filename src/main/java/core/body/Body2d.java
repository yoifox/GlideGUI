package core.body;

import org.joml.Vector2f;
import core.utils.MathUtils;

import java.util.Map;

public class Body2d extends Body
{
    //Direct access will ignore children.
    public float x = 0, y = 0;
    public float rotation = 0;
    public float scaleX = 1, scaleY = 1;

    public float x0 = 0, y0 = 0;
    public float rotation0 = 0;
    public float scaleX0 = 1, scaleY0 = 1;

    public float previousX = 0, previousY = 0;
    public float previousRotation = 0;
    public float previousScaleX = 1, previousScaleY = 1;

    public Body2d(float x, float y, float rotation, float scaleX, float scaleY)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.scaleX = scaleX;
        this.scaleY = scaleY;

        this.x0 = x;
        this.y0 = y;
        this.rotation0 = rotation;
        this.scaleX0 = scaleX;
        this.scaleY0 = scaleY;

        this.previousX = x;
        this.previousY = y;
        this.previousRotation = rotation;
        this.previousScaleX = scaleX;
        this.previousScaleY = scaleY;
    }

    public Body2d()
    {

    }

    public void move(float x, float y)
    {
        previousX = this.x;
        previousY = this.y;
        this.x += x;
        this.y += y;
        move(children, x, y);
    }

    public void setPosition(float x, float y)
    {
        move(x - this.x, y - this.y);
    }

    public void rotate(float angle)
    {
        previousRotation = rotation;
        rotation += angle;
        rotate(children, angle);
    }

    public void rotate(float angle, Body parent)
    {
        previousRotation = rotation;
        if(parent instanceof Body3d body3d)
        {
            Vector2f newPos = MathUtils.rotateVector(x, y, body3d.x, body3d.y, angle);
            setPosition(newPos.x, newPos.y);
        }
        else if(parent instanceof Body2d body2d)
        {
            Vector2f newPos = MathUtils.rotateVector(x, y, body2d.x, body2d.y, angle);
            setPosition(newPos.x, newPos.y);
        }
        rotation += angle;
        rotate(children, angle);
    }

    public void setRotation(float angle)
    {
        rotate(angle - this.rotation);
    }

    public void scale(float x, float y)
    {
        previousScaleX = scaleX;
        previousScaleY = scaleY;
        scaleX += x;
        scaleY += y;
        scale(children, x, y);
    }

    public void scale(float x, float y, Body parent)
    {
        previousScaleX = scaleX;
        previousScaleY = scaleY;

        if(parent instanceof Body3d body3d)
        {
            Vector2f newPos = MathUtils.scaleVector(this.x, this.y, body3d.x, body3d.y, x, y);
            setPosition(newPos.x, newPos.y);
        }
        else if(parent instanceof Body2d body2d)
        {
            Vector2f newPos = MathUtils.scaleVector(this.x, this.y, body2d.x, body2d.y, x, y);
            setPosition(newPos.x, newPos.y);
        }

        scaleX += x;
        scaleY += y;
        scale(children, x, y);
    }

    public void setScale(float x, float y)
    {
        scale(x - this.scaleX, y - this.scaleY);
    }

    private void move(Map<String, Body> bodies, float x, float y)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(entry.getValue() instanceof Body3d body3d)
            {
                body3d.move(x, y, 0);
            }
            else if(entry.getValue() instanceof Body2d body2d)
            {
                body2d.move(x, y);
            }
            else
            {
                move(entry.getValue().children, x, y);
            }
        }
    }

    private void rotate(Map<String, Body> bodies, float angle)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(entry.getValue() instanceof Body3d body3d)
            {
                body3d.rotate(angle, 0, 0, body3d.parent);
            }
            else if(entry.getValue() instanceof Body2d body2d)
            {
                body2d.rotate(angle, body2d.parent);
            }
            else
            {
                rotate(entry.getValue().children, angle);
            }
        }
    }

    private void scale(Map<String, Body> bodies, float x, float y)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(entry.getValue() instanceof Body3d body3d)
            {
                body3d.scale(x, y, 0, body3d.parent);
            }
            else if(entry.getValue() instanceof Body2d body2d)
            {
                body2d.scale(x, y, body2d.parent);
            }
            else
            {
                scale(entry.getValue().children, x, y);
            }
        }
    }
}
