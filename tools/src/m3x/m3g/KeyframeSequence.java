package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
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
 * @author jgasseli
 */
public class KeyframeSequence extends Object3D
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

        public abstract int getComponentCount();
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

        @Override
        public int getComponentCount()
        {
            return this.vectorValue.length;
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

        @Override
        public int getComponentCount()
        {
            return this.vectorValue.length;
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

        @Override
        public int getComponentCount()
        {
            return this.vectorValue.length;
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
    private int interpolationType;
    private int repeatMode;
    private int encoding;
    private int duration;
    private int validRangeFirst;
    private int validRangeLast;
    private int componentCount;
    private KeyFrame[] keyFrames;
    private float[] vectorBias;
    private float[] vectorScale;

    public KeyframeSequence()
    {
        super();
        setRepeatMode(CONSTANT);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof KeyframeSequence))
        {
            return false;
        }
        KeyframeSequence another = (KeyframeSequence) obj;
        boolean equal = super.equals(obj)
            && getInterpolationType() == another.getInterpolationType()
            && getRepeatMode() == another.getRepeatMode()
            && getEncoding() == another.getEncoding()
            && getDuration() == another.getDuration()
            && getValidRangeFirst() == another.getValidRangeLast()
            && getComponentCount() == another.getComponentCount()
            && getKeyframeCount() == another.getKeyframeCount();
        if (!equal)
        {
            return false;
        }

        //compare keyframes
        for (int i = 0; i < getKeyframeCount(); ++i)
        {
            if (!getKeyFrames()[i].equals(another.getKeyFrames()[i]))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setInterpolationType(deserialiser.readUnsignedByte());
        setRepeatMode(deserialiser.readUnsignedByte());
        this.encoding = deserialiser.readUnsignedByte();
        this.duration = deserialiser.readInt();
        this.validRangeFirst = deserialiser.readInt();
        this.validRangeLast = deserialiser.readInt();
        this.componentCount = deserialiser.readInt();
        int keyframeCount = deserialiser.readInt();

        switch (this.encoding)
        {
            case ENCODING_FLOATS:
                this.keyFrames = new FloatKeyFrame[keyframeCount];
                for (int i = 0; i < getKeyframeCount(); i++)
                {
                    int time = deserialiser.readInt();
                    float vectorValue[] = new float[this.componentCount];
                    for (int j = 0; j < this.componentCount; j++)
                    {
                        vectorValue[j] = deserialiser.readFloat();
                    }
                    this.keyFrames[i] = new FloatKeyFrame(time, vectorValue);
                }
                break;

            case ENCODING_BYTES:
                readBiasAndScale(deserialiser);
                this.keyFrames = new ByteKeyFrame[keyframeCount];
                for (int i = 0; i < getKeyframeCount(); i++)
                {
                    int time = deserialiser.readInt();
                    byte vectorValue[] = new byte[this.componentCount];
                    for (int j = 0; j < this.componentCount; j++)
                    {
                        vectorValue[j] = deserialiser.readByte();
                    }
                    this.keyFrames[i] = new ByteKeyFrame(time, vectorValue);
                }
                break;

            case ENCODING_SHORTS:
                readBiasAndScale(deserialiser);
                this.keyFrames = new ShortKeyFrame[keyframeCount];
                for (int i = 0; i < getKeyframeCount(); i++)
                {
                    int time = deserialiser.readInt();
                    short vectorValue[] = new short[this.componentCount];
                    for (int j = 0; j < this.componentCount; j++)
                    {
                        vectorValue[j] = deserialiser.readShort();
                    }
                    this.keyFrames[i] = new ShortKeyFrame(time, vectorValue);
                }
                break;

            default:
                throw new IllegalStateException("Invalid encoding: " + this.encoding);
        }
    }

    private void readBiasAndScale(Deserialiser deserialiser) throws IOException
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
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.interpolationType);
        serialiser.write(this.repeatMode);
        serialiser.write(getEncoding());
        serialiser.writeInt(this.duration);
        serialiser.writeInt(this.validRangeFirst);
        serialiser.writeInt(this.validRangeLast);

        serialiser.writeInt(componentCount);
        serialiser.writeInt(getKeyframeCount());

        switch (getEncoding())
        {
            case ENCODING_FLOATS:
                for (FloatKeyFrame keyFrame : (FloatKeyFrame[])this.keyFrames)
                {
                    serialiser.writeInt(keyFrame.getTime());
                    for (float component : keyFrame.getVectorValue())
                    {
                        serialiser.writeFloat(component);
                    }
                }
                break;

            case ENCODING_BYTES:
                writeBiasAndScale(serialiser);
                for (ByteKeyFrame keyFrame : (ByteKeyFrame[])this.keyFrames)
                {
                    serialiser.writeInt(keyFrame.getTime());
                    for (byte component : keyFrame.getVectorValue())
                    {
                        serialiser.write(component);
                    }
                }
                break;

            case ENCODING_SHORTS:
                writeBiasAndScale(serialiser);
                for (ShortKeyFrame keyFrame : (ShortKeyFrame[])this.keyFrames)
                {
                    serialiser.writeInt(keyFrame.getTime());
                    for (short component : keyFrame.getVectorValue())
                    {
                        serialiser.writeShort(component);
                    }
                }
                break;

            default:
                assert (false);
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.KEYFRAME_SEQUENCE;
    }

    public int getInterpolationType()
    {
        return this.interpolationType;
    }

    public void setInterpolationType(int interpolation)
    {
        if (!INTERPOLATION_MODES.contains(interpolation))
        {
            throw new IllegalStateException("Invalid interpolation type: " + interpolation);
        }

        this.interpolationType = interpolation;
    }

    public int getRepeatMode()
    {
        return this.repeatMode;
    }

    public void setRepeatMode(int repeatMode)
    {
        if (!REPEAT_MODES.contains(repeatMode))
        {
            throw new IllegalStateException("Invalid repeat mode: " + repeatMode);
        }
        this.repeatMode = repeatMode;
    }

    public int getEncoding()
    {
        return this.encoding;
    }

    private void setEncoding(int encoding)
    {
        this.encoding = encoding;
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

    public int getComponentCount()
    {
        return this.componentCount;
    }

    private void setComponentCount(int componentCount)
    {
        this.componentCount = componentCount;
    }

    public int getKeyframeCount()
    {
        return this.keyFrames.length;
    }

    public KeyFrame[] getKeyFrames()
    {
        return this.keyFrames;
    }

    public void setKeyframes(FloatKeyFrame[] keyFrames)
    {
        setEncoding(ENCODING_FLOATS);
        setComponentCount(keyFrames[0].getComponentCount());
        this.keyFrames = keyFrames;
    }

    public void setKeyframes(ShortKeyFrame[] keyFrames)
    {
        setEncoding(ENCODING_SHORTS);
        setComponentCount(keyFrames[0].getComponentCount());
        this.keyFrames = keyFrames;
    }

    public void setKeyframes(ByteKeyFrame[] keyFrames)
    {
        setEncoding(ENCODING_BYTES);
        setComponentCount(keyFrames[0].getComponentCount());
        this.keyFrames = keyFrames;
    }

    public void copyKeyframes(KeyFrame[] keyFrames)
    {
        System.arraycopy(keyFrames, 0, this.keyFrames, 0, this.keyFrames.length);
    }

    public float[] getVectorBias()
    {
        return this.vectorBias;
    }

    public float[] getVectorScale()
    {
        return this.vectorScale;
    }

    private void writeBiasAndScale(Serialiser serialiser)
        throws IOException
    {
        for (float component : this.vectorBias)
        {
            serialiser.writeFloat(component);
        }
        for (float component : this.vectorScale)
        {
            serialiser.writeFloat(component);
        }
    }
}
