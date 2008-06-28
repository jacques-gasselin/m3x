package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.M3GSupport;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationController extends Object3D implements M3GTypedObject
{
  private float speed;
  private float weight;
  private int activeIntervalStart;
  private int activeIntervalEnd;
  private float referenceSequenceTime;
  private int referenceWorldTime;
  
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
  
  public AnimationController(ObjectIndex[] animationTracks,
      UserParameter[] userParameters)
  {
    super(animationTracks, userParameters);
  }

  public void deserialize(DataInputStream dataInputStream, String version)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, version);
    this.speed = Float.intBitsToFloat(M3GSupport.swapBytes(dataInputStream.readInt()));
    this.weight = Float.intBitsToFloat(M3GSupport.swapBytes(dataInputStream.readInt()));
    this.activeIntervalStart = M3GSupport.swapBytes(dataInputStream.readInt());
    this.activeIntervalEnd = M3GSupport.swapBytes(dataInputStream.readInt());
    this.referenceSequenceTime = Float.intBitsToFloat(M3GSupport.swapBytes(dataInputStream.readInt()));
    this.referenceWorldTime = M3GSupport.swapBytes(dataInputStream.readInt());
  }

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
  
  public byte getObjectType()
  {
    return ObjectTypes.ANIMATION_CONTROLLER;
  }  
}
