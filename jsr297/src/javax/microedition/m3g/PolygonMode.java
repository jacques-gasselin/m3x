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
public class PolygonMode extends Object3D
{
    public static final int CULL_BACK = 160;
    public static final int CULL_FRONT = 161;
    public static final int CULL_NONE = 162;
    public static final int SHADE_FLAT = 164;
    public static final int SHADE_SMOOTH = 165;
    public static final int WINDING_CCW = 168;
    public static final int WINDING_CW = 169;

    private int culling = CULL_BACK;
    private int winding = WINDING_CCW;
    private float lineWidth = 1.0f;
    private int shading = SHADE_SMOOTH;
    private boolean twoSidedLightingEnabled;
    private boolean localCameraLightingEnabled;
    private boolean perspectiveCorrectionEnabled;
    
    public PolygonMode()
    {
        
    }

    public int getCulling()
    {
        return this.culling;
    }

    public float getLineWidth()
    {
        return this.lineWidth;
    }

    public int getShading()
    {
        return this.shading;
    }

    public int getWinding()
    {
        return this.winding;
    }

    @Deprecated
    public boolean isLocalCameraLightingEnabled()
    {
        return this.localCameraLightingEnabled;
    }

    @Deprecated
    public boolean isPerspectiveCorrectionEnabled()
    {
        return this.perspectiveCorrectionEnabled;
    }

    @Deprecated
    public boolean isTwoSidedLightingEnabled()
    {
        return this.twoSidedLightingEnabled;
    }

    public void setCulling(int mode)
    {
        Require.argumentInEnum(mode, "mode", CULL_BACK, CULL_NONE);

        this.culling = mode;
    }

    public void setLineWidth(float width)
    {

        this.lineWidth = width;
    }

    @Deprecated
    public void setLocalCameraLightingEnable(boolean enable)
    {
        this.localCameraLightingEnabled = enable;
    }

    @Deprecated
    public void setPerspectiveCorrectionEnable(boolean enable)
    {
        this.perspectiveCorrectionEnabled = enable;
    }

    public void setShading(int mode)
    {
        Require.argumentInEnum(mode, "mode", SHADE_FLAT, SHADE_SMOOTH);

        this.shading = mode;
    }

    @Deprecated
    public void setTwoSidedLightingEnabled(boolean enable)
    {
        this.twoSidedLightingEnabled = enable;
    }

    public void setWinding(int mode)
    {
        Require.argumentInEnum(mode, "mode", WINDING_CCW, WINDING_CW);

        this.winding = mode;
    }
}
