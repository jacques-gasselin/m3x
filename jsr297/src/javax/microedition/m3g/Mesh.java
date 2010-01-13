/*
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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
public class Mesh extends Node
{
    private AppearanceBase[] appearances;
    private IndexBuffer[] indexBuffers;
    private int submeshCount;
    private VertexBuffer vertexBuffer;
    private int morphTargetCount;

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

        setVertexBuffer(vertices);
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
        throw new UnsupportedOperationException();
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