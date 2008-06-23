package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class Light extends Node implements M3GSerializable 
{
	public final static byte MODE_AMBIENT = (byte)128;
	public final static byte MODE_DIRECTIONAL = (byte)129;
	public final static byte MODE_OMNI = (byte)130;
	public final static byte MODE_SPOT = (byte)131;
	
	private final float attenuationConstant;
	private final float attenuationLinear;
	private final float attenuationQuadratic;
	private final ColorRGB color;
	private final byte mode;
	private final float intensity;
	private final float spotAngle;
	private final float spotExponent;
	
	public Light(ObjectIndex[] animationTracks, UserParameter[] userParameters,
			Matrix transform, boolean enableRendering, boolean enablePicking,
			byte alphaFactor, int scope, float attenuationConstant,
			float attenuationLinear, float attenuationQuadratic,
			ColorRGB color, byte mode, float intensity, float spotAngle,
			float spotExponent) 
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

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.attenuationConstant));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.attenuationLinear));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.attenuationQuadratic));
    this.color.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.mode);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.intensity));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.spotAngle));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.spotExponent));
  }
}
