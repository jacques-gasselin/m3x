package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

/**
 * See http://java2me.org/m3g/file-format.html#TriangleStripArray<br>
  Byte       encoding;
  IF encoding == 0, THEN
    UInt32        startIndex;
  ELSE IF encoding == 1, THEN
    Byte          startIndex;
  ELSE IF encoding == 2, THEN
    UInt16        startIndex;
  ELSE IF encoding == 128, THEN
    UInt32[]      indices;
  ELSE IF encoding == 129, THEN
    Byte[]        indices;
  ELSE IF encoding == 130, THEN
    UInt16[]      indices;
  END
  UInt32[]      stripLengths;

 * @author jsaarinen
 */
public class TriangleStripArray extends IndexBuffer implements M3GTypedObject
{
    public TriangleStripArray()
    {
        super();
    }

    public TriangleStripArray(ObjectIndex[] animationTracks,
        UserParameter[] userParameters)
    {
        super(animationTracks, userParameters);
    }
    private int encoding;
    private int intStartIndex;
    private byte byteStartIndex;
    private short shortStartIndex;
    private int[] intIndices;
    private byte[] byteIndices;
    private short[] shortIndices;
    private int[] stripLengths;

    public TriangleStripArray(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, int intStartIndex,
        int[] stripLengths)
    {
        super(animationTracks, userParameters);
        this.encoding = 0;
        this.byteStartIndex = 0;
        this.shortStartIndex = 0;
        this.intStartIndex = intStartIndex;
        this.intIndices = null;
        this.byteIndices = null;
        this.shortIndices = null;
        this.stripLengths = stripLengths;
    }

    public TriangleStripArray(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, byte byteStartIndex,
        int[] stripLengths)
    {
        super(animationTracks, userParameters);
        this.encoding = 1;
        this.byteStartIndex = byteStartIndex;
        this.shortStartIndex = 0;
        this.intStartIndex = 0;
        this.intIndices = null;
        this.byteIndices = null;
        this.shortIndices = null;
        this.stripLengths = stripLengths;
    }

    public TriangleStripArray(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, short shortStartIndex,
        int[] stripLengths)
    {
        super(animationTracks, userParameters);
        this.encoding = 2;
        this.byteStartIndex = 0;
        this.shortStartIndex = shortStartIndex;
        this.intStartIndex = 0;
        this.intIndices = null;
        this.byteIndices = null;
        this.shortIndices = null;
        this.stripLengths = stripLengths;
    }

    public TriangleStripArray(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, int[] intIndices,
        int[] stripLengths)
    {
        super(animationTracks, userParameters);
        this.encoding = 128;
        this.byteStartIndex = 0;
        this.shortStartIndex = 0;
        this.intStartIndex = 0;
        this.intIndices = intIndices;
        this.byteIndices = null;
        this.shortIndices = null;
        this.stripLengths = stripLengths;
    }

    public TriangleStripArray(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, byte[] byteIndices,
        int[] stripLengths)
    {
        super(animationTracks, userParameters);
        this.encoding = 129;
        this.byteStartIndex = 0;
        this.shortStartIndex = 0;
        this.intStartIndex = 0;
        this.intIndices = null;
        this.byteIndices = byteIndices;
        this.shortIndices = null;
        this.stripLengths = stripLengths;
    }

    public TriangleStripArray(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, short[] shortIndices,
        int[] stripLengths)
    {
        super(animationTracks, userParameters);
        this.encoding = 130;
        this.byteStartIndex = 0;
        this.shortStartIndex = 0;
        this.intStartIndex = 0;
        this.intIndices = null;
        this.byteIndices = null;
        this.shortIndices = shortIndices;
        this.stripLengths = stripLengths;
    }

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.encoding = dataInputStream.readByte() & 0xFF;
        switch (this.encoding)
        {
            case 0:
                this.intStartIndex = M3GSupport.readInt(dataInputStream);
                break;

            case 1:
                this.shortStartIndex = M3GSupport.readShort(dataInputStream);
                break;

            case 2:
                this.byteStartIndex = dataInputStream.readByte();
                break;

            case 128:
                int intIndicesLength = M3GSupport.readInt(dataInputStream);
                this.intIndices = new int[intIndicesLength];
                for (int i = 0; i < this.intIndices.length; i++)
                {
                    this.intIndices[i] = M3GSupport.readInt(dataInputStream);
                }
                break;

            case 129:
                int byteIndicesLength = M3GSupport.readInt(dataInputStream);
                this.byteIndices = new byte[byteIndicesLength];
                for (int i = 0; i < this.byteIndices.length; i++)
                {
                    this.byteIndices[i] = dataInputStream.readByte();
                }
                break;

            case 130:
                int shortIndicesLength = M3GSupport.readInt(dataInputStream);
                this.shortIndices = new short[shortIndicesLength];
                for (int i = 0; i < this.shortIndices.length; i++)
                {
                    this.shortIndices[i] = M3GSupport.readShort(dataInputStream);
                }
                break;

            default:
                throw new FileFormatException("Invalid encoding: " + this.encoding);
        }

        int stripLengths = M3GSupport.readInt(dataInputStream);
        this.stripLengths = new int[stripLengths];
        for (int i = 0; i < this.stripLengths.length; i++)
        {
            this.stripLengths[i] = M3GSupport.readInt(dataInputStream);
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.write(this.encoding);
        switch (this.encoding)
        {
            case 0:
                M3GSupport.writeInt(dataOutputStream, this.intStartIndex);
                break;

            case 1:
                M3GSupport.writeShort(dataOutputStream, this.shortStartIndex);
                break;

            case 2:
                dataOutputStream.write(this.byteStartIndex);
                break;

            case 128:
                M3GSupport.writeInt(dataOutputStream, this.intIndices.length);
                for (int index : this.intIndices)
                {
                    M3GSupport.writeInt(dataOutputStream, index);
                }
                break;

            case 129:
                M3GSupport.writeInt(dataOutputStream, this.byteIndices.length);
                for (byte index : this.byteIndices)
                {
                    dataOutputStream.write(index);
                }
                break;

            case 130:
                M3GSupport.writeInt(dataOutputStream, this.shortIndices.length);
                for (short index : this.shortIndices)
                {
                    M3GSupport.writeShort(dataOutputStream, index);
                }
                break;

            default:
                assert (false);
                break;
        }
        M3GSupport.writeInt(dataOutputStream, this.stripLengths.length);
        for (int x : this.stripLengths)
        {
            M3GSupport.writeInt(dataOutputStream, x);
        }
    }

    public byte getObjectType()
    {
        return ObjectTypes.TRIANGLE_STRIP_ARRAY;
    }

    public int getEncoding()
    {
        return this.encoding;
    }

    public int getIntStartIndex()
    {
        return this.intStartIndex;
    }

    public byte getByteStartIndex()
    {
        return this.byteStartIndex;
    }

    public short getShortStartIndex()
    {
        return this.shortStartIndex;
    }

    public int[] getIntIndices()
    {
        return this.intIndices;
    }

    public byte[] getByteIndices()
    {
        return this.byteIndices;
    }

    public short[] getShortIndices()
    {
        return this.shortIndices;
    }

    public int[] getStripLengths()
    {
        return this.stripLengths;
    }
}
