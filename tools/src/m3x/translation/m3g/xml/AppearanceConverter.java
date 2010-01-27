/**
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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
public class AppearanceConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Appearance to = new m3x.m3g.Appearance();
        setFromXML(translator, to, (m3x.xml.Appearance)from);
        return to;
    }
    
    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Appearance to, m3x.xml.Appearance from)
    {
        super.setFromXML(translator, to, from);
        to.setLayer(from.getLayer());
        to.setCompositingMode(getCompositingMode(translator, from));
        to.setFog(getFog(translator, from));
        to.setPolygonMode(getPolygonMode(translator, from));
        to.setMaterial(getMaterial(translator, from));

        //set textures
        List<Object> textureList = from.getTexture2DInstanceOrTexture2D();
        to.setTextureCount(textureList.size());
        for (int i = 0; i < to.getTextureCount(); ++i)
        {
            m3x.xml.Texture2D xmlTex = getObjectOrInstance(textureList.get(i));
            m3x.m3g.Texture2D tex = (m3x.m3g.Texture2D)
                translator.getReference(xmlTex);
            to.setTexture(i, tex);
        }
    }

    private static m3x.m3g.CompositingMode getCompositingMode(
        XmlToBinaryTranslator translator, m3x.xml.Appearance from)
    {
        m3x.xml.CompositingMode cm = getObjectOrInstance(
            from.getCompositingMode(), from.getCompositingModeInstance());
        return (m3x.m3g.CompositingMode) translator.getReference(cm);
    }

    private static m3x.m3g.Fog getFog(
        XmlToBinaryTranslator translator, m3x.xml.Appearance from)
    {
        m3x.xml.Fog fog = getObjectOrInstance(from.getFog(), from.getFogInstance());
        return (m3x.m3g.Fog) translator.getReference(fog);
    }

    private static m3x.m3g.PolygonMode getPolygonMode(
        XmlToBinaryTranslator translator, m3x.xml.Appearance from)
    {
        m3x.xml.PolygonMode pm = getObjectOrInstance(
            from.getPolygonMode(), from.getPolygonModeInstance());
        return (m3x.m3g.PolygonMode) translator.getReference(pm);
    }

    private static m3x.m3g.Material getMaterial(
        XmlToBinaryTranslator translator, m3x.xml.Appearance from)
    {
        m3x.xml.Material mat = getObjectOrInstance(
            from.getMaterial(), from.getMaterialInstance());
        return (m3x.m3g.Material) translator.getReference(mat);
    }
}
