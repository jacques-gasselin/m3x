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
public class VertexArray extends Object3D
{

    public static final int BYTE = 1;
    public static final int SHORT = 2;
    public static final int FIXED = 3;
    public static final int FLOAT = 4;
    public static final int HALF = 5;

    private int vertexCount;
    private int componentCount;
    private int componentType;
    
    public VertexArray(int numVertices, int numComponents, int componentType)
    {
    }

    public void get(int firstVertex, int numVertices, byte[] dst)
    {
        throw new UnsupportedOperationException();
    }

    public void get(int firstVertex, int numVertices, float[] dst)
    {
        throw new UnsupportedOperationException();
    }
    
    public void get(int firstVertex, int numVertices, int[] dst)
    {
        throw new UnsupportedOperationException();
    }

    public void get(int firstVertex, int numVertices, short[] dst)
    {
        throw new UnsupportedOperationException();
    }

    public int getComponentCount()
    {
        return componentCount;
    }

    public int getComponentType()
    {
        return componentType;
    }

    public int getVertexCount()
    {
        return vertexCount;
    }

    public void set(int firstVertex, int numVertices, byte[] src)
    {
        throw new UnsupportedOperationException();
    }

    public void set(int firstVertex, int numVertices, float[] src)
    {
        throw new UnsupportedOperationException();
    }

    public void set(int firstVertex, int numVertices, int[] src)
    {
        throw new UnsupportedOperationException();
    }

    public void set(int firstVertex, int numVertices, short[] src)
    {
        throw new UnsupportedOperationException();
    }
}
