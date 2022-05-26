package core.body;

import org.lwjgl.opengl.GL11;
import core.loader.ObjectLoader;
import core.util.Util;

import java.nio.ByteBuffer;

public class Texture
{
    private final int id;
    private final int[] dimensionX = new int[1];
    private final int[] dimensionY = new int[1];
    private int channels;
    private int type;
    //public ByteBuffer buffer;

    public Texture(int id, int channels, int type)
    {
        this.id = id;
        this.channels = channels;
        GL11.glGetTexLevelParameteriv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, dimensionX);
        GL11.glGetTexLevelParameteriv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, dimensionY);
        this.type = type;
    }

    public Texture(int id, int channels, int type, ByteBuffer buffer)
    {
        this.id = id;
        this.channels = channels;
        GL11.glGetTexLevelParameteriv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, dimensionX);
        GL11.glGetTexLevelParameteriv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, dimensionY);
        this.type = type;
        //this.buffer = buffer;
    }

    public void setPixelRGBA(int x, int y, int r, int g, int b, int a)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, Util.wrap(new byte[]{(byte) r, (byte) g, (byte) b, (byte) a}));
        //buffer.put(x*y*4, (byte) r);
        //buffer.put(x*y*4 + 1, (byte) g);
        //buffer.put(x*y*4 + 2, (byte) b);
        //buffer.put(x*y*4 + 3, (byte) a);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public int getPixelRGBA(int x, int y)
    {
        byte[] bytes = new byte[4];
        int pixelOffset = channels * (y * getWidth() + x);

        //bytes[0] = buffer.get(pixelOffset);
        //bytes[1] = buffer.get(pixelOffset + 1);
        //bytes[2] = buffer.get(pixelOffset + 2);
        //bytes[3] = buffer.get(pixelOffset + 3);

        return Util.bytesToInt(bytes);
    }

    public int[] getPixelBytesRGBA(int x, int y)
    {
        byte[] bytes = new byte[4];
        int pixelOffset = channels * (y * getWidth() + x);

        //bytes[0] = buffer.get(pixelOffset);
        //bytes[1] = buffer.get(pixelOffset + 1);
        //bytes[2] = buffer.get(pixelOffset + 2);
        //bytes[3] = buffer.get(pixelOffset + 3);

        return new int[]{bytes[0] & 0xFF, bytes[1] & 0xFF, bytes[2] & 0xFF, bytes[3] & 0xFF};
    }

    public void free(ObjectLoader loader)
    {
        loader.free(this);
    }

    public int getWidth()
    {
        return dimensionX[0];
    }

    public int getHeight()
    {
        return dimensionY[0];
    }

    public int getId()
    {
        return id;
    }

    public int getChannels() {
        return channels;
    }

    public int getType() {
        return type;
    }
}
