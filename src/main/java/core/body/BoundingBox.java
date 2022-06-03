package core.body;

import org.joml.Vector3f;

public class BoundingBox extends Body3d
{
    public float width, height, depth;
    public float width0, height0, depth0;
    public boolean visible = false;

    public BoundingBox(float x, float y, float z, float rotationX, float rotationY, float rotationZ, float scaleX, float scaleY, float scaleZ, float width, float height, float depth)
    {
        super(x, y, z, rotationX, rotationY, rotationZ, scaleX, scaleY, scaleZ);
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.width0 = width;
        this.height0 = height;
        this.depth0 = depth;
    }

    public BoundingBox(Body3d body3d, float width, float height, float depth)
    {
        super(body3d.x, body3d.y, body3d.z, body3d.rotationX, body3d.rotationY, body3d.rotationZ, body3d.scaleX, body3d.scaleY, body3d.scaleZ);
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public BoundingBox(float width, float height, float depth)
    {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Vector3f getCenter()
    {
        //return new Vector3f((x + width) / 2, (y + height) / 2, (z + depth) / 2);
        return new Vector3f(x, y, z);
    }

    public float[] getVertices()
    {
        float y = this.y - height / 2f;
        return new float[] {
                x - width / 2f, y, z - depth / 2f,
                x + width / 2f, y, z - depth / 2f,
                x - width / 2f, y, z + depth / 2f,
                x + width / 2f, y, z + depth / 2f,

                x - width / 2f, y + height, z - depth / 2f,
                x + width / 2f, y + height, z - depth / 2f,
                x - width / 2f, y + height, z + depth / 2f,
                x + width / 2f, y + height, z + depth / 2f
        };
    }
}
