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
import java.util.List;

/**
 * @author jgasseli
 */
public class Mesh extends Node
{
    private AppearanceBase[] appearances;
    private IndexBuffer[] indexBuffers;
    private int submeshCount;
    private VertexBuffer vertexBuffer;
    private int morphTargetCount;
    private int morphSubsetSize;

    Mesh()
    {
        
    }

    /**
     *
     * @param numSubmeshes
     * @param numMorphTargets
     * @since M3G 2.0
     */
    public Mesh(int numSubmeshes, int numMorphTargets)
    {
        setSubmeshCount(numSubmeshes);
        setMorphTargetCount(numMorphTargets);
    }

    /**
     *
     * @param vertices
     * @param submeshes
     * @param appearances
     * @deprecated
     */
    @Deprecated
    public Mesh(VertexBuffer vertices, IndexBuffer[] submeshes,
            Appearance[] appearances)
    {
        Require.notNull(vertices, "vertices");
        Require.argumentNotEmpty(submeshes, "submeshes");
        for (IndexBuffer submesh : submeshes)
        {
            if (submesh == null)
            {
                throw new NullPointerException("an element in submeshes is null");
            }
        }
        if ((appearances != null) && (appearances.length < submeshes.length))
        {
            throw new IllegalArgumentException("appearances is not null yet" +
                    "of mismatched length");
        }

        setVertexBuffer(vertices);

        final int count = submeshes.length;
        setSubmeshCount(count);
        for (int i = 0; i < count; ++i)
        {
            setIndexBuffer(i, submeshes[i]);
            if (appearances != null)
            {
                setAppearance(i, appearances[i]);
            }
        }
    }

    /**
     * 
     * @param vertices
     * @param submesh
     * @param appearance
     * @deprecated
     */
    @Deprecated
    public Mesh(VertexBuffer vertices, IndexBuffer submesh,
            Appearance appearance)
    {
        Require.notNull(vertices, "vertices");
        Require.notNull(submesh, "submesh");

        setSubmeshCount(1);
        setVertexBuffer(vertices);
        setIndexBuffer(0, submesh);
        setAppearance(0, appearance);
    }

    @Override
    void duplicate(Object3D target)
    {
        super.duplicate(target);

        final Mesh m = (Mesh) target;
        m.setVertexBuffer(getVertexBuffer());
        {
            final int count = getSubmeshCount();
            m.setSubmeshCount(count);
            for (int i = 0; i < count; ++i)
            {
                m.setIndexBuffer(i, getIndexBuffer(i));
                m.setAppearanceBase(i, getAppearanceBase(i));
            }
        }
        {
            final int count = getMorphTargetCount();
            m.setMorphTargetCount(count);
            for (int i = 0; i < count; ++i)
            {
                m.setMorphTarget(i, getMorphTarget(i));
            }
            final int size = getMorphSubset(null);
            if (size > 0)
            {
                final int[] indices = new int[size];
                getMorphSubset(indices);
                m.setMorpSubset(size, indices);
            }
        }
    }
    
    public Appearance getAppearance(int index)
    {
        final AppearanceBase a = getAppearanceBase(index);
        if (a instanceof Appearance)
        {
            return (Appearance) a;
        }
        return null;
    }

    AppearanceBase getAppearanceBase(int index)
    {
        Require.indexInRange(index, getSubmeshCount());

        return this.appearances[index];
    }

    public IndexBuffer getIndexBuffer(int index)
    {
        Require.indexInRange(index, getSubmeshCount());
        
        return this.indexBuffers[index];
    }

    public int getMorphSubset(int[] morphIndices)
    {
        if (morphIndices != null)
        {
            throw new UnsupportedOperationException();
        }
        return this.morphSubsetSize;
    }

    public VertexBuffer getMorphTarget(int index)
    {
        Require.indexInRange(index, getMorphTargetCount());
        
        throw new UnsupportedOperationException();
    }

    public int getMorphTargetCount()
    {
        return this.morphTargetCount;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);
        if (vertexBuffer != null)
        {
            references.add(vertexBuffer);
        }
        for (AppearanceBase appearance : appearances)
        {
            if (appearance != null)
            {
                references.add(appearance);
            }
        }
        for (IndexBuffer indexBuffer : indexBuffers)
        {
            if (indexBuffer != null)
            {
                references.add(indexBuffer);
            }
        }
    }

    public ShaderAppearance getShaderAppearance(int index)
    {
        final AppearanceBase a = getAppearanceBase(index);
        if (a instanceof ShaderAppearance)
        {
            return (ShaderAppearance) a;
        }
        return null;
    }

    public int getSubmeshCount()
    {
        return this.submeshCount;
    }

    public VertexBuffer getVertexBuffer()
    {
        return this.vertexBuffer;
    }

    public void getWeights(float[] weights)
    {
        Require.argumentHasCapacity(weights, "weights", getMorphTargetCount());
        
        throw new UnsupportedOperationException();
    }

    public void setAppearance(int index, Appearance appearance)
    {
        setAppearanceBase(index, appearance);
    }

    void setAppearanceBase(int index, AppearanceBase appearance)
    {
        Require.indexInRange(index, getSubmeshCount());

        this.appearances[index] = appearance;
    }

    public void setIndexBuffer(int index, IndexBuffer submesh)
    {
        Require.indexInRange(index, getSubmeshCount());

        this.indexBuffers[index] = submesh;
    }

    public void setMorpSubset(int morphSubsetSize, int[] morphIndices)
    {
        throw new UnsupportedOperationException();
    }

    public void setMorphTarget(int index, VertexBuffer target)
    {
        Require.indexInRange(index, getMorphTargetCount());

        throw new UnsupportedOperationException();
    }

    final void setMorphTargetCount(int numMorphTargets)
    {
        Require.argumentNotNegative(numMorphTargets, "numMorphTargets");

        this.morphTargetCount = numMorphTargets;
    }

    public void setShaderAppearance(int index, ShaderAppearance appearance)
    {
        Require.indexInRange(index, getSubmeshCount());

        this.appearances[index] = appearance;
    }
    
    final void setSubmeshCount(int numSubmeshes)
    {
        Require.argumentGreaterThanZero(numSubmeshes, "numSubmeshes");

        this.appearances = new Appearance[numSubmeshes];
        this.indexBuffers = new IndexBuffer[numSubmeshes];
        this.submeshCount = numSubmeshes;
    }

    public void setVertexBuffer(VertexBuffer vertices)
    {
        this.vertexBuffer = vertices;
    }

    public void setWeights(float[] weights)
    {
        throw new UnsupportedOperationException();
    }
}
