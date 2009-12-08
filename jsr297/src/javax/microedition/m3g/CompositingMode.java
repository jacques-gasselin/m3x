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

    private int blendingMode = REPLACE;
    private float alphaThreshold;
    private int alphaTestFunction = GEQUAL;
    private float depthOffsetFactor, depthOffsetUnits;
    private boolean depthTestEnabled = true;
    private int depthTestFunction = LEQUAL;
    private boolean depthWriteEnabled = true;
    private int colorWriteMask = 0xffffffff;
    private Blender blender;
    private Stencil stencil;
    
    public CompositingMode()
    {
        
    }
}
