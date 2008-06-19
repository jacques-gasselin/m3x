package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

/**
 * This class is the data structure for Camera object in
 * M3G format, see the URL below for more information.
 * http://www.java2me.org/m3g/file-format.htm
 * 
 * @author jsaarinen
 */
public class Camera extends Node implements M3GSerializable
{
	public static final byte PROJECTION_TYPE_GENERIC = 48;
	public static final byte PROJECTION_TYPE_PARALLEL = 49;
	public static final byte PROJECTION_TYPE_PERSPECTIVE = 50;
	
	private final byte projectionType;
	private final Matrix projectionMatrix;
	private final float fovy;
	private final float aspectRatio;
	private final float near;
	private final float far;
	
	public Camera(ObjectIndex[] animationTracks,
			UserParameter[] userParameters, Matrix transform,
			boolean enableRendering, boolean enablePicking, byte alphaFactor,
			int scope, byte zTarget, byte yTarget, ObjectIndex zReference,
			ObjectIndex yReference,
			Matrix projectionMatrix) 
	{
		super(animationTracks, userParameters, transform, enableRendering,
				enablePicking, alphaFactor, scope, zTarget, yTarget, zReference,
				yReference);
		this.projectionType = PROJECTION_TYPE_GENERIC;
		this.projectionMatrix = projectionMatrix;
		this.fovy = 0.0f;
		this.aspectRatio = 0.0f;
		this.near = 0.0f;
		this.far = 0.0f;
	}

	public Camera(ObjectIndex[] animationTracks,
			UserParameter[] userParameters, Matrix transform,
			boolean enableRendering, boolean enablePicking, byte alphaFactor,
			int scope, byte projectionType,
			float fovy, float aspectRatio, float near, float far) 
	{
		super(animationTracks, userParameters, transform, enableRendering,
				enablePicking, alphaFactor, scope);
		this.projectionType = projectionType;
		this.projectionMatrix = null;
		this.fovy = fovy;
		this.aspectRatio = aspectRatio;
		this.near = near;
		this.far = far;
	}

	@Override
	public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
			throws IOException 
	{
		super.serialize(dataOutputStream, m3gVersion);
		dataOutputStream.write(this.projectionType);
		if (this.projectionType == PROJECTION_TYPE_GENERIC)
		{
			this.projectionMatrix.serialize(dataOutputStream, m3gVersion);
		}
		else
		{
			dataOutputStream.write(M3GSupport.swapBytes(this.fovy));
			dataOutputStream.write(M3GSupport.swapBytes(this.aspectRatio));
			dataOutputStream.write(M3GSupport.swapBytes(this.near));
			dataOutputStream.write(M3GSupport.swapBytes(this.far));
		}
	}
}
