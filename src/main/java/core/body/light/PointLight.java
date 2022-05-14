package core.body.light;

import core.body.ColorValue;

public class PointLight extends Light
{
    public float radius = 1;

    public PointLight(float radius, float intensity, ColorValue color)
    {
        this.radius = radius;
        this.intensity = intensity;
        this.color = color;
    }

    public PointLight(float x, float y, float z, float radius, float intensity, ColorValue color)
    {
        super(x, y, z);
        this.radius = radius;
        this.intensity = intensity;
        this.color = color;
    }
}
