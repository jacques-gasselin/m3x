package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GTypedObject;
import m3x.m3g.M3GSupport;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationController extends Object3D implements M3GTypedObject
{
  private final float speed;
  private final float weight;
  private final int activeIntervalStart;
  private final int activeIntervalEnd;
  private final float referenceSequenceTime;
  private final int referenceWorldTime;
  
  public AnimationController(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, float speed, float weight,
      int activeIntervalStart, int activeIntervalEnd,
      float referenceSequenceTime, int referenceWorldTime)
  {
    super(animationTracks, userParameters);
    this.speed = speed;
    this.weight = weight;
    this.activeIntervalStart = activeIntervalStart;
    this.activeIntervalEnd = activeIntervalEnd;
    this.referenceSequenceTime = referenceSequenceTime;
    this.referenceWorldTime = referenceWorldTime;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.speed));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.weight));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.activeIntervalStart));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.activeIntervalEnd));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.referenceSequenceTime));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.referenceWorldTime));
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.ANIMATION_CONTROLLER;
  }  
}
