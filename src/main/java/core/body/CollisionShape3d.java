package core.body;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CollisionShape3d extends PhysicsBody3d
{
    private final BoundingBox boundingBox;
    Mesh collisionMesh;
    boolean boxCollision = true;
    boolean singleVertex = false;
    float singleVertexRadius = 0;
    public List<String> masks = new ArrayList<>();
    public CollisionShapeListener collisionShapeListener;

    public CollisionShape3d(BoundingBox boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    public CollisionShape3d(Mesh collisionMesh, boolean boxCollision)
    {
        this.collisionMesh = collisionMesh;
        this.boxCollision = boxCollision;
        boundingBox = collisionMesh.boundingBox;
    }

    public CollisionShape3d(float singleVertexRadius)
    {
        this.singleVertexRadius = singleVertexRadius;
        boxCollision = false;
        singleVertex = true;
        boundingBox = null;
    }

    public float getSingleVertexRadius() {
        return singleVertexRadius;
    }

    public BoundingBox getBoundingBox()
    {
        BoundingBox boundingBox = this.boundingBox;
        BoundingBox result = new BoundingBox(0, 0, 0);
        result.x = boundingBox.x + x;
        result.y = boundingBox.y + y + boundingBox.height / 2f;
        result.z = boundingBox.z + z;
        result.width = boundingBox.width * scaleX;
        result.height = boundingBox.height * scaleY;
        result.depth = boundingBox.depth * scaleZ;
        return result;
    }

    public void addToParent(Entity entity)
    {
        setPosition(entity.x, entity.y, entity.z);
        setScale(entity.scaleX, entity.scaleY, entity.scaleZ);
        entity.addChild(this);
    }

    public interface CollisionShapeListener
    {
        void onCollisionEnter(CollisionShape3d shape, Vector3f normal);
        void onCollisionLeave(CollisionShape3d shape);
    }
}
