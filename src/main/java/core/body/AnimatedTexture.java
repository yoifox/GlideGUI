package core.body;

public class AnimatedTexture extends Texture
{
    public Texture[] textures;
    public int position = 0;
    public float frameTime;

    public AnimatedTexture(Texture[] textures, float frameTime)
    {
        super(textures[0].getId(), textures[0].getChannels(), textures[0].getType());
        this.textures = new Texture[textures.length - 1];
        System.arraycopy(textures, 1, this.textures, 0, textures.length - 1);
        this.frameTime = frameTime;
    }
}
