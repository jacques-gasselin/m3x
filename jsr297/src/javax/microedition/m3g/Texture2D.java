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
import java.util.List;

/**
 * @author jgasseli
 */
public class Texture2D extends Texture
{
    public static final int FUNC_ADD = 224;
    public static final int FUNC_BLEND = 225;
    public static final int FUNC_DECAL = 226;
    public static final int FUNC_MODULATE = 227;
    public static final int FUNC_REPLACE = 228;

    public static final int WRAP_CLAMP = 240;
    public static final int WRAP_REPEAT = 241;
    public static final int WRAP_MIRROR = 242;

    private int blendColor = 0x00000000;
    private int blending = FUNC_MODULATE;
    private TextureCombiner combiner;
    private int wrappingS = WRAP_REPEAT;
    private int wrappingT = WRAP_REPEAT;

    Texture2D()
    {

    }
    
    public Texture2D(DynamicImage2D image)
    {
        setImage(image);
    }

    public Texture2D(Image2D image)
    {
        setImage(image);
    }

    public int getBlendColor()
    {
        return this.blendColor;
    }

    public int getBlending()
    {
        return this.blending;
    }

    public TextureCombiner getCombiner()
    {
        return this.combiner;
    }

    public DynamicImage2D getDynamicImage2D()
    {
        final ImageBase image = getImageBase();
        return (image instanceof DynamicImage2D) ?
            (DynamicImage2D) image : null;
    }

    public Image2D getImage2D()
    {
        final ImageBase image = getImageBase();
        return (image instanceof Image2D) ?
            (Image2D) image : null;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);

        if (combiner != null)
        {
            references.add(combiner);
        }
    }
    
    public int getWrappingS()
    {
        return this.wrappingS;
    }

    public int getWrappingT()
    {
        return this.wrappingT;
    }

    public void setBlendColor(int argb)
    {
        this.blendColor = argb;
    }

    public void setBlending(int func)
    {
        Require.argumentInEnum(func, "func", FUNC_ADD, FUNC_REPLACE);

        this.blending = func;
    }

    public void setCombiner(TextureCombiner textureCombiner)
    {
        this.combiner = textureCombiner;
    }

    public void setImage(DynamicImage2D image)
    {
        Require.notNull(image, "image");
        
        setImageBase(image);
    }

    public void setImage(Image2D image)
    {
        Require.notNull(image, "image");
        
        setImageBase(image);
    }

    public void setWrapping(int wrapS, int wrapT)
    {
        Require.argumentInEnum(wrapS, "wrapS", WRAP_CLAMP, WRAP_MIRROR);
        Require.argumentInEnum(wrapT, "wrapT", WRAP_CLAMP, WRAP_MIRROR);

        this.wrappingS = wrapS;
        this.wrappingT = wrapT;
    }
}
