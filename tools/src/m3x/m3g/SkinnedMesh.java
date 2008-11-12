package m3x.m3g;

import m3x.m3g.Mesh;
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
import m3x.m3g.util.LittleEndianDataInputStream;

/**
 * See http://java2me.org/m3g/file-format.html#SkinnedMesh<br>
  ObjectIndex   skeleton;<br>
  UInt32        transformReferenceCount;<br>
  FOR each bone reference...<br>
    ObjectIndex   transformNode;<br>
    UInt32        firstVertex;<br>
    UInt32        vertexCount;<br>
    Int32         weight;<br>
  END<br>
  <br>
 * @author jsaarinen
 */
public class SkinnedMesh extends Mesh implements M3GTypedObject
{
    public static class BoneReference implements M3GSerializable
    {

        private ObjectIndex transformNode;
        private int firstVertex;
        private int vertexCount;
        private int weight;

        public BoneReference(ObjectIndex transformNode, int firstVertex,
            int vertexCount, int weight)
        {
            this.transformNode = transformNode;
            this.firstVertex = firstVertex;
            this.vertexCount = vertexCount;
            this.weight = weight;
        }

        public BoneReference()
        {
        }

        public ObjectIndex getTransformNode()
        {
            return this.transformNode;
        }

        public int getFirstVertex()
        {
            return this.firstVertex;
        }

        public int getVertexCount()
        {
            return this.vertexCount;
        }

        public int getWeight()
        {
            return this.weight;
        }

        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof BoneReference))
            {
                return false;
            }
            BoneReference another = (BoneReference) obj;
            return this.transformNode.equals(another.transformNode) &&
                this.firstVertex == another.firstVertex &&
                this.vertexCount == another.vertexCount &&
                this.weight == another.weight;
        }

        public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
            throws IOException, FileFormatException
        {
            this.transformNode = new ObjectIndex();
            this.transformNode.deserialize(dataInputStream, m3gVersion);
            this.firstVertex = dataInputStream.readInt();
            this.vertexCount = dataInputStream.readInt();
            this.weight = dataInputStream.readInt();
        }

        public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
            throws IOException
        {
            this.transformNode.serialize(dataOutputStream, m3gVersion);
            M3GSupport.writeInt(dataOutputStream, this.firstVertex);
            M3GSupport.writeInt(dataOutputStream, this.vertexCount);
            M3GSupport.writeInt(dataOutputStream, this.weight);
        }
    }
    private ObjectIndex skeleton;
    private int transformReferenceCount;
    private BoneReference[] boneReferences;

    public SkinnedMesh(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, Matrix transform,
        boolean enableRendering, boolean enablePicking, byte alphaFactor,
        int scope, ObjectIndex vertexBuffer, SubMesh[] subMeshes,
        ObjectIndex skeleton, BoneReference[] boneReferences) throws FileFormatException
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope, vertexBuffer, subMeshes);
        assert (skeleton != null);
        assert (boneReferences != null);
        this.skeleton = skeleton;
        this.transformReferenceCount = boneReferences.length;
        this.boneReferences = boneReferences;
    }

    public SkinnedMesh()
    {
        super();
    }

    public int getObjectType()
    {
        return ObjectTypes.SKINNED_MESH;
    }

    public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.skeleton = new ObjectIndex();
        this.skeleton.deserialize(dataInputStream, m3gVersion);
        this.transformReferenceCount = dataInputStream.readInt();
        this.boneReferences = new BoneReference[this.transformReferenceCount];
        for (int i = 0; i < this.boneReferences.length; i++)
        {
            this.boneReferences[i] = new BoneReference();
            this.boneReferences[i].deserialize(dataInputStream, m3gVersion);
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        this.skeleton.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeInt(dataOutputStream, this.transformReferenceCount);
        for (BoneReference boneReference : this.boneReferences)
        {
            boneReference.serialize(dataOutputStream, m3gVersion);
        }
    }

    public ObjectIndex getSkeleton()
    {
        return this.skeleton;
    }

    public int getTransformReferenceCount()
    {
        return this.transformReferenceCount;
    }

    public BoneReference[] getBoneReferences()
    {
        return this.boneReferences;
    }
}