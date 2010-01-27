/**
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;

/**
 * See http://java2me.org/m3g/file-format.html#Material<br>
  ColorRGB      ambientColor;<br>
  ColorRGBA     diffuseColor;<br>
  ColorRGB      emissiveColor;<br>
  ColorRGB      specularColor;<br>
  Float32       shininess;<br>
  Boolean       vertexColorTrackingEnabled;<br>
  <br>
 * @author jgasseli
 * @author jsaarinen
 */
public class Material extends Object3D
{
    public static final int AMBIENT = 1024;
    public static final int DIFFUSE = 2048;
    public static final int EMISSIVE = 4096;
    public static final int SPECULAR = 8192;

    private final ColorRGB ambientColor = new ColorRGB(0x33, 0x33, 0x33);
    private final ColorRGBA diffuseColor = new ColorRGBA(0xff, 0xcc, 0xcc, 0xcc);
    private final ColorRGB emissiveColor = new ColorRGB();
    private final ColorRGB specularColor = new ColorRGB();
    private float shininess;
    private boolean vertexColorTrackingEnabled;

    public Material()
    {
        super();
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.ambientColor.deserialise(deserialiser);
        this.diffuseColor.deserialise(deserialiser);
        this.emissiveColor.deserialise(deserialiser);
        this.specularColor.deserialise(deserialiser);
        this.shininess = deserialiser.readFloat();
        this.vertexColorTrackingEnabled = deserialiser.readBoolean();
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.ambientColor.serialise(serialiser);
        this.diffuseColor.serialise(serialiser);
        this.emissiveColor.serialise(serialiser);
        this.specularColor.serialise(serialiser);
        serialiser.writeFloat(this.shininess);
        serialiser.writeBoolean(this.vertexColorTrackingEnabled);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.MATERIAL;
    }

    public int getColor(int target)
    {
        switch (target)
        {
            case AMBIENT:
            {
                return this.ambientColor.getARGB();
            }
            case DIFFUSE:
            {
                return this.diffuseColor.getARGB();
            }
            case EMISSIVE:
            {
                return this.emissiveColor.getARGB();
            }
            case SPECULAR:
            {
                return this.specularColor.getARGB();
            }
            default:
            {
                throw new IllegalArgumentException("target is not exactly one" +
                        " of AMBIENT, DIFFUSE, EMISSIVE, and SPECULAR");
            }
        }
    }

    public ColorRGB getAmbientColor()
    {
        return this.ambientColor;
    }

    public ColorRGBA getDiffuseColor()
    {
        return this.diffuseColor;
    }

    public ColorRGB getEmissiveColor()
    {
        return this.emissiveColor;
    }

    public ColorRGB getSpecularColor()
    {
        return this.specularColor;
    }

    public float getShininess()
    {
        return this.shininess;
    }

    public boolean isVertexColorTrackingEnabled()
    {
        return this.vertexColorTrackingEnabled;
    }

    public void setColor(int target, byte[] rgba)
    {
        if (rgba == null)
        {
            throw new NullPointerException("rgba is null");
        }
        if (rgba.length < 3)
        {
            throw new IllegalArgumentException("rgba.length < 3");
        }

        final int r = rgba[0] & 0xff;
        final int g = rgba[1] & 0xff;
        final int b = rgba[2] & 0xff;
        final int a;
        if (rgba.length > 3)
        {
            a = rgba[3] & 0xff;
        }
        else
        {
            //default to full alpha
            a = 0xff;
        }

        final int argb = (a << 24) | (r << 16) | (g << 8) | (b << 0);
        setColor(target, argb);
    }

    public void setColor(int target, int argb)
    {
        final int targetMask = ~(AMBIENT | DIFFUSE | EMISSIVE | SPECULAR);
        if ((target & targetMask) != 0)
        {
            throw new IllegalArgumentException("target has a value other than" +
                    " a bitwise-OR of one or more of AMBIENT, DIFFUSE," +
                    " EMISSIVE, and SPECULAR");
        }

        if ((target & AMBIENT) != 0)
        {
            setAmbientColor(argb);
        }
        if ((target & DIFFUSE) != 0)
        {
            setDiffuseColor(argb);
        }
        if ((target & EMISSIVE) != 0)
        {
            setEmissiveColor(argb);
        }
        if ((target & SPECULAR) != 0)
        {
            setSpecularColor(argb);
        }
    }

    public void setAmbientColor(int argb)
    {
        this.ambientColor.set(argb);
    }

    public void setDiffuseColor(int argb)
    {
        this.diffuseColor.set(argb);
    }

    public void setEmissiveColor(int argb)
    {
        this.emissiveColor.set(argb);
    }

    public void setSpecularColor(int argb)
    {
        this.specularColor.set(argb);
    }

    public void setShininess(float shininess)
    {
        if (shininess < 0)
        {
            throw new IllegalArgumentException("shininess < 0");
        }
        if (shininess > 128)
        {
            throw new IllegalArgumentException("shininess > 128");
        }

        this.shininess = shininess;
    }

    public void setVertexColorTrackingEnabled(boolean enable)
    {
        this.vertexColorTrackingEnabled = enable;
    }
}
