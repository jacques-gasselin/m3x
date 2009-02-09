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
        set(r, g, b);
    }

    public ColorRGB(float r, float g, float b)
    {
        set(r, g, b);
    }

    public ColorRGB()
    {
        super();
    }

    public static final int clampColor(int value)
    {
        return Math.max(0, Math.min(255, value));
    }

    public static final int clampColor(float value)
    {
        return clampColor(Math.round(value * 255));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ColorRGB color = (ColorRGB) obj;
        return this.r == color.r
            && this.g == color.g
            && this.b == color.b;
    }

    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        this.r = deserialiser.readUnsignedByte();
        this.g = deserialiser.readUnsignedByte();
        this.b = deserialiser.readUnsignedByte();
    }

    public int getRGB()
    {
        return (getR() << 16)
             | (getG() << 8)
             | (getB() << 0);
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

    public void serialise(Serialiser serialiser) throws IOException
    {
        serialiser.writeByte(getR());
        serialiser.writeByte(getG());
        serialiser.writeByte(getB());
    }

    public void set(int r, int g, int b)
    {
        this.r = clampColor(r);
        this.g = clampColor(g);
        this.b = clampColor(b);
    }

    public void set(float r, float g, float b)
    {
        this.r = clampColor(r);
        this.g = clampColor(g);
        this.b = clampColor(b);
    }

    public void set(int argb)
    {
        set((argb >> 16) & 0xff, (argb >> 8) & 0xff, (argb >> 0) & 0xff);
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
        set(color.get(0), color.get(1), color.get(2));
    }

}
