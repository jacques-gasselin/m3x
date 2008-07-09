package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

/**
  Float32       attenuationConstant;
  Float32       attenuationLinear;
  Float32       attenuationQuadratic;
  ColorRGB      color;
  Byte          mode;
  Float32       intensity;
  Float32       spotAngle;
  Float32       spotExponent;

 * @author jsaarinen
 */
public class Light extends Node implements M3GTypedObject
{
  public final static int MODE_AMBIENT = 128;
  public final static int MODE_DIRECTIONAL = 129;
  public final static int MODE_OMNI = 130;
  public final static int MODE_SPOT = 131;

  private float attenuationConstant;
  private float attenuationLinear;
  private float attenuationQuadratic;
  private ColorRGB color;
  private int mode;
  private float intensity;
  private float spotAngle;
  private float spotExponent;

  public Light(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, float attenuationConstant,
      float attenuationLinear, float attenuationQuadratic, ColorRGB color,
      int mode, float intensity, float spotAngle, float spotExponent)
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope);
    this.attenuationConstant = attenuationConstant;
    this.attenuationLinear = attenuationLinear;
    this.attenuationQuadratic = attenuationQuadratic;
    this.color = color;
    this.mode = mode;
    this.intensity = intensity;
    this.spotAngle = spotAngle;
    this.spotExponent = spotExponent;
  }
  
  public Light()
  {
    super();
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {    
    super.deserialize(dataInputStream, m3gVersion);
    this.attenuationConstant = M3GSupport.readFloat(dataInputStream);
    this.attenuationLinear = M3GSupport.readFloat(dataInputStream);
    this.attenuationQuadratic = M3GSupport.readFloat(dataInputStream);
    this.color = new ColorRGB();
    this.color.deserialize(dataInputStream, m3gVersion);
    this.mode = dataInputStream.readByte();
    this.intensity = M3GSupport.readFloat(dataInputStream);
    this.spotAngle = M3GSupport.readFloat(dataInputStream);
    this.spotExponent = M3GSupport.readFloat(dataInputStream);
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    M3GSupport.writeFloat(dataOutputStream, this.attenuationConstant);
    M3GSupport.writeFloat(dataOutputStream, this.attenuationLinear);
    M3GSupport.writeFloat(dataOutputStream, this.attenuationQuadratic);
    this.color.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.mode);
    M3GSupport.writeFloat(dataOutputStream, this.intensity);
    M3GSupport.writeFloat(dataOutputStream, this.spotAngle);
    M3GSupport.writeFloat(dataOutputStream, this.spotExponent);
  }

  public byte getObjectType()
  {
    return ObjectTypes.LIGHT;
  }

  public float getAttenuationConstant()
  {
    return this.attenuationConstant;
  }

  public float getAttenuationLinear()
  {
    return this.attenuationLinear;
  }

  public float getAttenuationQuadratic()
  {
    return this.attenuationQuadratic;
  }

  public ColorRGB getColor()
  {
    return this.color;
  }

  public int getMode()
  {
    return this.mode;
  }

  public float getIntensity()
  {
    return this.intensity;
  }

  public float getSpotAngle()
  {
    return this.spotAngle;
  }

  public float getSpotExponent()
  {
    return this.spotExponent;
  }
}
