package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class PolygonMode extends Object3D implements M3GTypedObject
{
  public static final byte CULL_BACK = (byte)160;
  public static final byte CULL_FRONT = (byte)161;
  public static final byte CULL_NONE = (byte)162;
  public static final byte SHADE_FLAT = (byte)164;
  public static final byte SHADE_SMOOTH = (byte)165;
  public static final byte WINDING_CCW = (byte)168;
  public static final byte WINDING_CW = (byte)169;
  
  private final byte culling;
  private final byte shading;
  private final byte winding;
  private final boolean twoSidedLightingEnabled;
  private final boolean localCameraLightingEnabled;
  private final boolean perspectiveCorrectionEnabled;
  
  public PolygonMode(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, byte culling, byte shading, byte winding,
      boolean twoSidedLightingEnabled, boolean localCameraLightingEnabled,
      boolean perspectiveCorrectionEnabled)
  {
    super(animationTracks, userParameters);
    this.culling = culling;
    this.shading = shading;
    this.winding = winding;
    this.twoSidedLightingEnabled = twoSidedLightingEnabled;
    this.localCameraLightingEnabled = localCameraLightingEnabled;
    this.perspectiveCorrectionEnabled = perspectiveCorrectionEnabled;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.culling);
    dataOutputStream.write(this.shading);
    dataOutputStream.write(this.winding);
    dataOutputStream.writeBoolean(this.twoSidedLightingEnabled);
    dataOutputStream.writeBoolean(this.localCameraLightingEnabled);
    dataOutputStream.writeBoolean(this.perspectiveCorrectionEnabled);
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.POLYGON_MODE;
  }
}
