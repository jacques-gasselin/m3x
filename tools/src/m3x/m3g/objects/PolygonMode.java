package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

/**
  Byte          culling;
  Byte          shading;
  Byte          winding;
  Boolean       twoSidedLightingEnabled;
  Boolean       localCameraLightingEnabled;
  Boolean       perspectiveCorrectionEnabled;

 * @author jsaarinen
 */
public class PolygonMode extends Object3D implements M3GTypedObject
{
  public static final byte CULL_BACK = (byte) 160;
  public static final byte CULL_FRONT = (byte) 161;
  public static final byte CULL_NONE = (byte) 162;
  public static final byte SHADE_FLAT = (byte) 164;
  public static final byte SHADE_SMOOTH = (byte) 165;
  public static final byte WINDING_CCW = (byte) 168;
  public static final byte WINDING_CW = (byte) 169;

  private byte culling;
  private byte shading;
  private byte winding;
  private boolean twoSidedLightingEnabled;
  private boolean localCameraLightingEnabled;
  private boolean perspectiveCorrectionEnabled;

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
    
  public PolygonMode()
  {
    super();
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {    
    this.culling = dataInputStream.readByte();
    this.shading = dataInputStream.readByte();
    this.winding = dataInputStream.readByte();
    this.twoSidedLightingEnabled = dataInputStream.readBoolean();
    this.localCameraLightingEnabled = dataInputStream.readBoolean();
    this.perspectiveCorrectionEnabled = dataInputStream.readBoolean();
  }

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

  public byte getObjectType()
  {
    return ObjectTypes.POLYGON_MODE;
  }

  public byte getCulling()
  {
    return this.culling;
  }

  public byte getShading()
  {
    return this.shading;
  }

  public byte getWinding()
  {
    return this.winding;
  }

  public boolean isTwoSidedLightingEnabled()
  {
    return this.twoSidedLightingEnabled;
  }

  public boolean isLocalCameraLightingEnabled()
  {
    return this.localCameraLightingEnabled;
  }

  public boolean isPerspectiveCorrectionEnabled()
  {
    return this.perspectiveCorrectionEnabled;
  }
}
