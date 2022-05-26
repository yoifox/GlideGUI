package core.util;

import core.input.Keyboard;
import core.input.Mouse;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import core.Window;
import core.body.Body2d;
import core.body.Body3d;
import core.body.Camera;
import core.body.ui.Component;
import core.body.ui.Layout;
import core.body.ui.TextCharacter;
import org.lwjgl.glfw.GLFW;

public class Transformation
{
    public static Matrix4f createTransformationMatrix(Body2d body)
    {
        Matrix4f matrix = new Matrix4f();
        return matrix.identity().translate(body.x, body.y, 0).
                rotateX((float) Math.toRadians(body.rotation)).
                scaleXY(body.scaleX, body.scaleY);
    }

    public static Matrix4f createTransformationMatrix(Body3d body)
    {
        Matrix4f matrix = new Matrix4f();
        return matrix.identity().translate(body.x, body.y, body.z).
                rotateX((float) Math.toRadians(body.rotationX)).
                rotateY((float) Math.toRadians(body.rotationY)).
                rotateZ((float) Math.toRadians(body.rotationZ)).
                scale(body.scaleX, body.scaleY, body.scaleZ);
    }

    public static Vector2f getTopLeftCorner(Component component)
    {
        float width, height;
        width = component.getWidth();
        height = component.getHeight();

        Vector2f origin = new Vector2f(component.getX(), component.getY());

        if(component.origin == Component.ORIGIN_TOP_RIGHT)
        {
            origin.x -= width;
        }
        else if(component.origin == Component.ORIGIN_BOTTOM_LEFT)
        {
            origin.y -= height;
        }
        else if(component.origin == Component.ORIGIN_BOTTOM_RIGHT)
        {
            origin.x -= width;
            origin.y -= height;
        }
        else if(component.origin == Component.ORIGIN_CENTER)
        {
            origin.x -= width / 2f;
            origin.y -= height / 2f;
        }
        return origin;
    }

    public static Vector2f getContentTopLeftCornerLayout(Layout layout)
    {
        float width, height;
        width = layout.getContentWidth();
        height = layout.getContentHeight();

        Vector2f origin = new Vector2f(layout.getX(), layout.getY());

        if(layout.origin == Component.ORIGIN_TOP_RIGHT)
        {
            origin.x -= width;
        }
        else if(layout.origin == Component.ORIGIN_BOTTOM_LEFT)
        {
            origin.y -= height;
        }
        else if(layout.origin == Component.ORIGIN_BOTTOM_RIGHT)
        {
            origin.x -= width;
            origin.y -= height;
        }
        else if(layout.origin == Component.ORIGIN_CENTER)
        {
            origin.x -= width / 2f;
            origin.y -= height / 2f;
        }
        return origin;
    }

    public static Matrix4f createTransformationMatrix(Component component, Window window)
    {
        Matrix4f matrix = new Matrix4f();

        float width, height;
        width = component.getWidth();
        height = component.getHeight();

        Vector3f origin = new Vector3f(component.getX(), component.getY(), 0);

        if(component.origin == Component.ORIGIN_TOP_LEFT)
        {
            origin.x += width / 2f;
            origin.y += height / 2f;
        }
        else if(component.origin == Component.ORIGIN_TOP_RIGHT)
        {
            origin.x -= width / 2f;
            origin.y += height / 2f;
        }
        else if(component.origin == Component.ORIGIN_BOTTOM_LEFT)
        {
            origin.x += width / 2f;
            origin.y -= height / 2f;
        }
        else if(component.origin == Component.ORIGIN_BOTTOM_RIGHT)
        {
            origin.x -= width / 2f;
            origin.y -= height / 2f;
        }

        Vector3f pos = new Vector3f((origin.x * 2 - window.getWidth()) / window.getWidth(),
                ((-origin.y * 2 + window.getHeight()) / window.getHeight()), 0f);

        matrix.identity().translate(pos).rotateZ((float) Math.toRadians(component.rotation)).
            scaleXY(component.scaleX / window.getWidth(), component.scaleY / window.getHeight());

        return matrix;
    }

    public static Matrix4f createTransformationMatrix(TextCharacter textCharacter, Window window)
    {
        Matrix4f matrix = new Matrix4f();
        float width = textCharacter.getWidth();
        float height = textCharacter.getHeight();

        Vector3f origin = new Vector3f(textCharacter.getX(), textCharacter.getY(), 0);

        if(textCharacter.origin == Component.ORIGIN_TOP_LEFT)
        {
            origin.x += width / 2f;
            origin.y += height / 2f;
        }
        else if(textCharacter.origin == Component.ORIGIN_TOP_RIGHT)
        {
            origin.x -= width / 2f;
            origin.y += height / 2f;
        }
        else if(textCharacter.origin == Component.ORIGIN_BOTTOM_LEFT)
        {
            origin.x += width / 2f;
            origin.y -= height / 2f;
        }
        else if(textCharacter.origin == Component.ORIGIN_BOTTOM_RIGHT)
        {
            origin.x -= width / 2f;
            origin.y -= height / 2f;
        }

        Vector3f pos = new Vector3f((origin.x * 2 - window.getWidth()) / window.getWidth(),
                ((-origin.y * 2 + window.getHeight()) / window.getHeight()), 0f);

        matrix.identity().translate(pos).rotateZ((float) Math.toRadians(textCharacter.rotation)).
                scaleXY(textCharacter.scaleX * (width / window.getWidth()), textCharacter.scaleY * (height / window.getHeight()));

        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera)
    {
        Vector3f position = new Vector3f(camera.x, camera.y, camera.z);
        Vector3f rotation = new Vector3f(camera.rotationX, camera.rotationY, camera.rotationZ);
        Matrix4f matrix = new Matrix4f();
        matrix.identity();

        matrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public static void moveCamera(Mouse mouse, Keyboard keyboard, Camera camera, float speed)
    {
        if(mouse.isLeftButtonPressed())
        {
            camera.rotate((float) (mouse.getDx() * 0.2f), (float) (mouse.getDy() * 0.2f), 0);
            if (camera.rotationX > 80)
                camera.setRotation(80, camera.rotationY, camera.rotationZ);
            if (camera.rotationX < -80)
                camera.setRotation(-80, camera.rotationY, camera.rotationZ);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_W))
        {
            camera.move((float) (speed * Math.sin((float) Math.toRadians(camera.rotationY))), 0, 0);
            camera.move(0f, 0f, (float) (-speed * Math.sin(Math.toRadians(90 - camera.rotationY))));
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_S))
        {
            camera.move((float) (-speed * Math.sin((float) Math.toRadians(camera.rotationY))), 0, 0);
            camera.move(0, 0, (float) (speed * Math.sin(Math.toRadians(90 - camera.rotationY))));
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_A))
        {
            camera.move((float) (-speed * Math.sin((float) Math.toRadians(camera.rotationY + 90))), 0, 0);
            camera.move(0, 0, (float) (-speed * Math.sin(Math.toRadians(90 - camera.rotationY + 90))));
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_D))
        {
            camera.move((float) (speed * Math.sin((float) Math.toRadians(camera.rotationY + 90))), 0, 0);
            camera.move( 0, 0, (float) (speed * Math.sin(Math.toRadians(90 - camera.rotationY + 90))));
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE))
        {
            camera.move(0, speed, 0);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
        {
            camera.move(0, -speed, 0);
        }
    }
}
