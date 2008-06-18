package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationController extends Object3D implements M3GSerializable
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
    dataOutputStream.write(M3GSupport.swapBytes(this.speed));
    dataOutputStream.write(M3GSupport.swapBytes(this.weight));
    dataOutputStream.write(M3GSupport.swapBytes(this.activeIntervalStart));
    dataOutputStream.write(M3GSupport.swapBytes(this.activeIntervalEnd));
    dataOutputStream.write(M3GSupport.swapBytes(this.referenceSequenceTime));
    dataOutputStream.write(M3GSupport.swapBytes(this.referenceWorldTime));
  }  
}
