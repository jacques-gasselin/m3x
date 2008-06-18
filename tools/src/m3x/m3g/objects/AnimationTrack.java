package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationTrack extends Object3D implements M3GSerializable
{
  private final ObjectIndex keyframeSequence;
  private final ObjectIndex animationController;
  private final int propertyID;
  
  public AnimationTrack(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, ObjectIndex keyframeSequence,
      ObjectIndex animationController, int propertyID)
  {
    super(animationTracks, userParameters);
    this.keyframeSequence = keyframeSequence;
    this.animationController = animationController;
    this.propertyID = propertyID;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.keyframeSequence.serialize(dataOutputStream, null);
    this.animationController.serialize(dataOutputStream, null);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.propertyID));
  }
}
