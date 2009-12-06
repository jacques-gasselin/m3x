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
    private int type;

    /**
     * Package protected constructor to allow uninitialised construction
     * in Loader.
     */
    IndexBuffer()
    {
    }

    public IndexBuffer(int type, int[] stripLengths, int firstIndex)
    {
        setType(type);
        
        throw new UnsupportedOperationException();
    }

    public IndexBuffer(int type, int[] stripLengths, int[] indices)
    {
        setType(type);
        
        throw new UnsupportedOperationException();
    }

    public IndexBuffer(int type, int primitiveCount, int firstIndex)
    {
        setType(type);

        throw new UnsupportedOperationException();
    }

    public IndexBuffer(int type, int primitiveCount, int[] indices)
    {
        setType(type);
        
        throw new UnsupportedOperationException();
    }

    void setType(int type)
    {
        if (type < TRIANGLES)
        {
            throw new IllegalArgumentException("type < TRIANGLES");
        }
        if (type > POINT_SPRITES)
        {
            throw new IllegalArgumentException("type > POINT_SPRITES");
        }
        
        this.type = type;
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

    public boolean isReadable()
    {
        return this.readable;
    }
}
