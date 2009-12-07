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

package m3x.m3g;

import java.io.ByteArrayOutputStream;

/**
 * 
 * @author jgasseli
 */
public class Image2DTest extends AbstractTestCase
{
    private Image2D image;

    public Image2DTest()
    {
        image = new Image2D();
        image.setFormat(Image2D.RGBA);
        image.setMutable(false);
        image.setSize(256, 256);
        image.setPalette(new byte[256 * 4]);
        image.setPixels(new byte[256 * 256]);
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ image };

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Saver.save(out, roots, "1.0", "Image2DTest");

            byte[] data = out.toByteArray();

            Object3D[] loadRoots = Loader.load(data);

            for (int i = 0; i < roots.length; ++i)
            {
                doTestAccessors(roots[i], loadRoots[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testSerializationAndDeseriliazation1()
    {
        assertSerialiseSingle(image);
    }
}
