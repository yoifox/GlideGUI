package core.body;

import org.joml.Vector3f;

public class PhysicsBody3d extends Body3d
{
    public float speedX = 0, speedY = 0, speedZ = 0;
    public float rotationSpeedX = 0, rotationSpeedY = 0, rotationSpeedZ = 0;
    private Vector3f lerpRotation, lerpPosition;
    private float lerpRotationTime = 0, lerpPositionTime = 0;

    @Override
    public void updatePhysics(float delta)
    {
        super.updatePhysics(delta);
        move(delta * speedX, delta * speedY, delta * speedZ);
        rotate(delta * rotationSpeedX, delta * rotationSpeedY, delta * rotationSpeedZ);

        if(lerpRotation != null)
        {
            if(rotationX >= lerpRotation.x && rotationY >= lerpRotation.y && rotationZ >= lerpRotation.z)
            {
                setRotation(lerpRotation.x, lerpRotation.y, lerpRotation.z);
                lerpRotation = null;
            }
            else
            {
                Vector3f rot = new Vector3f(rotationX, rotationY, rotationZ).lerp(lerpRotation, lerpRotationTime * delta);
                setRotation(rot.x, rot.y, rot.z);
            }
        }

        if(lerpPosition != null)
        {
            if(x >= lerpPosition.x && y >= lerpPosition.y && z >= lerpPosition.z)
            {
                setPosition(lerpPosition.x, lerpPosition.y, lerpPosition.z);
                lerpPosition = null;
            }
            else
            {
                Vector3f pos = new Vector3f(x, y, z).lerp(lerpPosition, lerpPositionTime * delta);
                setPosition(pos.x, pos.y, pos.z);
            }
        }
    }

    public void lerpRotation(float x, float y, float z, float speed)
    {
        lerpRotation = new Vector3f(x, y, z);
        lerpRotationTime = speed;
    }

    public void lerpPosition(float x, float y, float z, float speed)
    {
        lerpPosition = new Vector3f(x, y, z);
        lerpPositionTime = speed;
    }
}
