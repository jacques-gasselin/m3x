/*
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

package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public class Graphics3D
{
    public static final int ANTIALIAS = 2;
    public static final int DITHER = 4;
    public static final int TRUE_COLOR = 8;
    public static final int OVERWRITE = 16;
    public static final int NO_OVERLAYS = 32;
    public static final int PURE3D = 48;
    public static final int DEPTH = 64;
    public static final int STENCIL = 128;
    public static final int VALIDATE = 256;

    private static Graphics3D instance;

    protected Graphics3D()
    {
        
    }

    public int addLight(Light light, Transform transform)
    {
        throw new UnsupportedOperationException();
    }

    public void bindTarget(Object target)
    {
        bindTarget(target, DEPTH);
    }

    public void bindTarget(Object target, boolean depthBuffer, int flags)
    {
        if (depthBuffer)
        {
            flags |= DEPTH;
        }
        bindTarget(target, flags);
    }

    public void bindTarget(Object target, int flags)
    {
        if (target instanceof RenderTarget)
        {
            //good to go
            throw new UnsupportedOperationException();
        }
        else
        {
            //wrap it in a RenderTarget
            if (target instanceof Image2D)
            {
                //is it a mutable image?
                throw new UnsupportedOperationException();
            }
        }

        throw new UnsupportedOperationException();
    }
}
