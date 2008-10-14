package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

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

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        this.x = M3GSupport.readFloat(dataInputStream);
        this.y = M3GSupport.readFloat(dataInputStream);
        this.z = M3GSupport.readFloat(dataInputStream);
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
    {
        M3GSupport.writeFloat(dataOutputStream, this.x);
        M3GSupport.writeFloat(dataOutputStream, this.y);
        M3GSupport.writeFloat(dataOutputStream, this.z);
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public float getZ()
    {
        return this.z;
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
