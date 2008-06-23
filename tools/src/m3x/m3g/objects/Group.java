package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class Group extends Node implements M3GSerializable 
{
	private final ObjectIndex[] children;

	public Group(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, ObjectIndex[] children)
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope);
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
