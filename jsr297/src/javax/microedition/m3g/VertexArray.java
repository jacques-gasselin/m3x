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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * An array of vertex attributes in byte, short, fixed, half, or float components.
 *
 * VertexArrays are used by VertexBuffer objects to supply the data for a desired
 * semantic. The same VertexArray may be shared among many VertexBuffer objects or
 * even the same one multiple times.
 *
 * @author jgasseli
 */
public class VertexArray extends Object3D
{
    /**
     * Use 8bit components.
     */
    public static final int BYTE = 1;
    public static final int SHORT = 2;
    public static final int FIXED = 3;
    public static final int FLOAT = 4;
    public static final int HALF = 5;

    private int vertexCount;
    private int componentCount;
    private int componentType;

    private ByteBuffer directBuffer;
    private Buffer directBufferView;
    /**Used for buffers that may be sparse*/
    private int vertexByteStride;
    private boolean bufferIsSparse;

    /**
     * Package protected default constructor used for Deserialising.
     *
     */
    VertexArray()
    {
        //leave it uninitialised.
        //init later using set()
    }

    /**
     * Constructs an empty VertexArray  with the given number of vertices and
     * components of the desired type.
     * 
     * @param numVertices the number of vertices contained in the array.
     * @param numComponents the number of components per vertex
     * @param componentType the data type of each component
     */
    public VertexArray(int numVertices, int numComponents, int componentType)
    {
        set(numVertices, numComponents, componentType);
    }

    private static final int getComponentByteSize(int componentType)
    {
        switch (componentType)
        {
            case BYTE:
                return 1;
            case SHORT:
            case HALF:
                return 2;
            case FIXED:
            case FLOAT:
                return 4;
            default:
                throw new IllegalArgumentException("invalid componentType");
        }
    }

    void set(int numVertices, int numComponents, int componentType)
    {
        vertexCount = numVertices;
        componentCount = numComponents;
        this.componentType = componentType;

        //tightly packed array
        bufferIsSparse = false;
        vertexByteStride = getComponentCount() * getComponentByteSize(componentType);
        directBuffer = allocate(getVertexCount(), getVertexByteStride(), getComponentType());
        switch (componentType)
        {
            case BYTE:
                directBufferView = directBuffer;
                break;
            case SHORT:
            case HALF:
                directBufferView = directBuffer.asShortBuffer();
                break;
            case FIXED:
                directBufferView = directBuffer.asIntBuffer();
                break;
            case FLOAT:
                directBufferView = directBuffer.asFloatBuffer();
                break;
        }

    }

    private static final ByteBuffer allocate(int numVertices, int vertexByteStride, int componentType)
    {
        final int capacity = numVertices * vertexByteStride;
        //ensure we get a direct native buffer
        ByteBuffer ret = ByteBuffer.allocateDirect(capacity);
        ret.order(ByteOrder.nativeOrder());
        return ret;
    }

    private final void requireValidIndexAndLength(int firstVertex, int numVertices, int length)
    {
        Require.argumentNotNegative(numVertices, "numVertices");
        if (length < numVertices * getComponentCount())
        {
            throw new IllegalArgumentException("length < numVertices * getComponentCount()");
        }

        Require.indexNotNegative(firstVertex, "firstVertex");
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

    private final ByteBuffer positionByteBuffer(int vertex)
    {
        ByteBuffer buf = getDirectBuffer();
        buf.position(vertex * getVertexByteStride());
        return buf;
    }

    private final ShortBuffer positionShortBuffer(int vertex)
    {
        ShortBuffer buf = (ShortBuffer) getDirectBufferView();
        buf.position((vertex * getVertexByteStride()) >> 1);
        return buf;
    }

    private final IntBuffer positionIntBuffer(int vertex)
    {
        IntBuffer buf = (IntBuffer) getDirectBufferView();
        buf.position((vertex * getVertexByteStride()) >> 2);
        return buf;
    }

    private final FloatBuffer positionFloatBuffer(int vertex)
    {
        FloatBuffer buf = (FloatBuffer) getDirectBufferView();
        buf.position((vertex * getVertexByteStride()) >> 2);
        return buf;
    }
    
    public void get(int firstVertex, int numVertices, byte[] dst)
    {
        Require.notNull(dst, "dst");
        requireByteType();
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        //BYTE type is always the direct buffer
        ByteBuffer buf = positionByteBuffer(firstVertex);
        buf.get(dst, 0, numVertices * getComponentCount());
    }

    public void get(int firstVertex, int numVertices, float[] dst)
    {
        Require.notNull(dst, "dst");
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        if (getComponentType() == FLOAT)
        {
            //no conversion
            if (!bufferIsSparse)
            {
                //we can use a bulk get
                FloatBuffer buf = positionFloatBuffer(firstVertex);
                buf.get(dst, 0, numVertices * getComponentCount());
            }
            else
            {
                //sparse buffer requires a more cautious approach to putting
                final int lastVertex = firstVertex + numVertices;
                int dstIndex = 0;
                for (int vertex = firstVertex; vertex < lastVertex; ++vertex)
                {
                    //put into the direct buffer, vertex by vertex
                    ByteBuffer buf = positionByteBuffer(vertex);
                    for (int component = 0; component < getComponentCount(); ++component)
                    {
                        dst[dstIndex++] = buf.getFloat();
                    }
                }
            }
        }
        else
        {
            //convert to float
            throw new UnsupportedOperationException();
        }
    }
    
    public void get(int firstVertex, int numVertices, int[] dst)
    {
        Require.notNull(dst, "dst");
        requireFixedType();
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        if (!bufferIsSparse)
        {
            IntBuffer buf = positionIntBuffer(firstVertex);
            buf.get(dst, 0, numVertices * getComponentCount());
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    public void get(int firstVertex, int numVertices, short[] dst)
    {
        Require.notNull(dst, "dst");
        requireShortType();
        requireValidIndexAndLength(firstVertex, numVertices, dst.length);

        if (!bufferIsSparse)
        {
            ShortBuffer buf = positionShortBuffer(firstVertex);
            buf.get(dst, 0, numVertices * getComponentCount());
        }
        else
        {
            throw new UnsupportedOperationException();
        }
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

    int getVertexByteStride()
    {
        return vertexByteStride;
    }

    private final ByteBuffer getDirectBuffer()
    {
        return directBuffer;
    }

    private final Buffer getDirectBufferView()
    {
        return directBufferView;
    }

    final Buffer getBuffer()
    {
        return directBufferView.rewind();
    }

    public void set(int firstVertex, int numVertices, byte[] src)
    {
        Require.notNull(src, "src");
        requireByteType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);

        ByteBuffer buf = positionByteBuffer(firstVertex);
        buf.put(src, 0, numVertices * getComponentCount());
    }

    public void set(int firstVertex, int numVertices, float[] src)
    {
        Require.notNull(src, "src");
        requireFloatType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);

        if (getComponentType() == FLOAT)
        {
            if (!bufferIsSparse)
            {
                FloatBuffer buf = positionFloatBuffer(firstVertex);
                buf.put(src, 0, numVertices * getComponentCount());
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
        else
        {
            throw new UnsupportedOperationException("HALF not supported yet");
        }
    }

    public void set(int firstVertex, int numVertices, int[] src)
    {
        Require.notNull(src, "src");
        requireFixedType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);

        if (!bufferIsSparse)
        {
            IntBuffer buf = positionIntBuffer(firstVertex);
            buf.put(src, 0, numVertices * getComponentCount());
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    public void set(int firstVertex, int numVertices, short[] src)
    {
        Require.notNull(src, "src");
        requireShortType();
        requireValidIndexAndLength(firstVertex, numVertices, src.length);

        if (getComponentType() == SHORT)
        {
            if (!bufferIsSparse)
            {
                ShortBuffer buf = positionShortBuffer(firstVertex);
                buf.put(src, 0, numVertices * getComponentCount());
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
        else
        {
            throw new UnsupportedOperationException("HALF not supported yet");
        }
    }
}
