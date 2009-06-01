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

import javax.media.opengl.GL;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Renderer;

/**
 * @author jgasseli
 */
public class GLRenderer extends Renderer
{
    private GL instanceGL;
    
    public GLRenderer()
    {

    }

    public GL getGL()
    {
        return instanceGL;
    }

    public void setGL(GL gl)
    {
        this.instanceGL = gl;
        clearCachedObjects();
    }

    /**
     * Clears any cached state objects
     */
    private void clearCachedObjects()
    {
        //TODO clear cached objects.
        //Used to lessen state changes in the renderer.
    }

    @Override
    public void clear(Background background)
    {
        final float byteToUniform = 1.0f / 255;
        
        int clearColorARGB = 0xff000000; //default to full alpha black
        int clearStencil = 0;
        float clearDepth = 1.0f;
        int clearFlags = 0;

        if (background != null)
        {
            clearColorARGB = background.getColor();
            clearStencil = background.getStencil();
            clearDepth = background.getDepth();
            clearFlags |= (background.isDepthClearEnabled()
                    ? GL.GL_DEPTH_BUFFER_BIT : 0);
            clearFlags |= (background.getColorClearMask() != 0
                    ? GL.GL_COLOR_BUFFER_BIT : 0);
            clearFlags |= (background.getStencilClearMask() != 0
                    ? GL.GL_STENCIL_BUFFER_BIT : 0);
        }
        else
        {
            //clear all buffers if no background is given
            clearFlags |= GL.GL_DEPTH_BUFFER_BIT;
            clearFlags |= GL.GL_COLOR_BUFFER_BIT;
            clearFlags |= GL.GL_STENCIL_BUFFER_BIT;
        }


        final float red = byteToUniform * ((clearColorARGB >> 16) & 0xff);
        final float green = byteToUniform * ((clearColorARGB >> 8) & 0xff);
        final float blue = byteToUniform * ((clearColorARGB >> 0) & 0xff);
        final float alpha = byteToUniform * ((clearColorARGB >> 24) & 0xff);

        final GL gl = getGL();

        gl.glClearColor(red, green, blue, alpha);
        gl.glClearStencil(clearStencil);
        gl.glClearDepth(clearDepth);
        gl.glClear(clearFlags);
    }

}
