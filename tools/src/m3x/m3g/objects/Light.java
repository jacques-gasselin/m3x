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

public class Light extends Node implements M3GTypedObject
{
  public final static int MODE_AMBIENT = 128;
  public final static int MODE_DIRECTIONAL = 129;
  public final static int MODE_OMNI = 130;
  public final static int MODE_SPOT = 131;

  private final float attenuationConstant;
  private final float attenuationLinear;
  private final float attenuationQuadratic;
  private final ColorRGB color;
  private final int mode;
  private final float intensity;
  private final float spotAngle;
  private final float spotExponent;

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

  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {    
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
}
