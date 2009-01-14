package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import java.util.Arrays;
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
    private List<Node> childNodes;

    public Group(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Node[] children)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        assert (children != null);
        setChildNodes(children);
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
        if (childNodes == null)
        {
            return 0;
        }
        return childNodes.size();
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
        return childNodes.get(index);
    }

    public List<Node> getChildNodes()
    {
        return this.childNodes;
    }

    public void setChildNodes(List<Node> childNodes)
    {
        //get rid of the old children
        for (int i = 0; i < getChildCount(); ++i)
        {
            removeChild(getChild(i));
        }
        if (childNodes == null)
        {
            this.childNodes = null;
        }
        else
        {
            for (Node child : childNodes)
            {
                addChild(child);
            }
        }
    }

    public void setChildNodes(Node[] children)
    {
        List<Node> childList = null;
        if (children != null)
        {
            childList = Arrays.asList(children);
        }
        setChildNodes(childList);
    }

    public void addChild(Node child)
    {
        if (child == null)
        {
            throw new IllegalArgumentException("null child in group");
        }

        if (childNodes == null)
        {
            childNodes = new Vector<Node>();
        }

        childNodes.add(child);
        child.setParent(this);
    }

    public void removeChild(Node child)
    {
        if (child == null)
        {
            return;
        }

        if (childNodes == null)
        {
            return;
        }

        childNodes.remove(child);
        child.setParent(null);
    }
}
