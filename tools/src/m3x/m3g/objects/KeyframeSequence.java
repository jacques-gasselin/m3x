package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class KeyframeSequence extends Object3D implements M3GTypedObject 
{
  public static class KeyFrame
  {
    public int time;
  }
  
  public static class FloatKeyFrame extends KeyFrame
  {
    public float[] vectorValue;
    
    
  }
  
  public static class ByteKeyFrame extends KeyFrame
  {
    public byte[] vectorValue;
  }
  
  public static class ShortKeyFrame extends KeyFrame
  {
    public short[] vectorValue;
  }
  
  public static final int CONSTANT = 192;
  public static final int LINEAR = 176;
  public static final int LOOP = 193;
  public static final int SLERP = 177;
  public static final int SPLINE = 178;
  public static final int SQUAD = 179;
  public static final int STEP = 180;
  
  private static final int ENCODING_FLOATS = 0;
  private static final int ENCODING_BYTES = 1;
  private static final int ENCODING_SHORTS = 2;
  
	private final int interpolation;
	private final int repeatMode;
	private final int encoding;
	private final int duration;
	private final int validRangeFirst;
	private final int validRangeLast;
	private final FloatKeyFrame[] floatKeyFrames;
	private final ByteKeyFrame[] byteKeyFrames;
	private final ShortKeyFrame[] shortKeyFrames;
	private final float[] vectorBias;
	private final float[] vectorScale;
	
  public KeyframeSequence(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, int interpolation, int repeatMode,
      int duration, int validRangeFirst, int validRangeLast,
      int componentCount, FloatKeyFrame[] keyFrames)
  {
    super(animationTracks, userParameters);
    assert(keyFrames != null);
    this.interpolation = interpolation;
    this.repeatMode = repeatMode;
    this.encoding = ENCODING_FLOATS;
    this.duration = duration;
    this.validRangeFirst = validRangeFirst;
    this.validRangeLast = validRangeLast;
    this.floatKeyFrames = keyFrames;
    this.byteKeyFrames = null;
    this.shortKeyFrames = null;
    this.vectorBias = null;
    this.vectorScale = null;
  }

  public KeyframeSequence(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, int interpolation, int repeatMode,
      int duration, int validRangeFirst, int validRangeLast,
      ByteKeyFrame[] keyFrames, float[] vectorBias, float[] vectorScale)
  {
    super(animationTracks, userParameters);
    assert(keyFrames != null);
    this.interpolation = interpolation;
    this.repeatMode = repeatMode;
    this.encoding = ENCODING_BYTES;
    this.duration = duration;
    this.validRangeFirst = validRangeFirst;
    this.validRangeLast = validRangeLast;
    this.floatKeyFrames = null;
    this.byteKeyFrames = keyFrames;
    this.shortKeyFrames = null;
    this.vectorBias = vectorBias;
    this.vectorScale = vectorScale;
  }

  public KeyframeSequence(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, int interpolation, int repeatMode,
      int duration, int validRangeFirst, int validRangeLast,
      ShortKeyFrame[] keyFrames, float[] vectorBias, float[] vectorScale)
  {
    super(animationTracks, userParameters);
    this.interpolation = interpolation;
    this.repeatMode = repeatMode;
    this.encoding = ENCODING_SHORTS;
    this.duration = duration;
    this.validRangeFirst = validRangeFirst;
    this.validRangeLast = validRangeLast;
    this.floatKeyFrames = null;
    this.byteKeyFrames = null;
    this.shortKeyFrames = keyFrames;
    this.vectorBias = vectorBias;
    this.vectorScale = vectorScale;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.interpolation);
    dataOutputStream.write(this.repeatMode);
    dataOutputStream.write(this.encoding);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.duration));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.validRangeFirst));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.validRangeLast));
    
    int componentCount;
    int keyFrameCount;
    switch (this.encoding)
    {
      case ENCODING_FLOATS: 
        componentCount = this.floatKeyFrames[0].vectorValue.length;
        keyFrameCount = this.floatKeyFrames.length;
        break;
        
      case ENCODING_BYTES: 
        componentCount = this.byteKeyFrames[0].vectorValue.length;
        keyFrameCount = this.byteKeyFrames.length;
        break;
        
      case ENCODING_SHORTS: 
        componentCount = this.shortKeyFrames[0].vectorValue.length;
        keyFrameCount = this.shortKeyFrames.length;
        break;
        
      default:
        throw new IOException("Invalid encoding: " + this.encoding);
    }
    
    dataOutputStream.writeInt(M3GSupport.swapBytes(componentCount));
    dataOutputStream.writeInt(M3GSupport.swapBytes(keyFrameCount));
    
    switch (this.encoding)
    {
      case ENCODING_FLOATS:
        for (FloatKeyFrame keyFrame : this.floatKeyFrames)
        {
          dataOutputStream.writeInt(M3GSupport.swapBytes(keyFrame.time));
          for (float component : keyFrame.vectorValue)
          {
            dataOutputStream.writeInt(M3GSupport.swapBytes(component));            
          }
        }
        break;

      case ENCODING_BYTES:
        writeBiasAndScale(dataOutputStream);
        for (ByteKeyFrame keyFrame : this.byteKeyFrames)
        {
          dataOutputStream.writeInt(M3GSupport.swapBytes(keyFrame.time));
          for (byte component : keyFrame.vectorValue)
          {
            dataOutputStream.write(component);            
          }
        }
        break;

      case ENCODING_SHORTS:
        writeBiasAndScale(dataOutputStream);
        for (ShortKeyFrame keyFrame : this.shortKeyFrames)
        {
          dataOutputStream.writeInt(M3GSupport.swapBytes(keyFrame.time));
          for (short component : keyFrame.vectorValue)
          {
            dataOutputStream.writeShort(M3GSupport.swapBytes(component));            
          }
        }
        break;
        
      default:
        assert(false);
    }
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.KEYFRAME_SEQUENCE;
  }

  private void writeBiasAndScale(DataOutputStream dataOutputStream)
      throws IOException
  {
    for (float component : this.vectorBias)
    {
      dataOutputStream.writeInt(M3GSupport.swapBytes(component));                      
    }
    for (float component : this.vectorScale)
    {
      dataOutputStream.writeInt(M3GSupport.swapBytes(component));                      
    }
  }
}
