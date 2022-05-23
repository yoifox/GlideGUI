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
    public OnClickListener onClickListener;
    public boolean isPressed;

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
        assert boundingBox != null;

        result.width = boundingBox.width * scaleX;
        result.height = boundingBox.height * scaleY;
        result.depth = boundingBox.depth * scaleZ;

        result.setPosition(x, y + result.height / 2f, z);
        return result;
    }

    public void addToParent(Entity entity)
    {
        setPosition(entity.x, entity.y, entity.z);
        setScale(entity.scaleX, entity.scaleY, entity.scaleZ);
        entity.addChild(this);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        masks.add("default");
    }

    CollisionShape3d pressingRay;
    public void press(CollisionShape3d shape, Vector3f normal)
    {
        isPressed = true;
        if(onClickListener != null)
            onClickListener.onClick(shape, normal);
        pressingRay = shape;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(isPressed)
        {
            if(scene.mouseInput.isLeftButtonJustReleased())
            {
                if(onClickListener != null) {
                    onClickListener.onRelease(pressingRay);
                    pressingRay = null;
                    isPressed = false;
                }
            }
        }
    }

    public interface CollisionShapeListener
    {
        void onCollisionEnter(CollisionShape3d shape, Vector3f normal);
        void onCollisionLeave(CollisionShape3d shape);
    }

    public interface OnClickListener
    {
        void onClick(CollisionShape3d shape, Vector3f normal);
        void onRelease(CollisionShape3d shape);
    }
}
