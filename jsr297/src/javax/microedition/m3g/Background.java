/*
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
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

    public Background()
    {
        
    }

    public int getColor()
    {
        throw new UnsupportedOperationException();
    }

    public int getColorClearMask()
    {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Image2D getImage()
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public int getImageModeX()
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public int getImageModeY()
    {
        throw new UnsupportedOperationException();
    }

    public int getStencil()
    {
        throw new UnsupportedOperationException();
    }

    public int getStencilClearMask()
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean isColorClearEnabled()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isDepthClearEnabled()
    {
        throw new UnsupportedOperationException();
    }

    public void setColor(int argb)
    {
        throw new UnsupportedOperationException();
    }

    public void setColorClearEnable(boolean enable)
    {
        throw new UnsupportedOperationException();
    }

    public void setColorClearMask(int mask)
    {
        throw new UnsupportedOperationException();
    }

    public void setCrop(int cropX, int cropY, int width, int height)
    {
        throw new UnsupportedOperationException();
    }

    public void setDepth(float depth)
    {
        throw new UnsupportedOperationException();
    }

    public void setDepthClearEnable(boolean enable)
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void setImage(Image2D image)
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void setImageMode(int modeX, int modeY)
    {
        throw new UnsupportedOperationException();
    }

    public void setStencil(int stencil)
    {
        throw new UnsupportedOperationException();
    }

    public void setStencilClearMask(int mask)
    {
        throw new UnsupportedOperationException();
    }
}
