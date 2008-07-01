package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;

public class Material extends Object3D implements M3GTypedObject
{
  private ColorRGB ambientColor;
  private ColorRGBA diffuseColor;
  private ColorRGB emissiveColor;
  private ColorRGB specularColor;
  private float shininess;
  private boolean vertexColorTrackingEnabled;

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

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {    
    this.ambientColor = new ColorRGB();
    this.ambientColor.deserialize(dataInputStream, m3gVersion);
    this.diffuseColor = new ColorRGBA();
    this.diffuseColor.deserialize(dataInputStream, m3gVersion);
    this.emissiveColor = new ColorRGB();
    this.emissiveColor.deserialize(dataInputStream, m3gVersion);
    this.specularColor = new ColorRGB();
    this.specularColor.deserialize(dataInputStream, m3gVersion);
    this.shininess = M3GSupport.readFloat(dataInputStream);
    this.vertexColorTrackingEnabled = dataInputStream.readBoolean();
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.ambientColor.serialize(dataOutputStream, m3gVersion);
    this.diffuseColor.serialize(dataOutputStream, m3gVersion);
    this.emissiveColor.serialize(dataOutputStream, m3gVersion);
    this.specularColor.serialize(dataOutputStream, m3gVersion);
    M3GSupport.writeFloat(dataOutputStream, this.shininess);
    dataOutputStream.writeBoolean(this.vertexColorTrackingEnabled);
  }
 
  public byte getObjectType()
  {
    return ObjectTypes.MATERIAL;
  }

  public ColorRGB getAmbientColor()
  {
    return this.ambientColor;
  }

  public ColorRGBA getDiffuseColor()
  {
    return this.diffuseColor;
  }

  public ColorRGB getEmissiveColor()
  {
    return this.emissiveColor;
  }

  public ColorRGB getSpecularColor()
  {
    return this.specularColor;
  }

  public float getShininess()
  {
    return this.shininess;
  }

  public boolean isVertexColorTrackingEnabled()
  {
    return this.vertexColorTrackingEnabled;
  }
}
