package core.body;

import org.joml.Vector2f;
import org.joml.Vector3f;
import core.loader.ObjectLoader;
import core.util.MathUtil;

public class Terrain extends Body3d
{
    float size, maxHeight = 0;
    int vertexCount;
    public Texture texture, height, grass;
    public Mesh mesh;


    public Terrain(float size, int vertexCount)
    {
        this.size = size;
        this.vertexCount = vertexCount;
    }

    public Terrain(float size, int vertexCount, Texture texture)
    {
        this.size = size;
        this.vertexCount = vertexCount;
        this.texture = texture;
    }

    public Terrain(float size, Texture texture, Texture height)
    {
        this.size = size;
        this.texture = texture;
        this.height = height;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        if(height == null)
        {
            height = scene.objectLoader.loadTexture(vertexCount, vertexCount);
        }
        mesh = createTerrain(scene.objectLoader);
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        height.free(scene.objectLoader);
    }

    public void recreate()
    {
        scene.objectLoader.free(mesh);
        if(height == null)
        {
            height = scene.objectLoader.loadTexture(vertexCount, vertexCount);
        }
        mesh = createTerrain(scene.objectLoader);
    }


    private Mesh createTerrain(ObjectLoader objectLoader)
    {
        if(height != null)
        {
            vertexCount = Math.min(height.getWidth(), height.getHeight());
        }
        int count = vertexCount * vertexCount;
        float[] vertices = new float[count * 3];
        int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
        float[] normals = new float[count * 3];
        float[] uvs = new float[count * 2];
        int counter = 0;

        for(int i = 0; i < vertexCount; i++)
        {
            for(int j = 0; j < vertexCount; j++)
            {
                vertices[counter * 3] = j / (vertexCount - 1.0f) * size;
                vertices[counter * 3 + 1] = calcHeight(i, j);
                vertices[counter * 3 + 2] = i / (vertexCount - 1.0f) * size;

                Vector3f normal = calcNormal(i, j);
                normals[counter * 3] = normal.x;
                normals[counter * 3 + 1] = normal.y;
                normals[counter * 3 + 2] = normal.z;

                uvs[counter * 2] = j / (vertexCount - 1.0f) * size;
                uvs[counter * 2 + 1] = i / (vertexCount - 1.0f) * size;

                counter++;
            }
        }
        counter = 0;
        for(int z = 0; z < vertexCount - 1.0f; z++)
        {
            for(int x = 0; x < vertexCount - 1.0f; x++)
            {
                int topLeft = (z * vertexCount) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * vertexCount) + x;
                int bottomRight = bottomLeft + 1;
                indices[counter++] = topLeft;
                indices[counter++] = bottomLeft;
                indices[counter++] = topRight;
                indices[counter++] = topRight;
                indices[counter++] = bottomLeft;
                indices[counter++] = bottomRight;
            }
        }
        return objectLoader.loadObject(vertices, uvs, normals, indices, MathUtil.createBoundingBox(vertices));
    }

    private float calcHeight(int x, int z)
    {
        if(x >= vertexCount || z >= vertexCount) return 0;
        //float height = this.height.getPixelRGBA(x, z);
        //height += (256f*256f*256f*256f) / 2f;
        //height /= (256f*256f*256f*256f) / 2f;
        //height *= maxHeight;

        int[] height = this.height.getPixelBytesRGBA(x, z);
        float avg = (height[0] + height[1] + height[2]) / (3f);
        if(avg > maxHeight) maxHeight = avg * scaleY;
        return avg;
    }

    private Vector3f calcNormal(int x, int z)
    {
        if(x == 0 || z == 0 || x == vertexCount - 1 || z == vertexCount - 1) return new Vector3f(0, 1, 0);
        float hl = calcHeight(x - 1, z);
        float hr = calcHeight(x + 1, z);
        float hd = calcHeight(x, z - 1);
        float hu = calcHeight(x, z + 1);
        return new Vector3f(hl - hr, 2f, hd - hu).normalize();
    }

    public float getHeight(float x, float z)
    {
        x *= scaleX;
        z *= scaleZ;
        float terrainX = x, terrainZ = z;
        terrainX = x - this.x;
        terrainZ = z - this.z;

        terrainX /= size;
        terrainZ /= size;

        if(terrainX <= 0 || terrainZ <= 0 || x >= vertexCount - 1 || y >= vertexCount - 1) return 0;
        return calcHeight((int) terrainX, (int) terrainZ);
    }

    public float getHeight(float x, float z, boolean worldCoordinates)
    {
        x *= scaleX;
        z *= scaleZ;
        float terrainX = x, terrainZ = z;
        if(worldCoordinates)
        {
            terrainX = x - this.x;
            terrainZ = z - this.z;
        }

        int vertexCountSquare = (int) (vertexCount * vertexCount);

        float gridSquareSize = Math.abs(size / (float)vertexCountSquare - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        //int gridX = (int) (terrainX / size + 1);
        //int gridZ = (int) (terrainZ / size + 1);

        if(gridX >= vertexCountSquare - 1 || gridZ >= vertexCountSquare - 1 || gridX < 0 || gridZ < 0)
            return 0;

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        float result;
        if (xCoord <= (1-zCoord))
        {
            result = MathUtil.barryCentric(new Vector3f(0, calcHeight(gridX, gridZ), 0), new Vector3f(1,
                            calcHeight(gridX + 1, gridZ), 0), new Vector3f(0,
                            calcHeight(gridX, gridZ + 1), 1), new Vector2f(xCoord, zCoord));
        }
        else
        {
            result = MathUtil.barryCentric(new Vector3f(1, calcHeight(gridX + 1, gridZ), 0), new Vector3f(1,
                            calcHeight(gridX + 1, gridZ + 1), 1), new Vector3f(0,
                            calcHeight(gridX, gridZ + 1), 1), new Vector2f(xCoord, zCoord));
        }

        return result * scaleY + this.y;
    }

    public float getSize() {
        return size;
    }
}
