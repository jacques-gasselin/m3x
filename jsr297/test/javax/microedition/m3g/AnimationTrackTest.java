/*
 * Copyright (c) 2010, Jacques Gasselin de Richebourg
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

import m3x.AbstractTestCase;

/**
 * @author jgasseli
 */
public class AnimationTrackTest extends AbstractTestCase
{
    private AnimationTrack a;

    @Override
    public void setUp()
    {
        a = new AnimationTrack();
    }

    @Override
    public void tearDown()
    {
        a = null;
    }

    public void testGetControllerUninitialized()
    {
        assertNull(a.getController());
    }

    public void testGetKeyframeSequenceUninitialized()
    {
        assertNull(a.getKeyframeSequence());
    }
    
    public void testSampleTwoLINEAR()
    {
        KeyframeSequence k = new KeyframeSequence(2, 1, KeyframeSequence.LINEAR);
        k.setKeyframe(0, 100, new float[]{ 1.0f });
        k.setKeyframe(1, 200, new float[]{ 2.0f });
        k.setDuration(300);
        
        AnimationController controller = new AnimationController();
        
        assertEquals(150, controller.getPosition(150), 0.0001f);
        
        a.setController(controller);
        a.setKeyframeSequence(k);
        
        assertEquals(1.0f, a.sample(50, 0), 0.0001f);
        assertEquals(1.0f, a.sample(99, 0), 0.0001f);
        assertEquals(1.0f, a.sample(100, 0), 0.0001f);
        assertEquals(1.5f, a.sample(150, 0), 0.0001f);
        assertEquals(2.0f, a.sample(200, 0), 0.0001f);
        assertEquals(2.0f, a.sample(201, 0), 0.0001f);
        assertEquals(2.0f, a.sample(250, 0), 0.0001f);
    }
}
