package core.render;

import core.Looper;
import core.body.AnimatedTexture;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import core.Scene;
import core.Shader;
import core.Window;
import core.body.Body;
import core.body.ColorValue;
import core.body.Mesh;
import core.body.ui.Component;
import core.body.ui.TextCharacter;
import core.loader.ObjectLoader;
import core.util.Transformation;
import core.util.Util;

import java.lang.Math;

public class GuiRenderer
{
    private Shader shader;
    private Window window;
    private Scene scene;
    private Mesh quad;
    public void init(Scene scene)
    {
        this.scene = scene;
        this.window = scene.window;
        shader = new Shader(Util.loadResourceString(getClass(), "/shaders/ui_component.vert"),
                Util.loadResourceString(getClass(), "/shaders/ui_component.frag"));
        shader.link();
        shader.validate();
        shader.createUniform("transformationMatrix");
        shader.createUniform("componentSize");
        shader.createUniform("isGradComp");
        shader.createUniform("gradCompColor");
        shader.createUniform("uvsMul");
        shader.createUniform("uvsPos");
        createColorUniform(shader);
        createBoundingUniform(shader);
        createDimensionsUniform(shader);
        createTextUniform(shader);

        float[] vertices = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = ObjectLoader.createQuad(vertices, new float[] {0, 0, 0, 1, 1, 0, 1, 1});
    }

    float animTime = 0;
    public void render(Component component)
    {
        if(scene.stopped) return;
        if(!component.visible) return;

        float[] cUvs = component.getUvs();

        Vector2f uvsPos = new Vector2f((cUvs[Component.UV_TOP_LEFT_U]), (cUvs[Component.UV_TOP_LEFT_V]));
        float mulX = cUvs[Component.UV_TOP_RIGHT_U] - cUvs[Component.UV_TOP_LEFT_U];
        float mulY = cUvs[Component.UV_BOTTOM_LEFT_V] - cUvs[Component.UV_TOP_LEFT_V];
        Vector2f uvsMul = new Vector2f(mulX, mulY);

        shader.bind();
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(component, window));
        shader.setUniform("uvsMul", uvsMul);
        shader.setUniform("uvsPos", uvsPos);
        shader.setUniform("isGradComp", component.isGradComp ? 1 : 0);
        shader.setUniform("gradCompColor", component.gradCompColor != null ? component.gradCompColor.vec4() : ColorValue.COLOR_WHITE.vec4());
        shader.setUniform("componentSize", new Vector2f(component.isWidthPercentage ? component.width * window.getWidth() : component.width,
                component.isHeightPercentage ? component.height * window.getHeight() : component.height));

        setColorUniform(shader, component);
        setBoundingUniform(shader, component);
        setDimensionsUniform(shader, component);
        setTextUniform(shader, component);

        GL30.glBindVertexArray(quad.getVao());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        if(component.bc.texture != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            if(component.bc.texture instanceof AnimatedTexture animatedTexture)
            {
                if(animatedTexture.position > animatedTexture.textures.length)
                    animatedTexture.position = 0;
                if(animatedTexture.position == 0)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, component.bc.texture.getId());
                else
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, animatedTexture.textures[animatedTexture.position - 1].getId());
                animTime += Looper.getDelta();
                if(animTime > animatedTexture.frameTime)
                {
                    animatedTexture.position++;
                    animTime = 0;
                }
            }
            else
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, component.bc.texture.getId());
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.unbind();

        GL13.glActiveTexture(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanup()
    {
        shader.cleanup();
        GL30.glDeleteVertexArrays(quad.getVao());
        if(quad.getVbos() != null)
            for(int vbo : quad.getVbos())
                GL30.glDeleteBuffers(vbo);
    }

    public static void createDimensionsUniform(Shader shader)
    {
        shader.createUniform("dimensions.x");
        shader.createUniform("dimensions.y");
        shader.createUniform("dimensions.width");
        shader.createUniform("dimensions.height");
    }

    public static void setDimensionsUniform(Shader shader, Component component)
    {
        Vector2f pos = Transformation.getTopLeftCorner(component);
        shader.setUniform("dimensions.x", pos.x);
        shader.setUniform("dimensions.y", pos.y);
        shader.setUniform("dimensions.width", component.getWidth());
        shader.setUniform("dimensions.height", component.getHeight());
    }

    public static void createBoundingUniform(Shader shader)
    {
        shader.createUniform("bounding.x");
        shader.createUniform("bounding.y");
        shader.createUniform("bounding.width");
        shader.createUniform("bounding.height");
        shader.createUniform("bounding.visibleOutsideParentBounds");
    }

    public static void setBoundingUniform(Shader shader, Component component)
    {
        if(component.parent instanceof Component parent)
        {
            Vector2f pos = Transformation.getTopLeftCorner(parent);
            shader.setUniform("bounding.x", pos.x);
            shader.setUniform("bounding.y", pos.y);
            shader.setUniform("bounding.width", parent.getWidth() * parent.scaleX);
            shader.setUniform("bounding.height", parent.getHeight() * parent.scaleY);
            shader.setUniform("bounding.visibleOutsideParentBounds", component.visibleOutsideParentBounds ? 1 : 0);
        }
        else
        {
            shader.setUniform("bounding.visibleOutsideParentBounds", 1);
        }
    }

    public static void createColorUniform(Shader shader)
    {
        shader.createUniform("color.hasTexture");
        shader.createUniform("color.color");
        shader.createUniform("color.borderColor");
        shader.createUniform("color.borderWidth");
        shader.createUniform("color.roundness");
        shader.createUniform("color.addColor");
        shader.createUniform("color.mulColor");
        shader.createUniform("color.addColorTransparent");
    }

    public static void setColorUniform(Shader shader, Component component)
    {
        if(component.bc == null) return;
        shader.setUniform("color.hasTexture", component.bc.texture == null ? 0 : 1);
        shader.setUniform("color.color", component.bc.color.vec4());
        shader.setUniform("color.borderColor", component.bc.borderColor.vec4());
        shader.setUniform("color.borderWidth", component.bc.borderWidth);
        float[] roundnessPx = component.getRoundness();
        float max = Math.max(component.getWidth(), component.getHeight());
        shader.setUniform("color.roundness", new Vector4f(roundnessPx[0] / max, roundnessPx[1] / max, roundnessPx[2] / max, roundnessPx[3] / max));
        shader.setUniform("color.addColor", component.bc.addColor.vec4());
        shader.setUniform("color.mulColor", component.bc.mulColor.vec4());
        shader.setUniform("color.addColorTransparent", component.bc.addColorTransparent.vec4());
    }

    public static void createTextUniform(Shader shader)
    {
        shader.createUniform("text.isText");
        shader.createUniform("text.textColor");
    }

    public static void setTextUniform(Shader shader, Body body)
    {
        shader.setUniform("text.isText", body instanceof TextCharacter ? 1 : 0);
        if(body instanceof TextCharacter textCharacter)
            shader.setUniform("text.textColor", textCharacter.textColor.vec4());
    }
}
