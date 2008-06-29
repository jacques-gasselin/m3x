package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class MorphingMesh extends Node implements M3GTypedObject
{
  public class TargetBuffer implements M3GSerializable
  {
    public ObjectIndex morphTarget;
    public float initialWeight;

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {      
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
      this.morphTarget.serialize(dataOutputStream, m3gVersion);
      dataOutputStream.writeInt(M3GSupport.swapBytes(this.initialWeight));
    }
  }

  private final int morphTargetCount;
  private final TargetBuffer[] morphTargets;

  public MorphingMesh(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, Matrix transform,
      boolean enableRendering, boolean enablePicking, byte alphaFactor,
      int scope, TargetBuffer[] morphTargets)
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope);
    assert (morphTargets != null);
    assert (morphTargets.length > 0);
    this.morphTargetCount = morphTargets.length;
    this.morphTargets = morphTargets;
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.morphTargetCount));
    for (TargetBuffer targetBuffer : this.morphTargets)
    {
      targetBuffer.serialize(dataOutputStream, m3gVersion);
    }
  }

  
  public byte getObjectType()
  {
    return ObjectTypes.MORPHING_MESH;
  }
}
