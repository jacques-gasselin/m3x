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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * @author jgasseli
 */
public class IndexBuffer extends Object3D
{
    public static final int TRIANGLES = 8;
    public static final int LINES = 9;
    public static final int POINT_SPRITES = 10;

    private boolean readable = true;
    private int primitiveType;

    private int firstIndex = -1;
    private int primitiveCount;
    private ShortBuffer indices;
    private int[] stripLengths;

    /**
     * Package protected constructor to allow uninitialised construction
     * in Loader.
     */
    IndexBuffer()
    {
    }

    public IndexBuffer(int type, int[] stripLengths, int firstIndex)
    {
        setPrimitiveType(type);
        setStripLengths(stripLengths);
        final int indexCount = sum(stripLengths);
        setImplicitIndex(firstIndex, indexCount);
    }

    public IndexBuffer(int type, int[] stripLengths, int[] indices)
    {
        setPrimitiveType(type);
        setStripLengths(stripLengths);
        final int indexCount = sum(stripLengths);
        setExplicitIndices(indices, indexCount);
    }

    public IndexBuffer(int type, int primitiveCount, int firstIndex)
    {
        setPrimitiveType(type);
        setPrimitiveCount(primitiveCount);
        final int indexCount = getIndicesPerPrimitive(type) * primitiveCount;
        setImplicitIndex(firstIndex, indexCount);
    }

    public IndexBuffer(int type, int primitiveCount, int[] indices)
    {
        setPrimitiveType(type);
        setPrimitiveCount(primitiveCount);
        final int indexCount = getIndicesPerPrimitive(type) * primitiveCount;
        setExplicitIndices(indices, indexCount);
    }

    private static final int sum(int[] stripLengths)
    {
        int result = 0;
        for (int l : stripLengths)
        {
            result += l;
        }
        return result;
    }

    private static final int getIndicesPerPrimitive(int type)
    {
        switch (type)
        {
            case TRIANGLES:
                return 3;
            case LINES:
                return 2;
            case POINT_SPRITES:
                return 1;
            default:
                throw new IllegalArgumentException("unknown type");
        }
    }

    final void setPrimitiveType(int type)
    {
        //require immutability
        if (getPrimitiveType() != 0)
        {
            throw new IllegalStateException("type is already set");
        }

        Require.argumentInEnum(type, "type", TRIANGLES, POINT_SPRITES);
        
        this.primitiveType = type;
    }

    final void setPrimitiveCount(int primitiveCount)
    {
        //require immutability
        if (isStripped())
        {
            throw new IllegalStateException("stripping is already set");
        }
        if (primitiveCount < 0)
        {
            throw new IndexOutOfBoundsException("primitiveCount < 0");
        }

        this.primitiveCount = primitiveCount;
    }

    final void setStripLengths(int[] stripLengths)
    {
        //require immutability
        if (this.stripLengths != null)
        {
            throw new IllegalStateException("stripLengths are already set");
        }
        //require type to be set first
        if (getPrimitiveType() == 0)
        {
            throw new IllegalStateException("type is not set");
        }
        if (getPrimitiveType() == POINT_SPRITES)
        {
            throw new IllegalStateException("POINT_SPRITES can not be stripped");
        }

        Require.argumentNotEmpty(stripLengths, "stripLengths");

        //require valid strip lengths
        final int indicesPerPrimitive = getIndicesPerPrimitive(getPrimitiveType());
        final int count = stripLengths.length;
        for (int i = 0; i < count; ++i)
        {
            if (stripLengths[i] < indicesPerPrimitive)
            {
                throw new IllegalArgumentException("stripLengths[" + i + "] is " +
                        stripLengths[i] + ", less than the required " +
                        indicesPerPrimitive);
            }
        }

        //copy it first
        final int[] lengths = new int[stripLengths.length];
        System.arraycopy(stripLengths, 0, lengths, 0, lengths.length);
        this.stripLengths = lengths;
    }

    void setImplicitIndex(int firstIndex, int indexCount)
    {
        //verify immutability
        if (isExplicit())
        {
            throw new IllegalStateException("explicit indices are already set");
        }
        if (firstIndex < 0)
        {
            throw new IndexOutOfBoundsException("firstIndex < 0");
        }
        if (firstIndex + indexCount > 65536)
        {
            throw new IndexOutOfBoundsException("firstIndex + indexCount > 65536");
        }

        this.firstIndex = firstIndex;
    }

    void setExplicitIndices(int[] indices, int indexCount)
    {
        //verify immutability
        if (isImplicit())
        {
            throw new IllegalStateException("implicit index already set");
        }

        Require.argumentNotEmpty(indices, "indices");
        
        if (indices.length < indexCount)
        {
            throw new IndexOutOfBoundsException("indices.length < indexCount");
        }

        //allocate a buffer and copy in
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indexCount * 2);
        //ensure native byte order
        byteBuffer.order(ByteOrder.nativeOrder());
        
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        for (int i = 0; i < indexCount; ++i)
        {
            final int index = indices[i];
            if (index < 0)
            {
                throw new IndexOutOfBoundsException("indices[" + i + "] is negative");
            }
            if (index > 65535)
            {
                throw new IndexOutOfBoundsException("indices[" + i + "] is greater" +
                        " than 65535");
            }
            shortBuffer.put((short)index);
        }
        shortBuffer.flip();

        this.indices = shortBuffer;
    }

    final boolean isImplicit()
    {
        return this.firstIndex != -1;
    }

    final boolean isExplicit()
    {
        return this.indices != null;
    }

    final boolean isStripped()
    {
        return this.stripLengths != null;
    }

    final int getFirstIndex()
    {
        return this.firstIndex;
    }

    final ShortBuffer getIndexBuffer()
    {
        return this.indices.rewind();
    }

    final int[] getStripLengths()
    {
        return this.stripLengths;
    }

    final int getPrimitiveCount()
    {
        return this.primitiveCount;
    }

    public void commit()
    {
        throw new UnsupportedOperationException();
    }

    public int getIndexCount()
    {
        if (isStripped())
        {
            return sum(stripLengths);
        }
        else
        {
            return getIndicesPerPrimitive(primitiveType) * primitiveCount;
        }
    }

    public void getIndices(int[] indices)
    {
        throw new UnsupportedOperationException();
    }

    public int getPrimitiveType()
    {
        return this.primitiveType;
    }

    public boolean isReadable()
    {
        return this.readable;
    }
}
