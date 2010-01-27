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

/**
 * See http://java2me.org/m3g/file-format.html#CompositingMode<br>
  Boolean       depthTestEnabled;<br>
  Boolean       depthWriteEnabled;<br>
  Boolean       colorWriteEnabled;<br>
  Boolean       alphaWriteEnabled;<br>
  Byte          blending;<br>
  Byte          alphaThreshold;<br>
  Float32       depthOffsetFactor;<br>
  Float32       depthOffsetUnits;<br>

 * @author jsaarinen
 * @author jgasseli
 */
public class CompositingMode extends Object3D
{
    public static final int ALPHA = 64;
    public static final int ALPHA_ADD = 65;
    public static final int MODULATE = 66;
    public static final int MODULATE_X2 = 67;
    public static final int REPLACE = 68;

    
    private boolean depthTestEnabled;
    private boolean depthWriteEnabled;
    private boolean colorWriteEnabled;
    private boolean alphaWriteEnabled;
    private int blending;
    private float alphaThreshold;
    private float depthOffsetFactor;
    private float depthOffsetUnits;

    public CompositingMode()
    {
        super();
        setBlending(REPLACE);
        setAlphaThreshold(0.0f);
        setDepthOffset(0.0f, 0.0f);
        setDepthTestEnabled(true);
        setDepthWriteEnabled(true);
        setColorWriteEnabled(true);
        setAlphaWriteEnabled(true);
    }


    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setDepthTestEnabled(deserialiser.readBoolean());
        setDepthWriteEnabled(deserialiser.readBoolean());
        setColorWriteEnabled(deserialiser.readBoolean());
        setAlphaWriteEnabled(deserialiser.readBoolean());
        setBlending(deserialiser.readUnsignedByte());
        setAlphaThresholdByte(deserialiser.readUnsignedByte());
        setDepthOffsetFactor(deserialiser.readFloat());
        setDepthOffsetUnits(deserialiser.readFloat());
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeBoolean(isDepthTestEnabled());
        serialiser.writeBoolean(isDepthWriteEnabled());
        serialiser.writeBoolean(isColorWriteEnabled());
        serialiser.writeBoolean(isAlphaWriteEnabled());
        serialiser.writeUnsignedByte(getBlending());
        serialiser.writeUnsignedByte(getAlphaThresholdByte());
        serialiser.writeFloat(getDepthOffsetFactor());
        serialiser.writeFloat(getDepthOffsetUnits());
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.COMPOSITING_MODE;
    }

    public boolean isDepthTestEnabled()
    {
        return this.depthTestEnabled;
    }

    public boolean isDepthWriteEnabled()
    {
        return this.depthWriteEnabled;
    }

    public boolean isColorWriteEnabled()
    {
        return this.colorWriteEnabled;
    }

    public boolean isAlphaWriteEnabled()
    {
        return this.alphaWriteEnabled;
    }

    public int getBlending()
    {
        return this.blending;
    }

    public float getAlphaThreshold()
    {
        return this.alphaThreshold;
    }

    private int getAlphaThresholdByte()
    {
        return Math.round(this.alphaThreshold * 255);
    }

    public float getDepthOffsetFactor()
    {
        return this.depthOffsetFactor;
    }

    public float getDepthOffsetUnits()
    {
        return this.depthOffsetUnits;
    }

    public void setAlphaThreshold(float threshold)
    {
        this.alphaThreshold = threshold;
    }

    private void setAlphaThresholdByte(int threshold)
    {
        this.alphaThreshold = threshold / 255.0f;
    }

    public void setDepthTestEnabled(boolean depthTestEnabled)
    {
        this.depthTestEnabled = depthTestEnabled;
    }

    public void setDepthWriteEnabled(boolean depthWriteEnabled)
    {
        this.depthWriteEnabled = depthWriteEnabled;
    }

    public void setColorWriteEnabled(boolean colorWriteEnabled)
    {
        this.colorWriteEnabled = colorWriteEnabled;
    }

    public void setAlphaWriteEnabled(boolean alphaWriteEnabled)
    {
        this.alphaWriteEnabled = alphaWriteEnabled;
    }

    public void setBlending(final int mode)
    {
        if (mode < ALPHA || mode > REPLACE)
        {
            throw new IllegalArgumentException("Invalid blending: " + mode);
        }

        this.blending = mode;
    }

    public void setBlending(String blending)
    {
        setBlending(getFieldValue(blending, "blending"));
    }

    private void setDepthOffsetFactor(float factor)
    {
        this.depthOffsetFactor = factor;
    }

    private void setDepthOffsetUnits(float units)
    {
        this.depthOffsetUnits = units;
    }

    public void setDepthOffset(float factor, float units)
    {
        setDepthOffsetFactor(factor);
        setDepthOffsetUnits(units);
    }
    
}
