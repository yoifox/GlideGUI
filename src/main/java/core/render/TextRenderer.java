package core.render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import core.Scene;
import core.Shader;
import core.Window;
import core.body.Mesh;
import core.body.Texture;
import core.body.ui.*;
import core.loader.ObjectLoader;
import core.util.Transformation;
import core.util.Util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextRenderer
{
    Window window;
    Scene scene;
    Shader shader;
    Mesh quad;
    private final Map<String, TextureInfo> textCharacterStringBitmapMap = new HashMap<>();

    public void init(Scene scene)
    {
        this.scene = scene;
        ObjectLoader objectLoader = new ObjectLoader(scene);
        this.window = scene.window;
        shader = new Shader(Util.loadResourceString(getClass(), "/shaders/ui_component.vert"),
                Util.loadResourceString(getClass(), "/shaders/ui_component.frag"));
        shader.link();
        shader.validate();
        shader.createUniform("transformationMatrix");
        shader.createUniform("componentSize");
        shader.createUniform("uvsMul");
        shader.createUniform("uvsPos");
        GuiRenderer.createColorUniform(shader);
        GuiRenderer.createBoundingUniform(shader);
        GuiRenderer.createDimensionsUniform(shader);
        GuiRenderer.createTextUniform(shader);

        float[] vertices = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = ObjectLoader.createQuad(vertices, new float[] {0, 0, 0, 1, 1, 0, 1, 1});
    }

    public void render(TextCharacter textCharacter)
    {
        if(textCharacter.initialized)
        {
            return;
        }
        else if(textCharacterStringBitmapMap.containsKey(textCharacter.toString()))
        {
            TextureInfo info = textCharacterStringBitmapMap.get(textCharacter.toString());
            textCharacter.bc = new Color(info.texture);
            textCharacter.width = info.width;
            textCharacter.height = info.height;
            textCharacter.margin[Component.MARGIN_RIGHT] = info.ax;
            textCharacter.margin[Component.MARGIN_LEFT] = info.lsb;
            textCharacter.descent = info.descent;
            textCharacter.initialized = true;
            textCharacter.onCreate();
            return;
        }
        MemoryStack stack = MemoryStack.stackPush();
        float scale = STBTruetype.stbtt_ScaleForPixelHeight(textCharacter.getFont().stbttFontinfo, textCharacter.textSize);
        IntBuffer ascent = stack.mallocInt(1);
        IntBuffer descent = stack.mallocInt(1);
        IntBuffer lineGap = stack.mallocInt(1);
        STBTruetype.stbtt_GetFontVMetrics(textCharacter.getFont().stbttFontinfo, ascent, descent, lineGap);

        int ascent_ = Math.round(ascent.get(0) * scale);
        int descent_ = Math.round(descent.get(0) * scale);

        int x = 0;

        IntBuffer ax = stack.mallocInt(1);
        IntBuffer lsb = stack.mallocInt(1);

        STBTruetype.stbtt_GetCodepointHMetrics(textCharacter.getFont().stbttFontinfo, textCharacter.text.codePointAt(0), ax, lsb);
        int ax_ = ax.get();
        int lsb_ = lsb.get();

        IntBuffer c_x1 = stack.mallocInt(1);
        IntBuffer c_y1 = stack.mallocInt(1);
        IntBuffer c_x2 = stack.mallocInt(1);
        IntBuffer c_y2 = stack.mallocInt(1);

        STBTruetype.stbtt_GetCodepointBitmapBox(textCharacter.getFont().stbttFontinfo, textCharacter.text.charAt(0), scale, scale, c_x1, c_y1, c_x2, c_y2);
        int c_x1_ = c_x1.get();
        int c_y1_ = c_y1.get();
        int c_x2_ = c_x2.get();
        int c_y2_ = c_y2.get();

        int width = c_x2_ - c_x1_;
        int height = c_y2_ - c_y1_;

        int bitmapSize = (width * height);
        ByteBuffer bitmap = MemoryUtil.memAlloc(bitmapSize);
        int bitmapPosition0 = bitmap.position();

        int y = ascent_ + c_y1_;

        textCharacter.descent = y;
        STBTruetype.stbtt_MakeCodepointBitmap(textCharacter.getFont().stbttFontinfo, bitmap, c_x2_ - c_x1_, c_y2_ - c_y1_, width, scale, scale, textCharacter.text.charAt(0));

        bitmap.position(bitmapPosition0);

        STBImageWrite.stbi_write_png("C:\\Users\\User\\Desktop\\test\\test.png", width, height, 1, bitmap, width);

        Texture texture = loadTexture(bitmap, width, height);
        //textCharacterBitmapMap.put(textCharacter, texture);
        textCharacterStringBitmapMap.put(textCharacter.toString(), new TextureInfo(texture, width, height, (int) (ax_ * scale - width), (int) (lsb_ * scale), y));

        textCharacter.width = width;
        textCharacter.height = height;
        textCharacter.bc = new Color(texture);
        textCharacter.margin[Component.MARGIN_RIGHT] = ax_ * scale - width;
        textCharacter.margin[Component.MARGIN_LEFT] = lsb_ * scale;

        textCharacter.initialized = true;
        textCharacter.onCreate();
        STBTruetype.stbtt_FreeBitmap(bitmap);

        stack.pop();
    }

    public void draw(TextCharacter textCharacter)
    {
        if(scene.stopped) return;
        if(!textCharacter.visible) return;

        shader.bind();
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(textCharacter, window));
        shader.setUniform("uvsMul", new Vector2f(1, 1));
        shader.setUniform("uvsPos", new Vector2f());
        shader.setUniform("componentSize", new Vector2f(textCharacter.isWidthPercentage ? textCharacter.width * window.getWidth() : textCharacter.width,
                textCharacter.isHeightPercentage ? textCharacter.height * window.getHeight() : textCharacter.height));

        GuiRenderer.setColorUniform(shader, textCharacter);
        GuiRenderer.setBoundingUniform(shader, textCharacter);
        GuiRenderer.setDimensionsUniform(shader, textCharacter);
        GuiRenderer.setTextUniform(shader, textCharacter);

        GL30.glBindVertexArray(quad.getVao());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        if(textCharacter.bc.texture != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textCharacter.bc.texture.getId());
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

        shader.unbind();

        GL13.glActiveTexture(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private static Texture loadTexture(ByteBuffer buffer, int width, int height)
    {
        int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, width, height, 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        return new Texture(id, 1, GL11.GL_RED, buffer);
    }

    public void cleanup()
    {
        for(Map.Entry<String, TextureInfo> entry : textCharacterStringBitmapMap.entrySet())
        {
            GL30.glDeleteTextures(entry.getValue().texture.getId());
        }
        textCharacterStringBitmapMap.clear();

        GL30.glDeleteVertexArrays(quad.getVao());
        if(quad.getVbos() != null)
            for(int vbo : quad.getVbos())
                GL30.glDeleteBuffers(vbo);

        shader.cleanup();
    }

    public void cleanup(Font font)
    {
        List<String> toRemove = new ArrayList<>();
        for(Map.Entry<String, TextureInfo> entry : textCharacterStringBitmapMap.entrySet())
        {
            if(entry.getKey().startsWith(font.getName()))
            {
                GL30.glDeleteTextures(entry.getValue().texture.getId());
                toRemove.add(entry.getKey());
            }
        }
        for(String str : toRemove)
        {
            textCharacterStringBitmapMap.remove(str);
        }
        shader.cleanup();
    }

    private static class TextureInfo
    {
        public Texture texture;
        public int width;
        public int height;
        public int ax;
        public int lsb;
        public int descent;

        public TextureInfo(Texture texture, int width, int height, int ax, int lsb, int descent) {
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.ax = ax;
            this.lsb = lsb;
            this.descent = descent;
        }
    }
}
