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
    
    final int getBitsPerPixel()
    {
        switch (getColorFormat())
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

    final int getFaceCount()
    {
        return this.faceLevelBuffers.length;
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

    private final void requireValidFaceAndLevel(int face, int level)
    {
        Require.argumentNotNegative(face, "face");
        Require.argumentNotNegative(level, "level");

        if (face >= getFaceCount())
        {
            throw new IllegalArgumentException(
                    "face >= getFaceCount()");
        }

        if (level >= getLevelCount())
        {
            throw new IllegalArgumentException(
                    "level >= getLevelCount()");
        }
    }
    
    /**
     * Checks if there is a buffer for the given face and mipmap level.
     * This should be used in any instance where the lazy instantiation
     * if the level buffer is not desired and a call to getLevelBuffer
     * can be avoided.
     * @param face
     * @param level
     * @return true if the given face and level has a buffer.
     */
    final boolean hasLevelBuffer(int face, int level)
    {
        requireValidFaceAndLevel(face, level);

        return this.faceLevelBuffers[face][level] != null;
    }

    /**
     * Returns the buffer for the given face and mipmap level.
     * Buffers may be lazy instantiated to avoid wasting memory.
     * 
     * @param face the texture face to get from
     * @param level the mipmap level to get from
     * @return the existing buffer, or a new one if there was none
     * previously.
     * @throws IllegalArgumentException if level is >= getLevelCount()
     * @throws
     */
    final ByteBuffer getLevelBuffer(int face, int level)
    {
        requireValidFaceAndLevel(face, level);

        ByteBuffer ret = this.faceLevelBuffers[face][level];
        if (ret == null)
        {
            //allocate one
            final int size = getLevelByteSize(level);
            ret = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
            this.faceLevelBuffers[face][level] = ret;
        }
        return ret;
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
        //filter down all levels
        final int faceCount = getFaceCount();
        final int levelCount = getLevelCount();
        final int colorFormat = getColorFormat();
        for (int face = 0; face < faceCount; ++face)
        {
            for (int dstLevel = 1; dstLevel < levelCount; ++dstLevel)
            {
                final int srcLevel = dstLevel - 1;
                
                final ByteBuffer src = getLevelBuffer(face, srcLevel);
                final int srcStride = getLevelRowByteStride(srcLevel);
                final int srcWidth = Math.max(1, getWidth() >> srcLevel);
                final int srcHeight = Math.max(1, getHeight() >> srcLevel);
                
                final ByteBuffer dst = getLevelBuffer(face, dstLevel);
                final int dstStride = getLevelRowByteStride(dstLevel);
                final int dstWidth = Math.max(1, getWidth() >> dstLevel);
                final int dstHeight = Math.max(1, getHeight() >> dstLevel);

                final int filterWidth = srcWidth / dstWidth;
                final int filterHeight = srcHeight / dstHeight;
                final float convolutionToByte = 255.0f / (filterWidth * filterHeight);
                final float byteToUniform = 1.0f / 255;
                
                switch (colorFormat)
                {
                    case RGB:
                    {
                        final int bpp = 3;
                        //convolve with a 2D box filter
                        for (int dstY = 0; dstY < dstHeight; ++dstY)
                        {
                            for (int dstX = 0; dstX < dstWidth; ++dstX)
                            {
                                float r = 0;
                                float g = 0;
                                float b = 0;

                                final int srcX = dstX * filterWidth;
                                final int srcY = dstY * filterHeight;
                                for (int j = 0; j < filterHeight; ++j)
                                {
                                    for (int i = 0; i < filterWidth; ++i)
                                    {
                                        //accumulate
                                        src.position((srcY + j) * srcStride
                                                + (srcX + i) * bpp);
                                        r += (src.get() & 0xff) * byteToUniform;
                                        g += (src.get() & 0xff) * byteToUniform;
                                        b += (src.get() & 0xff) * byteToUniform;
                                    }
                                }

                                dst.position(dstY * dstStride + dstX * bpp);
                                dst.put((byte) Math.round(r * convolutionToByte));
                                dst.put((byte) Math.round(g * convolutionToByte));
                                dst.put((byte) Math.round(b * convolutionToByte));
                            }
                        }
                        src.rewind();
                        dst.rewind();
                        break;
                    }
                    default:
                    {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }
    }
}
