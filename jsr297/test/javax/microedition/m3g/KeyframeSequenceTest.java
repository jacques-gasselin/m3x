/*
 * Copyright (c) 2009-2010, Jacques Gasselin de Richebourg
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
public class KeyframeSequenceTest extends AbstractTestCase
{
    public void testNewKeyframe()
    {
        KeyframeSequence k = new KeyframeSequence();
    }
    
    public void testNewKeyframeZeroKeys()
    {
        try
        {
            KeyframeSequence k = new KeyframeSequence(0, 4, KeyframeSequence.LINEAR);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            
        }
    }

    public void testGetInterpolationType()
    {
        KeyframeSequence k = new KeyframeSequence(1, 4, KeyframeSequence.LINEAR);
        assertEquals(KeyframeSequence.LINEAR, k.getInterpolationType());
    }
    
    public void testGetChannelCount()
    {
        KeyframeSequence k = new KeyframeSequence(1, 4, KeyframeSequence.LINEAR);
        assertEquals(1, k.getChannelCount());
    }
    
    public void testSampleSingleUninitialized()
    {
        KeyframeSequence k = new KeyframeSequence(1, 1, KeyframeSequence.LINEAR);
        try
        {
            k.sample(0, 0);
            fail("KeyframeSequence has deferred exceptions for uninitialized instances");
        }
        catch (IllegalStateException e)
        {
            
        }
    }

    public void testSampleSingleLINEAREmpty()
    {
        KeyframeSequence k = new KeyframeSequence(1, 1, KeyframeSequence.LINEAR);
        k.setDuration(100);
        assertEquals(0.0f, k.sample(0, 0), 0.0001f);
    }

    public void testSampleSingleLINEAR()
    {
        KeyframeSequence k = new KeyframeSequence(1, 1, KeyframeSequence.LINEAR);
        k.setKeyframe(0, 50, new float[]{ 1.0f });
        k.setDuration(100);
        assertEquals(1.0f, k.sample(0, 0), 0.0001f);
        assertEquals(1.0f, k.sample(50, 0), 0.0001f);
        assertEquals(1.0f, k.sample(100, 0), 0.0001f);
    }

    public void testSampleSingleSTEP()
    {
        KeyframeSequence k = new KeyframeSequence(1, 1, KeyframeSequence.STEP);
        k.setKeyframe(0, 100, new float[]{ 1.0f });
        k.setDuration(200);
        assertEquals(1.0f, k.sample(0, 0), 0.0001f);
    }

    public void testSampleTwoSTEP()
    {
        KeyframeSequence k = new KeyframeSequence(2, 1, KeyframeSequence.STEP);
        k.setKeyframe(0, 100, new float[]{ 1.0f });
        k.setKeyframe(1, 200, new float[]{ 2.0f });
        k.setDuration(300);
        assertEquals(1.0f, k.sample(50, 0), 0.0001f);
        assertEquals(1.0f, k.sample(150, 0), 0.0001f);
        assertEquals(2.0f, k.sample(250, 0), 0.0001f);
    }

    public void testSampleTwoLINEAR()
    {
        KeyframeSequence k = new KeyframeSequence(2, 1, KeyframeSequence.LINEAR);
        k.setKeyframe(0, 100, new float[]{ 1.0f });
        k.setKeyframe(1, 200, new float[]{ 2.0f });
        k.setDuration(300);
        assertEquals(1.0f, k.sample(50, 0), 0.0001f);
        assertEquals(1.0f, k.sample(99, 0), 0.0001f);
        assertEquals(1.0f, k.sample(100, 0), 0.0001f);
        assertEquals(1.5f, k.sample(150, 0), 0.0001f);
        assertEquals(2.0f, k.sample(200, 0), 0.0001f);
        assertEquals(2.0f, k.sample(201, 0), 0.0001f);
        assertEquals(2.0f, k.sample(250, 0), 0.0001f);
    }
}
