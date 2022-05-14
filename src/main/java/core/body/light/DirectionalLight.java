package core.body.light;

import core.body.ColorValue;

public class DirectionalLight extends Light
{
    public DirectionalLight(float rotationX, float rotationY, float rotationZ, float intensity, ColorValue color)
    {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;

        this.intensity = intensity;
        this.color = color;
    }

    public DirectionalLight()
    {
        intensity = 0;
    }
}
