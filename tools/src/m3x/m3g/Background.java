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

import java.util.List;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGBA;

/**
 * See http://java2me.org/m3g/file-format.html#Background<br>
  ColorRGBA     backgroundColor;<br>
  ObjectIndex   backgroundImage;<br>
  Byte          backgroundImageModeX;<br>
  Byte          backgroundImageModeY;<br>
  Int32         cropX;<br>
  Int32         cropY;<br>
  Int32         cropWidth;<br>
  Int32         cropHeight;<br>
  Boolean       depthClearEnabled;<br>
  Boolean       colorClearEnabled;<br>

 * @author jsaarinen
 * @author jgasseli
 */
public class Background extends Object3D
{
    public final static int BORDER = 32;
    public final static int REPEAT = 33;
    
    private ColorRGBA color;
    private Image2D image;
    private int imageModeX;
    private int imageModeY;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;
    private boolean colorClearEnabled;
    private boolean depthClearEnabled;

    public Background()
    {
        super();
        color = new ColorRGBA();
        setColor(0x0);
        setColorClearEnable(true);
        setDepthClearEnable(true);
        setImage(null);
        setImageMode(BORDER, BORDER);
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.color.deserialise(deserialiser);
        setImage((Image2D)deserialiser.readReference());
        final int modeX = deserialiser.readUnsignedByte();
        final int modeY = deserialiser.readUnsignedByte();
        setImageMode(modeX, modeY);
        final int x = deserialiser.readInt();
        final int y = deserialiser.readInt();
        final int width = deserialiser.readInt();
        final int height = deserialiser.readInt();
        setCrop(x, y, width, height);
        setDepthClearEnable(deserialiser.readBoolean());
        setColorClearEnable(deserialiser.readBoolean());
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.color.serialise(serialiser);
        serialiser.writeReference(getImage());
        serialiser.write(getImageModeX());
        serialiser.write(getImageModeY());
        serialiser.writeInt(getCropX());
        serialiser.writeInt(getCropY());
        serialiser.writeInt(getCropWidth());
        serialiser.writeInt(getCropHeight());
        serialiser.writeBoolean(isDepthClearEnabled());
        serialiser.writeBoolean(isColorClearEnabled());
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.BACKGROUND;
    }

    public Image2D getImage()
    {
        return this.image;
    }

    public int getImageModeX()
    {
        return this.imageModeX;
    }

    public int getImageModeY()
    {
        return this.imageModeY;
    }

    public int getCropX()
    {
        return this.cropX;
    }

    public int getCropY()
    {
        return this.cropY;
    }

    public int getCropWidth()
    {
        return this.cropWidth;
    }

    public int getCropHeight()
    {
        return this.cropHeight;
    }

    public boolean isDepthClearEnabled()
    {
        return this.depthClearEnabled;
    }

    public boolean isColorClearEnabled()
    {
        return this.colorClearEnabled;
    }

    public void setColor(List<Short> color)
    {
        this.color.set(color);
    }

    public void setColor(int argb)
    {
        this.color.set(argb);
    }

    public void setColorClearEnable(boolean enable)
    {
        this.colorClearEnabled = enable;
    }

    public void setDepthClearEnable(boolean enable)
    {
        this.depthClearEnabled = enable;
    }

    public void setImage(Image2D image)
    {
        this.image = image;
        if (image == null)
        {
            setCrop(0, 0, 0, 0);
        }
        else
        {
            setCrop(0, 0, image.getWidth(), image.getHeight());
        }
    }

    public void setImageMode(int modeX, int modeY)
    {
        this.imageModeX = modeX;
        this.imageModeY = modeY;
    }

    public void setCrop(int x, int y, int width, int height)
    {
        this.cropX = x;
        this.cropY = y;
        this.cropWidth = width;
        this.cropHeight = height;
    }

    public void setImageMode(String modeX, String modeY)
    {
        setImageMode(
            getFieldValue(modeX, "modeX"),
            getFieldValue(modeY, "modeY"));
    }
}
