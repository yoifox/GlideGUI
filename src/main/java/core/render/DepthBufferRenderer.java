package core.render;

import core.Scene;
import core.Shader;
import core.body.Camera;
import core.body.Entity;
import core.util.Transformation;
import core.util.Util;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class DepthBufferRenderer
{
    protected Shader shader;
    public void init()
    {
        shader = new Shader(Util.loadResourceString(getClass(), "/shaders/depth_buffer.vert"),
                Util.loadResourceString(getClass(), "/shaders/depth_buffer.frag"));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("transformationMatrix");
        shader.createUniform("viewMatrix");

        shader.validate();
    }

    public void render(Entity entity, Camera camera, Scene scene)
    {
        shader.bind();
        shader.setUniform("projectionMatrix", scene.window.getProjectionMatrix());
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("viewMatrix", Transformation.createViewMatrix(camera));

        GL30.glBindVertexArray(entity.mesh.getVao());
        GL20.glEnableVertexAttribArray(0);

        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);

        shader.unbind();
    }

    public void cleanup()
    {
        shader.cleanup();
    }
}
