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

package javax.microedition.m3g;

import java.util.ArrayList;
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

    private static final Graphics3D INSTANCE = new Graphics3D();
    private static final int LIGHTS_LIST_CAPACITY = 32;

    private Object target;
    private AbstractRenderTarget renderTarget;
    private Renderer renderer;
    private Camera camera;
    private final Transform cameraProjection = new Transform();
    private final Transform cameraTransform = new Transform();
    private final Transform inverseCameraTransform = new Transform();
    private final ArrayList<Light> lights = new ArrayList<Light>(LIGHTS_LIST_CAPACITY);
    private final ArrayList<Transform> lightTransforms = new ArrayList<Transform>(LIGHTS_LIST_CAPACITY);

    protected Graphics3D()
    {
        
    }

    private final void requireCurrentRenderTarget()
    {
        if (renderTarget == null)
        {
            throw new IllegalStateException(
                    "this Graphics3D does not have a rendering target");
        }
    }

    private final void requireCurrentCamera()
    {
        if (camera == null)
        {
            throw new IllegalStateException(
                    "this Graphics3D does not have a current camera");
        }
    }

    /**
     * Adds a light to the end of the lights array. The index of the added light
     * is returned for later use with setLight and getLight.
     * 
     * @param light the light to add
     * @param transform the model to world transform for the light, or null for
     * identity.
     * @return the index of the light into the lights array
     * @throws NullPointerException if light is null
     * @see #setLight(int, javax.microedition.m3g.Light, javax.microedition.m3g.Transform)
     * @see #getLight(int, javax.microedition.m3g.Transform)
     */
    public int addLight(Light light, Transform transform)
    {
        Require.notNull(light, "light");

        final Transform lightTransform;
        if (transform != null)
        {
            lightTransform = new Transform(transform);
        }
        else
        {
            lightTransform = new Transform();
        }

        lights.add(light);
        lightTransforms.add(transform);

        return lights.size();
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
        Require.notNull(target, "target");
        
        if (this.target != null)
        {
            throw new IllegalStateException("Graphics3D already has a bound target");
        }
        
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
        return INSTANCE;
    }

    /**
     * Gets the light at the given index.
     * 
     * @param index
     * @param transform the transform to store the light transform in, or null to
     * ignore the light transform.
     * @return the light at the index
     * @throws IndexOutOfBoundsException if index < 0 or index >= getLightCount()
     * @see #setLight(int, javax.microedition.m3g.Light, javax.microedition.m3g.Transform)
     * @see #getLightCount()
     */
    public Light getLight(int index, Transform transform)
    {
        Require.indexInRange(index, getLightCount());

        if (transform != null)
        {
            transform.set(this.lightTransforms.get(index));
        }

        return this.lights.get(index);
    }

    /**
     * Gets the count of lights in the light array.
     *
     * @return the count of lights
     */
    public int getLightCount()
    {
        return this.lights.size();
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
        Require.notNull(node, "node");
        
        requireCurrentRenderTarget();
        requireCurrentCamera();

        throw new UnsupportedOperationException();
    }

    public void render(RenderPass renderPass)
    {
        throw new UnsupportedOperationException();
    }

    public void render(VertexBuffer vertices, IndexBuffer primitives,
            Appearance appearance, Transform transform)
    {
        Require.notNull(vertices, "vertices");
        Require.notNull(primitives, "primitives");
        Require.notNull(appearance, "appearance");
        
        requireCurrentRenderTarget();
        requireCurrentCamera();
        
        renderer.render(vertices, primitives, appearance, transform, -1);
    }

    public void render(VertexBuffer vertices, IndexBuffer primitives,
            Appearance appearance, Transform transform, int scope)
    {
        Require.notNull(vertices, "vertices");
        Require.notNull(primitives, "primitives");
        Require.notNull(appearance, "appearance");

        requireCurrentRenderTarget();
        requireCurrentCamera();

        renderer.render(vertices, primitives, appearance, transform, scope);
    }

    public void render(VertexBuffer vertices, IndexBuffer primitives,
            ShaderAppearance appearance, Transform transform)
    {
        Require.notNull(vertices, "vertices");
        Require.notNull(primitives, "primitives");
        Require.notNull(appearance, "appearance");

        requireCurrentRenderTarget();
        requireCurrentCamera();

        throw new UnsupportedOperationException();
    }

    public void render(World world)
    {
        if (world == null)
        {
            throw new NullPointerException("world is null");
        }
        requireCurrentRenderTarget();

        final Camera activeCamera = world.getActiveCamera();
        if (activeCamera == null)
        {
            throw new IllegalStateException("world has no active camera");
        }


        Transform cameraToWorld = new Transform();
        camera.getTransformTo(world, cameraToWorld);
        setCamera(activeCamera, cameraToWorld);

        Background background = world.getBackground();
        clear(background);
        
        //TODO do lights


        //render as a node
        render(world, null);
        
        throw new UnsupportedOperationException();
    }

    /**
     * Resets the lights array.
     */
    public void resetLights()
    {
        this.lights.clear();
        //TODO: cache the unused transform objects here to avoid
        //allocations for addLight
        this.lightTransforms.clear();
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
        
        if (camera != null)
        {
            camera.getProjection(this.cameraProjection);
        }
        else
        {
            this.cameraProjection.setIdentity();
        }

        this.camera = camera;

        if (this.renderer != null)
        {
            this.renderer.setProjectionView(this.cameraProjection, this.inverseCameraTransform);
        }
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
