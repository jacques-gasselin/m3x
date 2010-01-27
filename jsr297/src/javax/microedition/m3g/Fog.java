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

/**
 * @author jgasseli
 */
public class Fog extends Object3D
{
    public static final int EXPONENTIAL = 80;
    public static final int LINEAR = 81;
    public static final int EXPONENTIAL_SQUARED = 82;

    private int color;
    private float density;
    private float farDistance;
    private int mode;
    private float nearDistance;
    
    public Fog()
    {
        setMode(LINEAR);
        setDensity(1.0f);
        setLinear(0.0f, 1.0f);
        setColor(0x0);
    }

    public int getColor()
    {
        return this.color;
    }

    public float getDensity()
    {
        return this.density;
    }

    public float getFarDistance()
    {
        return this.farDistance;
    }

    public int getMode()
    {
        return this.mode;
    }

    public float getNearDistance()
    {
        return this.nearDistance;
    }

    public void setColor(int rgb)
    {
        this.color = rgb & 0x00ffffff;
    }

    public void setDensity(float density)
    {
        this.density = density;
    }

    public void setLinear(float near, float far)
    {
        this.nearDistance = near;
        this.farDistance = far;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }
}
