/*
 * Copyright (c) 2010, Jacques Gasselin de Richebourg
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
package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public class KeyframeSequence extends Object3D
{

    public static final int ADDITIVE_LOOP = 194;
    public static final int CONSTANT = 192;
    public static final int LINEAR = 176;
    public static final int LOOP = 193;
    public static final int SLERP = 177;
    public static final int SPLINE = 178;
    public static final int SQUAD = 179;
    public static final int STEP = 180;
    
    private int channelCount;
    private int componentCount;
    private int duration;
    private int interpolationType;
    private int keyframeCount;
    private int repeatMode = CONSTANT;
    private int validRangeFirst;
    private int validRangeLast;
    
    private int keyTimes[];
    private float keyValues[];

    KeyframeSequence()
    {
    }
    
    public KeyframeSequence(int numKeyframes, int numComponents, int interpolation)
    {
        set(1, numKeyframes, numComponents, interpolation);
    }
    
    public KeyframeSequence(int numChannels, int numKeyframes, int numComponents, int interpolation)
    {
        set(numChannels, numKeyframes, numComponents, interpolation);
    }

    final void set(int numChannels, int numKeyframes, int numComponents, int interpolation)
    {
        Require.argumentGreaterThanZero(numKeyframes, "numKeyframes");
        Require.argumentGreaterThanZero(numComponents, "numComponents");
        
        if (interpolation < LINEAR || interpolation > STEP)
        {
            throw new IllegalArgumentException(
                    "interpolation is not one of valid the interpolation modes");
        }
        
        channelCount = numChannels;
        keyframeCount = numKeyframes;
        componentCount = numComponents;
        interpolationType = interpolation;
        
        validRangeFirst = 0;
        validRangeLast = keyframeCount - 1;
        
        keyTimes = new int[keyframeCount];
        keyValues = new float[keyframeCount * channelCount * componentCount];
    }

    private int valueOffset(int channel, int keyframeIndex)
    {
        return keyframeIndex * channelCount * componentCount + channel * componentCount;
    }
    
    public void addEvent(int time, int eventID)
    {
        throw new UnsupportedOperationException();
    }

    public int getChannelCount()
    {
        return this.channelCount;
    }

    public int getComponentCount()
    {
        return this.componentCount;
    }

    public int getDuration()
    {
        return this.duration;
    }

    public int getEventCount()
    {
        throw new UnsupportedOperationException();
    }

    public int getEventID(int eventIndex)
    {
        throw new UnsupportedOperationException();
    }

    public int getEventTime(int eventIndex)
    {
        throw new UnsupportedOperationException();
    }

    public int getInterpolationType()
    {
        return this.interpolationType;
    }

    public int getKeyframe(int index, float[] value)
    {
        return getKeyframe(0, index, value);
    }

    public int getKeyframe(int channel, int index, float[] value)
    {
        Require.indexInRange(channel, "channel", channelCount);
        Require.indexInRange(index, "index", keyframeCount);
        
        if (value != null)
        {
            Require.argumentHasCapacity(value, "value", componentCount);
            final int offset = valueOffset(channel, index);
            System.arraycopy(keyValues, offset, value, 0, componentCount);
        }
        return keyTimes[index];
    }

    public int getKeyframeCount()
    {
        return this.keyframeCount;
    }

    public int getRepeatMode()
    {
        return this.repeatMode;
    }

    public int getValidRangeFirst()
    {
        return this.validRangeFirst;
    }

    public int getValidRangeLast()
    {
        return this.validRangeLast;
    }

    public void removeEvent(int eventIndex)
    {
        throw new UnsupportedOperationException();
    }
    
    private int wrappedValidIndex(int index)
    {
        if (index >= keyframeCount)
        {
            index -= keyframeCount;
        }
        
        if (index > validRangeLast)
        {
            index = validRangeLast;
        }
        
        return index;
    }

    private int getKeyframeIndex(float time)
    {
        int keyframeIndex = validRangeFirst;
        for (int i = 0; i < keyframeCount; ++i)
        {
            int candidate = wrappedValidIndex(validRangeFirst + i);
            
            if (keyTimes[candidate] >= time)
            {
                break;
            }
            
            keyframeIndex = candidate;
        }
        
        return keyframeIndex;
    }
    
    private static float mix(float start, float end, float interpolant)
    {
        return start * (1.0f - interpolant) + end * interpolant;
    }
    
    public float sample(float time, int channel)
    {
        Require.indexInRange(channel, "channel", channelCount);
        
        if (getComponentCount() > 1)
        {
            throw new IllegalStateException("vector value keyframes samples as scalar");
        }
        
        if (getDuration() <= 0)
        {
            throw new IllegalStateException("no duration set");
        }
        
        switch (interpolationType)
        {
            case STEP:
            {
                int index = getKeyframeIndex(time);
                return keyValues[valueOffset(channel, index)];
            }
            case LINEAR:
            case SLERP:
            case SPLINE:
            case SQUAD:
            {
                int index = getKeyframeIndex(time);
                int nextIndex = wrappedValidIndex(index + 1);
                
                if (keyTimes[index] > time || keyTimes[nextIndex] < time)
                {
                    return keyValues[valueOffset(channel, index)];
                }
                
                final int timeDelta = keyTimes[nextIndex] - keyTimes[index];
                final float interpolant = (timeDelta == 0) ? 0.0f : (time - keyTimes[index]) / (float)timeDelta;
                return mix(keyValues[valueOffset(channel, index)],
                           keyValues[valueOffset(channel, nextIndex)],
                           interpolant);
            }
        }
        throw new UnsupportedOperationException();
    }

    public void sample(float time, int channel, float[] value)
    {
        Require.indexInRange(channel, "channel", channelCount);
        Require.argumentHasCapacity(value, "value", componentCount);
        
        if (getDuration() <= 0)
        {
            throw new IllegalStateException("no duration set");
        }
        
        switch (interpolationType)
        {
            case STEP:
            {
                int index = getKeyframeIndex(time);
                System.arraycopy(keyValues, valueOffset(channel, index), value, 0, componentCount);
                return;
            }
            case LINEAR:
            case SLERP:
            case SPLINE:
            case SQUAD:
            {
                int index = getKeyframeIndex(time);
                int nextIndex = wrappedValidIndex(index + 1);
                
                if (keyTimes[index] > time || keyTimes[nextIndex] < time)
                {
                    System.arraycopy(keyValues, valueOffset(channel, index), value, 0, componentCount);
                    return;
                }
                
                final int timeDelta = keyTimes[nextIndex] - keyTimes[index];
                final float interpolant = (timeDelta == 0) ? 0.0f : (time - keyTimes[index]) / (float)timeDelta;
                for (int c = 0; c < componentCount; ++c)
                {
                    value[c] = mix(keyValues[valueOffset(channel, index) + c],
                                   keyValues[valueOffset(channel, nextIndex) + c],
                                   interpolant);
                }
                return;
            }
        }
        throw new UnsupportedOperationException();
    }

    public void setDuration(int duration)
    {
        Require.argumentGreaterThanZero(duration, "duration");
        
        this.duration = duration;
    }

    public void setKeyframe(int index, int time, float[] value)
    {
        setKeyframeTime(index, time);
        setKeyframeValue(0, index, value);
    }

    public void setKeyframeTime(int index, int time)
    {
        Require.indexInRange(index, "index", keyframeCount);
        
        keyTimes[index] = time;
    }
    
    public void setKeyframeValue(int channel, int index, float[] value)
    {
        Require.indexInRange(channel, "channel", channelCount);
        Require.indexInRange(index, "index", keyframeCount);
        Require.argumentHasCapacity(value, "value", componentCount);
        
        final int offset = valueOffset(channel, index);
        System.arraycopy(value, 0, keyValues, offset, componentCount);
    }

    public void setRepeatMode(int mode)
    {
        Require.argumentInEnum(mode, "mode", CONSTANT, ADDITIVE_LOOP);
        if (mode == ADDITIVE_LOOP)
        {
            if (interpolationType == SLERP)
            {
                throw new IllegalArgumentException("mode is ADDITIVE_LOOP and interpolation is SLERP");
            }
            else if (interpolationType == SQUAD)
            {
                throw new IllegalArgumentException("mode is ADDITIVE_LOOP and interpolation is SQUAD");
            }
        }
        
        this.repeatMode = mode;
    }

    public void setValidRange(int first, int last)
    {
        Require.indexInRange(first, "first", keyframeCount);
        Require.indexInRange(first, "last", keyframeCount);
        
        this.validRangeFirst = first;
        this.validRangeLast = last;
    }
}
