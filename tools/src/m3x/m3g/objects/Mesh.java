package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

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

        private ObjectIndex indexBuffer;
        private ObjectIndex appearance;

        public SubMesh()
        {
        }

        public SubMesh(ObjectIndex indexBuffer, ObjectIndex appearance)
        {
            this.indexBuffer = indexBuffer;
            this.appearance = appearance;
        }

        public ObjectIndex getIndexBuffer()
        {
            return this.indexBuffer;
        }

        public ObjectIndex getAppearance()
        {
            return this.appearance;
        }

        public void deserialize(DataInputStream dataInputStream, String m3gVersion)
            throws IOException, FileFormatException
        {
            this.indexBuffer = new ObjectIndex();
            this.indexBuffer.deserialize(dataInputStream, m3gVersion);
            this.appearance = new ObjectIndex();
            this.appearance.deserialize(dataInputStream, m3gVersion);
        }

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
    private ObjectIndex vertexBuffer;
    private int subMeshCount;
    private SubMesh[] subMeshes;

    public Mesh(ObjectIndex[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, ObjectIndex vertexBuffer, SubMesh[] subMeshes) throws FileFormatException
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

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.vertexBuffer = new ObjectIndex();
        this.vertexBuffer.deserialize(dataInputStream, m3gVersion);
        this.subMeshCount = M3GSupport.readInt(dataInputStream);
        this.subMeshes = new SubMesh[subMeshCount];
        for (int i = 0; i < this.subMeshes.length; i++)
        {
            this.subMeshes[i] = new SubMesh();
            this.subMeshes[i].deserialize(dataInputStream, m3gVersion);
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

    public byte getObjectType()
    {
        return ObjectTypes.MESH;
    }

    public ObjectIndex getVertexBuffer()
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
