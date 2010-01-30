/*
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

package javax.microedition.m3g;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jgasseli
 */
public class Group extends Node
{
    private final ArrayList<Node> children = new ArrayList<Node>();
    private boolean lodEnabled;
    private float lodOffset;
    private float lodHysteresis;
    private int lodChild = -1;
    private float lodBlendFactor;

    public Group()
    {
        
    }

    private final boolean canParentChild(Node child)
    {
        Require.notNull(child, "child");

        if (child == this)
        {
            throw new IllegalArgumentException("child is this Group");
        }
        if (child instanceof World)
        {
            throw new IllegalArgumentException("child is a World node");
        }
        final Node parent = child.getParent();
        if (parent != null && parent != this)
        {
            throw new IllegalArgumentException("child already has a parent other than this group");
        }

        return parent != this;
    }

    public void addChild(Node child)
    {
        if (canParentChild(child))
        {
            child.setParent(this);
        }
        
        children.add(child);
    }

    @Override
    void duplicate(Object3D target)
    {
        super.duplicate(target);

        final Group g = (Group) target;
        //TODO support duplicates of the same child
        for (Node child : children)
        {
            g.addChild((Node) child.duplicate());
        }
        g.setLODEnable(isLodEnabled(), getLODHysteresis());
        g.setLODOffset(getLODOffset());
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

    public float getLODBlendFactor()
    {
        return this.lodBlendFactor;
    }

    public int getLODChild()
    {
        return this.lodChild;
    }

    public float getLODHysteresis()
    {
        return this.lodHysteresis;
    }

    public float getLODOffset()
    {
        return this.lodOffset;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);
        references.addAll(children);
    }

    public void insertChild(Node child, int index)
    {
        Require.indexInRange(index, getChildCount() + 1);
        
        if (canParentChild(child))
        {
            child.setParent(this);
        }

        children.add(index, child);
    }

    public boolean isLodEnabled()
    {
        return this.lodEnabled;
    }

    public boolean pick(int scope, float x, float y, Camera camera, RayIntersection ri)
    {
        Require.notNull(camera, "camera");

        final Transform cameraToGroup = new Transform();
        if (!camera.getTransformTo(this, cameraToGroup))
        {
            throw new IllegalStateException(
                    "there is no scene graph path between camera and this Group");
        }

        final Transform inverseProjection = new Transform();
        camera.getProjection(inverseProjection);
        inverseProjection.invert();
        
        final float[] pNear = { 2 * x - 1, 1 - 2 * y, -1, 1};
        final float[] pFar = { 2 * x - 1, 1 - 2 * y, 1, 1};

        inverseProjection.transform(pNear);
        inverseProjection.transform(pFar);

        final float invWNear = 1.0f / pNear[3];
        final float invWFar = 1.0f / pFar[3];
        pNear[0] *= invWNear;
        pNear[1] *= invWNear;
        pNear[2] *= invWNear;
        pNear[3] = 1.0f;

        pFar[0] *= invWFar;
        pFar[1] *= invWFar;
        pFar[2] *= invWFar;
        pFar[3] = 1.0f;

        cameraToGroup.transform(pNear);
        cameraToGroup.transform(pFar);

        final float ox = pNear[0];
        final float oy = pNear[1];
        final float oz = pNear[2];

        final float dx = pFar[0] - ox;
        final float dy = pFar[1] - oy;
        final float dz = pFar[2] - oz;

        return pick(scope, ox, oy, oz, dx, dy, dz, ri);
    }

    public boolean pick(int scope, float ox, float oy, float oz,
            float dx, float dy, float dz, RayIntersection ri)
    {
        if (dx == 0)
        {
            throw new IllegalArgumentException("dx is 0");
        }
        if (dy == 0)
        {
            throw new IllegalArgumentException("dy is 0");
        }
        if (dz == 0)
        {
            throw new IllegalArgumentException("dz is 0");
        }
            
        throw new UnsupportedOperationException();
    }

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

    public void setLODEnable(boolean enable, float hysteresis)
    {
        Require.argumentInRange(hysteresis, "hysteresis", 0, 1);
        
        this.lodEnabled = enable;
        this.lodHysteresis = hysteresis;
    }

    public void setLODOffset(float offset)
    {
        this.lodOffset = offset;
    }
}
