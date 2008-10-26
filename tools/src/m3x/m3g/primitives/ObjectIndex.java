package m3x.m3g.primitives;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.util.LittleEndianDataInputStream;

public class ObjectIndex implements M3GSerializable
{
    private int index;

    public ObjectIndex(int index)
    {
        this.index = index;
    }

    public ObjectIndex()
    {
    }

    public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        this.index = dataInputStream.readInt();
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
    {
        M3GSupport.writeInt(dataOutputStream, this.index);
    }

    public Object getIndex()
    {
        return this.index;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof ObjectIndex))
        {
            return false;
        }
        return this.index == ((ObjectIndex) obj).index;
    }
}
