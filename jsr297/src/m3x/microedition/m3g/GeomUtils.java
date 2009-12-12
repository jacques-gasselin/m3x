/*
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

package m3x.microedition.m3g;

import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.Mesh;

/**
 * @author jgasseli
 */
public final class GeomUtils
{
    /**
     * Static utility class.
     */
    private GeomUtils()
    {
        
    }

    public static final Mesh createSphere(float radius, int slices, int stacks)
    {
        if (radius <= 0)
        {
            throw new IllegalArgumentException("negative or 0 radius not allowed");
        }
        if (slices <= 0)
        {
            throw new IllegalArgumentException("negative or 0 slices not allowed");
        }
        if (stacks <= 0)
        {
            throw new IllegalArgumentException("negative or 0 stacks not allowed");
        }

        /*VertexBuffer vb = new VertexBuffer();
        //VertexArray positions = new VertexArray();

        final double stepZ = 2.0 * radius / stacks;
        final double stepAngle = 2.0 * Math.PI / slices;
        for (int stack = 0; stack < stacks; ++stack)
        {
            for (int slice = 0; slice < slices; ++slice)
            {
                
            }
        }*/

        throw new UnsupportedOperationException();
    }
}
