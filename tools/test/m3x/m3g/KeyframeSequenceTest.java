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

package m3x.m3g;

import m3x.m3g.KeyframeSequence.FloatKeyFrame;

/**
 *
 * @author jsaarinen
 * @author jgasseli
 */
public class KeyframeSequenceTest extends AbstractTestCase
{
    private KeyframeSequence sequence;

    public KeyframeSequenceTest()
    {
        sequence = new KeyframeSequence();
        FloatKeyFrame[] frames = new FloatKeyFrame[3];
        frames[0] = new KeyframeSequence.FloatKeyFrame(0, new float[]{0, 0, 1});
        frames[1] = new KeyframeSequence.FloatKeyFrame(0, new float[]{1, 0, 0});
        frames[2] = new KeyframeSequence.FloatKeyFrame(0, new float[]{0, 1, 0});
        sequence.setKeyframes(frames);
        sequence.setInterpolationType(KeyframeSequence.STEP);
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ sequence };
        assertSaveAndLoad(roots);
    }
}
