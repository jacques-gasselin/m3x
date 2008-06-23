package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class Group extends Object3D implements M3GSerializable 
{
	private final ObjectIndex[] children;

	public Group(ObjectIndex[] animationTracks, UserParameter[] userParameters,
			ObjectIndex[] children) 
	{
		super(animationTracks, userParameters);
		assert(children != null);
		this.children = children;
	}

	@Override
	public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
			throws IOException 
	{
		super.serialize(dataOutputStream, m3gVersion);
		for (ObjectIndex child : this.children)
		{
			child.serialize(dataOutputStream, m3gVersion);
		}
	}	
}
