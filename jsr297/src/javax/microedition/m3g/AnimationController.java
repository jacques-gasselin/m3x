/*
 * Copyright (c) 2009-2010, Jacques Gasselin de Richebourg
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
public final class AnimationController extends Object3D
{
    private int activeIntervalStart;
    private int activeIntervalEnd;
    private int duration;
    private float speed = 1.0f;
    private float weight = 1.0f;
    private float sequenceTime;
    private int worldTime;
    
    public AnimationController()
    {
        super();
    }

    public int getActiveIntervalEnd()
    {
        return this.activeIntervalEnd;
    }

    public int getActiveIntervalStart()
    {
        return this.activeIntervalStart;
    }

    public int getDuration()
    {
        return this.duration;
    }
    
    final boolean isWorldTimeInActiveInterval(int worldTime)
    {
        if (activeIntervalStart == 0 && activeIntervalEnd == 0)
        {
            return true;
        }
        
        if (worldTime >= activeIntervalStart && worldTime < activeIntervalEnd)
        {
            return true;
        }
        
        return false;
    }

    public float getPosition(int worldTime)
    {
        return sequenceTime + speed * (worldTime - this.worldTime); 
    }

    @Deprecated
    public int getRefWorldTime()
    {
        return this.worldTime;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public float getWeight()
    {
        return this.weight;
    }

    public int getWorldTime(float sequenceTime)
    {
        return worldTime + Math.round((sequenceTime - this.sequenceTime) / speed);
    }

    public void setActiveInterval(int start, int end)
    {
        if (start > end)
        {
            throw new IllegalArgumentException("start > end");
        }

        this.activeIntervalStart = start;
        this.activeIntervalEnd = end;
    }

    public void setPosition(float sequenceTime, int worldTime)
    {
        this.sequenceTime = sequenceTime;
        this.worldTime = worldTime;
    }

    public void setSpeed(float speed, int worldTime)
    {
        this.sequenceTime = getPosition(worldTime);
        this.worldTime = worldTime;
        this.speed = speed;
    }

    public void setWeight(float weight)
    {
        Require.argumentNotNegative(weight, "weight");

        this.weight = weight;
    }
}
