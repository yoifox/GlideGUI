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
    }

    public BoundingBox getBoundingBox()
    {
        BoundingBox boundingBox = mesh.boundingBox;
        BoundingBox result = new BoundingBox(0, 0, 0);
        result.x = boundingBox.x0 + x;
        result.y = boundingBox.y0 + y;
        result.z = boundingBox.z0 + z;
        result.width = boundingBox.width * scaleX;
        result.height = boundingBox.height * scaleY;
        result.depth = boundingBox.depth * scaleZ;
        return result;
    }
}

