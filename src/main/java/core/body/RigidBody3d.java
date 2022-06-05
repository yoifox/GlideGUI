package core.body;

import core.util.MathUtil;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RigidBody3d extends PhysicsBody3d
{
    CollisionShape3d collisionShape;
    List<CollisionShape3d> collidingWith = new ArrayList<>();
    public float maxDistance = -1;

    public RigidBody3d(CollisionShape3d collisionShape)
    {
        this.collisionShape = collisionShape;
    }

    @Override
    public void updatePhysics(float delta)
    {
        super.updatePhysics(delta);
        collisionShape.setPosition(x, y, z);
        detectCollision(scene.getCollisionShape3ds());
    }

    public final void detectCollision(List<CollisionShape3d> shapes)
    {
        for(CollisionShape3d c : shapes)
        {
            if(c.collisionShapeListener == null && collisionShape.collisionShapeListener == null) continue;
            if(c == collisionShape) continue;
            if(MathUtil.distance(collisionShape, c) > maxDistance && maxDistance != -1) continue;
            if(!shouldDetect(c)) continue;
            Vector3f intersectingVertex;
            Vector3f normal = MathUtil.normal(new Vector3f(x, y, z), c.getBoundingBox().getCenter());
            if(collisionShape.singleVertex)
                intersectingVertex = MathUtil.moveVertexAlongNormal(new Vector3f(x, y, z), normal, collisionShape.getSingleVertexRadius());
            else
                intersectingVertex = MathUtil.moveVertexAlongNormal(new Vector3f(x, y, z), normal, 0);

            if(c.boxCollision) //collisionShape is a box
            {
                if(collisionShape.boxCollision) //rigidBody is a box
                {
                    /*int i = 0;
                    BoundingBox box = collisionShape.getBoundingBox();
                    boolean isColliding = false;
                    float[] vertices = box.getVertices();
                    for(int j = 0; j < vertices.length / 3; j++)
                    {
                        Vector3f vertex = new Vector3f(vertices[i++], vertices[i++], vertices[i++]);
                        if(MathUtil.pointInBox(c.getBoundingBox(), vertex))
                            isColliding = true;
                    }*/
                    boolean isColliding = MathUtil.boxInBox(collisionShape.getBoundingBox(), c.getBoundingBox());

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
                    if(MathUtil.pointInBox(c.getBoundingBox(), intersectingVertex))
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

    /*private boolean shouldDetect(List<String> masks)
    {
        for(String s : masks) if(this.collisionShape.masks.contains(s)) return true;
        return false;
    }*/
    private boolean shouldDetect(CollisionShape3d c)
    {
        if(collisionShape.defaultMask && c.defaultMask) return true;
        for(int i = 0; i < c.masks.length; i++)
        {
            if(collisionShape.masks[i] && c.masks[i])
                return true;
        }
        return false;
    }
}
