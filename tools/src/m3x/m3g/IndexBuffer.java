/**
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

package m3x.m3g;

import java.io.IOException;

/**
 * See http://java2me.org/m3g/file-format.html#IndexBuffer<br>
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public abstract class IndexBuffer extends Object3D
{
    public static final int ENCODING_INT = 0;
    public static final int ENCODING_BYTE = 1;
    public static final int ENCODING_SHORT = 2;
    private static final int ENCODING_MASK = 127;
    public static final int ENCODING_EXPLICIT = 128;

    private int encoding;
    private int[] indices;
    private int firstIndex;

    private static final void requireIndexInRange(final int index, final int maximum)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException("index < 0");
        }
        if (index > maximum)
        {
            throw new IndexOutOfBoundsException("index > " + maximum);
        }
    }

    IndexBuffer()
    {
        this(ENCODING_SHORT);
    }

    IndexBuffer(int encoding)
    {
        super();
        setEncoding(encoding);
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setEncoding(deserialiser.readUnsignedByte());
        final int encodingSize = getEncoding() & ENCODING_MASK;
        if (!isExplicit())
        {
            //implicit indices
            int first = 0;
            switch (encodingSize)
            {
                case ENCODING_INT:
                    first = deserialiser.readInt();
                    break;

                case ENCODING_BYTE:
                    first = deserialiser.readUnsignedByte();
                    break;

                case ENCODING_SHORT:
                    first = deserialiser.readUnsignedShort();
                    break;

                default:
                    throw new IllegalArgumentException("Invalid encoding: " + getEncoding());
            }
            setFirstIndex(first);
        }
        else
        {
            final int indicesLength = deserialiser.readInt();
            final int[] dataIndices = new int[indicesLength];
            //explicit indices
            switch (encodingSize)
            {
                case ENCODING_INT:
                {
                    for (int i = 0; i < indicesLength; i++)
                    {
                        dataIndices[i] = deserialiser.readInt();
                    }
                    break;
                }
                case ENCODING_BYTE:
                {
                    for (int i = 0; i < indicesLength; i++)
                    {
                        dataIndices[i] = deserialiser.readUnsignedByte();
                    }
                    break;
                }
                case ENCODING_SHORT:
                {
                    for (int i = 0; i < indicesLength; i++)
                    {
                        dataIndices[i] = deserialiser.readUnsignedShort();
                    }
                    break;
                }
                default:
                {
                    throw new IllegalArgumentException("Invalid encoding: " + getEncoding());
                }
            }
            setIndices(dataIndices);
        }
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeUnsignedByte(getEncoding());
        if (!isExplicit())
        {
            final int first = getFirstIndex();
            switch (getEncoding())
            {
                case ENCODING_INT:
                {
                    serialiser.writeInt(first);
                    break;
                }
                case ENCODING_BYTE:
                {
                    serialiser.writeUnsignedByte(first);
                    break;
                }
                case ENCODING_SHORT:
                {
                    serialiser.writeUnsignedShort(first);
                    break;
                }
            }
        }
        else
        {
            serialiser.writeInt(getIndexCount());
            final int[] dataIndicies = getIndices();
            switch (getEncoding())
            {
                case ENCODING_EXPLICIT | ENCODING_INT:
                {
                    for (int index : dataIndicies)
                    {
                        serialiser.writeInt(index);
                    }
                    break;
                }
                case ENCODING_EXPLICIT | ENCODING_BYTE:
                {
                    for (int index : dataIndicies)
                    {
                        serialiser.writeUnsignedByte(index);
                    }
                    break;
                }
                case ENCODING_EXPLICIT | ENCODING_SHORT:
                {
                    for (int index : dataIndicies)
                    {
                        serialiser.writeUnsignedShort(index);
                    }
                    break;
                }
            }
        }
    }


    public final int getEncoding()
    {
        return this.encoding;
    }

    public final int getIndexCount()
    {
        if (this.indices == null)
        {
            return 0;
        }
        return this.indices.length;
    }

    public int[] getIndices()
    {
        return this.indices;
    }

    public int getFirstIndex()
    {
        return this.firstIndex;
    }

    public final boolean isExplicit()
    {
        return (getEncoding() & ENCODING_EXPLICIT) == ENCODING_EXPLICIT;
    }

    public final void setEncoding(int encoding)
    {
        this.encoding = encoding;
    }

    private final int getEncodingMaximum()
    {
        if ((encoding & ENCODING_BYTE) == ENCODING_BYTE)
        {
            //maximum unsigned byte
            return (1 << 8) - 1;
        }
        //maximum unsigned short
        return (1 << 16) - 1;
    }

    public void setIndices(int[] indices)
    {
        if (!isExplicit())
        {
            throw new IllegalStateException(
                "explicit indices not valid for implicit start index");
        }
        if (indices == null)
        {
            throw new IllegalArgumentException("indices == null");
        }
        final int length = indices.length;
        if (length == 0)
        {
            throw new IllegalArgumentException("indices is empty");
        }
        final int maximum = getEncodingMaximum();
        for (int i = 0; i < length; ++i)
        {
            final int index = indices[i];
            requireIndexInRange(index, maximum);
        }
        //copy the values in
        this.indices = new int[length];
        System.arraycopy(indices, 0, this.indices, 0, length);
    }

    public void setFirstIndex(int firstIndex)
    {
        if (isExplicit())
        {
            throw new IllegalStateException(
                "implicit start index not valid for explicit indices");
        }
        final int maximum = getEncodingMaximum();
        requireIndexInRange(firstIndex, maximum);
        
        this.firstIndex = firstIndex;
    }
}
