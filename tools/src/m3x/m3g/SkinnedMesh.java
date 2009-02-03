package m3x.m3g;

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import java.util.List;
import java.util.Vector;
import m3x.m3g.util.Object3DReferences;

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
 * @author jgasseli
 */
public class SkinnedMesh extends Mesh implements SectionSerialisable
{
    private static class Bone
    {
        private Node transformNode;
        private int firstVertex;
        private int vertexCount;
        private int weight;

        public Bone(Node transformNode, int firstVertex,
            int vertexCount, int weight)
        {
            this.transformNode = transformNode;
            this.firstVertex = firstVertex;
            this.vertexCount = vertexCount;
            this.weight = weight;
        }

        public Bone()
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
    }
    
    private Group skeleton;
    private List<Bone> boneReferences;

    public SkinnedMesh()
    {
        super();
        resetBones(10);
    }

    private final void resetBones(int capacity)
    {
        boneReferences = new Vector<Bone>(capacity);
    }

    public void addTransform(Node bone, int weight, int firstVertex, int vertexCount)
    {
        boneReferences.add(new Bone(bone, firstVertex, vertexCount, weight));
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getSkeleton());
    }

    @Override
    public int getSectionObjectType()
    {
        return ObjectTypes.SKINNED_MESH;
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setSkeleton((Group)deserialiser.readReference());
        final int transformReferenceCount = deserialiser.readInt();
        resetBones(transformReferenceCount);
        for (int i = 0; i < transformReferenceCount; ++i)
        {
            final Node bone = (Node) deserialiser.readReference();
            final int firstVertex = deserialiser.readInt();
            final int vertexCount = deserialiser.readInt();
            final int weight = deserialiser.readInt();
            addTransform(bone, weight, firstVertex, vertexCount);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(getSkeleton());
        final int transformReferenceCount = getTransformReferenceCount();
        serialiser.writeInt(transformReferenceCount);
        for (int i = 0; i < transformReferenceCount; ++i)
        {
            Bone ref = this.boneReferences.get(i);
            serialiser.writeReference(ref.getTransformNode());
            serialiser.writeInt(ref.getFirstVertex());
            serialiser.writeInt(ref.getVertexCount());
            serialiser.writeInt(ref.getWeight());
        }
    }

    public Group getSkeleton()
    {
        return this.skeleton;
    }

    public int getTransformReferenceCount()
    {
        return this.boneReferences.size();
    }

    public void setSkeleton(Group skeleton)
    {
        this.skeleton = skeleton;
        this.skeleton.setParent(this);
    }
}
