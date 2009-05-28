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
public abstract class ImageBase
{
    public static final int ALPHA = 96;
    public static final int LUMINANCE = 97;
    public static final int LUMINANCE_ALPHA = 98;
    public static final int RGB = 99;
    public static final int RGBA = 100;
    public static final int RGB565 = 101;
    public static final int RGBA5551 = 102;
    public static final int RGBA4444 = 103;
    public static final int RGB_ETC = 104;
    public static final int R_COMPRESSED = 105;
    public static final int RG_COMPRESSED = 106;
    public static final int RGB_COMPRESSED = 107;
    public static final int RGBA_COMPRESSED = 108;
    
    public static final int LOSSLESS = 16384;
    public static final int NO_MIPMAPS = 32768;
    public static final int Y_UP = 65536;

    private int format;
    private int width;
    private int height;
    private boolean mutable;

    ImageBase(int format, int width, int height, boolean mutable)
    {
        this.width = width;
        this.height = height;
        this.mutable = mutable;
        this.format = format;
    }
    
    public void commit()
    {
        mutable = false;
    }

    public int getFormat()
    {
        return format;
    }

    public int getHeight()
    {
        return height;
    }
    
    public int getWidth()
    {
        return width;
    }

    public boolean isMipmapGenerateEnabled()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isMipmapped()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isMutable()
    {
        return mutable;
    }

    public void setMipmapGenerateEnable(boolean enable)
    {
        throw new UnsupportedOperationException();
    }
}
