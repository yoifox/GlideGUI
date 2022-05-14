package core.body;

import org.joml.Vector3f;
import core.utils.MathUtils;

import java.util.Map;

public class Body3d extends Body
{
    //Direct access will ignore children.
    public float x = 0, y = 0, z = 0;
    public float rotationX = 0, rotationY = 0, rotationZ = 0;
    public float scaleX = 1, scaleY = 1, scaleZ = 1;

    public float x0 = 0, y0 = 0, z0 = 0;
    public float rotationX0 = 0, rotationY0 = 0, rotationZ0 = 0;
    public float scaleX0 = 1, scaleY0 = 1, scaleZ0 = 1;

    public float previousX = 0, previousY = 0, previousZ = 0;
    public float previousRotationX = 0, previousRotationY = 0, previousRotationZ = 0;
    public float previousScaleX = 1, previousScaleY = 1, previousScaleZ = 1;


    public Body3d(float x, float y, float z, float rotationX, float rotationY, float rotationZ, float scaleX, float scaleY, float scaleZ)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;

        this.x0 = x;
        this.y0 = y;
        this.z0 = z;
        this.rotationX0 = rotationX;
        this.rotationY0 = rotationY;
        this.rotationZ0 = rotationZ;
        this.scaleX0 = scaleX;
        this.scaleY0 = scaleY;
        this.scaleZ0 = scaleZ;

        this.previousX = x;
        this.previousY = y;
        this.previousZ = z;
        this.previousRotationX = rotationX;
        this.previousRotationY = rotationY;
        this.previousRotationZ = rotationZ;
        this.previousScaleX = scaleX;
        this.previousScaleY = scaleY;
        this.previousScaleZ = scaleZ;
    }

    public Body3d()
    {

    }

    public void move(float x, float y, float z)
    {
        previousX = this.x;
        previousY = this.y;
        previousZ = this.z;
        this.x += x;
        this.y += y;
        this.z += z;
        move(children, x, y, z);
    }

    public void scale(float x, float y, float z)
    {
        previousScaleX = scaleX;
        previousScaleY = scaleY;
        previousScaleZ = scaleZ;
        scaleX += x;
        scaleY += y;
        scaleZ += z;
        scale(children, x, y, z);
    }

    public void scale(float x, float y, float z, Body parent)
    {
        previousScaleX = scaleX;
        previousScaleY = scaleY;
        previousScaleZ = scaleZ;
        if(parent instanceof Body3d body3d)
        {
            Vector3f newPos = MathUtils.scaleVector(this.x, this.y, this.z, body3d.x, body3d.y, body3d.z, x, y, z);
            setPosition(newPos.x, newPos.y, newPos.z);
        }
        else if(parent instanceof Body2d body2d)
        {
            Vector3f newPos = MathUtils.scaleVector(this.x, this.y, this.z, body2d.x, body2d.y, 0, x, y, z);
            setPosition(newPos.x, newPos.y, newPos.z);
        }
        scaleX += x;
        scaleY += y;
        scaleZ += z;
        scale(children, x, y, z);
    }

    public void setScale(float x, float y, float z)
    {
        scale(x - this.scaleX, y - this.scaleY, z - this.scaleZ);
    }

    public void setPosition(float x, float y, float z)
    {
        move(x - this.x, y - this.y, z - this.z);
    }

    public void rotate(float x, float y, float z)
    {
        previousRotationX = rotationX;
        previousRotationY = rotationY;
        previousRotationZ = rotationZ;
        rotationX += x;
        rotationY += y;
        rotationZ += z;
        rotate(children, x, y, z);
    }

    public void rotate(float x, float y, float z, Body parent)
    {
        previousRotationX = rotationX;
        previousRotationY = rotationY;
        previousRotationZ = rotationZ;
        if(parent instanceof Body3d body3d)
        {
            Vector3f newPos = MathUtils.rotateVector(this.x, this.y, this.z, body3d.x, body3d.y, body3d.z, x, y, z);
            setPosition(newPos.x, newPos.y, newPos.z);
        }
        else if(parent instanceof Body2d body2d)
        {
            Vector3f newPos = MathUtils.rotateVector(this.x, this.y, this.z, body2d.x, body2d.y, 0, x, y, z);
            setPosition(newPos.x, newPos.y, newPos.z);
        }
        rotationX += x;
        rotationY += y;
        rotationZ += z;
        rotate(children, x, y, z);
    }

    public void setRotation(float x, float y, float z)
    {
        rotate(x - this.rotationX, y - this.rotationY, z - this.rotationZ);
    }

    private void move(Map<String, Body> bodies, float x, float y, float z)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(entry.getValue() instanceof Body3d body3d)
            {
                body3d.move(x, y, z);
            }
            else if(entry.getValue() instanceof Body2d body2d)
            {
                body2d.move(x, y);
            }
            else
            {
                move(entry.getValue().children, x, y, z);
            }
        }
    }

    private void rotate(Map<String, Body> bodies, float x, float y, float z)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(entry.getValue() instanceof Body3d body3d)
            {
                body3d.rotate(x, y, z, body3d.parent);
            }
            else if(entry.getValue() instanceof Body2d body2d)
            {
                body2d.rotate(x, body2d.parent);
            }
            else
            {
                rotate(entry.getValue().children, x, y, z);
            }
        }
    }

    private void scale(Map<String, Body> bodies, float x, float y, float z)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(entry.getValue() instanceof Body3d body3d)
            {
                body3d.scale(x, y, z, body3d.parent);
            }
            else if(entry.getValue() instanceof Body2d body2d)
            {
                body2d.scale(x, y, body2d.parent);
            }
            else
            {
                scale(entry.getValue().children, x, y, z);
            }
        }
    }
}
