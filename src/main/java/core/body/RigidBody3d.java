package core.body;

import core.utils.MathUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RigidBody3d extends PhysicsBody3d
{
    CollisionShape3d collisionShape;
    List<CollisionShape3d> collidingWith = new ArrayList<>();

    public RigidBody3d(CollisionShape3d collisionShape)
    {
        this.collisionShape = collisionShape;
    }

    @Override
    public void updatePhysics(float delta)
    {
        super.updatePhysics(delta);
        detectCollision(scene.getCollisionShape3ds());
    }

    public void detectCollision(List<CollisionShape3d> shapes)
    {
        for(CollisionShape3d c : shapes)
        {
            if(c.collisionShapeListener == null && collisionShape.collisionShapeListener == null) continue;
            if(c == collisionShape) continue;
            if(!shouldDetect(c.masks)) continue;
            Vector3f intersectingVertex;
            Vector3f normal = MathUtils.normal(new Vector3f(x, y, z), c.getBoundingBox().getCenter());
            if(collisionShape.singleVertex)
                intersectingVertex = MathUtils.moveVertexAlongNormal(new Vector3f(x, y, z), normal, collisionShape.getSingleVertexRadius());
            else
                intersectingVertex = MathUtils.moveVertexAlongNormal(new Vector3f(x, y, z), normal, 0);

            if(c.boxCollision) //collisionShape is a box
            {
                if(collisionShape.boxCollision) //rigidBody is a box
                {
                    int i = 0;
                    BoundingBox box = collisionShape.getBoundingBox();
                    boolean isColliding = false;
                    for(int j = 0; j < box.getVertices().length; j++)
                    {
                        Vector3f vertex = new Vector3f(box.getVertices()[i++], box.getVertices()[i++], box.getVertices()[i++]);
                        if(MathUtils.pointInBox(c.getBoundingBox(), vertex))
                            isColliding = true;
                    }

                    if(isColliding)
                    {
                        if(!collidingWith.contains(c))
                        {
                            if(c.collisionShapeListener != null)
                                c.collisionShapeListener.onCollisionEnter(collisionShape, normal);
                            if(collisionShape.collisionShapeListener != null)
                                collisionShape.collisionShapeListener.onCollisionEnter(c, normal);
                            collidingWith.add(c);
                        }
                    }
                    else if(collidingWith.contains(c))
                    {
                        collidingWith.remove(c);
                        if(c.collisionShapeListener != null)
                            c.collisionShapeListener.onCollisionLeave(collisionShape);
                        if(collisionShape.collisionShapeListener != null)
                            collisionShape.collisionShapeListener.onCollisionLeave(c);
                    }
                }
                else if(collisionShape.singleVertex) //RigidBody is a single vertex
                {
                    if(MathUtils.pointInBox(c.getBoundingBox(), intersectingVertex))
                    {
                        if(!collidingWith.contains(c))
                        {
                            if(c.collisionShapeListener != null)
                                c.collisionShapeListener.onCollisionEnter(collisionShape, normal);
                            if(collisionShape.collisionShapeListener != null)
                                collisionShape.collisionShapeListener.onCollisionEnter(c, normal);
                            collidingWith.add(c);
                        }
                    }
                    else if(collidingWith.contains(c))
                    {
                        collidingWith.remove(c);
                        if(c.collisionShapeListener != null)
                            c.collisionShapeListener.onCollisionLeave(collisionShape);
                        if(collisionShape.collisionShapeListener != null)
                            collisionShape.collisionShapeListener.onCollisionLeave(c);
                    }
                }
            }
        }
    }

    private boolean shouldDetect(List<String> masks)
    {
        for(String s : masks) if(this.collisionShape.masks.contains(s)) return true;
        return false;
    }
}
