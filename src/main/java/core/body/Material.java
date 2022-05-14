package core.body;

public class Material
{
    public Texture color;
    public Texture normal;
    public Texture metallic;
    public Texture specular;

    public ColorValue colorValue = new ColorValue(1, 1, 1, 1);
    public ColorValue metallicValue = new ColorValue(0, 0, 0, 1);
    public ColorValue specularValue = new ColorValue(0, 0, 0, 1);

    public float uvScaleX = 1;
    public float uvScaleY = 1;

    public Material(Texture color, Texture normal, Texture metallic, Texture specular)
    {
        this.color = color;
        this.normal = normal;
        this.metallic = metallic;
        this.specular = specular;
    }

    public Material(ColorValue colorValue, ColorValue metallicValue, ColorValue specularP)
    {
        this.colorValue = colorValue == null ? this.colorValue : colorValue;
        this.metallicValue = metallicValue == null ? this.metallicValue : metallicValue;
        this.specularValue = specularP == null ? this.specularValue : specularP;
    }

    public Material()
    {
    }
}
