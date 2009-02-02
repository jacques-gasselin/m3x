package m3x.m3g.primitives;

import java.io.IOException;

import java.util.List;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;

/**
 * @author jsaarinen
 * @author jgasseli
 */
public class ColorRGB implements Serialisable
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

    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        this.r = deserialiser.readUnsignedByte();
        this.g = deserialiser.readUnsignedByte();
        this.b = deserialiser.readUnsignedByte();
    }

    public void serialise(Serialiser serialiser) throws IOException
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

    public void set(int RGB)
    {
        this.r = (RGB >> 16) & 0xff;
        this.g = (RGB >> 8) & 0xff;
        this.b = (RGB >> 0) & 0xff;
    }

    public void set(List<Short> color)
    {
        if (color == null)
        {
            throw new NullPointerException("color is null");
        }
        if (color.size() < 3)
        {
            throw new IllegalArgumentException("color.size() < 3");
        }
        this.r = color.get(0) & 0xff;
        this.g = color.get(1) & 0xff;
        this.b = color.get(2) & 0xff;
    }

}
