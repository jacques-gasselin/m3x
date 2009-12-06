/*
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

package javax.microedition.m3g;

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
        
        throw new UnsupportedOperationException();
    }

    public IndexBuffer(int type, int[] stripLengths, int[] indices)
    {
        setPrimitiveType(type);
        
        throw new UnsupportedOperationException();
    }

    public IndexBuffer(int type, int primitiveCount, int firstIndex)
    {
        setPrimitiveType(type);

        throw new UnsupportedOperationException();
    }

    public IndexBuffer(int type, int primitiveCount, int[] indices)
    {
        setPrimitiveType(type);
        
        throw new UnsupportedOperationException();
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

    void setPrimitiveType(int type)
    {
        //require immutability
        if (getPrimitiveType() != 0)
        {
            throw new IllegalStateException("type is already set");
        }

        if (type < TRIANGLES)
        {
            throw new IllegalArgumentException("type < TRIANGLES");
        }
        if (type > POINT_SPRITES)
        {
            throw new IllegalArgumentException("type > POINT_SPRITES");
        }
        
        this.primitiveType = type;
    }

    void setStripLengths(int[] stripLengths)
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

        if (stripLengths == null)
        {
            throw new NullPointerException("stripLengths is null");
        }
        if (stripLengths.length == 0)
        {
            throw new IllegalArgumentException("stripLengths is empty");
        }

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

        this.stripLengths = stripLengths;
    }

    boolean isStripped()
    {
        return this.stripLengths != null;
    }

    public void commit()
    {
        throw new UnsupportedOperationException();
    }

    public int getIndexCount()
    {
        throw new UnsupportedOperationException();
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
