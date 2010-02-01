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

package javax.microedition.m3g;

import m3x.Require;

/**
 * @author jgasseli
 */
public class TextureCombiner extends Object3D
{
    public static final int ADD = 16;
    public static final int ADD_SIGNED = 17;
    public static final int DOT3_RGB = 18;
    public static final int DOT3_RGBA = 19;
    public static final int INTERPOLATE = 20;
    public static final int MODULATE = 21;
    public static final int REPLACE = 22;
    public static final int SUBTRACT = 23;

    public static final int CONSTANT = 28;
    public static final int PRIMARY = 29;
    public static final int PREVIOUS = 30;
    public static final int TEXTURE = 31;

    public static final int INVERT = 256;
    public static final int ALPHA = 512;
    
    private int alphaFunction = MODULATE;
    private int[] alphaSource = { TEXTURE, PREVIOUS, CONSTANT | ALPHA };
    private int alphaScale = 1;
    private int colorFunction = MODULATE;
    private int[] colorSource = { TEXTURE, PREVIOUS, CONSTANT };
    private int colorScale = 1;
    
    public TextureCombiner()
    {
        super();
    }

    public int getAlphaFunction()
    {
        return this.alphaFunction;
    }

    final int getAlphaScale()
    {
        return this.alphaScale;
    }

    public int getAlphaSource(int index)
    {
        Require.indexInRange(index, 3);

        return this.alphaSource[index];
    }

    public int getColorFunction()
    {
        return this.colorFunction;
    }

    final int getColorScale()
    {
        return this.colorScale;
    }

    public int getColorSource(int index)
    {
        Require.indexInRange(index, 3);

        return this.colorSource[index];
    }

    public int getScaling()
    {
        return (getColorScale() * 0x00010101) | (getAlphaScale() << 24);
    }

    public void setAlphaSource(int index, int source)
    {
        Require.indexInRange(index, 3);
        Require.argumentInEnum(source & (~INVERT),
                "source", CONSTANT, TEXTURE);

        this.alphaSource[index] = source;
    }

    public void setColorSource(int index, int source)
    {
        Require.indexInRange(index, 3);
        Require.argumentInEnum(source & (~(ALPHA | INVERT)),
                "source", CONSTANT, TEXTURE);
        
        this.colorSource[index] = source;
    }

    public void setFunctions(int colorFunction, int alphaFunction)
    {
        Require.argumentInEnum(colorFunction, "colorFunction", ADD, SUBTRACT);
        Require.argumentInEnum(alphaFunction, "alphaFunction", ADD, SUBTRACT);

        //special for alpha, DOT3 is not supported
        if (alphaFunction == DOT3_RGB || alphaFunction == DOT3_RGBA)
        {
            throw new IllegalArgumentException("DOT3 is not supported as the" +
                    "alpha function");
        }

        this.alphaFunction = alphaFunction;
        this.colorFunction = colorFunction;
    }

    private static final int[] VALID_SCALES = { 1, 2, 4 };
    
    public void setScaling(int colorScale, int alphaScale)
    {
        Require.argumentIn(colorScale, "colorScale", VALID_SCALES);
        Require.argumentIn(alphaScale, "alphaScale", VALID_SCALES);

        this.alphaScale = alphaScale;
        this.colorScale = colorScale;
    }
}
