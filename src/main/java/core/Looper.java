package core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import core.utils.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Looper
{
    protected static final List<Window> windows = new ArrayList<>();
    private static boolean isRunning;
    private static GLFWErrorCallback errorCallback;
    protected static float frame_time = 1f / 1000f;

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

    public static void start()
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
        startPhysics();
        run();
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
                for(Window window : toClose)
                {
                    window.getContext().cleanup();
                    windows.remove(window);
                    if(windows.isEmpty())
                    {
                        cleanup();
                        return;
                    }
                }
                toClose.clear();
            }
            if(render)
            {
                for(Window window : windows)
                {
                    GLFW.glfwMakeContextCurrent(window.getWindowId());
                    update(window, 1f / fps);
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
        if(window.isResized)
        {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.getContext().render(delta);
            window.update();
            window.isResized = false;
            return;
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
        physics.interrupt();
    }

    public static void addWindow(Window window)
    {
        mainThreadTaskQueue.add(new Runnable() {
            @Override
            public void run() {
                window.init();
                window.context.init(window);
                windows.add(window);
            }
        });
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

                for(Window window : windows)
                    window.context.updatePhysics((float) delta / (float)Const.NS);

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
        //physics.start();
    }
}
