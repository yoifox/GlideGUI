package core.body;

public class Material
{
    public Texture color;
    public Texture normal;
    public Texture metallic;
    public Texture specular;
    public Texture transparency;

    public ColorValue colorValue = new ColorValue(1, 1, 1, 1);
    public float metallicValue = 0;
    public ColorValue specularValue = new ColorValue(0, 0, 0, 1);
    public float transparencyValue = 1;

    public float uvScaleX = 1;
    public float uvScaleY = 1;

    public Material(Texture color, Texture normal, Texture metallic, Texture specular, Texture transparency)
    {
        this.color = color;
        this.normal = normal;
        this.metallic = metallic;
        this.specular = specular;
        this.transparency = transparency;
    }

    public Material(Texture color)
    {
        this.color = color;
    }

    public Material(ColorValue colorValue, float metallicValue, ColorValue specularP, float transparencyP)
    {
        this.colorValue = colorValue == null ? this.colorValue : colorValue;
        this.metallicValue = metallicValue;
        this.specularValue = specularP == null ? this.specularValue : specularP;
        this.transparencyValue = transparencyP;
    }

    public Material()
    {
    }

    public Material(ColorValue colorValue)
    {
        this.colorValue = colorValue;
    }
}
