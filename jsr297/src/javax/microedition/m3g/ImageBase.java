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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author jgasseli
 */
public abstract class ImageBase extends Object3D
{
    static abstract class RendererData
    {
        abstract void sourceDataChanged();
    }
    
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
    
    static final int FORMAT_COLOR_MASK = 16383;

    public static final int LOSSLESS = 16384;
    public static final int NO_MIPMAPS = 32768;
    public static final int Y_UP = 65536;

    private int format;
    private int width;
    private int height;
    private boolean mutable;

    /**
     * An array of mipmap level buffer data per face.
     */
    private ByteBuffer[][] faceLevelBuffers;

    private RendererData rendererData;

    ImageBase()
    {
        
    }

    void set(int format, int width, int height, int numFaces, boolean mutable)
    {
        Require.argumentInEnum(format & FORMAT_COLOR_MASK, "format", ALPHA, FORMAT_COLOR_MASK);
        Require.argumentGreaterThanZero(width, "width");
        Require.argumentGreaterThanZero(height, "height");
        Require.argumentGreaterThanZero(numFaces, "numFaces");
        
        this.format = format;
        this.width = width;
        this.height = height;
        this.mutable = mutable;


        final int maxDimension = Math.max(width, height);
        final int mipmapLevels = (format & NO_MIPMAPS) != 0 ?
            1 : Integer.numberOfTrailingZeros(maxDimension) + 1;

        this.faceLevelBuffers = new ByteBuffer[numFaces][mipmapLevels];
        for (int face = 0; face < numFaces; ++face)
        {
            for (int i = 0; i < mipmapLevels; ++i)
            {
                final int size = getLevelByteSize(i);
                this.faceLevelBuffers[face][i] = ByteBuffer.allocateDirect(size).
                        order(ByteOrder.nativeOrder());
            }
        }
    }

    public void commit()
    {
        mutable = false;
    }

    final int getColorFormat()
    {
        return getFormat() & FORMAT_COLOR_MASK;
    }

    public final int getFormat()
    {
        return format;
    }

    public final int getHeight()
    {
        return height;
    }
    
    public final int getWidth()
    {
        return width;
    }

    final RendererData getRendererData()
    {
        return rendererData;
    }

    final boolean isLossless()
    {
        return (getFormat() & LOSSLESS) != 0;
    }

    public boolean isMipmapGenerateEnabled()
    {
        throw new UnsupportedOperationException();
    }

    public final boolean isMipmapped()
    {
        return getLevelCount() > 1;
    }

    public final boolean isMutable()
    {
        return mutable;
    }

    public void setMipmapGenerateEnable(boolean enable)
    {
        throw new UnsupportedOperationException();
    }

    void setRendererData(RendererData rendererData)
    {
        this.rendererData = rendererData;
    }
    
    private final int getBitsPerPixel()
    {
        final int formatMask = (1 << 10) - 1;
        switch (getFormat() & formatMask)
        {
            case ALPHA:
            case LUMINANCE:
            case R_COMPRESSED:
            {
                return 8;
            }
            case LUMINANCE_ALPHA:
            case RGB565:
            case RGBA4444:
            case RGBA5551:
            case RG_COMPRESSED:
            {
                return 16;
            }
            case RGB:
            case RGB_COMPRESSED:
            {
                return 24;
            }
            case RGBA:
            case RGBA_COMPRESSED:
            {
                return 32;
            }
            default:
            {
                throw new UnsupportedOperationException();
            }
        }
    }

    final int getLevelCount()
    {
        return this.faceLevelBuffers[0].length;
    }

    private static final int bitsToBytes(int bits)
    {
        return (bits + 7) >> 3;
    }

    private static final int align4(int bytes)
    {
        return (bytes + 3) & (~3);
    }

    final ByteBuffer getLevelBuffer(int face, int level)
    {
        Require.argumentNotNegative(level, "level");

        final int levelCount = getLevelCount();
        if (level >= levelCount)
        {
            throw new IllegalArgumentException("level >= levelCount");
        }

        return this.faceLevelBuffers[face][level];
    }

    final int getLevelRowByteStride(int level)
    {
        Require.argumentNotNegative(level, "level");

        final int levelCount = getLevelCount();
        if (level >= levelCount)
        {
            throw new IllegalArgumentException("level >= levelCount");
        }

        final int w = Math.max(1, getWidth() >> level);
        final int bitsPerPixel = getBitsPerPixel();
        //4byte aligned bytes stride for all rows
        return align4(bitsToBytes(w * bitsPerPixel));
    }

    private final int getLevelByteSize(int level)
    {
        final int byteStride = getLevelRowByteStride(level);
        final int h = Math.max(1, getHeight() >> level);
        int size = byteStride * h;

        //potentially adjust size to some format minimum
        switch (getFormat())
        {
            default:
            {
                break;
            }
        }

        return size;
    }

    final void createMipmapLevels()
    {
        //TODO
        return;
    }
}
