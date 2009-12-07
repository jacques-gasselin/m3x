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
import m3x.xml.MeshType;

/**
 * 
 * @author jgasseli
 */
public class MeshConverter extends NodeConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Mesh to = new m3x.m3g.Mesh();
        setFromXML(translator, to, (m3x.xml.Mesh)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Mesh to, m3x.xml.MeshType from)
    {
        super.setFromXML(translator, to, from);
        to.setVertexBuffer(getVertexBuffer(translator, from));
        
        List<MeshType.Submesh> submeshes = from.getSubmesh();
        to.setSubmeshCount(submeshes.size());
        for (int i = 0; i < submeshes.size(); ++i)
        {
            final MeshType.Submesh submesh = submeshes.get(i);
            to.setAppearance(i, getAppearance(translator, submesh));
            to.setIndexBuffer(i, getIndexBuffer(translator, submesh));
        }
    }

    private static m3x.m3g.VertexBuffer getVertexBuffer(
        XmlToBinaryTranslator translator, m3x.xml.MeshType from)
    {
        m3x.xml.VertexBuffer vb = getObjectOrInstance(
            from.getVertexBuffer(), from.getVertexBufferInstance());
        return (m3x.m3g.VertexBuffer) translator.getReference(vb);
    }

    private static m3x.m3g.Appearance getAppearance(
        XmlToBinaryTranslator translator, MeshType.Submesh from)
    {
        m3x.xml.Appearance a = getObjectOrInstance(
            from.getAppearance(), from.getAppearanceInstance());
        return (m3x.m3g.Appearance) translator.getReference(a);
    }

    private static m3x.m3g.IndexBuffer getIndexBuffer(
        XmlToBinaryTranslator translator, MeshType.Submesh from)
    {
        m3x.xml.IndexBuffer ib = getObjectOrInstance(
            from.getTriangleStripArray(), from.getTriangleStripArrayInstance());
        return (m3x.m3g.IndexBuffer) translator.getReference(ib);
    }

}
