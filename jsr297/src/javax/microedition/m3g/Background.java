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
public class Background extends Object3D
{
    @Deprecated
    public static final int BORDER = 32;
    @Deprecated
    public static final int REPEAT = 33;

    private int colorClearMask = 0xffffffff;
    private int color;
    private boolean depthClearEnabled = true;
    private float depth = 1.0f;
    private int stencilClearMask = 0xffffffff;
    private int stencil;
    private Image2D image;
    private int imageModeX = BORDER;
    private int imageModeY = BORDER;
    
    public Background()
    {
    }

    public int getColor()
    {
        return color;
    }

    public int getColorClearMask()
    {
        return colorClearMask;
    }

    @Deprecated
    public int getCropHeight()
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public int getCropWidth()
    {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public int getCropX()
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public int getCropY()
    {
        throw new UnsupportedOperationException();
    }

    public float getDepth()
    {
        return depth;
    }

    @Deprecated
    public Image2D getImage()
    {
        return image;
    }

    @Deprecated
    public int getImageModeX()
    {
        return imageModeX;
    }

    @Deprecated
    public int getImageModeY()
    {
        return imageModeY;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);

        if (image != null)
        {
            references.add(image);
        }
    }
    
    public int getStencil()
    {
        return stencil;
    }

    public int getStencilClearMask()
    {
        return stencilClearMask;
    }

    @Deprecated
    public boolean isColorClearEnabled()
    {
        return colorClearMask != 0;
    }

    public boolean isDepthClearEnabled()
    {
        return depthClearEnabled;
    }

    public void setColor(int argb)
    {
        color = argb;
    }

    @Deprecated
    public void setColorClearEnable(boolean enable)
    {
        setColorClearEnabled(enable);
    }

    @Deprecated
    public void setColorClearEnabled(boolean enabled)
    {
        throw new UnsupportedOperationException();
    }

    public void setColorClearMask(int mask)
    {
        colorClearMask = mask;
    }

    @Deprecated
    public void setCrop(int cropX, int cropY, int width, int height)
    {
        throw new UnsupportedOperationException();
    }

    public void setDepth(float depth)
    {
        //silently clamp to the range [0, 1]
        if (depth > 1.0f)
        {
            depth = 1.0f;
        }
        if (depth < 0.0f)
        {
            depth = 0.0f;
        }
        this.depth = depth;
    }

    public void setDepthClearEnable(boolean enable)
    {
        setDepthClearEnabled(enable);
    }

    public void setDepthClearEnabled(boolean enabled)
    {
        depthClearEnabled = enabled;
    }

    @Deprecated
    public void setImage(Image2D image)
    {
        this.image = image;
    }

    @Deprecated
    public void setImageMode(int modeX, int modeY)
    {
        imageModeX = modeX;
        imageModeY = modeY;
    }

    public void setStencil(int stencil)
    {
        this.stencil = stencil;
    }

    public void setStencilClearMask(int mask)
    {
        stencilClearMask = mask;
    }
}
