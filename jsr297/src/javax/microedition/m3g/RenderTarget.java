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

/**
 * This is a modified version of RenderTarget. Used here to illustrate a more
 * type safe interface for Graphics3D.bind
 * @author jgasseli
 */
public class RenderTarget extends Object3D implements AbstractRenderTarget
{
    private final ImageBase target;
    private final int face;
    private final int miplevel;

    private RenderTarget(ImageBase target, int face, int miplevel)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("target is null");
        }
        
        this.target = target;
        this.face = face;
        this.miplevel = miplevel;
    }
    
    public RenderTarget(Image2D target, int miplevel)
    {
        this(target, 0, miplevel);
    }

    public RenderTarget(ImageCube target, int face, int miplevel)
    {
        this((ImageBase)target, face, miplevel);
    }
    
    public int getTargetFace()
    {
        return this.face;
    }
    
    public int getTargetLevel()
    {
        return this.miplevel;
    }
    
    @Override
    public int getWidth()
    {
        return target.getWidth();
    }

    @Override
    public int getHeight()
    {
        return target.getHeight();
    }

    @Override
    public float getContentScale()
    {
        return 1.0f;
    }

    @Override
    public boolean isDepthBuffered()
    {
        return false;
    }

    @Override
    public boolean isStencilBuffered()
    {
        return false;
    }

    private static final class NullRenderer extends Renderer
    {

        @Override
        public void clear(Background background)
        {
        }

        @Override
        public void setViewport(int x, int y, int width, int height)
        {
        }

        @Override
        public void setProjectionView(Transform projection, Transform view)
        {
        }

        @Override
        public void resetLights()
        {
        }

        @Override
        public void setLight(int index, Light light, Transform transform)
        {
        }

        @Override
        public void render(VertexBuffer vertices, IndexBuffer primitives, Appearance appearance, Transform transform, int scope, float alphaFactor)
        {
        }
        
    }
    
    @Override
    public Renderer bindRenderer()
    {
        //TODO: Fallback to GL binding here
        return new NullRenderer();
    }

    @Override
    public void releaseRenderer()
    {
        //TODO: Fallback to GL binding here
    }
}
