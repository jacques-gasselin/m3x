/**
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

package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;

/**
 * 
 * @author jgasseli
 */
public class BackgroundConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Background to = new m3x.m3g.Background();
        setFromXML(translator, to, (m3x.xml.Background)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Background to, m3x.xml.Background from)
    {
        super.setFromXML(translator, to, from);

        to.setColor(from.getColor());
        to.setImage(getImage2D(translator, from));
        //set crop
        {
            //only override values that were explicitly
            //set in the XML file.
            int x = getOptionalValueWithDefault(
                from.getCropX(), to.getCropX());
            int y = getOptionalValueWithDefault(
                from.getCropY(), to.getCropY());
            int width = getOptionalValueWithDefault(
                from.getCropWidth(), to.getCropWidth());
            int height = getOptionalValueWithDefault(
                from.getCropHeight(), to.getCropHeight());
            to.setCrop(x, y, width, height);
        }
        to.setImageMode(from.getImageModeX().value(),
            from.getImageModeY().value());
        to.setColorClearEnable(from.isColorClearEnabled());
        to.setDepthClearEnable(from.isDepthClearEnabled());
    }

    private static final int getOptionalValueWithDefault(final Integer value,
        final int defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        return value.intValue();
    }

    private static m3x.m3g.Image2D getImage2D(
        XmlToBinaryTranslator translator, m3x.xml.Background from)
    {
        m3x.xml.Image2D image = getObjectOrInstance(
            from.getImage2D(), from.getImage2DInstance());
        return (m3x.m3g.Image2D) translator.getReference(image);
    }
}
