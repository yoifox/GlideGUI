package core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.windows.POINT;
import org.lwjgl.system.windows.User32;

public class Display
{
    private int width, height, refreshRate;
    private long id;

    public Display(int width, int height, int refreshRate, long id) {
        this.width = width;
        this.height = height;
        this.refreshRate = refreshRate;
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public long getId() {
        return id;
    }

    private static Display primaryDisplay;

    public static Display primaryDisplay()
    {
        return primaryDisplay;
    }

    public static void init()
    {
        GLFW.glfwInit();
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        assert vidMode != null;
        primaryDisplay = new Display(vidMode.width(), vidMode.height(), vidMode.refreshRate(), GLFW.glfwGetPrimaryMonitor());
    }

    public static double getCursorX()
    {
        POINT point = POINT.calloc();
        User32.GetCursorPos(point);
        return point.x();
    }

    public static double getCursorY()
    {
        POINT point = POINT.calloc();
        User32.GetCursorPos(point);
        return point.y();
    }

    public static void setCursorPosition(int x, int y)
    {
        User32.SetCursorPos(x, y);
    }
}
