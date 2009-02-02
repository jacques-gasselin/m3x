package m3x.m3g;

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
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
    private int[] stripLengths;

    public TriangleStripArray()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);

        final int stripLengthCount = deserialiser.readInt();
        int[] lengths = new int[stripLengthCount];
        for (int i = 0; i < stripLengthCount; i++)
        {
            lengths[i] = deserialiser.readInt();
        }
        setStripLengths(lengths);
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeInt(getStripLengthCount());
        for (int x : this.stripLengths)
        {
            serialiser.writeInt(x);
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.TRIANGLE_STRIP_ARRAY;
    }

    public int getStripLengthCount()
    {
        if (this.stripLengths == null)
        {
            return 0;
        }
        return this.stripLengths.length;
    }

    public int[] getStripLengths()
    {
        return this.stripLengths;
    }

    public void setStripLengths(int[] stripLengths)
    {
        this.stripLengths = stripLengths;
    }
}
