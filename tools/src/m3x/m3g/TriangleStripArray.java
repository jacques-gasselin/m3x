package m3x.m3g;

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.DataOutputStream;
import java.io.IOException;

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
 * @author jgasseli
 */
public class TriangleStripArray extends IndexBuffer implements SectionSerialisable
{
    public TriangleStripArray()
    {
        super();
    }

    public TriangleStripArray(AnimationTrack[] animationTracks,
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

    public TriangleStripArray(AnimationTrack[] animationTracks,
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

    public TriangleStripArray(AnimationTrack[] animationTracks,
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

    public TriangleStripArray(AnimationTrack[] animationTracks,
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

    public TriangleStripArray(AnimationTrack[] animationTracks,
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

    public TriangleStripArray(AnimationTrack[] animationTracks,
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

    public TriangleStripArray(AnimationTrack[] animationTracks,
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

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.encoding = deserialiser.readUnsignedByte();
        switch (this.encoding)
        {
            case 0:
                this.intStartIndex = deserialiser.readInt();
                break;

            case 1:
                this.shortStartIndex = deserialiser.readShort();
                break;

            case 2:
                this.byteStartIndex = deserialiser.readByte();
                break;

            case 128:
                int intIndicesLength = deserialiser.readInt();
                this.intIndices = new int[intIndicesLength];
                for (int i = 0; i < this.intIndices.length; i++)
                {
                    this.intIndices[i] = deserialiser.readInt();
                }
                break;

            case 129:
                int byteIndicesLength = deserialiser.readInt();
                this.byteIndices = new byte[byteIndicesLength];
                for (int i = 0; i < this.byteIndices.length; i++)
                {
                    this.byteIndices[i] = deserialiser.readByte();
                }
                break;

            case 130:
                int shortIndicesLength = deserialiser.readInt();
                this.shortIndices = new short[shortIndicesLength];
                for (int i = 0; i < this.shortIndices.length; i++)
                {
                    this.shortIndices[i] = deserialiser.readShort();
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid encoding: " + this.encoding);
        }

        int stripLengthCount = deserialiser.readInt();
        this.stripLengths = new int[stripLengthCount];
        for (int i = 0; i < this.stripLengths.length; i++)
        {
            this.stripLengths[i] = deserialiser.readInt();
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.encoding);
        switch (this.encoding)
        {
            case 0:
                serialiser.writeInt(this.intStartIndex);
                break;

            case 1:
                serialiser.writeShort(this.shortStartIndex);
                break;

            case 2:
                serialiser.write(this.byteStartIndex);
                break;

            case 128:
                serialiser.writeInt(this.intIndices.length);
                for (int index : this.intIndices)
                {
                    serialiser.writeInt(index);
                }
                break;

            case 129:
                serialiser.writeInt(this.byteIndices.length);
                for (byte index : this.byteIndices)
                {
                    serialiser.write(index);
                }
                break;

            case 130:
                serialiser.writeInt(this.shortIndices.length);
                for (short index : this.shortIndices)
                {
                    serialiser.writeShort(index);
                }
                break;

            default:
                assert (false);
                break;
        }
        serialiser.writeInt(this.stripLengths.length);
        for (int x : this.stripLengths)
        {
            serialiser.writeInt(x);
        }
    }

    public int getSectionObjectType()
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
