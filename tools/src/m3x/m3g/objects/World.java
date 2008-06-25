package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class World extends Group implements M3GTypedObject
{
  private final ObjectIndex activeCamera;
  private final ObjectIndex background;
  
  public World(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, ObjectIndex[] children,
      ObjectIndex activeCamera, ObjectIndex background)
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope, children);
    this.activeCamera = activeCamera;
    this.background = background;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.activeCamera.serialize(dataOutputStream, m3gVersion);
    this.background.serialize(dataOutputStream, m3gVersion);
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.WORLD;
  }
}
