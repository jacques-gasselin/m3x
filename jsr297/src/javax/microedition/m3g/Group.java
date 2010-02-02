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

import m3x.Require;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import m3x.microedition.m3g.TransformUtils;

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
        invalidatePickingBoundingSphere();
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

    /**
     * Override to calculate the bounding sphere around all
     * child nodes. Child nodes that have picking disabled
     * will be ignored.
     *
     * It is acceptable to use any method that overestimates the
     * bounding or calculates it exactly. Any method that underestimates
     * the bounding should not be used. This gives picking certain
     * guarantees that are neccessary for proper operation.
     *
     * TODO: use a proper minimum enclosing ball of balls algorithm
     * @see http://www.cgal.org/
     */
    @Override
    void calculatePickingBoundingSphere(float[] sphere)
    {
        //FIXME: for now just use a linear time average sphere
        float ox = 0;
        float oy = 0;
        float oz = 0;
        float weighting = 0;
        
        for (Node child : children)
        {
            if (child.isPickingEnabled())
            {
                final float[] s = child.updatePickingBoundingSphere();
                final float weight = s[3]; //radius
                //weighted accumulate
                ox += s[0] * weight;
                oy += s[1] * weight;
                oz += s[2] * weight;
                weighting += weight;
            }
        }

        if (weighting == 0)
        {
            //quick exit if none of the children contain anything
            Arrays.fill(sphere, 0.0f);
            return;
        }

        //average it out
        final float invWeighting = 1.0f / weighting;
        ox *= invWeighting;
        oy *= invWeighting;
        oz *= invWeighting;

        //find largest radius
        float radius = 0.0f;
        for (Node child : children)
        {
            if (child.isPickingEnabled())
            {
                final float[] s = child.updatePickingBoundingSphere();
                final float dx = s[0] - ox;
                final float dy = s[1] - oy;
                final float dz = s[2] - oz;
                final float dist = s[3] +
                        ((float) Math.sqrt(dx * dx + dy * dy + dz * dz));
                if (dist > radius)
                {
                    radius = dist;
                }
            }
        }

        sphere[0] = ox;
        sphere[1] = oy;
        sphere[2] = oz;
        sphere[3] = radius;
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
        invalidatePickingBoundingSphere();
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

        final float[] near = new float[4];
        final float[] far = new float[4];
        TransformUtils.unproject(camera, cameraToGroup, x, y, near, far);

        final float ox = near[0];
        final float oy = near[1];
        final float oz = near[2];

        final float dx = far[0] - ox;
        final float dy = far[1] - oy;
        final float dz = far[2] - oz;

        return pick(scope, ox, oy, oz, dx, dy, dz, ri);
    }

    private static final float dot(float x0, float y0, float z0,
            float x1, float y1, float z1)
    {
        return x0 * x1 + y0 * y1 + z0 * z1;
    }

    private static final float mag(float x, float y, float z)
    {
        return (float) Math.sqrt(magSqr(x, y, z));
    }

    private static final float magSqr(float x, float y, float z)
    {
        return x * x + y * y + z * z;
    }

    static final boolean rayIntersectsSphere(Transform ray, float[] sphere)
    {
        final float radius = sphere[3];
        if (radius == 0)
        {
            //quick exit if sphere is disabled.
            return false;
        }

        //transform the sphere into the ray's view coordinate system.
        final float[] origin = new float[4];
        System.arraycopy(sphere, 0, origin, 0, 3);
        origin[3] = 1.0f;
        ray.transform(origin);

        //ray origin is (0, 0, 0) in the ray's coordinate system
        //ray direction is (0, 0, 1) in the ray's coordinate system
        
        //rs is the ray origin to sphere origin vector
        final float rsX = origin[0];
        final float rsY = origin[1];
        final float rsZ = origin[2];

        final float rayToRadiusLengthSqr = magSqr(
                rsX, rsY, rsZ) - radius * radius;
        final float discriminant = (rsZ * rsZ) - rayToRadiusLengthSqr;
        if (discriminant < 0)
        {
            //no intersection
            return false;
        }
        return true;
    }
    
    public boolean pick(int scope, float ox, float oy, float oz,
            float dx, float dy, float dz, RayIntersection ri)
    {
        if (dx == 0 && dy == 0 && dz == 0)
        {
            throw new IllegalArgumentException("dx, dy and dz are all 0");
        }

        final Transform view = new Transform();
        //make the view originate from the ray origin
        view.postTranslate(ox, oy, oz);
        //make the view z point along the ray
        {
            //normalize
            {
                final float invNorm = 1.0f / mag(dx, dy, dz);
                dx *= invNorm;
                dy *= invNorm;
                dz *= invNorm;
            }
            //axis is (dx, dy, dz) cross (0, 0, 1)
            float ax = dy;
            float ay = -dx;
            float az = 0;
            {
                final float norm = mag(ax, ay, az);
                if (norm < 0.001f)
                {
                    //replace with the up vector
                    ax = 0;
                    ay = 1;
                    az = 0;
                }
                else
                {
                    final float invNorm = 1.0f / norm;
                    ax *= invNorm;
                    ay *= invNorm;
                    az *= invNorm;
                }
            }
            final float c = (float) Math.sqrt(dz * 0.5 + 0.5);
            final float s = (float) Math.sqrt(1 - (c * c));
            view.postRotateQuat(ax * s, ay * s, az * s, c);
        }

        //quick check against the picking bounding sphere
        {
            final float[] sphere = updatePickingBoundingSphere();
            if (!rayIntersectsSphere(view, sphere))
            {
                return false;
            }
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
