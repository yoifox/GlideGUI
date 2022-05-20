package core;

import org.joml.Vector4f;
import org.lwjgl.opengl.*;
import core.body.*;
import core.body.light.DirectionalLight;
import core.body.light.PointLight;
import core.body.light.SpotLight;
import core.body.ui.*;
import core.input.Keyboard;
import core.input.Mouse;
import core.loader.ObjectLoader;
import core.render.*;

import java.util.*;

public class Scene implements Context
{
    public Font fontArial, fontOpenSans, fontRoboto;

    public Mouse mouseInput = new Mouse();
    public Keyboard keyInput = new Keyboard();
    private final Map<String, Body> bodies = new HashMap<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private List<PointLight> previousPointLights = new ArrayList<>();
    private final List<SpotLight> spotLights = new ArrayList<>();
    private List<SpotLight> previousSpotLights = new ArrayList<>();
    private final List<CollisionShape3d> collisionShape3ds = new ArrayList<>();
    private List<CollisionShape3d> previousCollisionShape3ds = new ArrayList<>();
    public DirectionalLight directionalLight = new DirectionalLight();
    public Camera camera;
    public Window window;
    public ObjectLoader objectLoader = new ObjectLoader(this);
    public DistanceFog distanceFog = new DistanceFog(0, ColorValue.COLOR_BLACK);
    protected EntityRenderer entityRenderer;
    protected GuiRenderer guiRenderer;
    protected TextRenderer textRenderer;
    protected TerrainRenderer terrainRenderer;
    protected FXAARenderer fxaaRenderer;
    public boolean stopped = false;
    public Texture grad, boxShadow;

    private final List<Runnable> treeModification = new ArrayList<>();
    private Runnable changeScene;

    int fbo;
    Texture frame;

    @Override
    public synchronized void render(float delta)
    {
        if(stopped) return;
        mouseInput.update();
        keyInput.update(delta);
        update(delta);

        while(!treeModification.isEmpty())
        {
            treeModification.get(0).run();
            treeModification.remove(0);
        }

        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(stopped) return;
            updateTree(entry.getValue(), delta);
        }

        renderGui();

        previousPointLights = new ArrayList<>(pointLights);
        previousSpotLights = new ArrayList<>(spotLights);
        pointLights.clear();
        spotLights.clear();

        if(changeScene != null)
            changeScene.run();
    }

    public void postProcessUi(float delta, Texture frame)
    {

    }

    public void postProcess(float delta, Texture frame, Texture depth)
    {

    }

    private final List<Component> toRender = new ArrayList<>();
    private void renderGui()
    {
        for(Component component : toRender) {
            if(component instanceof TextCharacter textCharacter)
                textRenderer.draw(textCharacter);
            else
                guiRenderer.render(component);
        }
        toRender.clear();
    }

    private boolean renderGradComp = false;
    private void updateTree(Body body, float delta)
    {
        if(!body.visible) return;
        if(!body.isCreated())
        {
            body.scene = this;
            body.onCreate();
        }
        if(!(body instanceof Text))
        {
            body.update(delta);
        }

        if(body instanceof Component component)
        {
            if(component.gradComp != null)
            {
                renderGradComp = true;
                updateTree(component.gradComp, delta);
            }
            if(component.isGradComp && !renderGradComp)
                return;
        }
        renderGradComp = false;

        if(body instanceof Entity entity)
        {
            entityRenderer.render(entity, camera, worldColor, previousPointLights, previousSpotLights, directionalLight, distanceFog);
            if(entity.mesh.boundingBox.visible)
            {
            }
        }
        else if(body instanceof Terrain terrain)
        {
            terrainRenderer.render(terrain, camera, worldColor, previousPointLights, previousSpotLights, directionalLight, distanceFog);
        }
        else if(body instanceof HorizontalList horizontalList)
        {
            //guiRenderer.render(horizontalList);
            toRender.add(horizontalList);
            for(Component component : horizontalList.components)
            {
                updateTree(component, delta);
            }
        }
        else if(body instanceof VerticalList verticalList)
        {
            //guiRenderer.render(verticalList);
            toRender.add(verticalList);
            for(Component component : verticalList.components)
            {
                updateTree(component, delta);
            }
        }
        else if(body instanceof Text text)
        {
            //guiRenderer.render(text);
            toRender.add(text);
            for(Map.Entry<String, Body> entry1 : text.children.entrySet())
            {
                if(entry1.getValue() instanceof TextCharacter textCharacter)
                {
                    textRenderer.render(textCharacter);
                }
            }
            text.update(delta);
            for(Map.Entry<String, Body> entry1 : text.children.entrySet())
            {
                if(entry1.getValue() instanceof TextCharacter textCharacter)
                {
                    //textRenderer.draw(textCharacter);
                    toRender.add(textCharacter);
                }
            }
        }
        else if(body instanceof TextCharacter textCharacter) {}
        else if(body instanceof Component component)
        {
            //guiRenderer.render(component);
            toRender.add(component);
        }
        else if(body instanceof PointLight pointLight)
        {
            pointLights.add(pointLight);
        }

        if(!(body instanceof Text))
        {
            for(Map.Entry<String, Body> entry : body.children.entrySet())
            {
                updateTree(entry.getValue(), delta);
            }
        }
    }

    private void updateTreePhysics(Body body, float delta)
    {
        if(body instanceof CollisionShape3d collisionShape3d)
        {
            collisionShape3ds.add(collisionShape3d);
        }
        for(Map.Entry<String, Body> entry : body.children.entrySet())
        {
            updateTreePhysics(entry.getValue(), delta);
        }
        body.updatePhysics(delta);
    }

    public synchronized void updatePhysics(float delta)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(stopped) return;
            updateTreePhysics(entry.getValue(), delta);
        }
        previousCollisionShape3ds = new ArrayList<>(collisionShape3ds);
        collisionShape3ds.clear();
    }

    public void update(float delta) {}
    public void onCreate() {}
    public void onDestroy() {}

    @Override
    public void init(Window window)
    {
        this.window = window;
        setClearColor(1, 1, 1, 1);

        camera = new Camera();

        mouseInput.init(window);
        keyInput.init(window);

        entityRenderer = new EntityRenderer();
        guiRenderer = new GuiRenderer();
        textRenderer = new TextRenderer();
        terrainRenderer = new TerrainRenderer();
        fxaaRenderer = new FXAARenderer();
        entityRenderer.init(this);
        guiRenderer.init(this);
        textRenderer.init(this);
        terrainRenderer.init(this);
        fxaaRenderer.init();
        fontArial = objectLoader.loadFont(getClass(), "/fonts/arial.ttf");
        fontOpenSans = objectLoader.loadFont(getClass(), "/fonts/openSans.ttf");
        fontRoboto = objectLoader.loadFont(getClass(), "/fonts/roboto.ttf");
        grad = objectLoader.loadTexture(getClass(), "/img/gradV2.png");
        boxShadow = objectLoader.loadTexture(getClass(), "/img/boxShadowV2.png");
        initFbo();

        onCreate();
    }

    private void initFbo()
    {
        fbo = GL30.glGenFramebuffers();
        frame = objectLoader.loadTexture(window.getWidth(), window.getHeight());
    }

    @Override
    public void cleanup()
    {
        stopped = true;
        onDestroy();
        bodies.clear();
        entityRenderer.cleanup();
        guiRenderer.cleanup();
        textRenderer.cleanup();
        //fxaaRenderer.cleanup();
        bodies.clear();
        objectLoader.cleanup();
        GL30.glDeleteFramebuffers(fbo);
    }

    public void changeScene(Scene scene)
    {
        changeScene = new Runnable() {
            @Override
            public void run() {
                cleanup();
                mouseInput.setMousePosition(window.getWidth() + 1, 0);
                window.setContext(scene);
            }
        };
    }

    public void changeScene(Scene scene, Object extra)
    {
        changeScene = new Runnable() {
            @Override
            public void run() {
                cleanup();
                scene.setExtra(extra);
                window.setContext(scene);
            }
        };
    }

    private Object extra;

    public void setExtra(Object extra)
    {
        this.extra = extra;
    }

    public Object getExtra() {
        return extra;
    }

    public void addBody(Body body)
    {
        treeModification.add(new Runnable() {
            @Override
            public void run() {
                bodies.put(body.getId(), body);
            }
        });

    }

    public void removeBody(Body body)
    {
        treeModification.add(new Runnable() {
            @Override
            public void run() {
                body.onDestroy();
                bodies.remove(body.getId());
            }
        });
    }

    public void removeBody(String id)
    {
        treeModification.add(new Runnable() {
            @Override
            public void run() {
                getBody(id).onDestroy();
                bodies.remove(id);
            }
        });
    }

    public void removeBody(int index)
    {
        treeModification.add(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                String remove = null;
                for(Map.Entry<String, Body> entry : bodies.entrySet())
                {
                    if(i == index)
                    {
                        remove = entry.getKey();
                        break;
                    }
                }
                if(remove != null)
                    removeBody(remove);
            }
        });
    }

    public Body getBody(String id)
    {
        return bodies.get(id);
    }

    public Body findBodyById(String id)
    {
        if(bodies.containsKey(id)) return bodies.get(id);
        else
        {
            for(Map.Entry<String, Body> entry : bodies.entrySet())
            {
                Body result = entry.getValue().findBodyById(id);
                if(result != null) return result;
            }
        }
        return null;
    }

    public Body getBody(int index)
    {
        int i = 0;
        for(Map.Entry<String, Body> entry : bodies.entrySet())
            if(i == index)
                return entry.getValue();
        return null;
    }

    public List<CollisionShape3d> getCollisionShape3ds() {
        return previousCollisionShape3ds;
    }

    public Map<String, Body> getBodies() {
        return bodies;
    }

    public void setClearColor(float r, float g, float b, float a)
    {
        GL11.glClearColor(r, g, b, a);
    }

    private Vector4f worldColor = new Vector4f(1, 1, 1, 1);
    public void setWorldColor(float r, float g, float b, float a) { worldColor = new Vector4f(r, g, b, a); };
}
