/*
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

package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public class Blender extends Object3D
{
    public static final int ADD = 88;
    public static final int SUBTRACT = 89;
    public static final int REVERSE_SUBTRACT = 90;

    public static final int ZERO = 112;
    public static final int ONE = 113;
    public static final int SRC_COLOR = 114;
    public static final int ONE_MINUS_SRC_COLOR = 115;
    public static final int SRC_ALPHA = 116;
    public static final int ONE_MINUS_SRC_ALPHA = 117;
    public static final int DST_ALPHA = 118;
    public static final int ONE_MINUS_DST_ALPHA = 119;
    public static final int DST_COLOR = 120;
    public static final int ONE_MINUS_DST_COLOR = 121;
    public static final int SRC_ALPHA_SATURATE = 122;
    public static final int CONSTANT_COLOR = 123;
    public static final int ONE_MINUS_CONSTANT_COLOR = 124;
    public static final int CONSTANT_ALPHA = 125;
    public static final int ONE_MINUS_CONSTANT_ALPHA = 126;

    private int blendFunctionAlpha = ADD;
    private int blendFunctionColor = ADD;
    private int blendFactorSrcColor = ONE;
    private int blendFactorDstColor = ZERO;
    private int blendFactorSrcAlpha = ONE;
    private int blendFactorDstAlpha = ZERO;
    private int blendColor;

    public Blender()
    {
        
    }

    public int getBlendColor()
    {
        return blendColor;
    }

    public int getBlendFactor(int component)
    {
        switch (component)
        {
            case SRC_COLOR:
            {
                return blendFactorSrcColor;
            }
            case SRC_ALPHA:
            {
                return blendFactorSrcAlpha;
            }
            case DST_COLOR:
            {
                return blendFactorDstColor;
            }
            case DST_ALPHA:
            {
                return blendFactorDstAlpha;
            }
            default:
            {
                throw new IllegalArgumentException("component is not SRC_COLOR," +
                        " SRC_ALPHA, DST_COLOR or DST_ALPHA");
            }
        }
    }

    public int getBlendFunction(int channel)
    {
        switch (channel)
        {
            case SRC_COLOR:
            {
                return blendFunctionColor;
            }
            case SRC_ALPHA:
            {
                return blendFunctionAlpha;
            }
            default:
            {
                throw new IllegalArgumentException("channel is not SRC_COLOR or" +
                        " SRC_ALPHA");
            }
        }
    }

    public void setBlendColor(int argb)
    {
        this.blendColor = argb;
    }

    public void setBlendFactors(int srcColor, int srcAlpha, int dstColor, int dstAlpha)
    {
        Require.argumentInEnum(srcColor, "srcColor", ZERO, ONE_MINUS_CONSTANT_ALPHA);
        Require.argumentInEnum(srcAlpha, "srcAlpha", ZERO, ONE_MINUS_CONSTANT_ALPHA);
        Require.argumentInEnum(dstColor, "dstColor", ZERO, ONE_MINUS_CONSTANT_ALPHA);
        Require.argumentInEnum(dstAlpha, "dstAlpha", ZERO, ONE_MINUS_CONSTANT_ALPHA);

        if (dstColor == SRC_ALPHA_SATURATE)
        {
            throw new IllegalArgumentException("dstColor is SRC_ALPHA_SATURATE");
        }
        if (dstAlpha == SRC_ALPHA_SATURATE)
        {
            throw new IllegalArgumentException("dstAlpha is SRC_ALPHA_SATURATE");
        }

        this.blendFactorSrcColor = srcColor;
        this.blendFactorSrcAlpha = srcAlpha;
        this.blendFactorDstColor = dstColor;
        this.blendFactorDstAlpha = dstAlpha;
    }

    public void setBlendFunctions(int funcColor, int funcAlpha)
    {
        Require.argumentInEnum(funcColor, "funcColor", ADD, REVERSE_SUBTRACT);
        Require.argumentInEnum(funcAlpha, "funcAlpha", ADD, REVERSE_SUBTRACT);

        this.blendFunctionColor = funcColor;
        this.blendFunctionAlpha = funcAlpha;
    }
}
