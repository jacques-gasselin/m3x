package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;

public class Material extends Object3D implements M3GTypedObject
{
  private final ColorRGB ambientColor;
  private final ColorRGBA diffuseColor;
  private final ColorRGB emissiveColor;
  private final ColorRGB specularColor;
  private final float shininess;
  private final boolean vertexColorTrackingEnabled;

  public Material(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, ColorRGB ambientColor,
      ColorRGBA diffuseColor, ColorRGB emissiveColor, ColorRGB specularColor,
      float shininess, boolean vertexColorTrackingEnabled)
  {
    super(animationTracks, userParameters);
    this.ambientColor = ambientColor;
    this.diffuseColor = diffuseColor;
    this.emissiveColor = emissiveColor;
    this.specularColor = specularColor;
    this.shininess = shininess;
    this.vertexColorTrackingEnabled = vertexColorTrackingEnabled;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.ambientColor.serialize(dataOutputStream, m3gVersion);
    this.diffuseColor.serialize(dataOutputStream, m3gVersion);
    this.emissiveColor.serialize(dataOutputStream, m3gVersion);
    this.specularColor.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.shininess));
    dataOutputStream.writeBoolean(this.vertexColorTrackingEnabled);
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.MATERIAL;
  }
}
