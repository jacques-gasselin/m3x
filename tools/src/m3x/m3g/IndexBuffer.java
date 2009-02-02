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
    public static final int ENCODING_EXPLICIT = 128;

    private int encoding;
    private int[] indices;
    private int firstIndex;
    
    public IndexBuffer()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setEncoding(deserialiser.readUnsignedByte());
        if (!isExplicit())
        {
            //implicit indices
            int first = 0;
            switch (getEncoding())
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
            switch (getEncoding())
            {
                case ENCODING_EXPLICIT | ENCODING_INT:
                    for (int i = 0; i < indicesLength; i++)
                    {
                        dataIndices[i] = deserialiser.readInt();
                    }
                    break;

                case ENCODING_EXPLICIT | ENCODING_BYTE:
                    for (int i = 0; i < indicesLength; i++)
                    {
                        dataIndices[i] = deserialiser.readUnsignedByte();
                    }
                    break;

                case ENCODING_EXPLICIT | ENCODING_SHORT:
                    for (int i = 0; i < indicesLength; i++)
                    {
                        dataIndices[i] = deserialiser.readUnsignedShort();
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Invalid encoding: " + getEncoding());
            }
            setIndices(dataIndices);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeByte(getEncoding());
        if (!isExplicit())
        {
            final int first = getFirstIndex();
            switch (getEncoding())
            {
                case ENCODING_INT:
                    serialiser.writeInt(first);
                    break;

                case ENCODING_BYTE:
                    serialiser.writeByte(first);
                    break;

                case ENCODING_SHORT:
                    serialiser.writeShort(first);
                    break;
            }
        }
        else
        {
            serialiser.writeInt(getIndexCount());
            final int[] dataIndicies = getIndices();
            switch (getEncoding())
            {
                case ENCODING_EXPLICIT | ENCODING_INT:
                    for (int index : dataIndicies)
                    {
                        serialiser.writeInt(index);
                    }
                    break;

                case ENCODING_EXPLICIT | ENCODING_BYTE:
                    for (int index : dataIndicies)
                    {
                        serialiser.writeByte(index);
                    }
                    break;

                case ENCODING_EXPLICIT | ENCODING_SHORT:
                    for (int index : dataIndicies)
                    {
                        serialiser.writeShort(index);
                    }
                    break;
            }
        }
    }


    public int getEncoding()
    {
        return this.encoding;
    }

    public int getIndexCount()
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

    public boolean isExplicit()
    {
        return (getEncoding() & ENCODING_EXPLICIT) == ENCODING_EXPLICIT;
    }

    public void setEncoding(int encoding)
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
        final int maximum = getEncodingMaximum();
        for (int i = 0; i < indices.length; ++i)
        {
            final int index = indices[i];
            if (index < 0)
            {
                throw new IndexOutOfBoundsException("indices[" + i + "] < 0");
            }
            if (index > maximum)
            {
                throw new IndexOutOfBoundsException("indices[" + i + "] > " + maximum);
            }
        }
        this.indices = indices;
    }

    public void setFirstIndex(int firstIndex)
    {
        if (isExplicit())
        {
            throw new IllegalStateException(
                "implicit start index not valid for explicit indices");
        }
        if (firstIndex < 0)
        {
            throw new IndexOutOfBoundsException("firstIndex < 0");
        }
        final int maximum = getEncodingMaximum();
        if (firstIndex > maximum)
        {
            throw new IndexOutOfBoundsException("firstIndex > " + maximum);
        }
        this.firstIndex = firstIndex;
    }
}
