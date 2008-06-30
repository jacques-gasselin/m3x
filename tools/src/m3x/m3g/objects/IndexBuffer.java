package m3x.m3g.objects;

import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.M3GSerializable;

public abstract class IndexBuffer extends Object3D implements M3GSerializable
{
  public IndexBuffer(ObjectIndex[] animationTracks,
      UserParameter[] userParameters)
  {
    super(animationTracks, userParameters);
  }

  public IndexBuffer()
  {
    super();
  }
}
