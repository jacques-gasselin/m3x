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
        if (!super.equals(obj))
        {
            return false;
        }
        return this.a == ((ColorRGBA) obj).a;
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.a = deserialiser.readUnsignedByte();
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.a);
    }

    public int getA()
    {
        return this.a;
    }

    @Override
    public void set(int r, int g, int b)
    {
        set(r, g, b, 255);
    }

    @Override
    public void set(float r, float g, float b)
    {
        set(r, g, b, 1.0f);
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
    public void set(int argb)
    {
        super.set(argb);
        this.a = clampColor((argb >>> 24) & 0xff);
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

    @Override
    public void set(byte[] color)
    {
        super.set(color);
        if (color.length < 4)
        {
            throw new IllegalArgumentException("color.length < 4");
        }
        this.a = clampColor(color[3] & 0xff);
    }
}
