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

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * @author jgasseli
 */
public class VertexBuffer extends Object3D
{
    private static final class VertexCounter
    {
        private int count;
        private final IdentityHashMap<VertexArray, VertexArray> arrays =
                new IdentityHashMap<VertexArray, VertexArray>();

        private VertexCounter()
        {

        }

        private int getCount()
        {
            return this.count;
        }

        private void replace(VertexArray oldVA, VertexArray newVA)
        {
            remove(oldVA);
            add(newVA);
        }

        private void remove(VertexArray va)
        {
            //no-op for null arrays
            if (va == null)
            {
                return;
            }

            //is this vertex array in the buffer?
            if (this.arrays.remove(va) == null)
            {
                throw new IllegalStateException("va does not belong to the counter");
            }
            if (this.count == 0)
            {
                throw new IllegalStateException("va was never counted");
            }
            if (this.count != va.getVertexCount())
            {
                throw new IllegalStateException("va.getVertexCount() != getVertexCount()");
            }

            //is the counter empty now?
            if (this.arrays.size() == 0)
            {
                this.count = 0;
            }
        }

        private void add(VertexArray va)
        {
            //no-op for null arrays
            if (va == null)
            {
                return;
            }

            final int vaCount = va.getVertexCount();
            //is this vertex array in the buffer?
            if (this.arrays.containsKey(va) && vaCount != this.count)
            {
                throw new IllegalStateException("va already belongs to the counter" +
                        " yet the vertex count does not match up."
                        + this.arrays);
            }
            //is it the first vertex array?
            if (this.count == 0)
            {
                if (this.arrays.size() != 0)
                {
                    throw new IllegalStateException("current count is incorrect");
                }

                this.count = vaCount;
            }
            else if (this.count != vaCount)
            {
                throw new IllegalArgumentException("va.getVertexCount() != getVertexCount()");
            }

            this.arrays.put(va, va);
        }
    }

    private static final class ScaleBiasedVertexArray
    {
        private VertexArray array;
        private float scale;
        private final float[] bias = new float[3];

        public VertexArray getArray()
        {
            return this.array;
        }

        public float[] getBias()
        {
            return this.bias;
        }

        public float getScale()
        {
            return this.scale;
        }

        private static final void requireArrayNotNull(VertexArray array)
        {
            if (array == null)
            {
                throw new NullPointerException("array is null");
            }
        }

        private static final void requireValidScaleAndBias(float[] scaleBias, VertexArray array)
        {
            if (array == null || scaleBias == null)
            {
                return;
            }

            if (scaleBias.length < Math.min(4, array.getComponentCount() + 1))
            {
                throw new IllegalArgumentException("scaleBias.length < min(4, array.getComponentCount() + 1)");
            }
        }

        private static final void requireValidBias(float[] bias, VertexArray array)
        {
            if (array == null || bias == null)
            {
                return;
            }

            if (bias.length < Math.min(3, array.getComponentCount()))
            {
                throw new IllegalArgumentException("bias.length < min(3, array.getComponentCount())");
            }
        }

        public void set(VertexArray array, float scale, float[] bias)
        {
            //apply the tests early to assert atomicity
            requireValidBias(bias, array);

            setArray(array);
            setScale(scale);
            setBias(bias);
        }

        public void setArray(VertexArray array)
        {
            this.array = array;
        }

        public void setBias(float[] bias)
        {
            requireValidBias(bias, this.array);
            
            Arrays.fill(this.bias, 0.0f);
            if (bias != null && this.array != null)
            {
                System.arraycopy(bias, 0,
                        this.bias, 0,
                        Math.min(3, array.getComponentCount()));
            }
        }

        public void setScale(float scale)
        {
            this.scale = scale;
        }

        private void getScaleAndBias(float[] scaleBias)
        {
            if (this.array != null)
            {
                requireValidScaleAndBias(scaleBias, this.array);

                if (scaleBias != null)
                {
                    scaleBias[0] = this.scale;
                    System.arraycopy(this.bias, 0,
                            scaleBias, 1,
                            Math.min(3, this.array.getComponentCount()));
                }
            }
        }
    }

    private boolean mutable = true;
    private int defaultColorARGB = 0xffffffff;
    private float defaultPointSize = 1.0f;
    private final VertexCounter vertexCounter = new VertexCounter();
    private final ScaleBiasedVertexArray positions = new ScaleBiasedVertexArray();
    private VertexArray normals;
    private VertexArray colors;
    private VertexArray pointSizes;
    private VertexArray boneWeights;
    private VertexArray boneIndices;
    private static final int MAX_TEXTURE_COORDS = 8;
    private final ScaleBiasedVertexArray[] textureCoordinates =
            new ScaleBiasedVertexArray[MAX_TEXTURE_COORDS];

    public VertexBuffer()
    {
        for (int i = 0; i < MAX_TEXTURE_COORDS; ++i)
        {
            textureCoordinates[i] = new ScaleBiasedVertexArray();
        }
    }

    private final void requireMutable()
    {
        if (!isMutable())
        {
            throw new IllegalStateException("this bufferer is immutable");
        }
    }

    private final void requireValidTextureIndex(int index)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException("index < 0");
        }
        if (index >= MAX_TEXTURE_COORDS)
        {
            throw new IndexOutOfBoundsException("index >= N, the implementation maximum");
        }
    }

    public void commit()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    void duplicate(Object3D target)
    {
        super.duplicate(target);

        final VertexBuffer vb = (VertexBuffer) target;
        //TODO set attributes and bones
        vb.setColors(getColors());
        vb.setDefaultColor(getDefaultColor());
        vb.setDefaultPointSize(getDefaultPointSize());
        vb.setNormals(getNormals());
        vb.setPointSizes(getPointSizes());
        final float[] scaleBias = new float[4];
        final float[] bias = new float[3];
        
        {
            final VertexArray pos = getPositions(scaleBias);
            final float scale = scaleBias[0];
            System.arraycopy(scaleBias, 1, bias, 0, 3);
            vb.setPositions(pos, scale, bias);
        }
        for (int i = 0; i < MAX_TEXTURE_COORDS; ++i)
        {
            //the fill is needed because there may be garbage values on the
            //end that aren't set by the following get if the component count < 3
            Arrays.fill(scaleBias, 0.0f);
            final VertexArray texCoords = getTexCoords(i, scaleBias);
            final float scale = scaleBias[0];
            System.arraycopy(scaleBias, 1, bias, 0, 3);
            vb.setTexCoords(i, texCoords, scale, bias);
        }
    }

    public VertexArray getAttribute(String name, boolean signedNormalized[],
            float[] defaultValue)
    {
        throw new UnsupportedOperationException();
    }

    public int getAttributeCount()
    {
        throw new UnsupportedOperationException();
    }

    public String[] getAttributeNames()
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getBoneIndices()
    {
        return this.boneIndices;
    }

    public VertexArray getBoneWeights()
    {
        return this.boneWeights;
    }

    public VertexArray getColors()
    {
        return this.colors;
    }

    public int getDefaultColor()
    {
        return this.defaultColorARGB;
    }

    public float getDefaultPointSize()
    {
        return this.defaultPointSize;
    }

    public VertexArray getNormals()
    {
        return this.normals;
    }

    public VertexArray getPointSizes()
    {
        return this.pointSizes;
    }

    public VertexArray getPositions(float[] scaleBias)
    {
        this.positions.getScaleAndBias(scaleBias);
        
        return this.positions.getArray();
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);
        
        if (positions.getArray() != null)
        {
            references.add(positions.getArray());
        }
        if (normals != null)
        {
            references.add(normals);
        }
        if (colors != null)
        {
            references.add(colors);
        }
        if (pointSizes != null)
        {
            references.add(pointSizes);
        }
        if (boneWeights != null)
        {
            references.add(boneWeights);
        }
        if (boneIndices != null)
        {
            references.add(boneIndices);
        }

        for (ScaleBiasedVertexArray t : textureCoordinates)
        {
            if (t != null && t.getArray() != null)
            {
                references.add(t.getArray());
            }
        }
    }

    public VertexArray getTexCoords(int index, float[] scaleBias)
    {
        requireValidTextureIndex(index);

        ScaleBiasedVertexArray sbva = this.textureCoordinates[index];

        sbva.getScaleAndBias(scaleBias);

        return sbva.getArray();
    }

    public int getVertexCount()
    {
        return this.vertexCounter.getCount();
    }

    public boolean isMutable()
    {
        return this.mutable;
    }

    public void setAttribute(String name, float v0, float v1, float v2, float v3)
    {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(String name, VertexArray attributes, boolean signed, boolean normalized)
    {
        throw new UnsupportedOperationException();
    }

    public void setBoneInfluences(VertexArray boneIndices, VertexArray boneWeights)
    {
        requireMutable();

        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.boneIndices, boneIndices);
        this.vertexCounter.replace(this.boneWeights, boneWeights);
        
        this.boneIndices = boneIndices;
        this.boneWeights = boneWeights;
    }

    public void setColors(VertexArray colors)
    {
        requireMutable();

        if (colors != null)
        {
            final int componentCount = colors.getComponentCount();
            if (componentCount < 3)
            {
                throw new IllegalArgumentException("colors.getComponentCount() < 3");
            }
            if (componentCount > 4)
            {
                throw new IllegalArgumentException("colors.getComponentCount() > 4");
            }
        }
        
        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.colors, colors);

        this.colors = colors;
    }

    public void setDefaultColor(int argb)
    {
        this.defaultColorARGB = argb;
    }

    public void setDefaultPointSize(float pointSize)
    {
        if (pointSize <= 0)
        {
            throw new IllegalArgumentException("pointSize <= 0");
        }
        
        this.defaultPointSize = pointSize;
    }

    public void setNormals(VertexArray normals)
    {
        requireMutable();
        
        if (normals != null && normals.getComponentCount() != 3)
        {
            throw new IllegalArgumentException("normals.getComponentCount() != 3");
        }

        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.normals, normals);

        this.normals = normals;
    }

    public void setPointSizes(VertexArray pointSizes)
    {
        requireMutable();

        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.pointSizes, pointSizes);
        
        this.pointSizes = pointSizes;
    }

    public synchronized void setPositions(VertexArray positions, float scale, float[] bias)
    {
        requireMutable();
        
        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.positions.getArray(), positions);

        this.positions.set(positions, scale, bias);
    }

    public void setTexCoords(int index, VertexArray texCoords, float scale, float[] bias)
    {
        requireMutable();
        requireValidTextureIndex(index);

        ScaleBiasedVertexArray sbva = this.textureCoordinates[index];
        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(sbva.getArray(), texCoords);

        sbva.set(texCoords, scale, bias);
    }
}
