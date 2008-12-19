package m3x.m3g.primitives;

import java.io.IOException;

import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;
import m3x.m3g.primitives.Serializable;

public class ColorRGBA extends ColorRGB implements Serializable
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
    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        this.a = deserialiser.readUnsignedByte();
    }

    @Override
    public void serialize(Serialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.write(this.a);
    }

    public int getA()
    {
        return this.a;
    }
}
