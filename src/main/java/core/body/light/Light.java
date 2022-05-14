package core.body.light;

import core.body.Body3d;
import core.body.ColorValue;

public class Light extends Body3d
{
    public ColorValue color = new ColorValue(0xFFFFFF);
    public float intensity = 1;

    public Light(float x, float y, float z)
    {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Light() {
    }
}
