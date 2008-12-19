package m3x.m3g;

import java.io.IOException;

import m3x.m3g.primitives.Matrix;

/**
 * See http://java2me.org/m3g/file-format.html#Group<br>
 * ObjectIndex[] children;<br>
 * 
 * @author jsaarinen
 */
public class Group extends Node implements M3GTypedObject
{
    private Node[] children;

    public Group(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Node[] children)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        assert (children != null);
        this.children = children;
    }

    public Group()
    {
        super();
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        int childrenLength = deserialiser.readInt();
        if (childrenLength < 0)
        {
            throw new IllegalArgumentException("Number of children < 0: " + childrenLength);
        }
        this.children = new Node[childrenLength];
        for (int i = 0; i < this.children.length; i++)
        {
            Node child = (Node)deserialiser.readReference();
            if (child == null)
            {
                throw new IllegalArgumentException("null child in group");
            }
            this.children[i] = child;
        }
    }

    @Override
    public void serialize(M3GSerialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.writeInt(getChildCount());
        for (Node child : getChildren())
        {
            serialiser.writeReference(child);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.GROUP;
    }

    public int getChildCount()
    {
        return this.children.length;
    }

    public Node[] getChildren()
    {
        return this.children;
    }
}
