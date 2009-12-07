/**
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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
import m3x.m3g.util.Object3DReferences;

/**
 * See http://java2me.org/m3g/file-format.html#AnimationTrack<br>
 * ObjectIndex   keyframeSequence;<br>
 * ObjectIndex   animationController;<br>
 * UInt32        propertyID;<br>
 *
 * @author jsaarinen
 * @author jgasseli
 */
public class AnimationTrack extends Object3D
{
    public static final int ALPHA = 256;
    public static final int AMBIENT_COLOR = 257;
    public static final int COLOR = 258;
    public static final int CROP = 259;
    public static final int DENSITY = 260;
    public static final int DIFFUSE_COLOR = 261;
    public static final int EMISSIVE_COLOR = 262;
    public static final int FAR_DISTANCE = 263;
    public static final int FIELD_OF_VIEW = 264;
    public static final int INTENSITY = 265;
    public static final int MORPH_WEIGHTS = 266;
    public static final int NEAR_DISTANCE = 267;
    public static final int ORIENTATION = 268;
    public static final int PICKABILITY = 269;
    public static final int SCALE = 270;
    public static final int SHININESS = 271;
    public static final int SPECULAR_COLOR = 272;
    public static final int SPOT_ANGLE = 273;
    public static final int SPOT_EXPONENT = 274;
    public static final int TRANSLATION = 275;
    public static final int VISIBILITY = 276;

    private KeyframeSequence keyframeSequence;
    private AnimationController animationController;
    private int targetProperty;

    public AnimationTrack()
    {
        super();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!super.equals(obj))
        {
            return false;
        }
        final AnimationTrack other = (AnimationTrack) obj;
        if (getTargetProperty() != other.getTargetProperty())
        {
            return false;
        }
        return true;
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setKeyframeSequence((KeyframeSequence)deserialiser.readReference());
        setController((AnimationController)deserialiser.readReference());
        setTargetProperty(deserialiser.readInt());
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(getKeyframeSequence());
        serialiser.writeReference(getController());
        serialiser.writeInt(getTargetProperty());
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getKeyframeSequence());
        queue.add(getController());
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.ANIMATION_TRACK;
    }

    public KeyframeSequence getKeyframeSequence()
    {
        return this.keyframeSequence;
    }

    public void setKeyframeSequence(KeyframeSequence sequence)
    {
        if (sequence == null)
        {
            throw new NullPointerException("sequence is null");
        }
        this.keyframeSequence = sequence;
    }

    public AnimationController getController()
    {
        return this.animationController;
    }

    public void setController(AnimationController controller)
    {
        this.animationController = controller;
    }

    public int getTargetProperty()
    {
        return this.targetProperty;
    }

    public void setTargetProperty(int property)
    {
        if (property < ALPHA || property > VISIBILITY)
        {
            throw new IllegalArgumentException("Invalid property ID: " + property);
        }
        this.targetProperty = property;
    }

    public void setTargetProperty(String property)
    {
        setTargetProperty(getFieldValue(property, "property"));
    }
}
