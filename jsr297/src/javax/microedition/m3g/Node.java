/*
 * Copyright (c) 2009-2010, Jacques Gasselin de Richebourg
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
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author jgasseli
 */
public abstract class Node extends Transformable
{
    public static final int NONE = 144;
    public static final int ORIGIN = 145;
    public static final int X_AXIS = 146;
    public static final int Y_AXIS = 147;
    public static final int Z_AXIS = 148;
    
    private Node parent;
    private boolean renderingEnabled = true;
    private boolean pickingEnabled = true;
    private boolean collisionEnabled = true;
    private float alphaFactor = 1.0f;
    private int scope = -1;
    private int alignmentTargetY = NONE;
    private Node alignmentReferenceY;
    private int alignmentTargetZ = NONE;
    private Node alignmentReferenceZ;
    private float lodResolution;
    private final float[] pickingBoundingSphere = new float[4];
    private final float[] boundingSphere = new float[4];
    private boolean boundingSphereEnabled;
    private final float[] boundingBox = new float[6];
    private boolean boundingBoxEnabled;
    private int collisionShapeOrientations;

    private boolean internalBoundingSphereNeedsUpdate;

    public Node()
    {
        
    }

    public void align(Node reference)
    {
        if (reference != null && !inSameTree(this, reference))
        {
            throw new IllegalArgumentException("reference is not in the same scene graph as this node");
        }

        if (reference == null)
        {
            reference = this;
        }
        
        throw new UnsupportedOperationException();
    }

    public void animateLOD(int time, Camera camera, float resX, float resY)
    {
        throw new UnsupportedOperationException();
    }

    public boolean collide(int colliderScope, Node collidees, int collideeScope,
            boolean fullTraversal, Collisions collisions)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    void duplicate(Object3D target)
    {
        super.duplicate(target);

        final Node n = (Node) target;
        n.setAlphaFactor(getAlphaFactor());
        n.setCollisionEnable(isCollisionEnabled());
        n.setLODResolution(getLODResolution());
        n.setPickingEnable(isPickingEnabled());
        n.setRenderingEnable(isRenderingEnabled());
        n.setScope(getScope());
    }

    final void invalidatePickingBoundingSphere()
    {
        internalBoundingSphereNeedsUpdate = true;
        if (parent != null)
        {
            parent.invalidatePickingBoundingSphere();
        }
    }

    final float[] updatePickingBoundingSphere()
    {
        if (internalBoundingSphereNeedsUpdate)
        {
            internalBoundingSphereNeedsUpdate = false;
            calculatePickingBoundingSphere(pickingBoundingSphere);
        }
        return pickingBoundingSphere;
    }

    /**
     * Override this to calculate the bounding sphere around all
     * child nodes.
     */
    void calculatePickingBoundingSphere(float[] sphere)
    {
        //default to not containing anything
        Arrays.fill(sphere, 0.0f);
    }

    public void generateCollisionShape(int orientations, boolean useExisting)
    {
        throw new UnsupportedOperationException();
    }

    public Node getAlignmentReference(int axis)
    {
        switch (axis)
        {
            case Y_AXIS:
            {
                return alignmentReferenceY;
            }
            case Z_AXIS:
            {
                return alignmentReferenceZ;
            }
            default:
            {
                throw new IllegalArgumentException("axis is not Y_AXIS or Z_AXIS");
            }
        }
    }

    public int getAlignmentTarget(int axis)
    {
        switch (axis)
        {
            case Y_AXIS:
            {
                return alignmentTargetY;
            }
            case Z_AXIS:
            {
                return alignmentTargetZ;
            }
            default:
            {
                throw new IllegalArgumentException("axis is not Y_AXIS or Z_AXIS");
            }
        }
    }

    public float getAlphaFactor()
    {
        return this.alphaFactor;
    }

    public boolean getBoundingBox(float[] bounds)
    {
        if (!boundingBoxEnabled)
        {
            return false;
        }

        if (bounds != null)
        {
            Require.argumentHasCapacity(bounds, "bounds", 6);

            System.arraycopy(this.boundingBox, 0, bounds, 0, 6);
        }

        return true;
    }

    public boolean getBoundingSphere(float[] bounds)
    {
        if (!boundingSphereEnabled)
        {
            return false;
        }

        if (bounds != null)
        {
            Require.argumentHasCapacity(bounds, "bounds", 4);

            System.arraycopy(this.boundingSphere, 0, bounds, 0, 4);
        }

        return true;
    }

    public int getCollisionShape(float[] min, float[] max)
    {
        throw new UnsupportedOperationException();
    }

    public float getLODResolution()
    {
        return this.lodResolution;
    }
    
    public Node getParent()
    {
        return this.parent;
    }

    public int getScope()
    {
        return this.scope;
    }

    private static final boolean ancestorPath(Node ancestor, Node child,
            ArrayList<Node> path)
    {
        Require.notNull(ancestor, "ancestor");
        Require.notNull(child, "child");
        Require.notNull(path, "path");

        //go up the ancestor hierachy until we hit the ancestor
        //or a parentless node.
        for (Node it = child; it != null; it = it.getParent())
        {
            if (it == ancestor)
            {
                return true;
            }
            path.add(it);
        }

        return false;
    }


    private static final boolean accumulatePathTransform(Node ancestor, Node child,
            Transform transform)
    {
        final ArrayList<Node> path = new ArrayList<Node>();
        if (!ancestorPath(ancestor, child, path))
        {
            return false;
        }
        //accumulate the transforms in reverse order
        for (int i = path.size() - 1; i >= 0; --i)
        {
            transform.postMultiply(path.get(i).getCompositeTransform());
        }
        return true;
    }

    public boolean getTransformTo(Node target, Transform transform)
    {
        Require.notNull(target, "target");
        Require.notNull(transform, "transform");
        //quick exit if not in the same tree
        final Node ancestor = findCommonAncestor(this, target);
        if (ancestor == null)
        {
            return false;
        }

        if (ancestor == this)
        {
            //target is below this
            transform.setIdentity();
            if (!accumulatePathTransform(this, target, transform))
            {
                throw new IllegalStateException("early detection of being" +
                            " in the same tree has false positives");
            }
            transform.invert();
        }
        else if (ancestor == target)
        {
            //this is below target
            transform.setIdentity();
            if (!accumulatePathTransform(target, this, transform))
            {
                throw new IllegalStateException("early detection of being" +
                            " in the same tree has false positives");
            }
        }
        else
        {
            //this and target are in separate branches of the same common ancestor
            //both of them are below ancestor.
            
            final Transform ancestorToThis = new Transform();
            if (!accumulatePathTransform(ancestor, this, ancestorToThis))
            {
                throw new IllegalStateException("early detection of being" +
                            " in the same tree has false positives");
            }

            final Transform ancestorToTarget = new Transform();
            if (!accumulatePathTransform(ancestor, target, ancestorToTarget))
            {
                throw new IllegalStateException("early detection of being" +
                            " in the same tree has false positives");
            }

            //is this the correct order?
            transform.set(ancestorToTarget);
            transform.invert();
            transform.postMultiply(ancestorToThis);
        }

        return true;
    }

    static Node getTreeRoot(Node a)
    {
        if (a == null)
        {
            return null;
        }
        
        while (true)
        {
            final Node parent = a.getParent();
            if (parent == null)
            {
                return a;
            }
            a = parent;
        }
    }
    
    static boolean inSameTree(Node a, Node b)
    {
        return getTreeRoot(a) == getTreeRoot(b);
    }

    static Node findCommonAncestor(Node a, Node b)
    {
        final HashSet<Node> ancestorsA = new HashSet<Node>();
        //get all the ancestors
        while (a != null)
        {
            ancestorsA.add(a);
            a = a.getParent();
        }
        while (b != null)
        {
            if (ancestorsA.contains(b))
            {
                return b;
            }
            b = b.getParent();
        }

        return null;
    }

    public boolean isCollisionEnabled()
    {
        return this.collisionEnabled;
    }

    public boolean isPickingEnabled()
    {
        return this.pickingEnabled;
    }

    public boolean isRenderingEnabled()
    {
        return this.renderingEnabled;
    }

    public void selectLOD(Camera camera, float resX, float resY)
    {
        throw new UnsupportedOperationException();
    }

    public void setAlignment(Node zRef, int zTarget, Node yRef, int yTarget)
    {
        Require.argumentInEnum(zTarget, "zTarget", NONE, Z_AXIS);
        Require.argumentInEnum(yTarget, "yTarget", NONE, Z_AXIS);

        if ((zRef == yRef) && (zTarget == yTarget) && (zTarget != NONE))
        {
            throw new IllegalArgumentException("illegal double alignment to the" +
                    " same axis on the same reference node");
        }
        if (zRef == this)
        {
            throw new IllegalArgumentException("self referential alignment," +
                    " zRef is this");
        }
        if (yRef == this)
        {
            throw new IllegalArgumentException("self referential alignment," +
                    " yRef is this");
        }

        this.alignmentReferenceZ = zRef;
        this.alignmentTargetZ = zTarget;
        this.alignmentReferenceY = yRef;
        this.alignmentTargetY = yTarget;
    }

    public void setAlphaFactor(float alphaFactor)
    {
        Require.argumentInRange(alphaFactor, "alphaFactor", 0, 1);
        
        this.alphaFactor = alphaFactor;
    }

    public void setBoundingBox(float minX, float maxX, float minY, float maxY,
            float minZ, float maxZ)
    {
        if (minX > maxX)
        {
            throw new IllegalArgumentException("minX > maxX");
        }
        if (minY > maxY)
        {
            throw new IllegalArgumentException("minY > maxY");
        }
        if (minZ > maxZ)
        {
            throw new IllegalArgumentException("minZ > maxZ");
        }

        if (minX == maxX && minY == maxY && minZ == maxZ)
        {
            this.boundingBoxEnabled = false;
            return;
        }
        
        final float[] box = this.boundingBox;
        box[0] = minX;
        box[1] = maxX;
        box[2] = minY;
        box[3] = maxY;
        box[4] = minZ;
        box[5] = maxZ;

        this.boundingBoxEnabled = true;
    }

    public void setBoundingSphere(float centerX, float centerY, float centerZ,
            float radius)
    {
        Require.argumentNotNegative(radius, "radius");

        if (radius == 0)
        {
            this.boundingSphereEnabled = false;
            return;
        }
        
        final float[] sphere = this.boundingSphere;
        sphere[0] = centerX;
        sphere[1] = centerY;
        sphere[2] = centerZ;
        sphere[3] = radius;

        this.boundingSphereEnabled = true;
    }

    @Deprecated
    public void setCollisionEnable(boolean enable)
    {
        setCollisionEnabled(enable);
    }

    public void setCollisionEnabled(boolean enabled)
    {
        collisionEnabled = enabled;
    }

    public void setCollisionShape(int orientations, float[] min, float[] max)
    {
        throw new UnsupportedOperationException();
    }

    public void setLODResolution(float featuresPerUnit)
    {
        lodResolution = featuresPerUnit;
    }

    @Deprecated
    public void setPickingEnable(boolean enable)
    {
        setPickingEnabled(enable);
    }

    public void setPickingEnabled(boolean enabled)
    {
        if (enabled != pickingEnabled)
        {
            pickingEnabled = enabled;
            invalidatePickingBoundingSphere();
        }
    }

    @Deprecated
    public void setRenderingEnable(boolean enable)
    {
        setRenderingEnabled(enable);
    }

    public void setRenderingEnabled(boolean enabled)
    {
        renderingEnabled = enabled;
    }

    public void setScope(int scope)
    {
        this.scope = scope;
    }
    
    void setParent(Node parent)
    {
        if (parent != null && this.parent != null)
        {
            throw new IllegalStateException("this node already has a parent");
        }

        this.parent = parent;
    }

    @Override
    public void setTransform(Transform transform)
    {
        if (transform != null && !transform.is4thRowUnit())
        {
            throw new IllegalArgumentException(
                    "Nodes only allow 3x4 matrices as transforms");
        }
        
        super.setTransform(transform);
    }
}
