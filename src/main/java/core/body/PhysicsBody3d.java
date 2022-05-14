package core.body;

public class PhysicsBody3d extends Body3d
{
    public float speedX = 0, speedY = 0, speedZ = 0;

    @Override
    public void updatePhysics(float delta)
    {
        super.updatePhysics(delta);
        move(delta * speedX, delta * speedY, delta * speedZ);
    }
}
