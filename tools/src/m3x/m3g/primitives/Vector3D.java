package m3x.m3g.primitives;

import java.io.IOException;

import m3x.m3g.M3GDeserialiser;
import m3x.m3g.M3GSerialiser;
import m3x.m3g.M3GSerializable;

public class Vector3D implements M3GSerializable
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

    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException
    {
        x = deserialiser.readFloat();
        y = deserialiser.readFloat();
        z = deserialiser.readFloat();
    }

    public void serialize(M3GSerialiser serialiser) throws IOException
    {
        serialiser.writeFloat(x);
        serialiser.writeFloat(y);
        serialiser.writeFloat(z);
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
