/**
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;


/**
 * See http://java2me.org/m3g/file-format.html#AnimationController<br>
 *  Float32       speed;<br>
 *  Float32       weight;<br>
 *  Int32         activeIntervalStart;<br>
 *  Int32         activeIntervalEnd;<br>
 *  Float32       referenceSequenceTime;<br>
 *  Int32         referenceWorldTime;<br>
 *
 * @author jsaarinen
 * @author jgasseli
 */
public class AnimationController extends Object3D
{
    private float speed;
    private float weight;
    private int activeIntervalStart;
    private int activeIntervalEnd;
    private float referenceSequenceTime;
    private int referenceWorldTime;

    public AnimationController()
    {
        super();
        setActiveInterval(0, 0);
        setSpeed(1.0f);
        setWeight(1.0f);
        setPosition(0.0f, 0);

    }

    @Override
    public boolean equals(Object obj)
    {
        if (!super.equals(obj))
        {
            return false;
        }
        final AnimationController other = (AnimationController) obj;
        if (getSpeed() != other.getSpeed())
        {
            return false;
        }
        if (getWeight() != other.getWeight())
        {
            return false;
        }
        if (getActiveIntervalStart() != other.getActiveIntervalStart())
        {
            return false;
        }
        if (getActiveIntervalEnd() != other.getActiveIntervalEnd())
        {
            return false;
        }
        if (getRefSequenceTime() != other.getRefSequenceTime())
        {
            return false;
        }
        if (getRefWorldTime() != other.getRefWorldTime())
        {
            return false;
        }
        return true;
    }


    /**
     * Deserializes the object as specified in the class JavaDoc.
     * @throws IOException
     */
    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setSpeed(deserialiser.readFloat());
        setWeight(deserialiser.readFloat());
        final int start = deserialiser.readInt();
        final int end = deserialiser.readInt();
        setActiveInterval(start, end);
        final float sequenceTime = deserialiser.readFloat();
        final int worldTime = deserialiser.readInt();
        setPosition(sequenceTime, worldTime);
    }

    /**
     * Serialization done as specified in the class JavaDoc.
     */
    @Override
    public void serialise(Serializer serialiser) throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeFloat(getSpeed());
        serialiser.writeFloat(getWeight());
        serialiser.writeInt(getActiveIntervalStart());
        serialiser.writeInt(getActiveIntervalEnd());
        serialiser.writeFloat(getRefSequenceTime());
        serialiser.writeInt(getRefWorldTime());
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.ANIMATION_CONTROLLER;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public float getWeight()
    {
        return this.weight;
    }

    public int getActiveIntervalStart()
    {
        return this.activeIntervalStart;
    }

    public int getActiveIntervalEnd()
    {
        return this.activeIntervalEnd;
    }

    private float getRefSequenceTime()
    {
        return this.referenceSequenceTime;
    }

    public int getRefWorldTime()
    {
        return this.referenceWorldTime;
    }

    private void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public void setSpeed(float speed, int worldTime)
    {
        final float sequenceTime = getPosition(worldTime);
        setPosition(sequenceTime, worldTime);
        setSpeed(speed);
    }

    public void setWeight(float weight)
    {
        this.weight = weight;
    }

    public float getPosition(int worldTime)
    {
        return getRefSequenceTime()
             + getSpeed() * (worldTime - getRefWorldTime());
    }

    public void setActiveInterval(int start, int end)
    {
        this.activeIntervalStart = start;
        this.activeIntervalEnd = end;
    }

    public void setPosition(float sequenceTime, int worldTime)
    {
        this.referenceSequenceTime = sequenceTime;
        this.referenceWorldTime = worldTime;
    }
}
