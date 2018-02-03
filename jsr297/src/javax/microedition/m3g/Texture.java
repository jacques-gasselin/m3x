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

import java.util.List;

/**
 * @author jgasseli
 */
public abstract class Texture extends Transformable
{
    static abstract class RendererData
    {
        abstract void sourceDataChanged();
    }

    public static final int FILTER_BASE_LEVEL = 208;
    public static final int FILTER_LINEAR = 209;
    public static final int FILTER_NEAREST = 210;
    public static final int FILTER_ANISOTROPIC = 211;

    private ImageBase imageBase;
    private int levelFilter = FILTER_BASE_LEVEL;
    private int imageFilter = FILTER_NEAREST;
    
    private RendererData rendererData;

    protected Texture()
    {
        
    }

    public final ImageBase getImageBase()
    {
        return this.imageBase;
    }

    public final int getImageFilter()
    {
        return this.imageFilter;
    }

    public final int getLevelFilter()
    {
        return this.levelFilter;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);

        if (imageBase != null)
        {
            references.add(imageBase);
        }
    }
    
    final RendererData getRendererData()
    {
        return this.rendererData;
    }

    public final void setFiltering(int levelFilter, int imageFilter)
    {
        Require.argumentInEnum(levelFilter, "levelFilter",
                FILTER_BASE_LEVEL, FILTER_NEAREST);
        Require.argumentInEnum(imageFilter, "imageFilter",
                FILTER_LINEAR, FILTER_ANISOTROPIC);
        
        this.levelFilter = levelFilter;
        this.imageFilter = imageFilter;
    }
    
    final void setImageBase(ImageBase imageBase)
    {
        this.imageBase = imageBase;
    }

    final void setRendererData(RendererData rendererData)
    {
        this.rendererData = rendererData;
    }
}
