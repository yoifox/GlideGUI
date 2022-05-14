package core.loader;

import com.jumi.scene.JUMIScene;
import com.jumi.scene.objects.JUMIMesh;
import core.body.BoundingBox;
import core.body.Mesh;
import core.utils.MathUtils;

public class OBJLoader
{
    public static Mesh loadModel(String file, ObjectLoader loader)
    {
        JUMIScene scene = com.jumi.obj.OBJLoader.loadModel(file);
        JUMIMesh mesh = scene.getMeshByIndex(0);
        mesh.flipUVY();
        mesh.triangulate();
        BoundingBox boundingBox = MathUtils.createBoundingBox(mesh.vertices);
        return loader.loadObject(mesh.vertices, mesh.uvs, mesh.normals, mesh.indices, mesh.tangents, boundingBox);
    }
}
