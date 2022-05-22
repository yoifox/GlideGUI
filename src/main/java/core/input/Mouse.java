package core.input;
import org.lwjgl.glfw.GLFW;
import core.Window;

public class Mouse
{
    private Window window;
    private double dx, dy;
    private double x, y, scroll;
    private boolean inWindow = true, leftButtonPressed, rightButtonPressed, leftButtonJustPressed, rightButtonJustPressed, leftButtonJustReleased, rightButtonJustReleased;

    private double previousX, previousY, previousScroll;
    private boolean previousInWindow, previousLeftButtonPressed, previousRightButtonPressed;

    public void init(Window window)
    {
        window.setGlContext();
        this.window = window;
        GLFW.glfwSetCursorPosCallback(window.getWindowId(), (w, x, y) ->
        {
            this.x = x;
            this.y = y;
        });

        GLFW.glfwSetCursorEnterCallback(window.getWindowId(), (w, entered) ->
                inWindow = entered);

        GLFW.glfwSetMouseButtonCallback(window.getWindowId(), (w, button, action, mods) ->
        {
            leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });

        GLFW.glfwSetScrollCallback(window.getWindowId(), (w, x, y) ->
                scroll = (float) y);
    }

    public void update()
    {
        dx = 0;
        dy = 0;
        if(previousX > 0 && previousY > 0 && inWindow)
        {
            double dx = x - previousX;
            double dy = y - previousY;
            boolean rotateX = dx != 0;
            boolean rotateY = dy != 0;
            if(rotateX)
                this.dy = (float)dx;
            if(rotateY)
                this.dx = (float)dy;
        }

        previousX = x;
        previousY = y;

        leftButtonJustPressed = leftButtonPressed && !previousLeftButtonPressed;
        rightButtonJustPressed = rightButtonPressed && !previousRightButtonPressed;

        leftButtonJustReleased = !leftButtonPressed && previousLeftButtonPressed;
        rightButtonJustReleased = !rightButtonPressed && previousRightButtonPressed;

        previousLeftButtonPressed = leftButtonPressed;
        previousRightButtonPressed = rightButtonPressed;

        previousScroll = scroll;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getScroll() {
        return scroll;
    }

    public boolean isInWindow() {
        return inWindow;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public boolean isLeftButtonJustPressed() {
        return leftButtonJustPressed;
    }

    public boolean isRightButtonJustPressed() {
        return rightButtonJustPressed;
    }

    public boolean isLeftButtonJustReleased() {
        return leftButtonJustReleased;
    }

    public boolean isRightButtonJustReleased() {
        return rightButtonJustReleased;
    }

    public double getPreviousX() {
        return previousX;
    }

    public double getPreviousY() {
        return previousY;
    }

    public double getPreviousScroll() {
        return previousScroll;
    }

    public boolean isPreviousInWindow() {
        return previousInWindow;
    }

    public boolean isPreviousLeftButtonPressed() {
        return previousLeftButtonPressed;
    }

    public boolean isPreviousRightButtonPressed() {
        return previousRightButtonPressed;
    }

    public void setMousePosition(double x, double y)
    {
        GLFW.glfwSetCursorPos(window.getWindowId(), x, y);
    }
}
