package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;
import java.lang.reflect.Field;

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
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setDepthTestEnabled(deserialiser.readBoolean());
        setDepthWriteEnabled(deserialiser.readBoolean());
        setColorWriteEnabled(deserialiser.readBoolean());
        setAlphaWriteEnabled(deserialiser.readBoolean());
        setAlphaThresholdByte(deserialiser.readUnsignedByte());
        setBlending(deserialiser.readUnsignedByte());
        setDepthOffsetFactor(deserialiser.readFloat());
        setDepthOffsetUnits(deserialiser.readFloat());
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeBoolean(isDepthTestEnabled());
        serialiser.writeBoolean(isDepthWriteEnabled());
        serialiser.writeBoolean(isColorWriteEnabled());
        serialiser.writeBoolean(isAlphaWriteEnabled());
        serialiser.writeByte(getAlphaThresholdByte());
        serialiser.writeByte(getBlending());
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

    public void setBlending(int blending)
    {
        if (blending < ALPHA || blending > REPLACE)
        {
            throw new IllegalArgumentException("Invalid blending: " + blending);
        }

        this.blending = blending;
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
