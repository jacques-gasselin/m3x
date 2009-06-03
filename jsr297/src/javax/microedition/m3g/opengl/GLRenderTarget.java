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

package javax.microedition.m3g.opengl;

import javax.microedition.m3g.Renderer;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.microedition.m3g.RenderTarget;

/**
 * @author jgasseli
 */
public class GLRenderTarget extends RenderTarget
{
    private GLAutoDrawable drawable;
    private GLRenderer renderer;
    
    public GLRenderTarget(GLAutoDrawable drawable)
    {
        this.drawable = drawable;
        this.renderer = new GLRenderer();
    }
    
    public int getWidth()
    {
        return drawable.getWidth();
    }

    public int getHeight()
    {
        return drawable.getHeight();
    }

    public boolean isDepthBuffered()
    {
        GLCapabilities caps = drawable.getChosenGLCapabilities();
        return caps.getDoubleBuffered();
    }

    public boolean isStencilBuffered()
    {
        GLCapabilities caps = drawable.getChosenGLCapabilities();
        return caps.getStencilBits() > 0;
    }

    public Renderer bindTarget()
    {
        renderer.setGL(drawable.getGL());
        return renderer;
    }

    public void releaseTarget()
    {
        //disable any more rendering
        renderer.setGL(null);
    }    
}
