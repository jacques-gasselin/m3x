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

import m3x.translation.m3g.XmlToBinaryTranslator;

/**
 * 
 * @author jgasseli
 */
public class AnimationTrackConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.AnimationTrack to = new m3x.m3g.AnimationTrack();
        setFromXML(translator, to, (m3x.xml.AnimationTrack)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.AnimationTrack to, m3x.xml.AnimationTrack from)
    {
        super.setFromXML(translator, to, from);
        to.setTargetProperty(from.getTargetProperty().value());
        to.setController(getController(translator, from));
        to.setKeyframeSequence(getKeyframeSequence(translator, from));
    }
    
    private static m3x.m3g.AnimationController getController(
        XmlToBinaryTranslator translator, m3x.xml.AnimationTrack from)
    {
        m3x.xml.AnimationController controller = getObjectOrInstance(
            from.getController(), from.getControllerInstance());
        return (m3x.m3g.AnimationController) translator.getReference(controller);
    }

    private static m3x.m3g.KeyframeSequence getKeyframeSequence(
        XmlToBinaryTranslator translator, m3x.xml.AnimationTrack from)
    {
        m3x.xml.KeyframeSequence sequence = getObjectOrInstance(
            from.getKeyframeSequence(), from.getKeyframeSequenceInstance());
        return (m3x.m3g.KeyframeSequence) translator.getReference(sequence);
    }
}
