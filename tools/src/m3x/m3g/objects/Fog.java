package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ObjectIndex;

public class Fog extends Object3D implements M3GSerializable 
{
	private final static int MODE_EXPONENTIAL = 80;
	private final static int MODE_LINEAR = 81;
	
	private final ColorRGB color;
	private final int mode;
	private final float density;
	private final float near;
	private final float far;
	
	public Fog(ObjectIndex[] animationTracks, UserParameter[] userParameters,
			ColorRGB color, float density) 
	{
		super(animationTracks, userParameters);
		this.color = color;
		this.mode = MODE_EXPONENTIAL;
		this.density = density;
		this.near = 0.0f;
		this.far = 0.0f;
	}

	public Fog(ObjectIndex[] animationTracks, UserParameter[] userParameters,
			ColorRGB color, float near, float far) 
	{
		super(animationTracks, userParameters);
		this.color = color;
		this.mode = MODE_LINEAR;
		this.density = 0.0f;
		this.near = near;
		this.far = far;
	}

	@Override
	public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
			throws IOException 
	{
		super.serialize(dataOutputStream, m3gVersion);
		this.color.serialize(dataOutputStream, m3gVersion);
		dataOutputStream.write(this.mode);
		if (this.mode == MODE_EXPONENTIAL)
		{
			dataOutputStream.writeInt(M3GSupport.swapBytes(this.density));
		}
		else
		if (this.mode == MODE_LINEAR)
		{
			dataOutputStream.writeInt(M3GSupport.swapBytes(this.near));
			dataOutputStream.writeInt(M3GSupport.swapBytes(this.far));
		}
		else
		{
			assert(false);
		}
	}
}
