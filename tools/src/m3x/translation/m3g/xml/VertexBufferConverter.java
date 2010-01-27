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
public class VertexBufferConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.VertexBuffer to = new m3x.m3g.VertexBuffer();
        setFromXML(translator, to, (m3x.xml.VertexBuffer)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, m3x.xml.VertexBuffer from)
    {
        super.setFromXML(translator, to, from);

        to.setDefaultColor(from.getDefaultColor());
        setPositions(translator, to, from.getPositions());
        to.setNormals(getNormals(translator, from.getNormals()));
        to.setColors(getColors(translator, from.getColors()));
        setTexCoords(translator, to, from.getTexcoords());
    }

    private static final float[] getBias(List<Float> list)
    {
        if (list == null)
        {
            return null;
        }

        float[] bias = new float[3];
        final int size = Math.min(bias.length, list.size());
        for (int i = 0; i < size; ++i)
        {
            bias[i] = list.get(i);
        }
        return bias;
    }

    private static final void setPositions(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, m3x.xml.VertexBuffer.Positions from)
    {
        if (from == null)
        {
            to.setPositions(null, 1.0f, null);
        }
        else
        {
            final float[] bias = getBias(from.getBias());
            final float scale = from.getScale();
            m3x.xml.VertexArray xmlVA = getObjectOrInstance(
                from.getVertexArray(), from.getVertexArrayInstance());
            m3x.m3g.VertexArray va = (m3x.m3g.VertexArray)
                translator.getReference(xmlVA);
            to.setPositions(va, scale, bias);
        }
    }

    private static final m3x.m3g.VertexArray getNormals(
        XmlToBinaryTranslator translator, m3x.xml.VertexBuffer.Normals from)
    {
        if (from == null)
        {
            return null;
        }
        m3x.xml.VertexArray xmlVA = getObjectOrInstance(
            from.getVertexArray(), from.getVertexArrayInstance());
        return (m3x.m3g.VertexArray) translator.getReference(xmlVA);
    }

    private static final m3x.m3g.VertexArray getColors(
        XmlToBinaryTranslator translator, m3x.xml.VertexBuffer.Colors from)
    {
        if (from == null)
        {
            return null;
        }
        m3x.xml.VertexArray xmlVA = getObjectOrInstance(
            from.getVertexArray(), from.getVertexArrayInstance());
        return (m3x.m3g.VertexArray) translator.getReference(xmlVA);
    }

    private static final void setTexCoords(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, List<m3x.xml.VertexBuffer.Texcoords> fromList)
    {
        final int texCoordCount = fromList.size();
        to.setTexCoordCount(texCoordCount);
        for (int i = 0; i < texCoordCount; ++i)
        {
            m3x.xml.VertexBuffer.Texcoords from = fromList.get(i);
            if (from == null)
            {
                to.setTexCoords(i, null, 1.0f, null);
            }
            else
            {
                final float[] bias = getBias(from.getBias());
                final float scale = from.getScale();
                m3x.xml.VertexArray xmlVA = getObjectOrInstance(
                    from.getVertexArray(), from.getVertexArrayInstance());
                m3x.m3g.VertexArray va = (m3x.m3g.VertexArray)
                    translator.getReference(xmlVA);
                to.setTexCoords(i, va, scale, bias);
            }
        }
    }

}
