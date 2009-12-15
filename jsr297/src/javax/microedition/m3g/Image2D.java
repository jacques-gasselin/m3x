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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    Image2D(int format, BufferedImage image)
    {
        set(format, image);
    }

    final void set(int format, BufferedImage image)
    {
        Require.notNull(image, "image");

        final int width = image.getWidth();
        final int height = image.getHeight();
        
        set(format, width, height, 1, true);

        //read in the data line by line
        final int[] argbScanline = new int[width];
        final ByteBuffer dest = getLevelBuffer(0, 0);
        final int destStride = getLevelRowByteStride(0);
        final int formatMask = (1 << 10) - 1;
        for (int j = 0; j < height; ++j)
        {
            //read in a line of ARGB packed ints
            image.getRGB(0, j, width, 1, argbScanline, 0, width);
            //write them out to the destination
            dest.position(j * destStride);
            switch (format & formatMask)
            {
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

        createMipmapLevels();

        commit();
    }
}
