package m3x.m3g.primitives;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GDeserialiser;
import m3x.m3g.M3GSerializable;

public class ColorRGBA extends ColorRGB implements M3GSerializable
{
    private int a;

    public ColorRGBA(int r, int g, int b, int a)
    {
        super(r, g, b);
        this.a = a;
    }

    public ColorRGBA(float r, float g, float b, float a)
    {
        super(r, g, b);
        this.a = (int) (a * 255.0f + 0.5f);
    }

    public ColorRGBA()
    {
        super();
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof ColorRGBA))
        {
            return false;
        }
        return super.equals(obj) && this.a == ((ColorRGBA) obj).a;
    }

    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.a = deserialiser.readUnsignedByte();
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.write(this.a);
    }

    public int getA()
    {
        return this.a;
    }
}
