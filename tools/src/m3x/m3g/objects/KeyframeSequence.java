package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

public class KeyframeSequence extends Object3D implements M3GTypedObject
{
  public class KeyFrame
  {
    public int time;
  }

  public class FloatKeyFrame extends KeyFrame
  {
    public float[] vectorValue;

  }

  public class ByteKeyFrame extends KeyFrame
  {
    public byte[] vectorValue;
  }

  public class ShortKeyFrame extends KeyFrame
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

  private int interpolation;
  private int repeatMode;
  private int encoding;
  private int duration;
  private int validRangeFirst;
  private int validRangeLast;
  private FloatKeyFrame[] floatKeyFrames;
  private ByteKeyFrame[] byteKeyFrames;
  private ShortKeyFrame[] shortKeyFrames;
  private float[] vectorBias;
  private float[] vectorScale;

  public KeyframeSequence(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, int interpolation, int repeatMode,
      int duration, int validRangeFirst, int validRangeLast,
      int componentCount, FloatKeyFrame[] keyFrames)
  {
    super(animationTracks, userParameters);
    assert (keyFrames != null);
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
    assert (keyFrames != null);
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

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, m3gVersion);
    this.interpolation = dataInputStream.readByte();
    this.repeatMode = dataInputStream.readByte();
    this.encoding = dataInputStream.readByte();
    this.duration = M3GSupport.readInt(dataInputStream);
    this.validRangeFirst = M3GSupport.readInt(dataInputStream);
    this.validRangeLast = M3GSupport.readInt(dataInputStream);
    int componentCount = M3GSupport.readInt(dataInputStream);
    int keyFrameCount = M3GSupport.readInt(dataInputStream);
    
    switch (this.encoding)
    {
      case ENCODING_FLOATS:
        this.floatKeyFrames = new FloatKeyFrame[keyFrameCount];
        for (int i = 0; i < keyFrameCount; i++)
        {
          this.floatKeyFrames[i] = new FloatKeyFrame();
          this.floatKeyFrames[i].time = M3GSupport.readInt(dataInputStream);
          this.floatKeyFrames[i].vectorValue = new float[componentCount];
          for (int j = 0; i < componentCount; i++)
          {
            this.floatKeyFrames[i].vectorValue[j] = M3GSupport.readFloat(dataInputStream);
          }
        }
        break;
        
      case ENCODING_BYTES:
        readBiasAndScale(dataInputStream, componentCount);
        this.byteKeyFrames = new ByteKeyFrame[keyFrameCount];
        for (int i = 0; i < keyFrameCount; i++)
        {
          this.byteKeyFrames[i] = new ByteKeyFrame();
          this.byteKeyFrames[i].time = M3GSupport.readInt(dataInputStream);
          this.byteKeyFrames[i].vectorValue = new byte[componentCount];
          for (int j = 0; i < componentCount; i++)
          {
            this.byteKeyFrames[i].vectorValue[j] = dataInputStream.readByte();
          }
        }
        break;
        
      case ENCODING_SHORTS:
        readBiasAndScale(dataInputStream, componentCount);
        this.shortKeyFrames = new ShortKeyFrame[keyFrameCount];
        for (int i = 0; i < keyFrameCount; i++)
        {
          this.shortKeyFrames[i] = new ShortKeyFrame();
          this.shortKeyFrames[i].time = M3GSupport.readShort(dataInputStream);
          this.shortKeyFrames[i].vectorValue = new short[componentCount];
          for (int j = 0; i < componentCount; i++)
          {
            this.shortKeyFrames[i].vectorValue[j] = M3GSupport.readShort(dataInputStream);
          }
        }
        break;
        
      default:
        throw new FileFormatException("Invalid encoding: " + this.encoding);
    }
  }

  private void readBiasAndScale(DataInputStream dataInputStream, int componentCount) throws IOException
  {    
    for (int i = 0; i < componentCount; i++)
    {
      this.vectorBias[i] = M3GSupport.readFloat(dataInputStream);
    }
    for (int i = 0; i < componentCount; i++)
    {
      this.vectorScale[i] = M3GSupport.readFloat(dataInputStream);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.interpolation);
    dataOutputStream.write(this.repeatMode);
    dataOutputStream.write(this.encoding);
    M3GSupport.writeInt(dataOutputStream, this.duration);
    M3GSupport.writeInt(dataOutputStream, this.validRangeFirst);
    M3GSupport.writeInt(dataOutputStream, this.validRangeLast);

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

    M3GSupport.writeInt(dataOutputStream, componentCount);
    M3GSupport.writeInt(dataOutputStream, keyFrameCount);

    switch (this.encoding)
    {
      case ENCODING_FLOATS:
        for (FloatKeyFrame keyFrame : this.floatKeyFrames)
        {
          M3GSupport.writeInt(dataOutputStream, keyFrame.time);
          for (float component : keyFrame.vectorValue)
          {
            M3GSupport.writeFloat(dataOutputStream, component);
          }
        }
        break;

      case ENCODING_BYTES:
        writeBiasAndScale(dataOutputStream);
        for (ByteKeyFrame keyFrame : this.byteKeyFrames)
        {
          M3GSupport.writeInt(dataOutputStream, keyFrame.time);
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
          M3GSupport.writeInt(dataOutputStream, keyFrame.time);
          for (short component : keyFrame.vectorValue)
          {
            M3GSupport.writeShort(dataOutputStream, component);
          }
        }
        break;

      default:
        assert (false);
    }
  }

  
  public byte getObjectType()
  {
    return ObjectTypes.KEYFRAME_SEQUENCE;
  }

  private void writeBiasAndScale(DataOutputStream dataOutputStream)
      throws IOException
  {
    for (float component : this.vectorBias)
    {
      M3GSupport.writeFloat(dataOutputStream, component);
    }
    for (float component : this.vectorScale)
    {
      M3GSupport.writeFloat(dataOutputStream, component);
    }
  }
}
