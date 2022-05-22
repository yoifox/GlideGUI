package core.body;

public class Mesh
{
    private final int vao;
    private final int[] vbos;
    private final int vertexCount;
    private int indicesCount;
    public BoundingBox boundingBox;
    public boolean twoSided = false;

    public Mesh(int vao, int[] vbos, int vertexCount, int indicesCount, BoundingBox box)
    {
        this.vao = vao;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
        this.indicesCount = indicesCount;
        boundingBox = box;
    }

    public Mesh(int vao, int[] vbos, int vertexCount)
    {
        this.vao = vao;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
    }

    public int getVao() {
        return vao;
    }

    public int[] getVbos() {
        return vbos;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getIndicesCount() {
        return indicesCount;
    }
}
