package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GDeserialiser;
import m3x.m3g.M3GSerializable;
import m3x.m3g.util.LittleEndianDataInputStream;

public class ColorRGB implements M3GSerializable
{
    private int r,  g,  b;

    public ColorRGB(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorRGB(float r, float g, float b)
    {
        this.r = (int) (r * 255.0f + 0.5f);
        this.g = (int) (g * 255.0f + 0.5f);
        this.b = (int) (b * 255.0f + 0.5f);
    }

    public ColorRGB()
    {
        super();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof ColorRGB))
        {
            return false;
        }
        ColorRGB color = (ColorRGB) obj;
        return this.r == color.r && this.g == color.g && this.b == color.b;
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
    {
        dataOutputStream.write(this.r);
        dataOutputStream.write(this.g);
        dataOutputStream.write(this.b);
    }

    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        this.r = deserialiser.readUnsignedByte();
        this.g = deserialiser.readUnsignedByte();
        this.b = deserialiser.readUnsignedByte();
    }

    public int getR()
    {
        return this.r;
    }

    public int getG()
    {
        return this.g;
    }

    public int getB()
    {
        return this.b;
    }
}
