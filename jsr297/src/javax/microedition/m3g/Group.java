package javax.microedition.m3g;

import java.util.ArrayList;

/**
 * @author jgasseli
 */
public class Group extends Node
{
    private final ArrayList<Node> children = new ArrayList<Node>();

    public Group()
    {
        
    }

    private final boolean canParentChild(Node child)
    {
        Require.notNull(child, "child");

        final int childId = System.identityHashCode(child);
        final int parentId = System.identityHashCode(child.getParent());
        final int thisId = System.identityHashCode(this);

        if (childId == thisId)
        {
            throw new IllegalArgumentException("child is this Group");
        }
        if (child instanceof World)
        {
            throw new IllegalArgumentException("child is a World node");
        }
        if (parentId != 0 && parentId != thisId)
        {
            throw new IllegalArgumentException("child already has a parent other than this group");
        }

        return parentId != thisId;
    }

    public void addChild(Node child)
    {
        if (canParentChild(child))
        {
            child.setParent(this);
        }
        
        children.add(child);
    }

    public Node getChild(int index)
    {
        Require.indexInRange(index, getChildCount());

        return children.get(index);
    }

    public int getChildCount()
    {
        return children.size();
    }

    //TODO lod

    public void insertChild(Node child, int index)
    {
        Require.indexInRange(index, getChildCount() + 1);
        
        if (canParentChild(child))
        {
            child.setParent(this);
        }

        children.add(index, child);
    }

    //TODO pick

    public void removeChild(Node child)
    {
        if (children.remove(child))
        {
            //was it the last reference as a parent?
            if (!children.contains(child))
            {
                child.setParent(null);
            }
        }
    }
}
