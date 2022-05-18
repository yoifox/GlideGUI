package core.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import core.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyboard
{
    Window window;
    private Map<Integer, Boolean> keyPressedMap = new HashMap<>();
    private List<Integer> justPressed = new ArrayList<>();
    private List<Integer> justReleased = new ArrayList<>();
    private List<CharCallback> charCallbacks = new ArrayList<>();
    private List<ActionCallback> actionCallbacks = new ArrayList<>();

    public void init(Window window)
    {
        this.window = window;
        window.setGlContext();

        GLFW.glfwSetKeyCallback(window.getWindowId(), new GLFWKeyCallback()
        {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                if(action == GLFW.GLFW_PRESS)
                {
                    if(!keyPressedMap.getOrDefault(key, false))
                        justPressed.add(key);
                    keyPressedMap.put(key, true);
                }
                else if(action == GLFW.GLFW_RELEASE)
                {
                    if(keyPressedMap.getOrDefault(key, false))
                        justReleased.add(key);
                    keyPressedMap.put(key, false);
                }
            }
        });

        GLFW.glfwSetCharCallback(window.getWindowId(), new GLFWCharCallback()
        {
            @Override
            public void invoke(long window, int codepoint)
            {
                for(CharCallback callback : charCallbacks)
                {
                    callback.onCharTyped(Keyboard.this.window, (char) codepoint);
                }
            }
        });
    }

    float actionCallbackCounter = 0;
    public void update(float delta)
    {
        justReleased.clear();
        justPressed.clear();

        actionCallbackCounter += delta;
        if(actionCallbackCounter > 0.1)
        {
            actionCallbackCounter = 0;
            if(isKeyPressed(GLFW.GLFW_KEY_ENTER))
            {
                for(ActionCallback callback : actionCallbacks)
                {
                    callback.onAction(window, ACTION_ENTER);
                }
            }
            if(isKeyPressed(GLFW.GLFW_KEY_SPACE))
            {
                for(ActionCallback callback : actionCallbacks)
                {
                    callback.onAction(window, ACTION_SPACE);
                }
            }
            if(isKeyPressed(GLFW.GLFW_KEY_BACKSPACE))
            {
                for(ActionCallback callback : actionCallbacks)
                {
                    callback.onAction(window, ACTION_BACKSPACE);
                }
            }
        }
    }

    public boolean isKeyPressed(int key)
    {
        return GLFW.glfwGetKey(window.getWindowId(), key) == GLFW.GLFW_PRESS;
    }

    public boolean isKeyJustPressed(int key)
    {
        return justPressed.contains(key);
    }

    public boolean isKeyJustReleased(int key)
    {
        return justReleased.contains(key);
    }

    public void addCharCallback(CharCallback callback)
    {
        charCallbacks.add(callback);
    }

    public void addActionCallback(ActionCallback callback) {actionCallbacks.add(callback);}

    public void removeCharCallback(CharCallback callback)
    {
        charCallbacks.remove(callback);
    }

    public interface CharCallback
    {
        void onCharTyped(Window window, char key);
    }

    public interface ActionCallback
    {
        void onAction(Window window, int action);
    }

    public static int ACTION_SPACE = 0, ACTION_ENTER = 1, ACTION_BACKSPACE = 2;
}
