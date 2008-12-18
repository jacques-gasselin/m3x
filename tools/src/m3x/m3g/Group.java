package m3x.m3g;

import java.io.DataOutputStream;
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
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        int childrenLength = deserialiser.readInt();
        if (childrenLength < 0)
        {
            throw new FileFormatException("Number of children < 0: " + childrenLength);
        }
        this.children = new Node[childrenLength];
        for (int i = 0; i < this.children.length; i++)
        {
            Node child = (Node)deserialiser.readObjectReference();
            if (child == null)
            {
                throw new FileFormatException("null child in group");
            }
            this.children[i] = child;
        }
    }

    @Override
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeInt(dataOutputStream, this.children.length);
        for (Node child : this.children)
        {
            child.serialize(dataOutputStream, m3gVersion);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.GROUP;
    }

    public Node[] getChildren()
    {
        return this.children;
    }
}
