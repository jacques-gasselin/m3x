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
public class Light extends Node
{
    public static final int AMBIENT = 128;
    public static final int DIRECTIONAL = 129;
    public static final int OMNI = 130;
    public static final int SPOT = 131;

    private int mode = DIRECTIONAL;
    private int color = 0x00FFFFFF;
    private float intensity = 1.0f;
    private float constantAttenuation = 1.0f;
    private float linearAttenuation;
    private float quadraticAttenuation;
    private float spotAngle = 45;
    private float spotExponent;
    
    public Light()
    {
        
    }

    public int getColor()
    {
        return this.color;
    }

    public float getConstantAttenuation()
    {
        return this.constantAttenuation;
    }

    public float getIntensity()
    {
        return this.intensity;
    }

    public float getLinearAttenuation()
    {
        return this.linearAttenuation;
    }

    public int getMode()
    {
        return this.mode;
    }

    public float getQuadraticAttenuation()
    {
        return this.quadraticAttenuation;
    }

    public float getSpotAngle()
    {
        return this.spotAngle;
    }

    public float getSpotExponent()
    {
        return this.spotExponent;
    }

    public void setAttenuation(float constant, float linear, float quadratic)
    {
        Require.argumentNotNegative(constant, "constant");
        Require.argumentNotNegative(linear, "linear");
        Require.argumentNotNegative(quadratic, "quadratic");

        if (constant == 0 && linear == 0 && quadratic == 0)
        {
            throw new IllegalArgumentException("all of the parameter values are zero");
        }
        
        this.constantAttenuation = constant;
        this.linearAttenuation = linear;
        this.quadraticAttenuation = quadratic;
    }

    public void setColor(int rgb)
    {
        this.color = rgb & 0x00FFFFFF;
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }

    public void setMode(int mode)
    {
        Require.argumentInEnum(mode, "mode", AMBIENT, SPOT);

        this.mode = mode;
    }

    public void setSpotAngle(float angle)
    {
        Require.argumentInRange(angle, "angle", 0, 90);
        
        this.spotAngle = angle;
    }

    public void setSpotExponent(float exponent)
    {
        Require.argumentInRange(exponent, "exponent", 0, 128);
        
        this.spotExponent = exponent;
    }
}
