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
public class SkinnedMeshConverter extends MeshConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.SkinnedMesh to = new m3x.m3g.SkinnedMesh();
        setFromXML(translator, to, (m3x.xml.SkinnedMesh)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.SkinnedMesh to, m3x.xml.SkinnedMesh from)
    {
        super.setFromXML(translator, to, (m3x.xml.MeshType)from);

        to.setSkeleton(getSkeleton(translator, from));

        for (m3x.xml.SkinnedMesh.Bone xmlBone : from.getBones())
        {
            m3x.m3g.Node bone = (m3x.m3g.Node)
                translator.getReference((m3x.xml.Node) xmlBone.getRef());
            to.addTransform(bone, xmlBone.getWeight(),
                xmlBone.getFirstVertex(), xmlBone.getVertexCount());
        }
    }

    private static m3x.m3g.Group getSkeleton(
        XmlToBinaryTranslator translator, m3x.xml.SkinnedMesh from)
    {
        return (m3x.m3g.Group) translator.getReference(from.getSkeleton());
    }

}
