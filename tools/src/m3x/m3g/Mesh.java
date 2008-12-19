package m3x.m3g;

import java.io.IOException;

import m3x.m3g.primitives.Matrix;

/**
 * See http://java2me.org/m3g/file-format.html#Mesh<br>
  ObjectIndex   vertexBuffer;<br>
  UInt32        submeshCount;<br>
  FOR each submesh...<br>
    ObjectIndex   indexBuffer;<br>
    ObjectIndex   appearance;<br>
  END<br>
  <br>
 * @author jsaarinen
 */
public class Mesh extends Node implements M3GTypedObject
{
    public static class SubMesh implements M3GSerializable
    {

        private IndexBuffer indexBuffer;
        private Appearance appearance;

        public SubMesh()
        {
        }

        public SubMesh(IndexBuffer indexBuffer, Appearance appearance)
        {
            this.indexBuffer = indexBuffer;
            this.appearance = appearance;
        }

        public IndexBuffer getIndexBuffer()
        {
            return this.indexBuffer;
        }

        public Appearance getAppearance()
        {
            return this.appearance;
        }

        public void deserialize(M3GDeserialiser deserialiser)
            throws IOException
        {
            this.indexBuffer = (IndexBuffer)deserialiser.readReference();
            this.appearance = (Appearance)deserialiser.readReference();
        }

        public void serialize(M3GSerialiser serialiser)
            throws IOException
        {
            serialiser.writeReference(getIndexBuffer());
            serialiser.writeReference(getAppearance());
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof SubMesh))
            {
                return false;
            }
            SubMesh another = (SubMesh) obj;
            return this.indexBuffer.equals(another.indexBuffer) &&
                this.appearance.equals(another.appearance);
        }
    }

    private VertexBuffer vertexBuffer;
    private int subMeshCount;
    private SubMesh[] subMeshes;

    public Mesh(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, VertexBuffer vertexBuffer, SubMesh[] subMeshes)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        assert (subMeshes != null);
        assert (subMeshes.length > 0);
        this.vertexBuffer = vertexBuffer;
        this.subMeshes = subMeshes;
        this.subMeshCount = subMeshes.length;
    }

    public Mesh()
    {
        super();
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        this.vertexBuffer = (VertexBuffer)deserialiser.readReference();
        this.subMeshCount = deserialiser.readInt();
        this.subMeshes = new SubMesh[subMeshCount];
        for (int i = 0; i < this.subMeshes.length; i++)
        {
            this.subMeshes[i] = new SubMesh();
            this.subMeshes[i].deserialize(deserialiser);
        }
    }

    @Override
    public void serialize(M3GSerialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.writeReference(getVertexBuffer());
        serialiser.writeInt(getSubmeshCount());
        for (SubMesh subMesh : this.subMeshes)
        {
            subMesh.serialize(serialiser);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.MESH;
    }

    public VertexBuffer getVertexBuffer()
    {
        return this.vertexBuffer;
    }

    public int getSubmeshCount()
    {
        return this.subMeshCount;
    }

    public SubMesh[] getSubMeshes()
    {
        return this.subMeshes;
    }
}
