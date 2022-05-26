package core.loader;

import core.body.BoundingBox;
import core.body.Mesh;
import core.err.UnsupportedFileFormatException;
import core.util.MathUtil;
import core.util.Util;
import org.lwjgl.assimp.*;

import java.nio.ByteBuffer;
import java.util.Objects;

public class ModelLoader
{
    public static Mesh load(String file, ObjectLoader loader)
    {
        AIScene scene = Assimp.aiImportFile(file, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate |
                Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_TransformUVCoords | Assimp.aiProcess_ValidateDataStructure);
        if(scene == null)
            throw new UnsupportedFileFormatException(file);

        if(scene.mMeshes() == null) return null;

        AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(0));

        AIVector3D.Buffer verticesBuff = mesh.mVertices();
        float[] vertices = new float[verticesBuff.limit() * 3];
        for(int i = 0, j = 0; i < verticesBuff.limit(); i++)
        {
            AIVector3D vector = verticesBuff.get(i);
            vertices[j++] = vector.x();
            vertices[j++] = vector.y();
            vertices[j++] = vector.z();
        }

        AIVector3D.Buffer uvsBuff = mesh.mTextureCoords(0);
        assert uvsBuff != null;
        float[] uvs = new float[uvsBuff.limit() * 2];
        for(int i = 0, j = 0; i < uvsBuff.limit(); i++)
        {
            AIVector3D vector = uvsBuff.get(i);
            uvs[j++] = vector.x();
            uvs[j++] = 1 - vector.y();
        }

        AIVector3D.Buffer normalsBuffer = mesh.mNormals();
        float[] normals = new float[1];
        if (normalsBuffer != null)
        {
            normals = new float[normalsBuffer.limit() * 3];
            for(int i = 0, j = 0; i < normalsBuffer.limit(); i++)
            {
                normals[j++] = normalsBuffer.get(i).x();
                normals[j++] = normalsBuffer.get(i).y();
                normals[j++] = normalsBuffer.get(i).z();
            }
        }

        AIFace.Buffer faceBuff = mesh.mFaces();
        int[] indices = new int[faceBuff.limit() * 3];
        for(int i = 0, j = 0; i < faceBuff.limit(); i++)
        {
            AIFace indicesBuffer = faceBuff.get(i);
            indices[j++] = indicesBuffer.mIndices().get(0);
            indices[j++] = indicesBuffer.mIndices().get(1);
            indices[j++] = indicesBuffer.mIndices().get(2);
        }

        float[] tangents = new float[0];
        AIVector3D.Buffer tangentsBuff = mesh.mTangents();
        if(tangentsBuff != null)
        {
            tangents = new float[tangentsBuff.limit() * 3];
            for(int i = 0, j = 0; i < tangentsBuff.limit(); i++)
            {
                AIVector3D vector = tangentsBuff.get(i);
                tangents[j++] = vector.x();
                tangents[j++] = vector.y();
                tangents[j++] = vector.z();
            }
        }

        BoundingBox boundingBox = MathUtil.createBoundingBox(vertices);
        Assimp.aiReleaseImport(scene);
        return loader.loadObject(vertices, uvs, normals, indices, tangents, boundingBox);
    }

    public static Mesh load(Class<?> cls, String res, ObjectLoader loader)
    {
        ByteBuffer buffer = Util.loadResourceBuffer(cls, res);
        AIScene scene = Assimp.aiImportFileFromMemory(buffer, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate |
                Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_ValidateDataStructure, "");
        if(scene == null)
            throw new UnsupportedFileFormatException(res);

        if(scene.mMeshes() == null) return null;

        AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(0));

        AIVector3D.Buffer verticesBuff = mesh.mVertices();
        float[] vertices = new float[verticesBuff.limit() * 3];
        for(int i = 0, j = 0; i < verticesBuff.limit(); i++)
        {
            AIVector3D vector = verticesBuff.get(i);
            vertices[j++] = vector.x();
            vertices[j++] = vector.y();
            vertices[j++] = vector.z();
        }

        AIVector3D.Buffer uvsBuff = mesh.mTextureCoords(0);
        assert uvsBuff != null;
        float[] uvs = new float[uvsBuff.limit() * 2];
        for(int i = 0, j = 0; i < uvsBuff.limit(); i++)
        {
            AIVector3D vector = uvsBuff.get(i);
            uvs[j++] = vector.x();
            uvs[j++] = 1 - vector.y();
        }

        AIVector3D.Buffer normalsBuffer = mesh.mNormals();
        float[] normals = new float[1];
        if (normalsBuffer != null)
        {
            normals = new float[normalsBuffer.limit() * 3];
            for(int i = 0, j = 0; i < normalsBuffer.limit(); i++)
            {
                normals[j++] = normalsBuffer.get(i).x();
                normals[j++] = normalsBuffer.get(i).y();
                normals[j++] = normalsBuffer.get(i).z();
            }
        }

        AIFace.Buffer faceBuff = mesh.mFaces();
        int[] indices = new int[faceBuff.limit() * 3];
        for(int i = 0, j = 0; i < faceBuff.limit(); i++)
        {
            AIFace indicesBuffer = faceBuff.get(i);
            indices[j++] = indicesBuffer.mIndices().get(0);
            indices[j++] = indicesBuffer.mIndices().get(1);
            indices[j++] = indicesBuffer.mIndices().get(2);
        }

        float[] tangents = new float[0];
        AIVector3D.Buffer tangentsBuff = mesh.mTangents();
        if(tangentsBuff != null)
        {
            tangents = new float[tangentsBuff.limit() * 3];
            for(int i = 0, j = 0; i < tangentsBuff.limit(); i++)
            {
                AIVector3D vector = tangentsBuff.get(i);
                tangents[j++] = vector.x();
                tangents[j++] = vector.y();
                tangents[j++] = vector.z();
            }
        }

        BoundingBox boundingBox = MathUtil.createBoundingBox(vertices);
        Assimp.aiReleaseImport(scene);
        return loader.loadObject(vertices, uvs, normals, indices, tangents, boundingBox);
    }
}
