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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * @author jgasseli
 */
public final class Loader
{
    /**
     * Static utility class
     */
    private Loader()
    {
        
    }

    @Deprecated
    public static final Object3D[] load(byte[] data, int offset)
        throws IOException
    {
        final InputStream stream = new ByteArrayInputStream(data);
        stream.skip(offset);
        return load(stream);
    }

    public static final Object3D[] load(InputStream stream)
        throws IOException
    {
        return load(stream, null);
    }

    public static final Object3D[] load(InputStream stream,
            Object3D[] referenceObjects)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public static final Object3D[] load(String name)
        throws IOException
    {
        return load(name, null);
    }

    public static final Object3D[] load(String name, Object3D[] referenceObjects)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public static final ImageBase loadImage(int format, InputStream stream)
        throws IOException
    {
        Require.notNull(stream, "stream");
        
        BufferedImage bufferedImage = ImageIO.read(stream);
        if (bufferedImage == null)
        {
            throw new IOException("unable to load image");
        }

        //TODO properly decode the image data with regards to format

        ImageBase image = new Image2D(format, bufferedImage);

        return image;
    }

    public static final ImageBase loadImage(int format, String name)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
