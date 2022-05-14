package core.body;

public class Entity extends Body3d
{
    public Mesh mesh;
    public Material material;

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
    }

    @Override
    public void updatePhysics(float delta)
    {
        super.updatePhysics(delta);
        mesh.boundingBox.x = mesh.boundingBox.x0 + x;
        mesh.boundingBox.y = mesh.boundingBox.y0 + y;
        mesh.boundingBox.z = mesh.boundingBox.z0 + z;
        mesh.boundingBox.rotationX = rotationX;
        mesh.boundingBox.rotationY = rotationY;
        mesh.boundingBox.rotationZ = rotationZ;
        mesh.boundingBox.scaleX = scaleX;
        mesh.boundingBox.scaleY = scaleY;
        mesh.boundingBox.scaleZ = scaleZ;
    }
}

