package core.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import core.Shader;
import core.body.Mesh;
import core.body.Texture;
import core.loader.ObjectLoader;

public class FXAARenderer
{
    Shader shader;
    Mesh quad;
    public void init()
    {
        //shader = new Shader(Utils.loadResourceString(getClass(), "/shaders/fxaa.vert"),
        //        Utils.loadResourceString(getClass(), "/shaders/fxaa.frag"));

        float[] vertices = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = ObjectLoader.createQuad(vertices, new float[] {0, 1, 0, 0, 1, 1, 1, 0});

        //shader.link();
    }

    public void render(Texture texture)
    {
        shader.bind();

        GL30.glBindVertexArray(quad.getVao());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.unbind();
    }

    public void cleanup()
    {
        GL30.glDeleteVertexArrays(quad.getVao());
        if(quad.getVbos() != null)
            for(int vbo : quad.getVbos())
                GL30.glDeleteBuffers(vbo);

        shader.cleanup();
    }
}
