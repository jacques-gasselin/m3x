package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * See http://java2me.org/m3g/file-format.html#KeyframeSequence<br>
  Byte          interpolation;<br>
  Byte          repeatMode;<br>
  Byte          encoding;<br>
  UInt32        duration;<br>
  UInt32        validRangeFirst;<br>
  UInt32        validRangeLast;<br>
  <br>
  UInt32        componentCount;<br>
  UInt32        keyframeCount;<br>
  <br>
  IF encoding == 0<br>
    FOR each key frame...<br>
        Int32                   time;<br>
        Float32[componentCount] vectorValue;<br>
    END<br>
  ELSE IF encoding == 1<br>
    Float32[componentCount] vectorBias;<br>
    Float32[componentCount] vectorScale;<br>
    FOR each key frame...<br>
        Int32                time;<br>
        Byte[componentCount] vectorValue;<br>
    END<br>
  ELSE IF encoding == 2<br>
    Float32[componentCount] vectorBias;<br>
    Float32[componentCount] vectorScale;<br>
    FOR each key frame...<br>
        Int32                  time;<br>
        UInt16[componentCount] vectorValue;<br>
    END<br>
  END<br>
  <br>
 * @author jsaarinen
 */
public class KeyframeSequence extends Object3D implements M3GTypedObject
{
    /**
     * Abstract base class for all types of key frames.
     *
     * @author jsaarinen
     */
    public static abstract class KeyFrame
    {

        private int time;

        public KeyFrame(int time)
        {
            this.time = time;
        }

        public int getTime()
        {
            return this.time;
        }

        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof KeyFrame))
            {
                return false;
            }
            return this.time == ((KeyFrame) obj).time;
        }
    }

    /**
     * Keyframe class in floating point.
     *
     * @author jsaarinen
     */
    public static class FloatKeyFrame extends KeyFrame
    {

        private float[] vectorValue;

        public FloatKeyFrame(int time, float[] vectorValue)
        {
            super(time);
            this.vectorValue = vectorValue;
        }

        public float[] getVectorValue()
        {
            return this.vectorValue;
        }

        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof FloatKeyFrame))
            {
                return false;
            }
            FloatKeyFrame another = (FloatKeyFrame) obj;
            return super.equals(obj) && Arrays.equals(this.vectorValue, another.vectorValue);
        }
    }

    /**
     * Keyframe class in integer.
     *
     * @author jsaarinen
     */
    public static class ByteKeyFrame extends KeyFrame
    {

        private byte[] vectorValue;

        public ByteKeyFrame(int time, byte[] vectorValue)
        {
            super(time);
            this.vectorValue = vectorValue;
        }

        public byte[] getVectorValue()
        {
            return this.vectorValue;
        }

        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof ByteKeyFrame))
            {
                return false;
            }
            ByteKeyFrame another = (ByteKeyFrame) obj;
            return super.equals(obj) && Arrays.equals(this.vectorValue, another.vectorValue);
        }
    }

    /**
     * Keyframe class in integer.
     *
     * @author jsaarinen
     */
    public static class ShortKeyFrame extends KeyFrame
    {

        private short[] vectorValue;

        public ShortKeyFrame(int time, short[] vectorValue)
        {
            super(time);
            this.vectorValue = vectorValue;
        }

        public short[] getVectorValue()
        {
            return this.vectorValue;
        }

        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof ShortKeyFrame))
            {
                return false;
            }
            ShortKeyFrame another = (ShortKeyFrame) obj;
            return super.equals(obj) && Arrays.equals(this.vectorValue, another.vectorValue);
        }
    }

    public static final int CONSTANT = 192;
    public static final int LINEAR = 176;
    public static final int LOOP = 193;
    public static final int SLERP = 177;
    public static final int SPLINE = 178;
    public static final int SQUAD = 179;
    public static final int STEP = 180;
    private static final Set<Integer> INTERPOLATION_MODES = new HashSet<Integer>();
    private static final Set<Integer> REPEAT_MODES = new HashSet<Integer>();


    static
    {
        INTERPOLATION_MODES.add(LINEAR);
        INTERPOLATION_MODES.add(SLERP);
        INTERPOLATION_MODES.add(SPLINE);
        INTERPOLATION_MODES.add(SQUAD);
        INTERPOLATION_MODES.add(STEP);

        REPEAT_MODES.add(CONSTANT);
        REPEAT_MODES.add(LOOP);
    }
    
    private static final int ENCODING_FLOATS = 0;
    private static final int ENCODING_BYTES = 1;
    private static final int ENCODING_SHORTS = 2;
    private int interpolation;
    private int repeatMode;
    private int encoding;
    private int duration;
    private int validRangeFirst;
    private int validRangeLast;
    private int componentCount;
    private int keyframeCount;
    private FloatKeyFrame[] floatKeyFrames;
    private ByteKeyFrame[] byteKeyFrames;
    private ShortKeyFrame[] shortKeyFrames;
    private float[] vectorBias;
    private float[] vectorScale;

    public KeyframeSequence(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, int interpolation, int repeatMode,
        int duration, int validRangeFirst, int validRangeLast,
        int componentCount, FloatKeyFrame[] keyFrames) throws FileFormatException
    {
        super(animationTracks, userParameters);
        assert (keyFrames != null);
        validateInterpolationType(interpolation);
        this.interpolation = interpolation;
        validateRepeatMode(repeatMode);
        this.repeatMode = repeatMode;
        this.encoding = ENCODING_FLOATS;
        this.duration = duration;
        this.validRangeFirst = validRangeFirst;
        this.validRangeLast = validRangeLast;
        this.componentCount = componentCount;
        this.keyframeCount = keyFrames.length;
        this.floatKeyFrames = keyFrames;
        this.byteKeyFrames = null;
        this.shortKeyFrames = null;
        this.vectorBias = null;
        this.vectorScale = null;
    }

    private static void validateRepeatMode(int repeatMode) throws FileFormatException
    {
        if (!REPEAT_MODES.contains(repeatMode))
        {
            throw new FileFormatException("Invalid repeat mode: " + repeatMode);
        }
    }

    private static void validateInterpolationType(int interpolation)
        throws FileFormatException
    {
        if (!INTERPOLATION_MODES.contains(interpolation))
        {
            throw new FileFormatException("Invalid interpolation type: " + interpolation);
        }
    }

    public KeyframeSequence()
    {
        super();
    // TODO Auto-generated constructor stub
    }

    public KeyframeSequence(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, int interpolation, int repeatMode,
        int duration, int validRangeFirst, int validRangeLast, int componentCount,
        ByteKeyFrame[] keyFrames, float[] vectorBias, float[] vectorScale) throws FileFormatException
    {
        super(animationTracks, userParameters);
        assert (keyFrames != null);
        validateInterpolationType(interpolation);
        this.interpolation = interpolation;
        validateRepeatMode(repeatMode);
        this.repeatMode = repeatMode;
        this.encoding = ENCODING_BYTES;
        this.duration = duration;
        this.validRangeFirst = validRangeFirst;
        this.validRangeLast = validRangeLast;
        this.componentCount = componentCount;
        this.keyframeCount = keyFrames.length;
        this.floatKeyFrames = null;
        this.byteKeyFrames = keyFrames;
        this.shortKeyFrames = null;
        this.vectorBias = vectorBias;
        this.vectorScale = vectorScale;
    }

    public KeyframeSequence(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, int interpolation, int repeatMode,
        int duration, int validRangeFirst, int validRangeLast, int componentCount,
        ShortKeyFrame[] keyFrames, float[] vectorBias, float[] vectorScale) throws FileFormatException
    {
        super(animationTracks, userParameters);
        validateInterpolationType(interpolation);
        this.interpolation = interpolation;
        validateRepeatMode(repeatMode);
        this.repeatMode = repeatMode;
        this.encoding = ENCODING_SHORTS;
        this.duration = duration;
        this.validRangeFirst = validRangeFirst;
        this.validRangeLast = validRangeLast;
        this.componentCount = componentCount;
        this.keyframeCount = keyFrames.length;
        this.floatKeyFrames = null;
        this.byteKeyFrames = null;
        this.shortKeyFrames = keyFrames;
        this.vectorBias = vectorBias;
        this.vectorScale = vectorScale;
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.interpolation = deserialiser.readUnsignedByte();
        validateInterpolationType(this.interpolation);
        this.repeatMode = deserialiser.readUnsignedByte();
        validateRepeatMode(this.repeatMode);
        this.encoding = deserialiser.readUnsignedByte();
        this.duration = deserialiser.readInt();
        this.validRangeFirst = deserialiser.readInt();
        this.validRangeLast = deserialiser.readInt();
        this.componentCount = deserialiser.readInt();
        this.keyframeCount = deserialiser.readInt();

        switch (this.encoding)
        {
            case ENCODING_FLOATS:
                this.floatKeyFrames = new FloatKeyFrame[this.keyframeCount];
                for (int i = 0; i < this.floatKeyFrames.length; i++)
                {
                    int time = deserialiser.readInt();
                    float vectorValue[] = new float[this.componentCount];
                    for (int j = 0; j < this.componentCount; j++)
                    {
                        vectorValue[j] = deserialiser.readFloat();
                    }
                    this.floatKeyFrames[i] = new FloatKeyFrame(time, vectorValue);
                }
                break;

            case ENCODING_BYTES:
                readBiasAndScale(deserialiser);
                this.byteKeyFrames = new ByteKeyFrame[this.keyframeCount];
                for (int i = 0; i < this.keyframeCount; i++)
                {
                    int time = deserialiser.readInt();
                    byte vectorValue[] = new byte[this.componentCount];
                    for (int j = 0; j < this.componentCount; j++)
                    {
                        vectorValue[j] = deserialiser.readByte();
                    }
                    this.byteKeyFrames[i] = new ByteKeyFrame(time, vectorValue);
                }
                break;

            case ENCODING_SHORTS:
                readBiasAndScale(deserialiser);
                this.shortKeyFrames = new ShortKeyFrame[this.keyframeCount];
                for (int i = 0; i < this.keyframeCount; i++)
                {
                    int time = deserialiser.readInt();
                    short vectorValue[] = new short[this.componentCount];
                    for (int j = 0; j < this.componentCount; j++)
                    {
                        vectorValue[j] = deserialiser.readShort();
                    }
                    this.shortKeyFrames[i] = new ShortKeyFrame(time, vectorValue);
                }
                break;

            default:
                throw new FileFormatException("Invalid encoding: " + this.encoding);
        }
    }

    private void readBiasAndScale(M3GDeserialiser deserialiser) throws IOException
    {
        this.vectorBias = new float[this.componentCount];
        for (int i = 0; i < this.componentCount; i++)
        {
            this.vectorBias[i] = deserialiser.readFloat();
        }

        this.vectorScale = new float[this.componentCount];
        for (int i = 0; i < this.componentCount; i++)
        {
            this.vectorScale[i] = deserialiser.readFloat();
        }
    }

    @Override
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

        M3GSupport.writeInt(dataOutputStream, componentCount);
        M3GSupport.writeInt(dataOutputStream, keyframeCount);

        switch (this.encoding)
        {
            case ENCODING_FLOATS:
                for (FloatKeyFrame keyFrame : this.floatKeyFrames)
                {
                    M3GSupport.writeInt(dataOutputStream, keyFrame.getTime());
                    for (float component : keyFrame.getVectorValue())
                    {
                        M3GSupport.writeFloat(dataOutputStream, component);
                    }
                }
                break;

            case ENCODING_BYTES:
                writeBiasAndScale(dataOutputStream);
                for (ByteKeyFrame keyFrame : this.byteKeyFrames)
                {
                    M3GSupport.writeInt(dataOutputStream, keyFrame.getTime());
                    for (byte component : keyFrame.getVectorValue())
                    {
                        dataOutputStream.write(component);
                    }
                }
                break;

            case ENCODING_SHORTS:
                writeBiasAndScale(dataOutputStream);
                for (ShortKeyFrame keyFrame : this.shortKeyFrames)
                {
                    M3GSupport.writeInt(dataOutputStream, keyFrame.getTime());
                    for (short component : keyFrame.getVectorValue())
                    {
                        M3GSupport.writeShort(dataOutputStream, component);
                    }
                }
                break;

            default:
                assert (false);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.KEYFRAME_SEQUENCE;
    }

    public int getInterpolation()
    {
        return this.interpolation;
    }

    public int getRepeatMode()
    {
        return this.repeatMode;
    }

    public int getEncoding()
    {
        return this.encoding;
    }

    public int getDuration()
    {
        return this.duration;
    }

    public int getValidRangeFirst()
    {
        return this.validRangeFirst;
    }

    public int getValidRangeLast()
    {
        return this.validRangeLast;
    }

    public FloatKeyFrame[] getFloatKeyFrames()
    {
        return this.floatKeyFrames;
    }

    public ByteKeyFrame[] getByteKeyFrames()
    {
        return this.byteKeyFrames;
    }

    public ShortKeyFrame[] getShortKeyFrames()
    {
        return this.shortKeyFrames;
    }

    public float[] getVectorBias()
    {
        return this.vectorBias;
    }

    public float[] getVectorScale()
    {
        return this.vectorScale;
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
