package core.body;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entity extends Body3d
{
    public Mesh mesh;
    public Material material;
    public float[] uvs;

    public Entity(Mesh mesh) {
        this.mesh = mesh;
        material = new Material();
    }

    public Entity(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material == null ? new Material() : material;
    }

    public Entity(float x, float y, float z, float rotationX, float rotationY, float rotationZ, float scaleX, float scaleY, float scaleZ, Mesh mesh, Material material)
    {
        super(x, y, z, rotationX, rotationY, rotationZ, scaleX, scaleY, scaleZ);
        this.mesh = mesh;
        this.material = material == null ? new Material() : material;
        uvs = Arrays.copyOf(uvs, uvs.length);
    }

    @Override
    public void updatePhysics(float delta)
    {
        super.updatePhysics(delta);
    }

    public BoundingBox getBoundingBox()
    {
        BoundingBox boundingBox = mesh.boundingBox;
        BoundingBox result = new BoundingBox(0, 0, 0);
        assert boundingBox != null;

        result.width = boundingBox.width * scaleX;
        result.height = boundingBox.height * scaleY;
        result.depth = boundingBox.depth * scaleZ;

        result.setPosition(x, y + result.height / 2f, z);
        return result;
    }

    public CollisionShape3d addBoxCollision(String... masks)
    {
        CollisionShape3d collisionShape3d = new CollisionShape3d(mesh.boundingBox);
        collisionShape3d.masks.addAll(Arrays.asList(masks));
        addChild(collisionShape3d);
        return collisionShape3d;
    }
}

