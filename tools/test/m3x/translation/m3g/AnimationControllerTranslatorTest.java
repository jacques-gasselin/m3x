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

package m3x.translation.m3g;

import m3x.translation.*;
import m3x.m3g.AnimationController;

/**
 * 
 * @author jgasseli
 */
public class AnimationControllerTranslatorTest extends TranslatorSupport
{
    public void testTranslator()
    {
        XmlToBinaryTranslator translator = new XmlToBinaryTranslator("1.0");

        m3x.xml.AnimationController ac = new m3x.xml.AnimationController();
        ac.setActiveIntervalEnd(1);
        ac.setActiveIntervalStart(2);
        ac.setReferenceSequenceTime(0.5f);
        ac.setReferenceWorldTime(3);
        ac.setSpeed(1.0f);
        ac.setWeight(2.0f);

        AnimationController m3gAC = (AnimationController) translator.getObject(ac);

        assertEquals(ac.getActiveIntervalEnd(), m3gAC.getActiveIntervalEnd());
        assertEquals(ac.getActiveIntervalStart(), m3gAC.getActiveIntervalStart());
        assertEquals(ac.getReferenceSequenceTime(), m3gAC.getPosition(m3gAC.getRefWorldTime()));
        assertEquals(ac.getReferenceWorldTime(), m3gAC.getRefWorldTime());
        assertEquals(ac.getSpeed(), m3gAC.getSpeed());
        assertEquals(ac.getWeight(), m3gAC.getWeight());
    }
}
