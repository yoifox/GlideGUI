package core.render;

import core.Scene;
import core.Shader;
import core.body.Camera;
import core.body.CubeMap;
import core.body.Mesh;
import core.util.Transformation;
import core.util.Util;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class SkyboxRenderer
{
    protected static final float SIZE = 500f;
    protected Shader shader;
    protected Scene scene;

    public void init(Scene scene)
    {
        this.scene = scene;
        shader = new Shader(Util.loadResourceString(getClass(), "/shaders/cubemap.vert"),
                Util.loadResourceString(getClass(), "/shaders/cubemap.frag"));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("size");
        shader.link();
        shader.validate();
    }

    public void render(Camera camera, CubeMap cubeMap)
    {
        shader.bind();
        shader.setUniform("projectionMatrix", scene.window.getProjectionMatrix());
        Matrix4f viewMat = Transformation.createViewMatrix(camera);
        viewMat.setTranslation(0, 0, 0);
        shader.setUniform("viewMatrix", viewMat);
        shader.setUniform("size", cubeMap.size);

        GL30.glBindVertexArray(scene.cubeMesh.getVao());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMap.id);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glDrawElements(GL11.GL_TRIANGLES, scene.cubeMesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.unbind();
    }
}
