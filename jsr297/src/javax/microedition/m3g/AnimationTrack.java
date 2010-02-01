/*
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
package javax.microedition.m3g;

import m3x.Require;
import java.util.List;

/**
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
    public static final int ANIMATION_POSITION = 277;
    public static final int ANIMATION_SPEED = 278;
    public static final int ANIMATION_WEIGHT = 279;
    public static final int BOUNDING_BOX = 280;
    public static final int BOUNDING_SPHERE = 281;
    public static final int COLLISION_SHAPE = 282;
    public static final int DEPTH = 283;
    public static final int POINT_SIZE = 284;
    public static final int STENCIL = 285;
    
    private AnimationController controller;
    private KeyframeSequence sequence;
    private int targetProperty;
    private boolean normalized;

    AnimationTrack()
    {
    }

    public AnimationTrack(KeyframeSequence sequence, int property)
    {
        Require.notNull(sequence, "sequence");

        setTargetProperty(property);
        setKeyframeSequence(sequence);
    }

    public AnimationTrack(KeyframeSequence sequence, int basicType, boolean normalize)
    {
        Require.notNull(sequence, "sequence");

        throw new UnsupportedOperationException();
    }

    public AnimationController getController()
    {
        return this.controller;
    }

    public KeyframeSequence getKeyframeSequence()
    {
        return this.sequence;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);

        if (controller != null)
        {
            references.add(controller);
        }

        if (sequence != null)
        {
            references.add(sequence);
        }
    }
    
    public int getTargetProperty()
    {
        return this.targetProperty;
    }

    boolean isNormalizeEnabled()
    {
        return this.normalized;
    }

    public float sample(int worldTime, int channel)
    {
        throw new UnsupportedOperationException();
    }

    public void sample(int worldTime, int channel, float[] value)
    {
        throw new UnsupportedOperationException();
    }

    public void setController(AnimationController controller)
    {
        this.controller = controller;
    }

    public void setKeyframeSequence(KeyframeSequence sequence)
    {
        Require.notNull(sequence, "sequence");
        //TODO check compatibility

        if (this.sequence == null)
        {
            this.sequence = sequence;
        }
        else
        {
            //TODO implement replace
            throw new UnsupportedOperationException();
        }
    }

    void setTargetProperty(int property)
    {
        Require.argumentInEnum(property, "property", ALPHA, STENCIL);

        this.targetProperty = property;
    }
}
