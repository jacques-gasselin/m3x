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

/**
 * @author jgasseli
 */
public class SkinnedMesh extends Mesh
{
    private Group skeleton;
    private Node[] bones;
    private Transform[] restPoseTransforms;
    private boolean isLegacy = false;
    
    SkinnedMesh()
    {
        super();
    }
    
    public SkinnedMesh(int numSubmeshes, int numMorphTargets, Group skeleton)
    {
        super(numSubmeshes, numMorphTargets);
        this.skeleton = skeleton;
    }
    
    @Deprecated()
    public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances, Group skeleton)
    {
        super(vertices, submeshes, appearances);
        this.skeleton = skeleton;
        this.isLegacy = true;
    }

    @Deprecated()
    public SkinnedMesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance, Group skeleton)
    {
        super(vertices, submesh, appearance);
        this.skeleton = skeleton;
        this.isLegacy = true;
    }
    
    boolean isLegacy()
    {
        return isLegacy;
    }
    
    void setLegacy(boolean b)
    {
        isLegacy = b;
    }
    
    void setSkeleton(Group skeleton)
    {
        Require.notNull(skeleton, "skeleton");
        if (this.skeleton != null)
        {
            throw new IllegalStateException("already has a skeleton");
        }
        this.skeleton = skeleton;
    }
    
    @Deprecated
    public void addTransform(Node bone, int weight, int firstVertex, int numVertices)
    {
        Require.notNull(bone, "bone");
        Require.argumentGreaterThanZero(weight, "weight");
        Require.argumentGreaterThanZero(numVertices, "numVertices");
        Require.indexNotNegative(firstVertex, "firstVertex");
        Require.indexInRange(firstVertex + numVertices, "firstVertex + numVertices", 65535);
        
        if (!isLegacy)
        {
            throw new IllegalStateException("not a legacy skinned mesh");
        }
    }
    
    public Node[] getBones()
    {
        return this.bones;
    }
    
    public void getBoneTransform(Node bone, Transform transform)
    {
        Require.notNull(bone, "bone");
        Require.notNull(transform, "transform");
        
        for (int i = 0; i < bones.length; ++i)
        {
            if (bone == bones[i])
            {
                transform.set(restPoseTransforms[i]);
                return;
            }
        }
        
        throw new IllegalArgumentException("bone is not in skeleton group");
    }
    
    @Deprecated
    public int getBoneVertices(Node bone, int[] indices, float[] weights)
    {
        Require.notNull(bone, "bone");
        
        if (!isLegacy)
        {
            throw new IllegalStateException("not a legacy skinned mesh");
        }
        
        return 0;
    }
    
    public Group getSkeleton()
    {
        return skeleton;
    }
    
    void setBones(Node[] bones)
    {
        Require.notNull(bones, "bones");
        
        this.restPoseTransforms = new Transform[bones.length];

        for (int i = 0; i < bones.length; ++i)
        {
            if (!getTransformTo(bones[i], restPoseTransforms[i]))
            {
                throw new IllegalArgumentException("bone not in the skeleton group");
            }
        }

        this.bones = new Node[bones.length];
        System.arraycopy(bones, 0, this.bones, 0, bones.length);
    }
}
