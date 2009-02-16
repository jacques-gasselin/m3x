/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package m3x.m3g;

import java.util.List;
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
     * Keyframe class in floating point.
     * @author jsaarinen
     * @author jgasseli
     */
    private static final class FloatKeyFrame
    {
        private int time;
        private final float[] value;

        private FloatKeyFrame(int componentCount)
        {
            this.value = new float[componentCount];
        }

        public final void set(int time, float[] value)
        {
            if (value == null)
            {
                throw new NullPointerException("value is null");
            }
            if (value.length < this.value.length)
            {
                throw new IllegalArgumentException("value.length < componentCount");
            }
            this.time = time;
            System.arraycopy(value, 0,
                this.value, 0, this.value.length);
        }

        public final int getTime()
        {
            return this.time;
        }

        public final float[] getValue()
        {
            return this.value;
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
    
    public static final int FLOAT = 0;
    public static final int BYTE = 1;
    public static final int SHORT = 2;
    
    private int interpolationType;
    private int repeatMode;
    private int encoding;
    private int duration;
    private int validRangeFirst;
    private int validRangeLast;
    private int componentCount;
    private FloatKeyFrame[] keyFrames;

    public KeyframeSequence()
    {
        super();
        setRepeatMode(CONSTANT);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setInterpolationType(deserialiser.readUnsignedByte());
        setRepeatMode(deserialiser.readUnsignedByte());
        final int encoding = deserialiser.readUnsignedByte();
        setEncoding(encoding);
        setDuration(deserialiser.readInt());
        final int first = deserialiser.readInt();
        final int last = deserialiser.readInt();
        setValidRange(first, last);
        final int componentCount = deserialiser.readInt();
        final int keyframeCount = deserialiser.readInt();
        setSize(keyframeCount, componentCount);

        KeyframeReader reader = null;
        switch (encoding)
        {
            case FLOAT:
                reader = new FloatKeyframeReader();
                break;
            case BYTE:
                reader = new ByteKeyframeReader(deserialiser, componentCount);
                break;
            case SHORT:
                reader = new ShortKeyframeReader(deserialiser, componentCount);
                break;
        }

        final float value[] = new float[componentCount];
        for (int i = 0; i < keyframeCount; ++i)
        {
            final int time = deserialiser.readInt();
            reader.readKeyframe(deserialiser, value, componentCount);
            setKeyframe(i, time, value);
        }
    }

    /**
     * Interface for simplifying keyframe deserialising
     * @author jgasseli
     */
    private interface KeyframeReader
    {
        public void readKeyframe(Deserialiser deserialiser, float[] value,
            final int componentCount) throws IOException;
    }

    private static final class FloatKeyframeReader implements KeyframeReader
    {
        public final void readKeyframe(Deserialiser deserialiser, float[] value,
            final int componentCount) throws IOException
        {
            for (int j = 0; j < componentCount; ++j)
            {
                value[j] = deserialiser.readFloat();
            }
        }
    }
    
    private static abstract class ScaleBiasedKeyframeReader implements KeyframeReader
    {
        private final float[] bias;
        private final float[] scale;

        protected ScaleBiasedKeyframeReader(Deserialiser deserialiser, final int componentCount)
        {
            bias = new float[componentCount];
            scale = new float[componentCount];
        }

        public final void readKeyframe(Deserialiser deserialiser, float[] value,
            final int componentCount) throws IOException
        {
            for (int j = 0; j < componentCount; ++j)
            {
                final float uniform = readUniform(deserialiser);
                value[j] = uniform * scale[j] + bias[j];
            }
        }

        public abstract float readUniform(Deserialiser deserialiser) throws IOException;
    }

    private static final class ByteKeyframeReader extends ScaleBiasedKeyframeReader
    {
        private static final float FACTOR = 1.0f / 255;
        
        public ByteKeyframeReader(Deserialiser deserialiser, int componentCount)
        {
            super(deserialiser, componentCount);
        }

        @Override
        public final float readUniform(Deserialiser deserialiser) throws IOException
        {
            return deserialiser.readUnsignedByte() * FACTOR;
        }
    }

    private static final class ShortKeyframeReader extends ScaleBiasedKeyframeReader
    {
        private static final float FACTOR = 1.0f / 65535;

        public ShortKeyframeReader(Deserialiser deserialiser, int componentCount)
        {
            super(deserialiser, componentCount);
        }

        @Override
        public final float readUniform(Deserialiser deserialiser) throws IOException
        {
            return deserialiser.readUnsignedShort() * FACTOR;
        }
    }

    
    public void setSize(int keyframeCount, int componentCount)
    {
        this.keyFrames = new FloatKeyFrame[keyframeCount];
        this.componentCount = componentCount;
        for (int i = 0; i < keyframeCount; ++i)
        {
            this.keyFrames[i] = new FloatKeyFrame(componentCount);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeByte(getInterpolationType());
        serialiser.writeByte(getRepeatMode());
        final int encoding = getEncoding();
        serialiser.writeByte(encoding);
        serialiser.writeInt(getDuration());
        serialiser.writeInt(getValidRangeFirst());
        serialiser.writeInt(getValidRangeLast());

        final int componentCount = getComponentCount();
        final int keyframeCount = getKeyframeCount();
        serialiser.writeInt(componentCount);
        serialiser.writeInt(keyframeCount);

        KeyframeWriter writer = null;
        switch (encoding)
        {
            case FLOAT:
                writer = new FloatKeyframeWriter();
                break;
            case BYTE:
                writer = new ByteKeyframeWriter(this);
                break;
            case SHORT:
                writer = new ShortKeyframeWriter(this);
                break;
        }

        final float value[] = new float[componentCount];
        for (int i = 0; i < keyframeCount; ++i)
        {
            final int time = getKeyframe(i, value);
            serialiser.writeInt(time);
            writer.writeKeyframe(serialiser, value, componentCount);
        }
    }

    /**
     * Interface for simplifying keyframe serialising
     * @author jgasseli
     */
    private interface KeyframeWriter
    {
        public void writeKeyframe(Serialiser serialiser, float[] value,
            final int componentCount) throws IOException;
    }

    private static final class FloatKeyframeWriter implements KeyframeWriter
    {
        public final void writeKeyframe(Serialiser serialiser, float[] value,
            final int componentCount) throws IOException
        {
            for (int j = 0; j < componentCount; ++j)
            {
                serialiser.writeFloat(value[j]);
            }
        }
    }

    private static abstract class ScaleBiasedKeyframeWriter implements KeyframeWriter
    {
        private final float[] bias;
        private final float[] scale;

        protected ScaleBiasedKeyframeWriter(KeyframeSequence sequence)
        {
            final int keyframeCount = sequence.getKeyframeCount();
            final int componentCount = sequence.getComponentCount();
            
            //get max and min
            final float[] min = new float[componentCount];
            final float[] max = new float[componentCount];
            if (keyframeCount > 0)
            {
                //set the max and minimum for the first iteration
                sequence.getKeyframe(0, min);
                sequence.getKeyframe(0, max);
            }

            final float[] value = new float[componentCount];
            for (int i = 0; i < keyframeCount; ++i)
            {
                sequence.getKeyframe(i, value);
                for (int j = 0; j < componentCount; ++j)
                {
                    final float v = value[j];
                    max[j] = Math.max(max[j], v);
                    min[j] = Math.min(min[j], v);
                }
            }

            //bias is min
            bias = min;
            //scale is max - min
            for (int j = 0; j < componentCount; ++j)
            {
                max[j] = max[j] - min[j];
            }
            scale = max;
        }

        public final void writeKeyframe(Serialiser serialiser, float[] value,
            final int componentCount) throws IOException
        {
            for (int j = 0; j < componentCount; ++j)
            {
                final float uniform = (value[j] - bias[j]) / scale[j];
                writeUniform(serialiser, uniform);
            }
        }

        public abstract void writeUniform(Serialiser serialiser,
            float uniform) throws IOException;
    }
    
    private static final class ByteKeyframeWriter extends ScaleBiasedKeyframeWriter
    {
        private static final int MAX = 255;

        public ByteKeyframeWriter(KeyframeSequence sequence)
        {
            super(sequence);
        }

        @Override
        public void writeUniform(Serialiser serialiser, float uniform) throws IOException
        {
            final int value = Math.max(0, Math.min(MAX, Math.round(uniform * MAX)));
            serialiser.writeByte(value);
        }
    }

    private static final class ShortKeyframeWriter extends ScaleBiasedKeyframeWriter
    {
        private static final int MAX = 65535;

        public ShortKeyframeWriter(KeyframeSequence sequence)
        {
            super(sequence);
        }

        @Override
        public void writeUniform(Serialiser serialiser, float uniform) throws IOException
        {
            final int value = Math.max(0, Math.min(MAX, Math.round(uniform * MAX)));
            serialiser.writeShort(value);
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.KEYFRAME_SEQUENCE;
    }

    public int getEncoding()
    {
        return this.encoding;
    }

    public void setEncoding(int encoding)
    {
        this.encoding = encoding;
    }

    public void setEncoding(String value)
    {
        setEncoding(getFieldValue(value, "encoding"));
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
    
    public void setInterpolationType(String value)
    {
        setInterpolationType(getFieldValue(value, "interpolationType"));
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

    public void setRepeatMode(String repeatMode)
    {
        setRepeatMode(getFieldValue(repeatMode, "repeatMode"));
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

    public int getKeyframeCount()
    {
        return this.keyFrames.length;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public void setValidRange(int first, int last)
    {
        this.validRangeFirst = first;
        this.validRangeLast = last;
    }

    public int getKeyframe(int index, float[] value)
    {
        final FloatKeyFrame keyframe = this.keyFrames[index];
        if (value != null)
        {
            System.arraycopy(keyframe.getValue(), 0,
                value, 0, getComponentCount());
        }
        return keyframe.getTime();
    }

    public void setKeyframe(int index, int time, float[] value)
    {
        final FloatKeyFrame keyframe = this.keyFrames[index];
        keyframe.set(time, value);
    }

    public void setKeyframes(List<Integer> keyTimes, List<Float> keyValues)
    {
        final int keyframeCount = getKeyframeCount();
        final int componentCount = getComponentCount();

        final float[] value = new float[componentCount];
        for (int i = 0; i < keyframeCount; ++i)
        {
            final int time = keyTimes.get(i);
            final int offset = i * componentCount;
            for (int j = 0; j < componentCount; ++j)
            {
                value[j] = keyValues.get(offset + j);
            }
            setKeyframe(i, time, value);
        }
    }
}
