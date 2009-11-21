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

import java.util.Hashtable;

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

    private Object target;
    private AbstractRenderTarget renderTarget;
    private Renderer renderer;
    private Camera camera;
    private final Transform cameraTransform = new Transform();
    private final Transform inverseCameraTransform = new Transform();


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
        if (target instanceof AbstractRenderTarget)
        {
            //good to go
            renderTarget = (AbstractRenderTarget) target;
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

        this.target = target;
        renderer = renderTarget.bindTarget();
        renderer.setViewport(0, 0, renderTarget.getWidth(), renderTarget.getHeight());
    }

    /**
     * Clears the currently bound target with the values conatined in the
     * Background object.
     * 
     * @param background the parameters to use for clearing.
     */
    public void clear(Background background)
    {
        renderer.clear(background);
    }

    /**
     * Gets the current camera used for immediate mode rendering.
     * 
     * @param transform if not null will contain the model to world transform
     * of the camera.
     * @return the current camera.
     * @see #setCamera(javax.microedition.m3g.Camera, javax.microedition.m3g.Transform)
     */
    public Camera getCamera(Transform transform)
    {
        if (transform != null)
        {
            transform.set(cameraTransform);
        }
        return this.camera;
    }

    public float getDepthRangeFar()
    {
        throw new UnsupportedOperationException();
    }

    public float getDepthRangeNear()
    {
        throw new UnsupportedOperationException();
    }

    public int getHints()
    {
        throw new UnsupportedOperationException();
    }

    public static Graphics3D getInstance()
    {
        synchronized(Graphics3D.class)
        {
            if (instance == null)
            {
                instance = new Graphics3D();
            }
        }
        return instance;
    }

    public Light getLight(int index, Transform transform)
    {
        throw new UnsupportedOperationException();
    }

    public int getLightCount()
    {
        throw new UnsupportedOperationException();
    }

    public static Hashtable getProperties()
    {
        throw new UnsupportedOperationException();
    }

    public static Hashtable getProperties(Object target)
    {
        throw new UnsupportedOperationException();
    }

    public int getRenderingFlags()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the currently bound target.
     *
     * @return the current target, or null if none is bound.
     * @see #bindTarget(java.lang.Object, int)
     * @see #releaseTarget()
     */
    public Object getTarget()
    {
        return target;
    }

    public int getViewportHeight()
    {
        throw new UnsupportedOperationException();
    }

    public int getViewportWidth()
    {
        throw new UnsupportedOperationException();
    }

    public int getViewportX()
    {
        throw new UnsupportedOperationException();
    }

    public int getViewportY()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isDepthBufferEnabled()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Releases the current target. This will make getTarget() return null
     * until another target is bound.
     * @see #bindTarget(java.lang.Object, int)
     * @see #getTarget()
     */
    public void releaseTarget()
    {
        if (renderTarget != null)
        {
            renderTarget.releaseTarget();
        }
        renderer = null;
        target = null;
    }

    public void render(Node node, Transform transform)
    {
        throw new UnsupportedOperationException();
    }

    public void render(RenderPass renderPass)
    {
        throw new UnsupportedOperationException();
    }

    public void render(VertexBuffer vertices, IndexBuffer primitives,
            Appearance appearance, Transform transform)
    {
        throw new UnsupportedOperationException();
    }

    public void render(VertexBuffer vertices, IndexBuffer primitives,
            Appearance appearance, Transform transform, int scope)
    {
        throw new UnsupportedOperationException();
    }

    public void render(VertexBuffer vertices, IndexBuffer primitives,
            ShaderAppearance appearance, Transform transform)
    {
        throw new UnsupportedOperationException();
    }

    public void render(World world)
    {
        throw new UnsupportedOperationException();
    }

    public void resetLights()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the current camera used for immediate mode rendering.
     *
     * @param camera the camera to set, or null to unset.
     * @param transform the model to world transfor of the camera, or null for
     * identity.
     * @throws ArithmeticException if transform is not null and not invertible.
     * @see #getCamera(javax.microedition.m3g.Transform)
     */
    public void setCamera(Camera camera, Transform transform)
    {
        if (transform != null)
        {
            this.cameraTransform.set(transform);
            this.inverseCameraTransform.set(transform);
            this.inverseCameraTransform.invert();
        }
        else
        {
            this.cameraTransform.setIdentity();
            this.inverseCameraTransform.setIdentity();
        }
        this.camera = camera;
    }

    public void setDepthRange(float near, float far)
    {
        throw new UnsupportedOperationException();
    }

    public void setLight(int index, Light light, Transform transform)
    {
        throw new UnsupportedOperationException();
    }

    public void setViewport(int x, int y, int width, int height)
    {
        throw new UnsupportedOperationException();
    }
}
