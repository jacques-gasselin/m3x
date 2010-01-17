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

package m3x.m3g.primitives;

import java.io.IOException;

import java.util.List;
import m3x.m3g.Deserializer;
import m3x.m3g.Serializer;

/**
 * @author jsaarinen
 * @author jgasseli
 */
public class ColorRGB implements Serializable
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

    public void deserialise(Deserializer deserialiser)
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

    public void serialise(Serializer serialiser) throws IOException
    {
        serialiser.writeUnsignedByte(getR());
        serialiser.writeUnsignedByte(getG());
        serialiser.writeUnsignedByte(getB());
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

    public void set(byte[] color)
    {
        if (color == null)
        {
            throw new NullPointerException("color is null");
        }
        if (color.length < 3)
        {
            throw new IllegalArgumentException("color.length < 3");
        }
        set(color[0] & 0xff, color[1] & 0xff, color[2] & 0xff);
    }
}
