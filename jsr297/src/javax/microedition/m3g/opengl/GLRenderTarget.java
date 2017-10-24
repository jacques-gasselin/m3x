/*
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

import com.jogamp.opengl.DebugGL2;
import javax.microedition.m3g.RendererOpenGL2;
import javax.microedition.m3g.Renderer;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import javax.microedition.m3g.AbstractRenderTarget;

/**
 * @author jgasseli
 */
public class GLRenderTarget extends AbstractRenderTarget
{
    private GLAutoDrawable drawable;
    private RendererOpenGL2 renderer;
    private final boolean debug;
    private DebugGL2 debugGL;
    private GL lastGL;

    public GLRenderTarget(GLAutoDrawable drawable)
    {
        this(drawable, false);
    }

    public GLRenderTarget(GLAutoDrawable drawable, boolean debug)
    {
        this.drawable = drawable;
        this.debug = debug;

        renderer = new RendererOpenGL2();
    }
    
    @Override
    public int getWidth()
    {
        if (drawable == null)
        {
            throw new IllegalStateException("drawable is null");
        }
        return drawable.getSurfaceWidth();
    }

    @Override
    public int getHeight()
    {
        if (drawable == null)
        {
            throw new IllegalStateException("drawable is null");
        }
        return drawable.getSurfaceHeight();
    }

    @Override
    public float getContentScale()
    {
        if (drawable == null)
        {
            throw new IllegalStateException("drawable is null");
        }
        final int[] dims = new int[2];
        dims[0] = 1;
        dims[1] = 1;
        if (drawable.getNativeSurface() != null) {
            final int[] windowDims = drawable.getNativeSurface().convertToPixelUnits(dims);
            return windowDims[0];
        }
        else {
            return 1.0f;
        }
    }
    
    @Override
    public boolean isDepthBuffered()
    {
        GLCapabilitiesImmutable caps = drawable.getChosenGLCapabilities();
        return caps.getDepthBits() > 0;
    }

    @Override
    public boolean isStencilBuffered()
    {
        GLCapabilitiesImmutable caps = drawable.getChosenGLCapabilities();
        return caps.getStencilBits() > 0;
    }

    private static void requireValidContext(GLContext context)
    {
        if (context == null)
        {
            throw new IllegalStateException("no GL context");
        }
    }

    @Override
    public Renderer bindRenderer()
    {
        final GLContext context = drawable.getContext();
        requireValidContext(context);
        
        //FIXME: Only do this if it isn't already.
        //context.makeCurrent();
        GL2 gl = (GL2) context.getGL();
        if (lastGL != gl)
        {
            lastGL = gl;
            if (debug)
            {
                debugGL = new DebugGL2(gl);
            }
        }
        
        if (debug) 
        {
            renderer.bindContext(debugGL, getWidth(), getHeight());
        }
        else
        {
            renderer.bindContext(gl, getWidth(), getHeight());
        }
        return renderer;
    }

    @Override
    public void releaseRenderer()
    {
        //disable any more rendering
        renderer.releaseContext();
        
        final GLContext context = drawable.getContext();
        requireValidContext(context);
        
        //context.release();
    }    
}
