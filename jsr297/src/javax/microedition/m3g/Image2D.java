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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * @author jgasseli
 */
public class Image2D extends ImageBase
{
    Image2D()
    {
        
    }
    
    public Image2D(int format, int width, int height)
    {
        set(format, width, height, 1, true);
    }

    public Image2D(int format, BufferedImage image)
    {
        set(format, image);
    }

    public Image2D(int format, int width, int height, ByteBuffer buffer)
    {
        if (buffer == null)
        {
            throw new NullPointerException("buffer is null");
        }
        set(format, width, height, 1, true);
        set(0, 0, 0, width, height, buffer);
    }
    
    final void set(int format, BufferedImage image)
    {
        Require.notNull(image, "image");

        final int width = image.getWidth();
        final int height = image.getHeight();
        
        set(format, width, height, 1, true);

        //read in the data line by line
        final int[] argbScanline = new int[width];
        //TODO simplify reading of alpha and color depending on ColorModel
        //and AlphaRaster.
        final ByteBuffer dest = getLevelBuffer(0, 0);
        final int destStride = getLevelRowByteStride(0);
        for (int j = 0; j < height; ++j)
        {
            //read in a line of ARGB packed ints
            //image.getRGB(0, j, width, 1, argbScanline, 0, width);
            //y-flipped
            image.getRGB(0, height - 1 - j, width, 1, argbScanline, 0, width);
            //write them out to the destination
            dest.position(j * destStride);
            switch (getColorFormat())
            {
                case ALPHA:
                {
                    for (int i = 0; i < width; ++i)
                    {
                        final int argb = argbScanline[i];
                        dest.put((byte)((argb >> 24) & 0xff));
                    }
                    break;
                }
                case LUMINANCE:
                {
                    for (int i = 0; i < width; ++i)
                    {
                        final int argb = argbScanline[i];
                        final int red = (argb >> 16) & 0xff;
                        final int green = (argb >> 8) & 0xff;
                        final int blue = (argb >> 0) & 0xff;
                        final int luminance = (red + green + blue) / 3;
                        dest.put((byte)luminance);
                    }
                    break;
                }
                case LUMINANCE_ALPHA:
                {
                    for (int i = 0; i < width; ++i)
                    {
                        final int argb = argbScanline[i];
                        final int red = (argb >> 16) & 0xff;
                        final int green = (argb >> 8) & 0xff;
                        final int blue = (argb >> 0) & 0xff;
                        final int luminance = (red + green + blue) / 3;
                        dest.put((byte)luminance);
                        dest.put((byte)((argb >> 24) & 0xff));
                    }
                    break;
                }
                case RGB:
                {
                    for (int i = 0; i < width; ++i)
                    {
                        final int argb = argbScanline[i];
                        dest.put((byte)((argb >> 16) & 0xff));
                        dest.put((byte)((argb >> 8) & 0xff));
                        dest.put((byte)((argb >> 0) & 0xff));
                    }
                    break;
                }
                case RGBA:
                {
                    for (int i = 0; i < width; ++i)
                    {
                        final int argb = argbScanline[i];
                        dest.put((byte)((argb >> 16) & 0xff));
                        dest.put((byte)((argb >> 8) & 0xff));
                        dest.put((byte)((argb >> 0) & 0xff));
                        dest.put((byte)((argb >> 24) & 0xff));
                    }
                    break;
                }
                default:
                {
                    throw new UnsupportedOperationException();
                }
            }
        }
        dest.rewind();

        createMipmapLevels();

        commit();
    }

    void set(int miplevel, int x, int y, int width, int height,
            byte[] palette, byte[] indices)
    {
        Require.notNull(palette, "palette");
        Require.notNull(indices, "indices");
        Require.argumentInRange(miplevel, "miplevel", 0, getLevelCount());

        if ((getFormat() & NO_MIPMAPS) != 0 && miplevel > 0)
        {
            throw new IllegalArgumentException("miplevel > 0, yet NO_MIPMAPS" +
                    " implies all levels beyond 0 are invalid");
        }

        if (!isMutable())
        {
            throw new IllegalStateException("image is not mutable");
        }

        final int bitsPerPixel = getBitsPerPixel();
        if ((bitsPerPixel & 0x7) != 0)
        {
            throw new IllegalStateException("image bits per pixel" +
                    " is not a multiple of 8, palette is not supported for non" +
                    " byte aligned formats");
        }
        
        final ByteBuffer dest = getLevelBuffer(0, miplevel);
        final int destStride = getLevelRowByteStride(miplevel);
        final int bpp = bitsPerPixel >> 3;
        //All allowed formats that map 1:1 are copied in.
        for (int j = 0; j < height; ++j)
        {
            final int destY = y + j;
            dest.position(destStride * destY + x * bpp);
            final int offset = width * j;
            for (int i = 0; i < width; ++i)
            {
                final int index = bpp * (indices[offset + i] & 0xff);
                dest.put(palette, index, bpp);
            }
        }
        dest.rewind();
    }

    public void set(int miplevel, int x, int y, int width, int height,
            ByteBuffer image)
    {
        byte[] bytes = new byte[image.capacity()];
        image.get(bytes);
        set(miplevel, x, y, width, height, bytes);
    }
            
    public void set(int miplevel, int x, int y, int width, int height,
            byte[] bytes)
    {
        Require.notNull(bytes, "bytes");
        Require.argumentInRange(miplevel, "miplevel", 0, getLevelCount());

        if ((getFormat() & NO_MIPMAPS) != 0 && miplevel > 0)
        {
            throw new IllegalArgumentException("miplevel > 0, yet NO_MIPMAPS" +
                    " implies all levels beyond 0 are invalid");
        }

        if (!isMutable())
        {
            throw new IllegalStateException("image is not mutable");
        }

        final ByteBuffer dest = getLevelBuffer(0, miplevel);
        final int destStride = getLevelRowByteStride(miplevel);
        final int bitsPerPixel = getBitsPerPixel();
        final int srcStride = (width * bitsPerPixel + 7) >> 3;

        //All allowed formats that map 1:1 are copied in
        for (int j = 0; j < height; ++j)
        {
            final int destY = y + j;
            dest.position(destStride * destY + ((x * bitsPerPixel) >> 3));
            dest.put(bytes, srcStride * j, (width * bitsPerPixel) >> 3);
        }
        dest.rewind();
    }
}
