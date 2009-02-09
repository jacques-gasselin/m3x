/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.util.Object3DReferences;

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

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        for (int i = 0; i < getChildCount(); ++i)
        {
            queue.add(getChild(i));
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
