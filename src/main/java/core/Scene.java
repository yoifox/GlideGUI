package core;

import core.tools.RayCast;
import org.joml.Vector3f;
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

import java.nio.ByteBuffer;
import java.util.*;

public abstract class Scene implements Context
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
    protected SkyboxRenderer skyboxRenderer;
    protected DepthBufferRenderer depthBufferRenderer;
    public boolean stopped = false;
    public Texture grad, boxShadow;
    public RigidBody3d ray;
    public RayCast rayCast;
    public Mesh cubeMesh;
    public CubeMap skyBox;

    private final List<Runnable> treeModification = new ArrayList<>();
    private Runnable changeScene;

    int fbo;
    Texture frame;
    int shadowFbo;
    Texture shadowDepth;

    @Override
    public final synchronized void render(float delta)
    {
        if(stopped) return;
        if(Float.isInfinite(delta)) delta = 0;
        mouseInput.update();
        update(delta);

        while(!treeModification.isEmpty())
        {
            treeModification.get(0).run();
            treeModification.remove(0);
        }

        if(skyBox != null)
            skyboxRenderer.render(camera, skyBox);
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(stopped) return;
            updateTree(entry.getValue(), delta);
        }

        renderGui();

        GL30.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        //renderShadows(delta);

        previousPointLights = new ArrayList<>(pointLights);
        previousSpotLights = new ArrayList<>(spotLights);
        pointLights.clear();
        spotLights.clear();

        ray.x = camera.x;
        ray.y = camera.y;
        ray.z = camera.z;
        ray.maxDistance = 250;

        keyInput.update(delta);

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

    private Texture renderShadows(float delta)
    {
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            List<Class<?>> types = new ArrayList<>();
            types.add(Entity.class);
            updateTree(entry.getValue(), delta, types, shadowFbo, null, shadowDepth, false);
        }
        return shadowDepth;
    }

    private boolean renderGradComp = false;
    //pass types = null to render all
    private void updateTree(Body body, float delta, List<Class<?>> types, int fbo, Texture color, Texture depth, boolean update)
    {
        if(!body.visible) return;

        boolean render = false;
        if(types != null)
        {
            for(Class<?> cls : types)
            {
                if(body.getClass().isInstance(cls)) {
                    render = true;
                    break;
                }
            }
        }
        else render = true;

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        if(depth != null)
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depth.getId(), 0);
        else
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, 0, 0);
        if(color != null)
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, color.getId(), 0);
        else
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, 0, 0);

        if(!body.isCreated() && update)
        {
            body.scene = this;
            body.onCreate();
        }
        if(!(body instanceof Text))
        {
            if(update)
                body.update(delta);
        }
        if(body instanceof Component component)
        {
            if(component.gradComp != null)
            {
                renderGradComp = true;
                updateTree(component.gradComp, delta, types, fbo, color, depth, update);
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
                Entity bbCube = new Entity(cubeMesh, new Material(new ColorValue(1, 0, 0, 1), 0, null, 0.2f));
                bbCube.setPosition(entity.x, entity.y, entity.z);
                BoundingBox boundingBox = entity.getBoundingBox();
                bbCube.setScale(boundingBox.width / 2f + 0.05f, boundingBox.height / 2f + 0.05f, boundingBox.depth / 2f + 0.05f);
                bbCube.y -= 0.05f;
                if(render)
                    entityRenderer.render(bbCube, camera, worldColor, pointLights, spotLights, directionalLight, distanceFog);
            }
        }
        else if(body instanceof Terrain terrain)
        {
            if(render)
                terrainRenderer.render(terrain, camera, worldColor, previousPointLights, previousSpotLights, directionalLight, distanceFog);
        }
        else if(body instanceof HorizontalList horizontalList)
        {
            //guiRenderer.render(horizontalList);
            if(render)
                toRender.add(horizontalList);
            for(Component component : horizontalList.components)
            {
                updateTree(component, delta, types, fbo, color, depth, update);
            }
        }
        else if(body instanceof VerticalList verticalList)
        {
            //guiRenderer.render(verticalList);
            if(render)
                toRender.add(verticalList);
            for(Component component : verticalList.components)
            {
                updateTree(component, delta, types, fbo, color, depth, update);
            }
        }
        else if(body instanceof Text text)
        {
            //guiRenderer.render(text);
            if(render)
                toRender.add(text);
            for(Map.Entry<String, Body> entry1 : text.children.entrySet())
            {
                if(entry1.getValue() instanceof TextCharacter textCharacter)
                {
                    if(render)
                        textRenderer.render(textCharacter);
                }
            }
            if(update)
                text.update(delta);
            for(Map.Entry<String, Body> entry1 : text.children.entrySet())
            {
                if(entry1.getValue() instanceof TextCharacter textCharacter)
                {
                    //textRenderer.draw(textCharacter);
                    if(render)
                        toRender.add(textCharacter);
                }
            }
        }
        else if(body instanceof TextCharacter textCharacter) {}
        else if(body instanceof Component component)
        {
            //guiRenderer.render(component);
            if(render)
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
                updateTree(entry.getValue(), delta, types, fbo, color, depth, update);
            }
        }
    }

    private void updateTree(Body body, float delta)
    {
        updateTree(body, delta, null, 0, null, null, true);
    }

    public int numOfPhysicsSubThreads = 1;
    private final Set<Thread> physicsSubThreads = new HashSet<>();
    private void updateTreePhysics(Body body, float delta)
    {
        Runnable r = new Runnable() {
            @Override
            public void run() {
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
        };
        if(body instanceof RigidBody3d && physicsSubThreads.size() < numOfPhysicsSubThreads)
        {
            Thread thread = new Thread(r);
            thread.start();
            physicsSubThreads.add(thread);
        }
        else
            r.run();
    }

    public float rayStep = -1;
    public synchronized void updatePhysics(float delta)
    {
        if(Float.isInfinite(delta)) delta = 0;
        for(Map.Entry<String, Body> entry : bodies.entrySet())
        {
            if(stopped) return;
            updateTreePhysics(entry.getValue(), delta);
        }
        previousCollisionShape3ds = new ArrayList<>(collisionShape3ds);
        collisionShape3ds.clear();
        Vector3f direction = rayCast.getRay(this, (float) mouseInput.getX(), (float) mouseInput.getY());
        //float s = ray.maxDistance / (Looper.getDelta() / delta);
        //ray.move(direction.x * s, direction.y * s, direction.z * s);
        if(rayStep == -1)
            ray.move(direction.x / ray.maxDistance, direction.y / ray.maxDistance, direction.z / ray.maxDistance);
        else
            ray.move(direction.x * rayStep, direction.y * rayStep, direction.z * rayStep);

        for(Thread thread : physicsSubThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        physicsSubThreads.clear();
    }

    public void update(float delta) {}
    public void onCreate() {}
    public void onDestroy() {}

    @Override
    public final void init(Window window)
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
        skyboxRenderer = new SkyboxRenderer();
        depthBufferRenderer = new DepthBufferRenderer();
        entityRenderer.init(this);
        guiRenderer.init(this);
        textRenderer.init(this);
        terrainRenderer.init(this);
        fxaaRenderer.init();
        depthBufferRenderer.init();
        fontArial = objectLoader.loadFont(getClass(), "/fonts/arial.ttf");
        fontOpenSans = objectLoader.loadFont(getClass(), "/fonts/openSans.ttf");
        fontRoboto = objectLoader.loadFont(getClass(), "/fonts/roboto.ttf");
        grad = objectLoader.loadTexture(getClass(), "/img/gradV2.png");
        boxShadow = objectLoader.loadTexture(getClass(), "/img/boxShadowV2.png");
        directionalLight = new DirectionalLight(20, 20, 20, 1, ColorValue.COLOR_WHITE);
        cubeMesh = objectLoader.loadMesh(getClass(), "/shapes/cube.fbx");
        skyboxRenderer.init(this);

        CollisionShape3d rayCollision = new CollisionShape3d(0);
        rayCollision.defaultMask = true;
        ray = new RigidBody3d(rayCollision);
        rayCast = new RayCast(camera);
        rayCollision.collisionShapeListener = new CollisionShape3d.CollisionShapeListener() {
            @Override
            public void onCollisionEnter(CollisionShape3d shape, Vector3f normal) {
                if(mouseInput.isLeftButtonJustPressed())
                    shape.press(rayCollision, normal);
            }

            @Override
            public void onCollisionLeave(CollisionShape3d shape) {

            }
        };

        addBody(ray);
        initFbo();

        onCreate();
    }

    private void initFbo()
    {
        fbo = GL30.glGenFramebuffers();
        frame = objectLoader.loadTexture(window.getWidth(), window.getHeight());

        shadowFbo = GL30.glGenFramebuffers();
        int tex = GL13.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, 1000, 1000, 0,
                GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);

        shadowDepth = new Texture(tex, 1, GL11.GL_DEPTH_COMPONENT);
    }

    @Override
    public final void cleanup()
    {
        stopped = true;
        onDestroy();
        bodies.clear();
        entityRenderer.cleanup();
        guiRenderer.cleanup();
        textRenderer.cleanup();
        //fxaaRenderer.cleanup();
        depthBufferRenderer.cleanup();
        bodies.clear();
        objectLoader.cleanup();
        GL30.glDeleteFramebuffers(fbo);
    }

    public final void changeScene(Scene scene)
    {
        changeScene = new Runnable() {
            @Override
            public void run() {
                cleanup();
                window.setContext(scene);
            }
        };
    }

    public final void changeScene(Scene scene, Object extra)
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

    public final void setExtra(Object extra)
    {
        this.extra = extra;
    }

    public Object getExtra() {
        return extra;
    }

    public final void addBody(Body body)
    {
        treeModification.add(new Runnable() {
            @Override
            public void run() {
                bodies.put(body.getId(), body);
            }
        });

    }

    public final void removeBody(Body body)
    {
        treeModification.add(new Runnable() {
            @Override
            public void run() {
                body.onDestroy();
                bodies.remove(body.getId());
            }
        });
    }

    public final void removeBody(String id)
    {
        treeModification.add(new Runnable() {
            @Override
            public void run() {
                getBody(id).onDestroy();
                bodies.remove(id);
            }
        });
    }

    public final void removeBody(int index)
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

    public final Body getBody(String id)
    {
        return bodies.get(id);
    }

    public final Body findBodyById(String id)
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

    public final Body getBody(int index)
    {
        int i = 0;
        for(Map.Entry<String, Body> entry : bodies.entrySet())
            if(i == index)
                return entry.getValue();
        return null;
    }

    public final List<CollisionShape3d> getCollisionShape3ds() {
        return previousCollisionShape3ds;
    }

    public final Map<String, Body> getBodies() {
        return bodies;
    }

    public final void setClearColor(float r, float g, float b, float a)
    {
        GL11.glClearColor(r, g, b, a);
    }

    private Vector4f worldColor = new Vector4f(0.5f, 0.5f, 0.5f, 1);
    public final void setWorldColor(float r, float g, float b, float a) { worldColor = new Vector4f(r, g, b, a); };
}
