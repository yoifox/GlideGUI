package core.render;

import core.Looper;
import core.body.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import core.Scene;
import core.Shader;
import core.Window;
import core.body.light.DirectionalLight;
import core.body.light.PointLight;
import core.body.light.SpotLight;
import core.util.Transformation;
import core.util.Util;

import java.util.List;

public class EntityRenderer
{
    public Shader shader;
    public Window window;
    public Scene scene;

    public void init(Scene scene)
    {
        shader = new Shader(Util.loadResourceString(getClass(), "/shaders/entity.vert"),
                Util.loadResourceString(getClass(), "/shaders/entity.frag"));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("transformationMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("worldColor");

        shader.createUniform("matColor");
        shader.createUniform("matMetallic");
        shader.createUniform("matSpecular");
        shader.createUniform("matTransparency");
        shader.createUniform("matNormal");

        createDistanceFogUniform(shader);
        createMaterialUniform(shader);
        createPointLightsUniform(shader, 32);
        createDirectionalLightUniform(shader);

        this.scene = scene;
        this.window = scene.window;
    }

    public void render(Entity entity, Camera camera, Vector4f worldColor, List<PointLight> pointLights, List<SpotLight> spotLights, DirectionalLight directionalLight, DistanceFog distanceFog)
    {
        if(scene.stopped) return;
        if(!entity.visible) return;

        shader.bind();
        shader.setUniform("projectionMatrix", window.getProjectionMatrix());
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("viewMatrix", Transformation.createViewMatrix(camera));
        shader.setUniform("worldColor", worldColor);

        shader.setUniform("matColor", 0);
        shader.setUniform("matMetallic", 1);
        shader.setUniform("matSpecular", 2);
        shader.setUniform("matTransparency", 3);
        shader.setUniform("matNormal", 4);

        setMaterialUniform(shader, entity.material);
        setPointLightsUniform(shader, pointLights);
        setDistanceFogUniform(shader, distanceFog);
        setDirectionalLightUniform(shader, directionalLight);

        if(entity.mesh.twoSided)
            GL11.glDisable(GL11.GL_CULL_FACE);
        else
            GL11.glEnable(GL11.GL_CULL_FACE);

        bind(entity);
        if(entity.material.transparencyValue < 1 || entity.material.transparency != null)
        {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        unbind();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);

        shader.unbind();
    }

    float animTime = 0;

    private void bind(Entity entity)
    {
        GL30.glBindVertexArray(entity.mesh.getVao());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        if(entity.material.color != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            if(entity.material.color instanceof AnimatedTexture animatedTexture)
            {
                if(animatedTexture.position > animatedTexture.textures.length)
                    animatedTexture.position = 0;
                if(animatedTexture.position == 0)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.color.getId());
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
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.color.getId());
        }
        if(entity.material.metallic != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            if(entity.material.metallic instanceof AnimatedTexture animatedTexture)
            {
                if(animatedTexture.position > animatedTexture.textures.length)
                    animatedTexture.position = 0;
                if(animatedTexture.position == 0)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.color.getId());
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
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.metallic.getId());
        }
        if(entity.material.specular != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            if(entity.material.specular instanceof AnimatedTexture animatedTexture)
            {
                if(animatedTexture.position > animatedTexture.textures.length)
                    animatedTexture.position = 0;
                if(animatedTexture.position == 0)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.color.getId());
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
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.specular.getId());
        }
        if(entity.material.transparency != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            if(entity.material.transparency instanceof AnimatedTexture animatedTexture)
            {
                if(animatedTexture.position > animatedTexture.textures.length)
                    animatedTexture.position = 0;
                if(animatedTexture.position == 0)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.color.getId());
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
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.transparency.getId());
        }
        if(entity.material.normal != null)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
            if(entity.material.normal instanceof AnimatedTexture animatedTexture)
            {
                if(animatedTexture.position > animatedTexture.textures.length)
                    animatedTexture.position = 0;
                if(animatedTexture.position == 0)
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.color.getId());
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
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.material.normal.getId());
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

    public static void createMaterialUniform(Shader shader)
    {
        shader.createUniform("material.colorValue");
        shader.createUniform("material.metallicValue");
        shader.createUniform("material.specularValue");
        shader.createUniform("material.hasColor");
        shader.createUniform("material.hasMetallic");
        shader.createUniform("material.hasNormal");
        shader.createUniform("material.hasSpecular");
        shader.createUniform("material.hasTransparency");
        shader.createUniform("material.uvScale");
        shader.createUniform("material.transparency");
    }

    public static void createDistanceFogUniform(Shader shader)
    {
        shader.createUniform("distanceFog.density");
        shader.createUniform("distanceFog.color");
    }

    public static void setDistanceFogUniform(Shader shader, DistanceFog distanceFog)
    {
        shader.setUniform("distanceFog.density", distanceFog.density);
        shader.setUniform("distanceFog.color", distanceFog.color.vec4());
    }

    public static void setMaterialUniform(Shader shader, Material material)
    {
        shader.setUniform("material.colorValue", material.colorValue.vec4());
        shader.setUniform("material.metallicValue", material.metallicValue.vec4());
        shader.setUniform("material.specularValue", material.specularValue.vec4());
        shader.setUniform("material.hasColor", material.color == null ? 0 : 1);
        shader.setUniform("material.hasMetallic", material.metallic == null ? 0 : 1);
        shader.setUniform("material.hasNormal", material.normal == null ? 0 : 1);
        shader.setUniform("material.hasSpecular", material.specular == null ? 0 : 1);
        shader.setUniform("material.uvScale", new Vector2f(material.uvScaleX, material.uvScaleY));
        shader.setUniform("material.transparency", material.transparencyValue);
        shader.setUniform("material.hasTransparency", material.transparency == null ? 0 : 1);
    }

    public static void createPointLightsUniform(Shader shader, int size)
    {
        for(int i = 0; i < size; i++)
        {
            shader.createUniform("pointLights[" + i + "].color");
            shader.createUniform("pointLights[" + i + "].radius");
            shader.createUniform("pointLights[" + i + "].intensity");
            shader.createUniform("pointLights[" + i + "].position");
            shader.createUniform("pointLights[" + i + "].isLast");
        }
    }

    public static void setPointLightsUniform(Shader shader, List<PointLight> pointLights)
    {
        for(int i = 0; i < pointLights.size() && i < 255; i++)
        {
            shader.setUniform("pointLights[" + i + "].color", pointLights.get(i).color.vec4());
            shader.setUniform("pointLights[" + i + "].radius", pointLights.get(i).radius);
            shader.setUniform("pointLights[" + i + "].intensity", pointLights.get(i).intensity);
            shader.setUniform("pointLights[" + i + "].position", new Vector3f(pointLights.get(i).x, pointLights.get(i).y, pointLights.get(i).z));
            shader.setUniform("pointLights[" + i + "].isLast", i == pointLights.size() - 1 ? 1 : 0);
        }
    }

    public static void createDirectionalLightUniform(Shader shader)
    {
        shader.createUniform("directionalLight.color");
        shader.createUniform("directionalLight.intensity");
        shader.createUniform("directionalLight.rotation");
    }

    public static void setDirectionalLightUniform(Shader shader, DirectionalLight directionalLight)
    {
        shader.setUniform("directionalLight.color", directionalLight.color.vec4());
        shader.setUniform("directionalLight.intensity", directionalLight.intensity);
        shader.setUniform("directionalLight.rotation", new Vector3f(directionalLight.rotationX, directionalLight.rotationY, directionalLight.rotationZ));
    }
}
