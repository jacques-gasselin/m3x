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
public class Background extends Object3D
{
    @Deprecated
    public static final int BORDER = 32;
    @Deprecated
    public static final int REPEAT = 33;

    private int colorClearMask;
    private int color;
    private boolean depthClearEnabled;
    private float depth;
    private int stencilClearMask;
    private int stencil;
    private Image2D image;
    private int imageModeX;
    private int imageModeY;
    
    public Background()
    {
        setColorClearMask(0xffffffff);
        setColor(0x00000000);
        setDepthClearEnable(true);
        setDepth(1.0f);
        setStencilClearMask(0xffffffff);
        setStencil(0);
        setImage(null);
        setImageMode(BORDER, BORDER);
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
        throw new UnsupportedOperationException();
    }

    public void setColorClearMask(int mask)
    {
        colorClearMask = mask;
    }

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
        depthClearEnabled = enable;
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
