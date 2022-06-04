package core.util;

import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Util
{
    public static void loadResourceDll(Class<?> cls, String name) throws IOException {
        InputStream in = cls.getResourceAsStream(name);
        byte[] buffer = new byte[1024];
        int read = -1;
        File temp = File.createTempFile(name, "");
        FileOutputStream fos = new FileOutputStream(temp);

        if(in == null) throw new RuntimeException();
        while((read = in.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        in.close();

        System.load(temp.getAbsolutePath());
    }

    public static String loadResourceString(Class<?> cls, String file)
    {
        String result;
        try(InputStream in = cls.getResourceAsStream(file)) {
            assert in != null;
            try(Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name()))
            {
                result = scanner.useDelimiter("\\A").next();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Resource not found: " + file);
        }
        return result;
    }

    public static ByteBuffer loadResourceBuffer(Class<?> cls, String file)
    {
        ByteBuffer result;
        try(InputStream in = cls.getResourceAsStream(file)) {
            assert in != null;
            result = MemoryUtil.memAlloc(in.available());
            while (in.available() > 0)
            {
                result.put((byte) in.read());
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Resource not found: " + file);
        }
        return result.flip();
    }

    public static String loadResourceString(ClassLoader classLoader, String file)
    {
        String result;
        try(InputStream in = classLoader.getResourceAsStream(file)) {
            assert in != null;
            try(Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name()))
            {
                result = scanner.useDelimiter("\\A").next();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Resource not found: " + file);
        }
        return result;
    }

    public static ByteBuffer loadResourceBuffer(ClassLoader classLoader, String file)
    {
        ByteBuffer result;
        try(InputStream in = classLoader.getResourceAsStream(file)) {
            assert in != null;
            result = MemoryUtil.memAlloc(in.available());
            while (in.available() > 0)
            {
                result.put((byte) in.read());
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Resource not found: " + file);
        }
        return result.flip();
    }

    public static ByteBuffer loadFile(String file) throws IOException
    {
        return wrap(Files.readAllBytes(Paths.get(file)));
    }

    public static ByteBuffer wrap(byte[] data)
    {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length);
        return buffer.put(data).flip();
    }

    public static FloatBuffer wrap(float[] data)
    {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        return buffer.put(data).flip();
    }

    public static IntBuffer wrap(int[] data)
    {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        return buffer.put(data).flip();
    }

    public static int[] intToBytes(int value)
    {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        return new int[] {bytes[0] & 0xFF, bytes[1] & 0xFF, bytes[2] & 0xFF, bytes[3] & 0xFF};
        //return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static int bytesToInt(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
