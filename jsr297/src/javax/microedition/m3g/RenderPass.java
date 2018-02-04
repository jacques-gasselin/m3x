/*
 * Copyright (c) 2010, Jacques Gasselin de Richebourg
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
public class RenderPass
{
    private Camera camera;
    private Node scene;
    private Background background;
    private Object3D target;
    private int targetFlags;
    private float depthRangeFar;
    private float depthRangeNear;
    
    public RenderPass()
    {
        camera = null;
        scene = null;
        background = null;
        target = null;
        targetFlags = 0;
        depthRangeFar = 1.0f;
        depthRangeNear = 0.0f;
    }
    
    public Background getBackground()
    {
        return this.background;
    }
    
    public Camera getCamera()
    {
        return this.camera;
    }
    
    public float getDepthRangeFar()
    {
        return this.depthRangeFar;
    }
    
    public float getDepthRangeNear()
    {
        return this.depthRangeNear;
    }
    
    public Node getScene()
    {
        return this.scene;
    }
    
    public Object3D getTarget()
    {
        return this.target;
    }
    
    public int getTargetFlags()
    {
        return this.targetFlags;
    }
    
    public void setBackground(Background background)
    {
        this.background = background;
    }
    
    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }
    
    public void setDepthRange(float near, float far)
    {
        this.depthRangeNear = near;
        this.depthRangeFar = far;
    }
    
    public void setScene(Node scene)
    {
        this.scene = scene;
    }
    
    public void setTarget(ImageCube target, int flags)
    {
        this.target = target;
        this.targetFlags = flags;
    }

    public void setTarget(RenderTarget target, int flags)
    {
        this.target = target;
        this.targetFlags = flags;
    }
}
