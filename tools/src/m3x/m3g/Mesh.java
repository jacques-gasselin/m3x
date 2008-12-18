package m3x.m3g;

import java.io.DataOutputStream;
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
            throws IOException, FileFormatException
        {
            this.indexBuffer = (IndexBuffer)deserialiser.readObjectReference();
            this.appearance = (Appearance)deserialiser.readObjectReference();
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

        public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
            throws IOException
        {
            this.indexBuffer.serialize(dataOutputStream, m3gVersion);
            this.appearance.serialize(dataOutputStream, m3gVersion);
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
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.vertexBuffer = (VertexBuffer)deserialiser.readObjectReference();
        this.subMeshCount = deserialiser.readInt();
        this.subMeshes = new SubMesh[subMeshCount];
        for (int i = 0; i < this.subMeshes.length; i++)
        {
            this.subMeshes[i] = new SubMesh();
            this.subMeshes[i].deserialize(deserialiser);
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        this.vertexBuffer.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeInt(dataOutputStream, this.subMeshCount);
        for (SubMesh subMesh : this.subMeshes)
        {
            subMesh.serialize(dataOutputStream, m3gVersion);
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
