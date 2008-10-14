package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

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
 */
public class CompositingMode extends Object3D implements M3GTypedObject
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

    public CompositingMode(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, boolean depthTestEnabled,
        boolean depthWriteEnabled, boolean colorWriteEnabled,
        boolean alphaWriteEnabled, int blending, int alphaThreshold,
        float depthOffsetFactor, float depthOffsetUnits) throws FileFormatException
    {
        super(animationTracks, userParameters);
        this.depthTestEnabled = depthTestEnabled;
        this.depthWriteEnabled = depthWriteEnabled;
        this.colorWriteEnabled = colorWriteEnabled;
        this.alphaWriteEnabled = alphaWriteEnabled;
        if (blending < ALPHA || blending > REPLACE)
        {
            throw new FileFormatException("Invalid blending: " + blending);
        }
        this.blending = blending;
        this.alphaThreshold = alphaThreshold;
        this.depthOffsetFactor = depthOffsetFactor;
        this.depthOffsetUnits = depthOffsetUnits;
    }

    public CompositingMode()
    {
        super();
    }

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.depthTestEnabled = dataInputStream.readBoolean();
        this.depthWriteEnabled = dataInputStream.readBoolean();
        this.colorWriteEnabled = dataInputStream.readBoolean();
        this.alphaWriteEnabled = dataInputStream.readBoolean();
        this.alphaThreshold = dataInputStream.readByte() & 0xFF;
        this.blending = dataInputStream.readByte() & 0xFF;
        this.depthOffsetFactor = M3GSupport.readFloat(dataInputStream);
        this.depthOffsetUnits = M3GSupport.readFloat(dataInputStream);
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.writeBoolean(this.depthTestEnabled);
        dataOutputStream.writeBoolean(this.depthWriteEnabled);
        dataOutputStream.writeBoolean(this.colorWriteEnabled);
        dataOutputStream.writeBoolean(this.alphaWriteEnabled);
        dataOutputStream.write(this.alphaThreshold);
        dataOutputStream.write(this.blending);
        M3GSupport.writeFloat(dataOutputStream, this.depthOffsetFactor);
        M3GSupport.writeFloat(dataOutputStream, this.depthOffsetUnits);
    }

    public byte getObjectType()
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
}
