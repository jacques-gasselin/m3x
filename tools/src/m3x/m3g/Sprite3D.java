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

import m3x.m3g.primitives.SectionSerializable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;


/**
 * See http://java2me.org/m3g/file-format.html#Sprite<br>
  ObjectIndex   image;<br>
  ObjectIndex   appearance;<br>
  <br>
  Boolean       isScaled;<br>
  <br>
  Int32         cropX;<br>
  Int32         cropY;<br>
  Int32         cropWidth;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Sprite3D extends Node implements SectionSerializable
{
    private Image2D image;
    private Appearance appearance;
    private boolean isScaled;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;

    public Sprite3D()
    {
        super();
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.image = (Image2D)deserialiser.readReference();
        this.appearance = (Appearance)deserialiser.readReference();
        this.isScaled = deserialiser.readBoolean();
        this.cropX = deserialiser.readInt();
        this.cropY = deserialiser.readInt();
        this.cropWidth = deserialiser.readInt();
        this.cropHeight = deserialiser.readInt();
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(getImage());
        serialiser.writeReference(getAppearance());
        serialiser.writeBoolean(isScaled);
        serialiser.writeInt(this.cropX);
        serialiser.writeInt(this.cropY);
        serialiser.writeInt(this.cropWidth);
        serialiser.writeInt(this.cropHeight);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.SPRITE;
    }

    public Image2D getImage()
    {
        return this.image;
    }

    public Appearance getAppearance()
    {
        return this.appearance;
    }

    public boolean isScaled()
    {
        return this.isScaled;
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
}
