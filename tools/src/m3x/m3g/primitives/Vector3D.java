package m3x.m3g.primitives;

import java.io.IOException;

import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;
import m3x.m3g.primitives.Serialisable;

public class Vector3D implements Serialisable
{
    private float x,  y,  z;

    public Vector3D(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D()
    {
    }

    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        x = deserialiser.readFloat();
        y = deserialiser.readFloat();
        z = deserialiser.readFloat();
    }

    public void serialise(Serialiser serialiser) throws IOException
    {
        serialiser.writeFloat(x);
        serialiser.writeFloat(y);
        serialiser.writeFloat(z);
    }

    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getZ()
    {
        return z;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof Vector3D))
        {
            return false;
        }
        Vector3D another = (Vector3D) obj;
        return this.x == another.x && this.y == another.y && this.z == another.z;
    }
}
