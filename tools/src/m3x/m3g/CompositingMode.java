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
    private int alphaThreshold;
    private float depthOffsetFactor;
    private float depthOffsetUnits;

    public CompositingMode(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, boolean depthTestEnabled,
        boolean depthWriteEnabled, boolean colorWriteEnabled,
        boolean alphaWriteEnabled, int blending, int alphaThreshold,
        float depthOffsetFactor, float depthOffsetUnits)
    {
        super(animationTracks, userParameters);
        setDepthTestEnabled(depthTestEnabled);
        setDepthWriteEnabled(depthWriteEnabled);
        setColorWriteEnabled(colorWriteEnabled);
        setAlphaWriteEnabled(alphaWriteEnabled);
        setBlending(blending);
        setAlphaThreshold(alphaThreshold);
        setDepthOffsetFactor(depthOffsetFactor);
        setDepthOffsetUnits(depthOffsetUnits);
    }

    public CompositingMode()
    {
        super();
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
        setAlphaThreshold(deserialiser.readUnsignedByte());
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
        serialiser.write(getAlphaThreshold());
        serialiser.write(getBlending());
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

    public int getAlphaThreshold()
    {
        return this.alphaThreshold;
    }

    public float getDepthOffsetFactor()
    {
        return this.depthOffsetFactor;
    }

    public float getDepthOffsetUnits()
    {
        return this.depthOffsetUnits;
    }

    private void setDepthTestEnabled(boolean depthTestEnabled)
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

    public void setAlphaThreshold(int alphaThreshold)
    {
        this.alphaThreshold = alphaThreshold;
    }

    public void setDepthOffsetFactor(float depthOffsetFactor)
    {
        this.depthOffsetFactor = depthOffsetFactor;
    }

    public void setDepthOffsetUnits(float depthOffsetUnits)
    {
        this.depthOffsetUnits = depthOffsetUnits;
    }
    
}
