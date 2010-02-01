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

/**
 * @author jgasseli
 */
public class Material extends Object3D
{
    public static final int AMBIENT = 1024;
    public static final int DIFFUSE = 2048;
    public static final int EMISSIVE = 4096;
    public static final int SPECULAR = 8192;

    private boolean vertexColorTrackingEnabled;
    private int ambientColor = 0x00333333;
    private int diffuseColor = 0xFFCCCCCC;
    private int emissiveColor = 0x00000000;
    private int specularColor = 0x00000000;
    private float shininess;

    public Material()
    {
        
    }

    public int getColor(int target)
    {
        switch (target)
        {
            case AMBIENT:
            {
                return this.ambientColor;
            }
            case DIFFUSE:
            {
                return this.diffuseColor;
            }
            case EMISSIVE:
            {
                return this.emissiveColor;
            }
            case SPECULAR:
            {
                return this.specularColor;
            }
            default:
            {
                throw new IllegalArgumentException("target is not exactly one" +
                        " of AMBIENT, DIFFUSE, EMISSIVE, and SPECULAR");
            }
        }
    }

    public float getShininess()
    {
        return this.shininess;
    }

    public boolean isVertexColorTrackingEnabled()
    {
        return this.vertexColorTrackingEnabled;
    }

    public void setColor(int target, int argb)
    {
        if ((target & (AMBIENT | DIFFUSE | EMISSIVE | SPECULAR)) == 0)
        {
            throw new IllegalArgumentException("target is not a bitmask" +
                    " of AMBIENT, DIFFUSE, EMISSIVE, and SPECULAR");
        }

        if ((target & AMBIENT) != 0)
        {
            this.ambientColor = argb;
        }
        if ((target & DIFFUSE) != 0)
        {
            this.diffuseColor = argb;
        }
        if ((target & EMISSIVE) != 0)
        {
            this.emissiveColor = argb;
        }
        if ((target & SPECULAR) != 0)
        {
            this.specularColor = argb;
        }
    }

    public void setShininess(float shininess)
    {
        Require.argumentInRange(shininess, "shininess", 0, 128);

        this.shininess = shininess;
    }

    public void setVertexColorTrackingEnabled(boolean enabled)
    {
        this.vertexColorTrackingEnabled = enabled;
    }
}
