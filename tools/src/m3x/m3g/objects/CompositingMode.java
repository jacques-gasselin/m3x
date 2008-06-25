package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class CompositingMode extends Object3D implements M3GTypedObject
{
  public static final int ALPHA = 64;
  public static final int ALPHA_ADD = 65;
  public static final int MODULATE = 66;
  public static final int MODULATE_X2 = 67;
  public static final int REPLACE = 68;
  
  private final boolean depthTestEnabled;
  private final boolean depthWriteEnabled;
  private final boolean colorWriteEnabled;
  private final boolean alphaWriteEnabled;
  private final int blending;
  private final byte alphaThreshold;
  private final float depthOffsetFactor;
  private final float depthOffsetUnits;
  
  public CompositingMode(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, boolean depthTestEnabled,
      boolean depthWriteEnabled, boolean colorWriteEnabled,
      boolean alphaWriteEnabled, int blending, byte alphaThreshold,
      float depthOffsetFactor, float depthOffsetUnits)
  {
    super(animationTracks, userParameters);
    this.depthTestEnabled = depthTestEnabled;
    this.depthWriteEnabled = depthWriteEnabled;
    this.colorWriteEnabled = colorWriteEnabled;
    this.alphaWriteEnabled = alphaWriteEnabled;
    this.blending = blending;
    this.alphaThreshold = alphaThreshold;
    this.depthOffsetFactor = depthOffsetFactor;
    this.depthOffsetUnits = depthOffsetUnits;
  }

  @Override
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
    dataOutputStream.write(M3GSupport.swapBytes(this.depthOffsetFactor));
    dataOutputStream.write(M3GSupport.swapBytes(this.depthOffsetUnits));
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.COMPOSITING_MODE;
  }
}
