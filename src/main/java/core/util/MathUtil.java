package core.util;

import core.body.Body3d;
import org.joml.*;
import core.body.BoundingBox;
import core.body.ui.Component;

import java.lang.Math;

public class MathUtil
{
    public static Vector2f rotateVector(float srcX, float srcY, float originX, float originY, float angle)
    {
        Vector3f src = new Vector3f(srcX, srcY, 0);
        Vector3f origin = new Vector3f(originX, originY, 0);

        src.sub(origin);
        Matrix3f matrix = new Matrix3f();
        matrix.rotateZ(-(float) Math.toRadians(angle));
        Vector3f result = new Vector3f(src).mul(matrix).add(origin);
        return new Vector2f(result.x, result.y);
    }

    public static Vector3f rotateVector(float srcX, float srcY, float srcZ, float originX, float originY, float originZ, float angleX, float angleY, float angleZ)
    {
        Vector3f src = new Vector3f(srcX, srcY, srcZ);
        Vector3f origin = new Vector3f(originX, originY, originZ);

        src.sub(origin);
        Matrix3f matrix = new Matrix3f();
        matrix.rotateX((float) Math.toRadians(angleX)).rotateY((float) Math.toRadians(angleY)).rotateZ((float) Math.toRadians(angleZ));
        return new Vector3f(src).mul(matrix).add(origin);
    }

    public static Vector2f scaleVector(float srcX, float srcY, float originX, float originY, float scaleX, float scaleY)
    {
        Vector2f src = new Vector2f(srcX, srcY);
        Vector2f origin = new Vector2f(originX, originY);
        Matrix2f matrix = new Matrix2f();
        matrix.scale(scaleX, scaleY);
        return src.mul(matrix).add(origin);
    }

    public static Vector3f scaleVector(float srcX, float srcY, float srcZ, float originX, float originY, float originZ, float scaleX, float scaleY, float scaleZ)
    {
        Vector3f src = new Vector3f(srcX, srcY, srcZ);
        Vector3f origin = new Vector3f(originX, originY, originZ);

        src.sub(origin);
        Matrix3f matrix = new Matrix3f();
        matrix.scale(scaleX, scaleY, scaleZ);
        return new Vector3f(src).mul(matrix).add(origin);
    }

    public static boolean pointInQuad(Component component, float x, float y)
    {
        Vector2f pos = Transformation.getTopLeftCorner(component);
        float minHeight = pos.y, maxHeight = pos.y + component.getHeight();
        float minWidth = pos.x, maxWidth = pos.x + component.getWidth();
        if(x <= minWidth || x >= maxWidth) return false;
        if(y <= minHeight || y >= maxHeight) return false;
        return true;
    }

    public static float distance(Body3d body, Body3d body2)
    {
        return (float) Math.sqrt((body2.x - body.x)*(body2.x - body.x) + (body2.y - body.y)*(body2.y - body.y) + (body2.z - body.z)*(body2.z - body.z));
    }

    public static float distance(Component component, Component component2)
    {
        Vector2f p1 = component.getTopLeftCorner();
        Vector2f p2 = component2.getTopLeftCorner();
        return p1.sub(p2).length();
    }

    public static float distance(Component component, Vector2f point)
    {
        Vector2f p1 = component.getTopLeftCorner();
        return p1.sub(point).length();
    }

    public static BoundingBox createBoundingBox(float[] vertices)
    {
        return createBoundingBox(vertices, new Matrix4f());
    }

    public static BoundingBox createBoundingBox(float[] vertices, Matrix4f transformation)
    {
        float xn = Float.MAX_VALUE;
        float yn = xn;
        float zn = xn;

        float xf = Float.MIN_VALUE;
        float yf = xf;
        float zf = xf;

        for(int index = 0; index < vertices.length; index += 3) {

            Vector3f vertex = multiply(transformation, vertices[index], vertices[index + 1], vertices[index + 2]);

            float x = vertex.x;
            float y = vertex.y;
            float z = vertex.z;

            if(x < xn) xn = x; else if(x > xf) xf = x;
            if(y < yn) yn = y; else if(y > yf) yf = y;
            if(z < zn) zn = z; else if(z > zf) zf = z;
        }

        float width = xf - xn;
        float height = yf - yn;
        float depth = zf - zn;

        BoundingBox boundingBox =  new BoundingBox(width, height, depth);
        boundingBox.setPosition(xn + width / 2f, yn + height / 2f, zn + depth / 2f);
        return boundingBox;
    }

    public static Vector3f multiply(Matrix4f matrix, float x, float y, float z)
    {
        Vector3f result = new Vector3f();
        result.x = (x * matrix.m00()) + (y * matrix.m10()) + (z * matrix.m20()) + matrix.m30();
        result.y = (x * matrix.m01()) + (y * matrix.m11()) + (z * matrix.m21()) + matrix.m31();
        result.z = (x * matrix.m02()) + (y * matrix.m12()) + (z * matrix.m22()) + matrix.m32();
        return result;
    }

    public static Vector2f multiply(Matrix2f matrix, float x, float y)
    {
        return new Vector2f(x, y).mul(matrix);
    }

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos)
    {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Vector3f moveVertexAlongNormal(Vector3f vertex, Vector3f normal, float distance)
    {
        return new Vector3f(vertex).add(new Vector3f(normal).mul(distance));
    }

    public static Vector3f normal(Vector3f v1, Vector3f v2)
    {
        return new Vector3f(v2).sub(v1).normalize();
    }

    public static boolean pointInBox(BoundingBox box, Vector3f point)
    {
        //Vector3f center = box.getCenter();
        //float x = center.x;
        //float y = center.y;
        //float z = center.z;
        float x = box.x;
        float y = box.y;
        float z = box.z;
        float minHeight = y - box.height / 2, maxHeight = y + box.height / 2;
        float minWidth = x - box.width / 2, maxWidth = x + box.width / 2;
        float minDepth = z - box.depth / 2, maxDepth = z + box.depth / 2;
        if(point.x <= minWidth || point.x >= maxWidth) return false;
        if(point.y <= minHeight || point.y >= maxHeight) return false;
        if(point.z <= minDepth || point.z >= maxDepth) return false;
        return true;
    }

    public static boolean boxInBox(BoundingBox box1, BoundingBox box2)
    {
        float[] vertices = box1.getVertices();
        for(int i = 0; i < vertices.length; )
            if(pointInBox(box2, new Vector3f(vertices[i++], vertices[i++], vertices[i++])))
                return true;

        vertices = box2.getVertices();
        for(int i = 0; i < vertices.length; )
            if(pointInBox(box1, new Vector3f(vertices[i++], vertices[i++], vertices[i++])))
                return true;

        return false;
    }
}
