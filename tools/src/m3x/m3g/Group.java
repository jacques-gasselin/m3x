package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import java.util.List;
import java.util.Vector;
import m3x.m3g.primitives.Matrix;

/**
 * See http://java2me.org/m3g/file-format.html#Group<br>
 * ObjectIndex[] children;<br>
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public class Group extends Node
{
    private List<Node> children;

    public Group(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Node[] children)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        assert (children != null);
        setChildren(children);
    }

    public Group()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        int childCount = deserialiser.readInt();
        if (childCount < 0)
        {
            throw new IllegalArgumentException("Number of children < 0: " + childCount);
        }
        for (int i = 0; i < childCount; ++i)
        {
            addChild((Node)deserialiser.readReference());
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeInt(getChildCount());
        for (int i = 0; i < getChildCount(); ++i)
        {
            serialiser.writeReference(getChild(i));
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.GROUP;
    }

    public int getChildCount()
    {
        if (children == null)
        {
            return 0;
        }
        return children.size();
    }

    public Node getChild(int index)
    {
        if (index < 0)
        {
            throw new IllegalArgumentException("index < 0");
        }
        if (index >= getChildCount())
        {
            throw new IllegalArgumentException("index >= getChildCount()");
        }
        return children.get(index);
    }

    public Node[] getChildren()
    {
        Node[] arr = null;
        final int childCount = getChildCount();
        if (childCount > 0)
        {
            arr = new Node[childCount];
            for (int i = 0; i < childCount; ++i)
            {
                arr[i] = getChild(i);
            }
        }
        return arr;
    }

    public void setChildren(Node[] children)
    {
        //get rid of the old children
        for (int i = 0; i < getChildCount(); ++i)
        {
            removeChild(getChild(i));
        }
        if (children == null)
        {
            this.children = null;
        }
        else
        {
            for (int i = 0; i < children.length; ++i)
            {
                addChild(children[i]);
            }
        }
    }

    public void addChild(Node child)
    {
        if (child == null)
        {
            throw new IllegalArgumentException("null child in group");
        }

        if (children == null)
        {
            children = new Vector<Node>();
        }

        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Node child)
    {
        if (child == null)
        {
            return;
        }

        if (children == null)
        {
            return;
        }

        children.remove(child);
        child.setParent(null);
    }
}
