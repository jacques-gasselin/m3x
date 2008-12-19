package m3x.m3g.primitives;

import java.io.IOException;

import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;
import m3x.m3g.primitives.Serializable;

public class ColorRGB implements Serializable
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

    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        this.r = deserialiser.readUnsignedByte();
        this.g = deserialiser.readUnsignedByte();
        this.b = deserialiser.readUnsignedByte();
    }

    public void serialize(Serialiser serialiser) throws IOException
    {
        serialiser.write(this.r);
        serialiser.write(this.g);
        serialiser.write(this.b);
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
