/**
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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

package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import java.util.List;
import m3x.m3g.primitives.ColorRGB;

/**
 * See See http://java2me.org/m3g/file-format.html#Fog<br>
  ColorRGB      color;<br>
  Byte          mode;<br>
  IF mode==EXPONENTIAL, THEN<br>
    Float32       density;<br>
  ELSE IF mode==LINEAR, THEN<br>
    Float32       near;<br>
    Float32       far;<br>
  END<br>

 * @author jsaarinen
 * @author jgasseli
 */
public class Fog extends Object3D
{
    public final static int EXPONENTIAL = 80;
    public final static int LINEAR = 81;
    
    private ColorRGB color;
    private int mode;
    private float density;
    private float near;
    private float far;

    public Fog()
    {
        super();
        this.color = new ColorRGB();
        setMode(LINEAR);
        setDensity(1.0f);
        setLinear(0.0f, 1.0f);
        setColor(0x0);
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.color = new ColorRGB();
        this.color.deserialise(deserialiser);
        setMode(deserialiser.readUnsignedByte());
        if (getMode() == EXPONENTIAL)
        {
            setDensity(deserialiser.readFloat());
        }
        else if (getMode() == LINEAR)
        {
            setNear(deserialiser.readFloat());
            setFar(deserialiser.readFloat());
        }
        else
        {
            throw new IllegalArgumentException("Invalid fog mode: " + this.mode);
        }
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.color.serialise(serialiser);
        serialiser.writeUnsignedByte(getMode());
        if (getMode() == EXPONENTIAL)
        {
            serialiser.writeFloat(getDensity());
        }
        else if (getMode() == LINEAR)
        {
            serialiser.writeFloat(getNear());
            serialiser.writeFloat(getFar());
        }
        else
        {
            assert (false);
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.FOG;
    }

    public int getColor()
    {
        return this.color.getRGB();
    }

    public float getDensity()
    {
        return this.density;
    }

    public float getFar()
    {
        return this.far;
    }

    public int getMode()
    {
        return this.mode;
    }

    public float getNear()
    {
        return this.near;
    }

    public void setDensity(float density)
    {
        this.density = density;
    }

    public void setColor(List<Short> color)
    {
        this.color.set(color);
    }

    public void setColor(byte[] color)
    {
        this.color.set(color);
    }

    public void setColor(int rgb)
    {
        this.color.set(rgb);
    }

    private void setFar(float far)
    {
        this.far = far;
    }

    public void setLinear(float near, float far)
    {
        setNear(near);
        setFar(far);
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public void setMode(String mode)
    {
        setMode(getFieldValue(mode, "mode"));
    }

    private void setNear(float near)
    {
        this.near = near;
    }
}
