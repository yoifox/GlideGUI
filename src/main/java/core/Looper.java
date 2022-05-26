package core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import core.util.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Looper
{
    protected static final List<Window> windows = new ArrayList<>();
    private static boolean isRunning;
    private static GLFWErrorCallback errorCallback;
    protected static float frame_time = 1f / 1000f;
    private static float delta = 0;

    private static final LinkedBlockingQueue<Runnable> mainThreadTaskQueue = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<Runnable> physicsThreadTaskQueue = new LinkedBlockingQueue<>();
    public static void addTaskToMainThread(Runnable r)
    {
        mainThreadTaskQueue.add(r);
    }
    public static void addTaskToPhysicsThread(Runnable r)
    {
        physicsThreadTaskQueue.add(r);
    }

    public static long currentContext = 0;

    public static void start(boolean withPhysics)
    {
        errorCallback = GLFWErrorCallback.createPrint(System.err).set();
        GLFW.glfwSetErrorCallback(errorCallback);

        for(Window window : windows)
        {
            window.init();
            window.getContext().init(window);
        }

        if(isRunning)
            return;
        if(withPhysics)
            startPhysics();
        run();
    }

    public static void start()
    {
        start(true);
    }

    private static void run()
    {
        isRunning = true;
        int frames = 0;
        float fps = 0;
        long frameCounter = 0;
        long time1 = System.nanoTime();
        double unprocessedTime = 0;

        List<Window> toClose = new ArrayList<>();
        while(isRunning)
        {
            boolean render = false;
            long time0 = System.nanoTime();
            long deltaTime = time0 - time1;
            time1 = time0;
            unprocessedTime += deltaTime / (double) Const.NS;
            frameCounter += deltaTime;

            while(unprocessedTime > frame_time)
            {
                for(Window window : windows)
                {
                    render = true;
                    unprocessedTime -= frame_time;
                    if(!window.shouldClose)
                        window.shouldClose = GLFW.glfwWindowShouldClose(window.windowId);
                    if(window.shouldClose)
                    {
                        toClose.add(window);
                        window.cleanup();
                    }
                    if(frameCounter >= Const.NS)
                    {
                        fps = frames;
                        frames = 0;
                        frameCounter = 0;
                    }
                }

                //closing windows
                synchronized (windows)
                {
                    boolean changeContext = false;
                    for(Window window : toClose)
                    {
                        window.getContext().cleanup();
                        boolean firstWindow = window == windows.get(0);
                        windows.remove(window);
                        if(currentContext == window.windowId)
                            changeContext = true;
                        if(windows.isEmpty() || firstWindow)
                        {
                            cleanup();
                            return;
                        }
                    }
                    toClose.clear();
                    if(changeContext)
                        windows.get(0).setGlContext();
                }
            }
            if(render)
            {
                for(Window window : windows)
                {
                    window.setGlContext();
                    delta = (1f / fps) * windows.size();
                    if(Float.isInfinite(delta))
                        delta = 0;
                    update(window, (1f / fps) * windows.size());
                    frames++;
                }
            }
            while (!mainThreadTaskQueue.isEmpty())
            {
                try
                {
                    mainThreadTaskQueue.take().run();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        cleanup();
    }

    private static void update(Window window, float delta)
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
        if(window.isResized)
        {
            window.isResized = false;
        }
        window.getContext().render(delta);
        window.update();
    }

    public static void cleanup()
    {
        isRunning = false;
        for (Window window : windows)
        {
            window.cleanup();
            window.getContext().cleanup();
            errorCallback.free();
            GLFW.glfwTerminate();
        }
    }

    public static void addWindow(Window window)
    {
        mainThreadTaskQueue.add(new Runnable() {
            @Override
            public void run() {
                window.init();
                window.context.init(window);
                synchronized (windows) {
                    windows.add(window);
                }
            }
        });
    }

    public static float getDelta() {
        return delta;
    }

    protected static Thread physics;
    protected static void startPhysics()
    {
        physics = new Thread(() ->
        {
            long delta = 0;
            while(isRunning)
            {
                long t0 = System.nanoTime();

                synchronized (windows)
                {
                    for(Window window : windows)
                        window.context.updatePhysics((float) delta / (float)Const.NS);
                }

                while(!physicsThreadTaskQueue.isEmpty())
                {
                    try
                    {
                        physicsThreadTaskQueue.take().run();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                long t1 = System.nanoTime();
                delta = (t1 - t0);
            }
        });
        physics.start();
    }
}
