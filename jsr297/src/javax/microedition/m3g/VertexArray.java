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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

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

    private Buffer buffer;

    VertexArray()
    {
        //leave it uninitialised.
        //init later using set()
    }
    
    public VertexArray(int numVertices, int numComponents, int componentType)
    {
        set(numVertices, numComponents, componentType);
    }

    void set(int numVertices, int numComponents, int componentType)
    {
        vertexCount = numVertices;
        componentCount = numComponents;
        this.componentType = componentType;
        
        buffer = allocate(getVertexCount(), getComponentCount(), getComponentType());
    }

    private static final Buffer allocate(int numVertices, int numComponents, int componentType)
    {
        final int componentByteSize;
        switch (componentType)
        {
            case BYTE:
                componentByteSize = 1;
                break;
            case SHORT:
            case HALF:
                componentByteSize = 2;
                break;
            case FIXED:
            case FLOAT:
                componentByteSize = 4;
                break;
            default:
                componentByteSize = 0;
                throw new IllegalArgumentException("invalid componentType");
        }

        final int capacity = numVertices * numComponents * componentByteSize;
        //ensure we get a direct native buffer
        ByteBuffer ret = ByteBuffer.allocateDirect(capacity);
        ret.order(ByteOrder.nativeOrder());

        switch (componentType)
        {
            case SHORT:
            case HALF:
                return ret.asShortBuffer();
            case FIXED:
                return ret.asIntBuffer();
            case FLOAT:
                return ret.asFloatBuffer();
            default:
                return ret;
        }
    }

    private static final void requireNotNull(Object obj, String name)
    {
        if (obj == null)
        {
            throw new NullPointerException(name + " is null");
        }
    }

    private final void requireValidIndexAndLength(int firstVertex, int numVertices, int length)
    {
        if (numVertices < 0)
        {
            throw new IllegalArgumentException("numVertices < 0");
        }
        if (length < numVertices * getComponentCount())
        {
            throw new IllegalArgumentException("length < numVertices * getComponentCount()");
        }
        if (firstVertex < 0)
        {
            throw new IndexOutOfBoundsException("firstVertex < 0");
        }
        if (firstVertex + numVertices > getVertexCount())
        {
            throw new IndexOutOfBoundsException("firstVertex + numVertices > getVertexCount()");
        }
    }

    private final void requireByteType()
    {
        if (getComponentType() != BYTE)
        {
            throw new IllegalStateException("componentType is not BYTE");
        }
    }

    private final void requireShortType()
    {
        if (getComponentType() != SHORT && getComponentType() != HALF)
        {
            throw new IllegalStateException("componentType is not one of [SHORT, HALF]");
        }
    }

    private final void requireFixedType()
    {
        if (getComponentType() != FIXED)
        {
            throw new IllegalStateException("componentType is not FIXED");
        }
    }

    private final void requireFloatType()
    {
        if (getComponentType() != FLOAT && getComponentType() != HALF)
        {
            throw new IllegalStateException("componentType is not one of [FLOAT, HALF]");
        }
    }

    private final ByteBuffer positionByteBuffer(int firstVertex)
    {
        ByteBuffer buf = (ByteBuffer) buffer;
        buf.position(firstVertex * getComponentCount());
        return buf;
    }

    private final ShortBuffer positionShortBuffer(int firstVertex)
    {
        ShortBuffer buf = (ShortBuffer) buffer;
        buf.position(firstVertex * getComponentCount());
        return buf;
    }

    private final IntBuffer positionIntBuffer(int firstVertex)
    {
        IntBuffer buf = (IntBuffer) buffer;
        buf.position(firstVertex * getComponentCount());
        return buf;
    }

    private final FloatBuffer positionFloatBuffer(int firstVertex)
    {
        FloatBuffer buf = (FloatBuffer) buffer;
        buf.position(firstVertex * getComponentCount());
        return buf;
    }
    
    public void get(int firstVertex, int numVertices, byte[] dst)
    {
        requireNotNull(dst, "dst");
        requireByteType();
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        ByteBuffer buf = positionByteBuffer(firstVertex);
        buf.get(dst, 0, numVertices * getComponentCount());
    }

    public void get(int firstVertex, int numVertices, float[] dst)
    {
        requireNotNull(dst, "dst");
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        if (getComponentType() == FLOAT)
        {
            //no conversion
            FloatBuffer buf = positionFloatBuffer(firstVertex);
            buf.get(dst, 0, numVertices * getComponentCount());
        }
        else
        {
            //convert to float
            throw new UnsupportedOperationException();
        }
    }
    
    public void get(int firstVertex, int numVertices, int[] dst)
    {
        requireNotNull(dst, "dst");
        requireFixedType();
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        IntBuffer buf = positionIntBuffer(firstVertex);
        buf.get(dst, 0, numVertices * getComponentCount());
    }

    public void get(int firstVertex, int numVertices, short[] dst)
    {
        requireNotNull(dst, "dst");
        requireShortType();
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        ShortBuffer buf = positionShortBuffer(firstVertex);
        buf.get(dst, 0, numVertices * getComponentCount());
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
        requireNotNull(src, "src");
        requireByteType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);

        ByteBuffer buf = positionByteBuffer(firstVertex);
        buf.put(src, 0, numVertices * getComponentCount());
    }

    public void set(int firstVertex, int numVertices, float[] src)
    {
        requireNotNull(src, "src");
        requireFloatType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);

        if (getComponentType() == FLOAT)
        {
            FloatBuffer buf = positionFloatBuffer(firstVertex);
            buf.put(src, 0, numVertices * getComponentCount());
        }
        else
        {
            throw new UnsupportedOperationException("HALF not supported yet");
        }
    }

    public void set(int firstVertex, int numVertices, int[] src)
    {
        requireNotNull(src, "src");
        requireFixedType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);
        
        IntBuffer buf = positionIntBuffer(firstVertex);
        buf.put(src, 0, numVertices * getComponentCount());
    }

    public void set(int firstVertex, int numVertices, short[] src)
    {
        requireNotNull(src, "src");
        requireShortType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);
        
        throw new UnsupportedOperationException();
    }
}
