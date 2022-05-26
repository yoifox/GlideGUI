package core.render;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import core.Scene;
import core.Shader;
import core.Window;
import core.body.*;
import core.body.light.DirectionalLight;
import core.body.light.PointLight;
import core.body.light.SpotLight;
import core.util.Transformation;
import core.util.Util;

import java.util.List;

public class TerrainRenderer
{
    public Shader shader;
    public Window window;
    public Scene scene;


    public void init(Scene scene)
    {
        shader = new Shader(Util.loadResourceString(getClass(), "/shaders/terrain.vert"),
                Util.loadResourceString(getClass(), "/shaders/terrain.frag"));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("transformationMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("worldColor");
        shader.createUniform("terrainSize");

        shader.createUniform("height");
        shader.createUniform("matColor");
        shader.createUniform("matColorGrass");
        shader.createUniform("matMetallic");
        shader.createUniform("matSpecular");

        EntityRenderer.createDistanceFogUniform(shader);
        EntityRenderer.createMaterialUniform(shader);
        EntityRenderer.createPointLightsUniform(shader, 32);
        EntityRenderer.createDirectionalLightUniform(shader);

        this.scene = scene;
        this.window = scene.window;
    }

    public void render(Terrain terrain, Camera camera, Vector4f worldColor, List<PointLight> pointLights, List<SpotLight> spotLights, DirectionalLight directionalLight, DistanceFog distanceFog)
    {
        if(scene.stopped) return;

        Entity entity = new Entity(terrain.mesh, new Material(terrain.texture, null, null, null, null));

        if(!entity.visible) return;

        shader.bind();
        shader.setUniform("projectionMatrix", window.projectionMatrix);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(terrain));
        shader.setUniform("viewMatrix", Transformation.createViewMatrix(camera));
        shader.setUniform("worldColor", worldColor);
        shader.setUniform("terrainSize", new Vector2f(terrain.getSize()));

        shader.setUniform("height", 0);
        shader.setUniform("matColor", 1);
        shader.setUniform("matColorGrass", 2);
        shader.setUniform("matMetallic", 3);
        shader.setUniform("matSpecular", 4);

        EntityRenderer.setMaterialUniform(shader, entity.material);
        EntityRenderer.setPointLightsUniform(shader, pointLights);
        EntityRenderer.setDistanceFogUniform(shader, distanceFog);
        EntityRenderer.setDirectionalLightUniform(shader, directionalLight);

        if(entity.mesh.twoSided)
            GL11.glDisable(GL11.GL_CULL_FACE);
        else
            GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        bind(terrain, entity.material);
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        unbind();

        GL11.glEnable(GL11.GL_CULL_FACE);

        shader.unbind();
    }

    private void bind(Terrain terrain, Material material)
    {
        GL30.glBindVertexArray(terrain.mesh.getVao());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);


        if(terrain.height != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.height.getId());
        }
        if(material.color != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.color.getId());
        }
        if(terrain.grass != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.grass.getId());
        }
        if(material.metallic != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.metallic.getId());
        }
        if(material.specular != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.specular.getId());
        }
    }

    private void unbind()
    {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
    }

    public void cleanup()
    {
        shader.cleanup();
    }
}
