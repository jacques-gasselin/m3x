package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.util.LittleEndianDataInputStream;

public class ColorRGB implements M3GSerializable
{
    private byte r,  g,  b;

    public ColorRGB(byte r, byte g, byte b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorRGB(float r, float g, float b)
    {
        this.r = (byte) (r * 255.0f + 0.5f);
        this.g = (byte) (g * 255.0f + 0.5f);
        this.b = (byte) (b * 255.0f + 0.5f);
    }

    public ColorRGB()
    {
        super();
    }

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

    public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        this.r = dataInputStream.readByte();
        this.g = dataInputStream.readByte();
        this.b = dataInputStream.readByte();
    }

    public byte getR()
    {
        return this.r;
    }

    public byte getG()
    {
        return this.g;
    }

    public byte getB()
    {
        return this.b;
    }
}
