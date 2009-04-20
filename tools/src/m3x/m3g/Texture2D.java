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

package m3x.m3g;

import java.util.List;
import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.util.Object3DReferences;

/**
 * See http://java2me.org/m3g/file-format.html#Texture2D<br>
  ObjectIndex   image;
  ColorRGB      blendColor;
  Byte          blending;
  Byte          wrappingS;
  Byte          wrappingT;
  Byte          levelFilter;
  Byte          imageFilter;
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Texture2D extends Transformable implements SectionSerialisable
{
    public static final int FILTER_BASE_LEVEL = 208;
    public static final int FILTER_LINEAR = 209;
    public static final int FILTER_NEAREST = 210;
    public static final int FUNC_ADD = 224;
    public static final int FUNC_BLEND = 225;
    public static final int FUNC_DECAL = 226;
    public static final int FUNC_MODULATE = 227;
    public static final int FUNC_REPLACE = 228;
    public static final int WRAP_CLAMP = 240;
    public static final int WRAP_REPEAT = 241;
    private Image2D image;
    private ColorRGB blendColor;
    private int blending;
    private int wrappingS;
    private int wrappingT;
    private int levelFilter;
    private int imageFilter;

    public Texture2D()
    {
        super();
        this.blendColor = new ColorRGB();
        setBlendColor(0xffffff);
    }

    public Texture2D(Image2D image)
    {
        this();
        setImage(image);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.image = (Image2D)deserialiser.readReference();
        this.blendColor = new ColorRGB();
        this.blendColor.deserialise(deserialiser);
        this.blending = deserialiser.readUnsignedByte();
        this.wrappingS = deserialiser.readUnsignedByte();
        this.wrappingT = deserialiser.readUnsignedByte();
        this.levelFilter = deserialiser.readUnsignedByte();
        this.imageFilter = deserialiser.readUnsignedByte();
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(this.image);
        this.blendColor.serialise(serialiser);
        serialiser.write(this.blending);
        serialiser.write(this.wrappingS);
        serialiser.write(this.wrappingT);
        serialiser.write(this.levelFilter);
        serialiser.write(this.imageFilter);
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getImage());
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.TEXTURE_2D;
    }

    public Image2D getImage()
    {
        return this.image;
    }

    public int getBlendColor()
    {
        return this.blendColor.getRGB();
    }

    public int getBlending()
    {
        return this.blending;
    }

    public int getWrappingS()
    {
        return this.wrappingS;
    }

    public int getWrappingT()
    {
        return this.wrappingT;
    }

    public int getLevelFilter()
    {
        return this.levelFilter;
    }

    public int getImageFilter()
    {
        return this.imageFilter;
    }

    public void setBlendColor(int argb)
    {
        this.blendColor.set(argb);
    }

    public void setBlendColor(List<Short> blendColor)
    {
        if (blendColor != null && blendColor.size() > 0)
        {
            this.blendColor.set(blendColor);
        }
    }

    public void setBlending(int func)
    {
        this.blending = func;
    }

    public void setBlending(String func)
    {
        setBlending(getFieldValue(func, "func"));
    }

    public void setFiltering(int levelFilter, int imageFilter)
    {
        this.levelFilter = levelFilter;
        this.imageFilter = imageFilter;
    }

    public void setFiltering(String levelFilter, String imageFilter)
    {
        setFiltering(getFieldValue(levelFilter, "level filter"),
            getFieldValue(imageFilter, "image filter"));
    }

    public void setImage(Image2D image)
    {
        this.image = image;
    }

    public void setWrapping(int wrapS, int wrapT)
    {
        this.wrappingS = wrapS;
        this.wrappingT = wrapT;
    }

    public void setWrapping(String wrapS, String wrapT)
    {
        setWrapping(getFieldValue(wrapS, "wrapS"), getFieldValue(wrapT, "wrapT"));
    }

}
