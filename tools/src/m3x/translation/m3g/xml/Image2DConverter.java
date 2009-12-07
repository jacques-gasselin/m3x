/**
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

package m3x.translation.m3g.xml;

import java.util.List;
import m3x.translation.m3g.XmlToBinaryTranslator;

/**
 * 
 * @author jgasseli
 */
public class Image2DConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Image2D to = new m3x.m3g.Image2D();
        setFromXML(translator, to, (m3x.xml.Image2D)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Image2D to, m3x.xml.Image2D from)
    {
        if (from.getFormat() == null)
        {
            throw new IllegalArgumentException("no image format set");
        }
        final int width = from.getWidth();
        if (width <= 0)
        {
            throw new IllegalArgumentException("Invalid width: " + width);
        }
        final int height = from.getHeight();
        if (height <= 0)
        {
            throw new IllegalArgumentException("Invalid height: " + height);
        }
        
        super.setFromXML(translator, to, from);

        to.setFormat(from.getFormat().value());
        to.setSize(width, height);

        final boolean mutable = from.isMutable();
        to.setMutable(mutable);
        if (!mutable)
        {
            List<Short> pixels = from.getPalette();
            if (pixels == null || pixels.size() == 0)
            {
                to.clearPixels();
            }
            else
            {
                to.setPixels(from.getPixels());
            }
            to.setPalette(from.getPalette());
        }
    }
}
