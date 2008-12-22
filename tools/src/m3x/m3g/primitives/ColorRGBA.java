package m3x.m3g.primitives;

import java.io.IOException;

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
}
