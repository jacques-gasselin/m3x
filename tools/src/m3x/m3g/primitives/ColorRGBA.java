package m3x.m3g.primitives;

import java.io.IOException;

import java.util.List;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;

/**
 * @author jsaarinen
 * @author jgasseli
 */
public class ColorRGBA extends ColorRGB implements Serialisable
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

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.a = deserialiser.readUnsignedByte();
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.a);
    }

    public int getA()
    {
        return this.a;
    }

    public void set(int r, int g, int b, int a)
    {
        super.set(r, g, b);
        this.a = clampColor(a);
    }

    public void set(float r, float g, float b, float a)
    {
        super.set(r, g, b);
        this.a = clampColor(a);
    }

    @Override
    public void set(int rgba)
    {
        super.set(rgba);
        this.a = clampColor((rgba >>> 24) & 0xff);
    }

    @Override
    public void set(List<Short> color)
    {
        super.set(color);
        if (color.size() < 4)
        {
            throw new IllegalArgumentException("color.size() < 4");
        }
        this.a = clampColor(color.get(3) & 0xff);
    }
}
