package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class Group extends Node implements M3GTypedObject
{
  private ObjectIndex[] children;

  public Group(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, ObjectIndex[] children)
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope);
    assert (children != null);
    this.children = children;
  }
  
  public Group()
  {
    super();
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {    
    for (ObjectIndex objectIndex : this.children)
    {
      objectIndex.deserialize(dataInputStream, m3gVersion);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    for (ObjectIndex child : this.children)
    {
      child.serialize(dataOutputStream, m3gVersion);
    }
  }
  
  public byte getObjectType()
  {
    return ObjectTypes.GROUP;
  }
}
