package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.primitives.Vector3D;

public abstract class Node extends Transformable implements M3GSerializable
{
	private final boolean enableRendering;
	private final boolean enablePicking;
	private final byte alphaFactor;
	private final int scope;
	private final boolean hasAlignment;
	
	private final byte zTarget;
	private final byte yTarget;
	private final ObjectIndex zReference;
	private final ObjectIndex yReference;
	
	public Node(ObjectIndex[] animationTracks, UserParameter[] userParameters,
			Matrix transform,
			boolean enableRendering,
			boolean enablePicking,
			byte alphaFactor,
			int scope) 
	{
		super(animationTracks, userParameters, transform);
		this.enableRendering = enableRendering;
		this.enablePicking = enablePicking;
		this.alphaFactor = alphaFactor;
		this.scope = scope;
		this.hasAlignment = false;
		this.zTarget = 0;
		this.yTarget = 0;
		this.zReference = null;
		this.yReference = null;
	}

	public Node(ObjectIndex[] animationTracks, UserParameter[] userParameters,
			Matrix transform,
			boolean enableRendering,
			boolean enablePicking,
			byte alphaFactor,
			int scope,
			byte zTarget,
			byte yTarget,
			ObjectIndex zReference,
			ObjectIndex yReference) 
	{
		super(animationTracks, userParameters, transform);
		this.enableRendering = enableRendering;
		this.enablePicking = enablePicking;
		this.alphaFactor = alphaFactor;
		this.scope = scope;
		this.hasAlignment = true;
		this.zTarget = zTarget;
		this.yTarget = yTarget;
		this.zReference = zReference;
		this.yReference = yReference;
	}

	@Override
	public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
			throws IOException 
	{
		super.serialize(dataOutputStream, m3gVersion);
		dataOutputStream.writeBoolean(this.enableRendering);
		dataOutputStream.writeBoolean(this.enablePicking);
		dataOutputStream.write(this.alphaFactor);
		dataOutputStream.write(M3GSupport.swapBytes(this.scope));
		if (this.hasAlignment)
		{
			dataOutputStream.write(this.zTarget);
			dataOutputStream.write(this.yTarget);
			this.zReference.serialize(dataOutputStream, m3gVersion);
			this.yReference.serialize(dataOutputStream, m3gVersion);
			
		}
	}	
}