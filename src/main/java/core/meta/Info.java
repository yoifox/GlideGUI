package core.meta;

public class Info
{
    public static String[] getSupportedMeshFormats()
    {
        return new String[] {"fbx", "obj"};
    }
    public static String[] getSupportedImageFormats()
    {
        return new String[] {"png", "jpg", "bmp"};
    }
    public static String ffmpegPath = "C:/Users/User/Desktop/test/ffmpeg/bin";
    public static int collisionMaskArraySize = 1;
}
