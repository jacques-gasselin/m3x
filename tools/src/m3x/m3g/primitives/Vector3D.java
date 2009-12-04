/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package m3x.m3g.primitives;

import java.io.IOException;

import java.util.List;
import m3x.m3g.Deserializer;
import m3x.m3g.Serializer;

public class Vector3D implements Serializable
{
    private float x, y, z;

    public Vector3D(float x, float y, float z)
    {
        set(x, y, z);
    }

    public Vector3D()
    {
    }

    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        setX(deserialiser.readFloat());
        setY(deserialiser.readFloat());
        setZ(deserialiser.readFloat());
    }

    public void serialise(Serializer serialiser) throws IOException
    {
        serialiser.writeFloat(getX());
        serialiser.writeFloat(getY());
        serialiser.writeFloat(getZ());
    }

    public void set(List<Float> value)
    {
        switch (value.size())
        {
            case 0:
            {
                throw new IllegalArgumentException("value is empty");
            }
            default:
            case 3:
            {
                set(value.get(0), value.get(1), value.get(2));
                break;
            }
            case 2:
            {
                set(value.get(0), value.get(1), 0.0f);
                break;
            }
            case 1:
            {
                set(value.get(0), 0.0f, 0.0f);
                break;
            }
        }
    }

    public void set(float[] value)
    {
        switch (value.length)
        {
            case 0:
            {
                throw new IllegalArgumentException("value is empty");
            }
            default:
            case 3:
            {
                set(value[0], value[1], value[2]);
                break;
            }
            case 2:
            {
                set(value[0], value[1], 0.0f);
                break;
            }
            case 1:
            {
                set(value[0], 0.0f, 0.0f);
                break;
            }
        }
    }

    public void set(float x, float y, float z)
    {
        setX(x);
        setY(y);
        setZ(z);
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public float[] get()
    {
        return new float[]{ x, y, z };
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
        Vector3D another = (Vector3D) obj;
        return getX() == another.getX()
            && getY() == another.getY()
            && getZ() == another.getZ();
    }
}
