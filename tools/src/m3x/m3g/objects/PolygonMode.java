package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class PolygonMode extends Object3D implements M3GSerializable
{
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
}
