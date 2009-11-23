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

    /**
     * Interface for simplifying keyframe deserialising
     * @author jgasseli
     */
    private static class KeyframeSequenceInfo
    {
        private final int keyframeCount;
        private final int componentCount;

        protected KeyframeSequenceInfo(KeyframeSequence sequence)
        {
            keyframeCount = sequence.getKeyframeCount();
            componentCount = sequence.getComponentCount();
        }

        final int getKeyframeCount()
        {
            return keyframeCount;
        }

        final int getComponentCount()
        {
            return componentCount;
        }
    }


    private static abstract class KeyframeReader extends KeyframeSequenceInfo
    {
        protected KeyframeReader(KeyframeSequence sequence)
        {
            super(sequence);
        }

        abstract void readScaleAndBias(Deserializer deserialiser) throws IOException;
        
        abstract void readKeyframe(Deserializer deserialiser, float[] value) throws IOException;
    }

    private static final class FloatKeyframeReader extends KeyframeReader
    {
        FloatKeyframeReader(KeyframeSequence sequence)
        {
            super(sequence);
        }
        
        @Override
        void readScaleAndBias(Deserializer deserialiser) throws IOException
        {
            //do nothing
        }
        
        @Override
        void readKeyframe(Deserializer deserialiser, float[] value) throws IOException
        {
            final int componentCount = getComponentCount();
            for (int j = 0; j < componentCount; ++j)
            {
                value[j] = deserialiser.readFloat();
            }
        }
    }
    
    private static abstract class ScaleBiasedKeyframeReader extends KeyframeReader
    {
        private final float[] bias;
        private final float[] scale;

        protected ScaleBiasedKeyframeReader(KeyframeSequence sequence)
        {
            super(sequence);
            
            final int componentCount = getComponentCount();
            bias = new float[componentCount];
            scale = new float[componentCount];
        }

        @Override
        final void readScaleAndBias(Deserializer deserialiser) throws IOException
        {
            final int componentCount = getComponentCount();
            for (int j = 0; j < componentCount; ++j)
            {
                bias[j] = deserialiser.readFloat();
            }
            for (int j = 0; j < componentCount; ++j)
            {
                scale[j] = deserialiser.readFloat();
            }
        }

        @Override
        final void readKeyframe(Deserializer deserialiser, float[] value) throws IOException
        {
            final int componentCount = getComponentCount();
            for (int j = 0; j < componentCount; ++j)
            {
                final float uniform = readUniform(deserialiser);
                value[j] = uniform * scale[j] + bias[j];
            }
        }

        abstract float readUniform(Deserializer deserialiser) throws IOException;
    }

    private static final class ByteKeyframeReader extends ScaleBiasedKeyframeReader
    {
        private static final float FACTOR = 1.0f / 255;
        
        ByteKeyframeReader(KeyframeSequence sequence)
        {
            super(sequence);
        }

        @Override
        float readUniform(Deserializer deserialiser) throws IOException
        {
            return deserialiser.readUnsignedByte() * FACTOR;
        }
    }

    private static final class ShortKeyframeReader extends ScaleBiasedKeyframeReader
    {
        private static final float FACTOR = 1.0f / 65535;

        ShortKeyframeReader(KeyframeSequence sequence)
        {
            super(sequence);
        }

        @Override
        float readUniform(Deserializer deserialiser) throws IOException
        {
            return deserialiser.readUnsignedShort() * FACTOR;
        }
    }

    /**
     * Interface for simplifying keyframe serialising
     * @author jgasseli
     */
    private static abstract class KeyframeWriter extends KeyframeSequenceInfo
    {
        protected KeyframeWriter(KeyframeSequence sequence)
        {
            super(sequence);
        }

        abstract void writeScaleAndBias(Serializer serialiser) throws IOException;

        abstract void writeKeyframe(Serializer serialiser, float[] value) throws IOException;
    }

    private static final class FloatKeyframeWriter extends KeyframeWriter
    {
        protected FloatKeyframeWriter(KeyframeSequence sequence)
        {
            super(sequence);
        }

        @Override
        void writeScaleAndBias(Serializer serialiser) throws IOException
        {
            //do nothing
        }

        @Override
        void writeKeyframe(Serializer serialiser, float[] value) throws IOException
        {
            final int componentCount = getComponentCount();
            for (int j = 0; j < componentCount; ++j)
            {
                serialiser.writeFloat(value[j]);
            }
        }
    }

    private static abstract class ScaleBiasedKeyframeWriter extends KeyframeWriter
    {
        private final float[] bias;
        private final float[] scale;

        protected ScaleBiasedKeyframeWriter(KeyframeSequence sequence)
        {
            super(sequence);

            final int keyframeCount = getKeyframeCount();
            final int componentCount = getComponentCount();

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

        @Override
        final void writeScaleAndBias(Serializer serialiser) throws IOException
        {
            final int componentCount = getComponentCount();
            for (int j = 0; j < componentCount; ++j)
            {
                serialiser.writeFloat(bias[j]);
            }
            for (int j = 0; j < componentCount; ++j)
            {
                serialiser.writeFloat(scale[j]);
            }
        }

        @Override
        final void writeKeyframe(Serializer serialiser, float[] value) throws IOException
        {
            final int componentCount = getComponentCount();
            for (int j = 0; j < componentCount; ++j)
            {
                final float uniform = (value[j] - bias[j]) / scale[j];
                writeUniform(serialiser, uniform);
            }
        }

        abstract void writeUniform(Serializer serialiser, float uniform) throws IOException;
    }

    private static final class ByteKeyframeWriter extends ScaleBiasedKeyframeWriter
    {
        private static final int MAX = 255;

        ByteKeyframeWriter(KeyframeSequence sequence)
        {
            super(sequence);
        }

        @Override
        void writeUniform(Serializer serialiser, float uniform) throws IOException
        {
            final int value = Math.max(0, Math.min(MAX, Math.round(uniform * MAX)));
            serialiser.writeUnsignedByte(value);
        }
    }

    /**
     * This is untested and may have an error in it.
     */
    private static final class ShortKeyframeWriter extends ScaleBiasedKeyframeWriter
    {
        private static final int MAX = 65535;

        ShortKeyframeWriter(KeyframeSequence sequence)
        {
            super(sequence);
        }

        @Override
        void writeUniform(Serializer serialiser, float uniform) throws IOException
        {
            final int value = Math.max(0, Math.min(MAX, Math.round(uniform * MAX)));
            serialiser.writeUnsignedShort(value);
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
    public void deserialise(Deserializer deserialiser)
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
                reader = new FloatKeyframeReader(this);
                break;
            case BYTE:
                reader = new ByteKeyframeReader(this);
                break;
            case SHORT:
                reader = new ShortKeyframeReader(this);
                break;
        }
        reader.readScaleAndBias(deserialiser);

        final float value[] = new float[componentCount];
        for (int i = 0; i < keyframeCount; ++i)
        {
            final int time = deserialiser.readInt();
            reader.readKeyframe(deserialiser, value);
            setKeyframe(i, time, value);
        }
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeUnsignedByte(getInterpolationType());
        serialiser.writeUnsignedByte(getRepeatMode());
        final int encoding = getEncoding();
        serialiser.writeUnsignedByte(encoding);
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
                writer = new FloatKeyframeWriter(this);
                break;
            case BYTE:
                writer = new ByteKeyframeWriter(this);
                break;
            case SHORT:
                writer = new ShortKeyframeWriter(this);
                break;
        }
        writer.writeScaleAndBias(serialiser);
        
        final float value[] = new float[componentCount];
        for (int i = 0; i < keyframeCount; ++i)
        {
            final int time = getKeyframe(i, value);
            serialiser.writeInt(time);
            writer.writeKeyframe(serialiser, value);
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

    public void setEncoding(String encoding)
    {
        setEncoding(getFieldValue(encoding, "encoding"));
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
    
    public void setInterpolationType(String interpolation)
    {
        setInterpolationType(getFieldValue(interpolation, "interpolationType"));
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
        if (this.keyFrames == null)
        {
            return 0;
        }
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
