package m3x.m3g;

import m3x.m3g.primitives.Serializable;
import m3x.m3g.primitives.TypedObject;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

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
public class SkinnedMesh extends Mesh implements TypedObject
{
    public static class BoneReference implements Serializable
    {

        private Node transformNode;
        private int firstVertex;
        private int vertexCount;
        private int weight;

        public BoneReference(Node transformNode, int firstVertex,
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

        public Node getTransformNode()
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

        public void deserialize(Deserialiser deserialiser)
            throws IOException
        {
            this.transformNode = (Node)deserialiser.readReference();
            this.firstVertex = deserialiser.readInt();
            this.vertexCount = deserialiser.readInt();
            this.weight = deserialiser.readInt();
        }

        public void serialize(Serialiser serialiser)
            throws IOException
        {
            serialiser.writeReference(getTransformNode());
            serialiser.writeInt(this.firstVertex);
            serialiser.writeInt(this.vertexCount);
            serialiser.writeInt(this.weight);
        }
    }
    private Group skeleton;
    private BoneReference[] boneReferences;

    public SkinnedMesh(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, Matrix transform,
        boolean enableRendering, boolean enablePicking, byte alphaFactor,
        int scope, VertexBuffer vertexBuffer, SubMesh[] subMeshes,
        Group skeleton, BoneReference[] boneReferences)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope, vertexBuffer, subMeshes);
        assert (skeleton != null);
        assert (boneReferences != null);
        this.skeleton = skeleton;
        this.boneReferences = boneReferences;
    }

    public SkinnedMesh()
    {
        super();
    }

    @Override
    public int getObjectType()
    {
        return ObjectTypes.SKINNED_MESH;
    }

    @Override
    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        this.skeleton = (Group)deserialiser.readReference();
        int transformReferenceCount = deserialiser.readInt();
        this.boneReferences = new BoneReference[transformReferenceCount];
        for (int i = 0; i < this.boneReferences.length; i++)
        {
            this.boneReferences[i] = new BoneReference();
            this.boneReferences[i].deserialize(deserialiser);
        }
    }

    @Override
    public void serialize(Serialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.writeReference(getSkeleton());
        serialiser.writeInt(this.boneReferences.length);
        for (BoneReference boneReference : this.boneReferences)
        {
            boneReference.serialize(serialiser);
        }
    }

    public Group getSkeleton()
    {
        return this.skeleton;
    }

    public int getTransformReferenceCount()
    {
        return this.boneReferences.length;
    }

    public BoneReference[] getBoneReferences()
    {
        return this.boneReferences;
    }
}
