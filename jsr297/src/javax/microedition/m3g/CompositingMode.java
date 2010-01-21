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

import java.util.List;

/**
 * @author jgasseli
 */
public class CompositingMode extends Object3D
{
    public static final int ALPHA = 64;
    public static final int ALPHA_ADD = 65;
    public static final int MODULATE = 66;
    public static final int MODULATE_X2 = 67;
    public static final int REPLACE = 68;
    public static final int ADD = 69;
    public static final int ALPHA_DARKEN = 70;
    public static final int ALPHA_PREMULTIPLIED = 71;
    public static final int MODULATE_INV = 72;

    public static final int NEVER = 512;
    public static final int LESS = 513;
    public static final int EQUAL = 514;
    public static final int LEQUAL = 515;
    public static final int GREATER = 516;
    public static final int NOTEQUAL = 517;
    public static final int GEQUAL = 518;
    public static final int ALWAYS = 519;

    private int blending = REPLACE;
    private float alphaThreshold;
    private int alphaTest = GEQUAL;
    private float depthOffsetFactor, depthOffsetUnits;
    private boolean depthTestEnabled = true;
    private int depthTest = LEQUAL;
    private boolean depthWriteEnabled = true;
    private int colorWriteMask = 0xffffffff;
    private Blender blender;
    private Stencil stencil;
    
    public CompositingMode()
    {
        
    }

    public int getAlphaTest()
    {
        return this.alphaTest;
    }

    public float getAlphaThreshold()
    {
        return this.alphaThreshold;
    }

    public Blender getBlender()
    {
        return this.blender;
    }

    public int getBlending()
    {
        return this.blending;
    }

    public int getColorWriteMask()
    {
        return this.colorWriteMask;
    }

    public float getDepthOffsetFactor()
    {
        return this.depthOffsetFactor;
    }

    public float getDepthOffsetUnits()
    {
        return this.depthOffsetUnits;
    }

    public int getDepthTest()
    {
        return this.depthTest;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);

        if (blender != null)
        {
            references.add(blender);
        }

        if (stencil != null)
        {
            references.add(stencil);
        }
    }
    
    public Stencil getStencil()
    {
        return this.stencil;
    }

    @Deprecated
    public boolean isAlphaWriteEnabled()
    {
        return (this.colorWriteMask & 0xff000000) != 0;
    }

    @Deprecated
    public boolean isColorWriteEnabled()
    {
        return (this.colorWriteMask & 0x00ffffff) != 0;
    }

    public boolean isDepthTestEnabled()
    {
        return this.depthTestEnabled;
    }

    public boolean isDepthWriteEnabled()
    {
        return this.depthWriteEnabled;
    }

    public void setAlphaTest(int func)
    {
        Require.argumentInEnum(func, "func", NEVER, ALWAYS);

        this.alphaTest = func;
    }

    public void setAlphaThreshold(float threshold)
    {
        Require.argumentInRange(threshold, "threshold", 0.0f, 1.0f);

        this.alphaThreshold = threshold;
    }

    @Deprecated
    public void setAlphaWriteEnable(boolean enable)
    {
        this.colorWriteMask = (this.colorWriteMask & 0x00ffffff) |
                (enable ? 0xff000000 : 0x00000000);
    }

    public void setBlender(Blender blender)
    {
        this.blender = blender;
    }

    public void setBlending(int mode)
    {
        Require.argumentInEnum(mode, "mode", ALPHA, MODULATE_INV);

        this.blending = mode;
    }

    @Deprecated
    public void setColorWriteEnable(boolean enable)
    {
        this.colorWriteMask = (this.colorWriteMask & 0xff000000) |
                (enable ? 0x00ffffff : 0x00000000);
    }

    public void setColorWriteMask(int mask)
    {
        this.colorWriteMask = mask;
    }

    public void setDepthOffset(float factor, float units)
    {
        this.depthOffsetFactor = factor;
        this.depthOffsetUnits = units;
    }

    public void setDepthTest(int func)
    {
        Require.argumentInEnum(func, "func", NEVER, ALWAYS);
        
        this.depthTest = func;
    }

    public void setDepthTestEnable(boolean enable)
    {
        this.depthTestEnabled = enable;
    }

    public void setDepthWriteEnable(boolean enable)
    {
        this.depthWriteEnabled = enable;
    }

    public void setStencil(Stencil stencil)
    {
        this.stencil = stencil;
    }
}
