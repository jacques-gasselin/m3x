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

import m3x.Require;

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
    private int repeatMode;
    private int validRangeFirst;
    private int validRangeLast;

    KeyframeSequence()
    {
    }

    public KeyframeSequence(int numKeyframes, int numComponents, int interpolation)
    {
        Require.argumentGreaterThanZero(numKeyframes, "numKeyframes");
        Require.argumentGreaterThanZero(numComponents, "numComponents");

        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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

    public float sample(float time, int channel)
    {
        throw new UnsupportedOperationException();
    }

    public float sample(float time, int channel, float[] value)
    {
        throw new UnsupportedOperationException();
    }

    public void setDuration(int duration)
    {
        throw new UnsupportedOperationException();
    }

    public void setKeyframe(int index, int time, float[] value)
    {
        throw new UnsupportedOperationException();
    }

    public void setKeyframeTime(int index, int time)
    {
        throw new UnsupportedOperationException();
    }

    public void setKeyframeValue(int channel, int index, float[] value)
    {
        throw new UnsupportedOperationException();
    }

    public void setRepeatMode(int mode)
    {
        throw new UnsupportedOperationException();
    }

    public void setValidRange(int first, int last)
    {
        throw new UnsupportedOperationException();
    }
}
