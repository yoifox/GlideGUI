package core;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.openal.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import core.body.ui.Component;

public class Window
{
    private static final float FOV = (float) Math.toRadians(60);
    private static float zNear = 1f;
    private static float zFar = 10000f;

    protected boolean shouldClose, isResized, fullScreen;
    protected int width, height, x, y;
    protected long windowId;
    protected long audioContext, audioDevice;
    protected GLFWVidMode vidMode;
    protected Display display;
    protected Context context;
    public Matrix4f projectionMatrix = new Matrix4f();

    public Window(boolean fullScreen, int width, int height, Context context)
    {
        this.fullScreen = fullScreen;
        this.width = width;
        this.height = height;
        this.context = context;
    }

    private boolean transparent;

    public Window(boolean fullScreen, int width, int height, boolean transparent, Context context)
    {
        this.fullScreen = fullScreen;
        this.width = width;
        this.height = height;
        this.context = context;
        this.transparent = transparent;
    }

    protected void init()
    {
        GLFWErrorCallback err = GLFWErrorCallback.createPrint(System.err).set();
        GLFW.glfwSetErrorCallback(err);
        GLFW.glfwInit();
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparent ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8);

        vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        assert vidMode != null;
        display = new Display(vidMode.width(), vidMode.height(), vidMode.refreshRate(), GLFW.glfwGetPrimaryMonitor());

        if(fullScreen)
            windowId = GLFW.glfwCreateWindow(width, height, "", GLFW.glfwGetPrimaryMonitor(), MemoryUtil.NULL);
        else
            windowId = GLFW.glfwCreateWindow(width, height, "", MemoryUtil.NULL, MemoryUtil.NULL);
        if(windowId == MemoryUtil.NULL)
            throw new RuntimeException();

        GLFW.glfwSetFramebufferSizeCallback(windowId, (windowId, width, height) ->
        {
            this.width = width;
            this.height = height;
            float aspectRatio = ((float)width / (float)height);
            projectionMatrix = projectionMatrix.setPerspective(FOV, aspectRatio, zNear, zFar);
            isResized = true;
        });

        GLFW.glfwSetWindowPosCallback(windowId, new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {
                x = xpos;
                y = ypos;
            }
        });

        GLFW.glfwSetWindowPos(windowId, (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2);
        GLFW.glfwMakeContextCurrent(windowId);
        Looper.currentContext = windowId;
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(windowId);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL13.glEnable(GL13.GL_MULTISAMPLE);
        GL13.glEnable(GL13.GL_MULTISAMPLE_BIT);

        String audioDeviceStr = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = ALC10.alcOpenDevice(audioDeviceStr);
        audioContext = ALC10.alcCreateContext(audioDevice, new int[] {0});
        ALC10.alcMakeContextCurrent(audioContext);
        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
    }

    public Matrix4f getProjectionMatrix() {
        float aspectRatio = ((float)width / (float)height);
        projectionMatrix = projectionMatrix.setPerspective(FOV, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    protected void update()
    {
        setGlContext();
        ALC10.alcMakeContextCurrent(audioContext);
        GLFW.glfwSwapBuffers(windowId);
        GLFW.glfwPollEvents();
    }

    //

    public void setTitle(String title)
    {
        GLFW.glfwSetWindowTitle(windowId, title);
    }

    public static final int UNLIMITED_SIZE = -1;
    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight)
    {
        GLFW.glfwSetWindowSizeLimits(windowId, minWidth, minHeight, maxWidth, maxHeight);
    }
    public void setVerticalSync(boolean enable)
    {
        GLFW.glfwMakeContextCurrent(windowId);
        GLFW.glfwSwapInterval(enable ? 1 : 0);
    }

    public void setWidth(int width)
    {
        GLFW.glfwSetWindowSize(windowId, width, height);
        this.width = width;
    }

    public void setHeight(int height)
    {
        GLFW.glfwSetWindowSize(windowId, width, height);
        this.height = height;
    }

    public void setPosition(int x, int y)
    {
        GLFW.glfwSetWindowPos(windowId, x, y);
        this.x = x;
        this.y = y;
    }

    public void setFullScreen(boolean fullScreen)
    {
        if(fullScreen)
            GLFW.glfwSetWindowMonitor(windowId, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, vidMode.refreshRate());
        else
            GLFW.glfwSetWindowMonitor(windowId, MemoryUtil.NULL, x, y, width, height, vidMode.refreshRate());
    }

    public Display getDisplay()
    {
        return display;
    }

    public void setDisplay(long monitor)
    {
        vidMode = GLFW.glfwGetVideoMode(monitor);
        assert vidMode != null;
        display = new Display(vidMode.width(), vidMode.height(), vidMode.refreshRate(), monitor);
        GLFW.glfwSetWindowMonitor(windowId, monitor, x, y, width, height, vidMode.refreshRate());
    }

    public void setBorderless(boolean borderless)
    {
        GLFW.glfwMakeContextCurrent(windowId);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, borderless ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, borderless ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwSetWindowAttrib(windowId, GLFW.GLFW_DECORATED, borderless ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, borderless ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public void setCursorVisible(boolean visible)
    {
        GLFW.glfwSetInputMode(windowId, GLFW.GLFW_CURSOR, visible ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_HIDDEN);
    }

    public int x() {
        return x;
    }
    public int y() {
        return y;
    }

    public void setResizable(boolean resizable)
    {
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GL11.GL_TRUE : GL11.GL_FALSE);
    }

    //

    public void setGlContext()
    {
        if(Looper.currentContext != windowId)
        {
            Looper.currentContext = windowId;
            GLFW.glfwMakeContextCurrent(windowId);
        }
    }

    public void close()
    {
        shouldClose = true;
    }

    protected void cleanup()
    {
        GLFW.glfwDestroyWindow(windowId);
        ALC10.alcDestroyContext(audioContext);
        ALC10.alcCloseDevice(audioDevice);
    }

    public Context getContext() {
        return context;
    }

    public long getWindowId(){return windowId;}

    protected void setContext(Context context)
    {
        this.context = context;
        context.init(this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getzFar() {
        return zFar;
    }

    public float getzNear() {
        return zNear;
    }

    public void setZNear(float zNear) {
        Window.zNear = zNear;
        getProjectionMatrix();
    }

    public void setZFar(float zFar) {
        Window.zFar = zFar;
        getProjectionMatrix();
    }

    public Component component()
    {
        return new Component(width, height);
    }
}
