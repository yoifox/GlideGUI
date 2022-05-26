package core.loader;

import core.err.FileNotFoundException;
import core.err.UnsupportedFileFormatException;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.libc.LibCStdlib;
import core.Scene;
import core.body.BoundingBox;
import core.body.Mesh;
import core.body.Sound;
import core.body.Texture;
import core.body.ui.Font;
import core.util.Util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;

public class ObjectLoader
{
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Texture> textures = new ArrayList<>();
    private final List<Sound> sounds = new ArrayList<>();
    private Scene context;

    public ObjectLoader(Scene context) {
        this.context = context;
    }

    public Mesh loadMesh(String src)
    {
        return ModelLoader.load(src, this);
    }

    public Mesh loadMesh(Class<?> cls, String res)
    {
        return ModelLoader.load(cls, res, this);
    }

    public Mesh loadObject(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices, float[] tangents, BoundingBox box)
    {
        int vao = createVAO();
        int[] vbos = new int[5];
        vbos[0] = storeIndicesBuffer(indices);
        vbos[1] = storeDataInAttrList(0, 3, vertices);
        vbos[2] = storeDataInAttrList(1, 2, textureCoordinates);
        vbos[3] = storeDataInAttrList(2, 3, normals);
        vbos[4] = storeDataInAttrList(3, 3, tangents);
        unbind();
        return new Mesh(vao, vbos, vertices.length * 2, indices.length, box);
    }

    public Mesh loadObject(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices, BoundingBox box)
    {
        int vao = createVAO();
        int[] vbos = new int[5];
        vbos[0] = storeIndicesBuffer(indices);
        vbos[1] = storeDataInAttrList(0, 3, vertices);
        vbos[2] = storeDataInAttrList(1, 2, textureCoordinates);
        vbos[3] = storeDataInAttrList(2, 3, normals);
        unbind();
        return new Mesh(vao, vbos, vertices.length * 2, indices.length, box);
    }

    //ObjectLoader will not delete buffers for quads. make sure to delete them in the renderer.
    public static Mesh createQuad(float[] vertices, float[] imgUvs)
    {
        float[] uvs = new float[] {0, 0, 0, 1, 1, 0, 1, 1};

        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        int vbo1 = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo1);
        buffer = MemoryUtil.memAllocFloat(uvs.length);
        buffer.put(uvs).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        int vbo2 = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo1);
        buffer = MemoryUtil.memAllocFloat(uvs.length);
        buffer.put(imgUvs).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(0);
        return new Mesh(vao, new int[]{vbo, vbo1, vbo2}, vertices.length / 2);
    }

    private int storeIndicesBuffer(int[] indices)
    {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Util.wrap(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        return vbo;
    }

    private int createVAO()
    {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private int storeDataInAttrList(int attrNo, int vertexCount, float[] data)
    {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Util.wrap(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attrNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void cleanup()
    {
        for(int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for(int vbo : vbos)
            GL30.glDeleteBuffers(vbo);
        for(Texture texture : textures)
        {
            GL30.glDeleteTextures(texture.getId());
        }
        for(Sound sound : sounds)
        {
            AL10.alDeleteBuffers(sound.getBufferId());
            AL10.alDeleteSources(sound.getSourceId());
        }

        vaos.clear();
        vbos.clear();
        textures.clear();
        fonts.clear();
        sounds.clear();
    }

    public void free(Texture texture)
    {
        textures.remove(texture);
        GL30.glDeleteTextures(texture.getId());
    }

    public void free(Mesh mesh)
    {
        vaos.remove(Integer.valueOf(mesh.getVao()));
        for(int vbo : mesh.getVbos())
        {
            vbos.remove(Integer.valueOf(vbo));
            GL30.glDeleteBuffers(vbo);
        }
        GL30.glDeleteVertexArrays(mesh.getVao());
    }

    public void free(Sound sound)
    {
        sounds.remove(sound);
        AL10.alDeleteBuffers(sound.getBufferId());
        AL10.alDeleteSources(sound.getSourceId());
    }

    private void unbind() { GL30.glBindVertexArray(0); }

    public Sound loadSound(Class<?> cls, String res, boolean loop)
    {
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer channelsBuffer = stack.mallocInt(1);
        IntBuffer sampleRateBuffer = stack.mallocInt(1);

        ByteBuffer fileBuffer = Util.loadResourceBuffer(cls, res);

        ShortBuffer decodedAudioBuffer = STBVorbis.stb_vorbis_decode_memory(fileBuffer, channelsBuffer, sampleRateBuffer);
        assert decodedAudioBuffer != null;
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        stack.pop();

        int format = -1;
        if(channels == 1) format = AL10.AL_FORMAT_MONO16;
        else if(channels == 2) format = AL10.AL_FORMAT_STEREO16;
        int bufferId = AL10.alGenBuffers();
        AL10.alBufferData(bufferId, format, decodedAudioBuffer, sampleRate);
        int sourceId = AL10.alGenSources();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? 1 : 0);
        AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
        AL10.alSourcef(sourceId, AL10.AL_GAIN, 0.3f);

        LibCStdlib.free(decodedAudioBuffer);
        return new Sound(bufferId, sourceId);
    }

    public Sound loadSound(String src, boolean loop)
    {
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer channelsBuffer = stack.mallocInt(1);
        IntBuffer sampleRateBuffer = stack.mallocInt(1);

        ShortBuffer decodedAudioBuffer = STBVorbis.stb_vorbis_decode_filename(src, channelsBuffer, sampleRateBuffer);
        if(decodedAudioBuffer == null)
            throw new UnsupportedFileFormatException(src);
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        stack.pop();

        int format = -1;
        if(channels == 1) format = AL10.AL_FORMAT_MONO16;
        else if(channels == 2) format = AL10.AL_FORMAT_STEREO16;
        int bufferId = AL10.alGenBuffers();
        AL10.alBufferData(bufferId, format, decodedAudioBuffer, sampleRate);
        int sourceId = AL10.alGenSources();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? 1 : 0);
        AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
        AL10.alSourcef(sourceId, AL10.AL_GAIN, 0.3f);

        LibCStdlib.free(decodedAudioBuffer);
        return new Sound(bufferId, sourceId);
    }

    public ByteBuffer loadTextureBuffer(String src, int[] w, int[] h, int[] c)
    {
        ByteBuffer buffer;
        try(MemoryStack stack = stackPush())
        {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = STBImage.stbi_load(src, width, height, channels, 4);
            if(buffer == null)
                throw new RuntimeException(STBImage.stbi_failure_reason());
            w[0] = width.get();
            h[0] = height.get();
            c[0] = channels.get();
        }
        return buffer;
    }

    public ByteBuffer loadTextureBuffer(Class<?> cls, String res, int[] w, int[] h, int[] c)
    {
        ByteBuffer buffer;
        try(MemoryStack stack = stackPush())
        {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = STBImage.stbi_load_from_memory(Util.loadResourceBuffer(cls, res), w, h, c, 4);
            if(buffer == null)
                throw new RuntimeException(STBImage.stbi_failure_reason());
            w[0] = width.get();
            h[0] = height.get();
            c[0] = channels.get();
        }
        return buffer;
    }

    public Texture loadTexture(String src)
    {
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(src, w, h, c, 4);
            if(buffer == null)
                throw new RuntimeException(STBImage.stbi_failure_reason());
            width = w.get();
            height = h.get();
        }
        int id = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, 4);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        Texture texture = new Texture(id, 4, GL11.GL_RGBA, buffer);
        textures.add(texture);
        return texture;
    }

    public Texture loadTexture(Class<?> cls, String res)
    {
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load_from_memory(Util.loadResourceBuffer(cls, res), w, h, c, 4);
            if(buffer == null)
                throw new RuntimeException(STBImage.stbi_failure_reason());
            width = w.get();
            height = h.get();
        }
        int id = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, 4);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        Texture texture = new Texture(id, 4, GL11.GL_RGBA, buffer);
        textures.add(texture);
        return texture;
    }

    public Texture loadTexture(ByteBuffer buffer, int width, int height)
    {
        int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, 4);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        Texture texture = new Texture(id, 4, GL11.GL_RGBA, buffer);
        textures.add(texture);
        return texture;
    }

    public Texture loadTexture(int width, int height)
    {
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, 4);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        Texture texture = new Texture(id, 4, GL11.GL_RGBA, buffer);
        textures.add(texture);
        return texture;
    }

    private final List<Font> fonts = new ArrayList<>();
    public Font loadFont(String src)
    {
        ByteBuffer ttfBuffer;
        try
        {
            ttfBuffer = Util.loadFile(src);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new FileNotFoundException(src);
        }
        STBTTFontinfo stbttFontinfo = STBTTFontinfo.create();
        if(!STBTruetype.stbtt_InitFont(stbttFontinfo, ttfBuffer))
            throw new UnsupportedFileFormatException(src);

        Font font = new Font(stbttFontinfo, src);
        fonts.add(font);
        return font;
    }

    public Font loadFont(Class<?> cls, String src)
    {
        ByteBuffer ttfBuffer;
        try
        {
            ttfBuffer = Util.loadResourceBuffer(cls, src);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new FileNotFoundException(src);
        }
        STBTTFontinfo stbttFontinfo = STBTTFontinfo.create();
        if(!STBTruetype.stbtt_InitFont(stbttFontinfo, ttfBuffer))
        {
            throw new UnsupportedFileFormatException(src);
        }
        Font font = new Font(stbttFontinfo, cls.getName() + "?/" + src);
        fonts.add(font);
        return font;
    }
}
