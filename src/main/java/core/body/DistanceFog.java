package core.body;

public class DistanceFog
{
    public float density = 0.01f;
    public ColorValue color;

    public DistanceFog(float density, ColorValue color)
    {
        this.density = density;
        this.color = color;
    }
}
