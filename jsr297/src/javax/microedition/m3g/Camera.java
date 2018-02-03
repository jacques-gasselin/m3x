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

import m3x.Require;

/**
 * @author jgasseli
 */
public class Camera extends Node
{
    public static final int GENERIC = 48;
    public static final int PARALLEL = 49;
    public static final int PERSPECTIVE = 50;
    public static final int SCREEN = 51;
    
    private int projectionType = GENERIC;
    private final float[] projectionParams = new float[4];
    //default is GENERIC, which is already up to date
    private boolean projectionNeedsUpdate = false;
    private final Transform projection = new Transform();
    
    public Camera()
    {
        
    }

    private void setProjectionType(int type)
    {
        Require.argumentInEnum(type, "type", GENERIC, SCREEN);

        this.projectionType = type;
    }

    public int getProjection(float[] params)
    {
        if (params != null)
        {
            Require.argumentHasCapacity(params, "params", 4);

            if (this.projectionType != GENERIC)
            {
                System.arraycopy(this.projectionParams, 0,
                        params, 0, 4);
            }
        }
        
        return this.projectionType;
    }

    private final void updateProjection()
    {
        //assume all zero
        final float[] matrix = new float[16];
        final float[] params = this.projectionParams;

        switch (projectionType)
        {
            case PARALLEL:
            {
                final float h = params[0];
                final float w = params[1] * h;
                final float near = params[2];
                final float far = params[3];
                final float d = far - near;

                //row-major
                matrix[0] = 2.0f / w;
                matrix[5] = 2.0f / h;
                matrix[10] = -2 / d;
                matrix[11] = -(near + far) / d;
                matrix[15] = 1.0f;

                break;
            }
            case PERSPECTIVE:
            {
                final float h = (float) Math.tan(Math.toRadians(params[0]) * 0.5f);
                final float w = params[1] * h;
                final float near = params[2];
                final float far = params[3];
                final float d = far - near;

                //row-major
                matrix[0] = 1.0f / w;
                matrix[5] = 1.0f / h;
                matrix[10] = -(near + far) / d;
                matrix[11] = -2.0f * near * far / d;
                matrix[14] = -1.0f;

                break;
            }
            case SCREEN:
            {
                final float w = params[2];
                final float h = params[3];
                final float tx = -(2 * params[0] + w) / w;
                final float ty = -(2 * params[1] + h) / h;

                //row-major
                matrix[0] = 2.0f / w;
                matrix[3] = tx;
                matrix[5] = 2.0f / h;
                matrix[7] = ty;
                matrix[10] = -1;
                matrix[15] = 1.0f;

                break;
            }
            default:
            {
                throw new UnsupportedOperationException();
            }
        }

        projection.set(matrix);
        projectionNeedsUpdate = false;
    }
    
    public int getProjection(Transform transform)
    {
        if (transform != null)
        {
            if (projectionNeedsUpdate)
            {
                updateProjection();
            }
            
            transform.set(this.projection);
        }

        return this.projectionType;
    }

    public void setGeneric(Transform transform)
    {
        Require.notNull(transform, "transform");
        
        projection.set(transform);
        projectionNeedsUpdate = false;
        setProjectionType(GENERIC);
    }

    public void setParallel(float height, float aspectRatio, float near, float far)
    {
        Require.argumentGreaterThanZero(height, "height");
        Require.argumentGreaterThanZero(aspectRatio, "aspectRatio");

        final float[] params = this.projectionParams;
        params[0] = height;
        params[1] = aspectRatio;
        params[2] = near;
        params[3] = far;
        
        this.projectionNeedsUpdate = true;
        setProjectionType(PARALLEL);
    }

    public void setPerspective(float fovy, float aspectRatio, float near, float far)
    {
        Require.argumentGreaterThanZero(fovy, "fovy");
        Require.argumentGreaterThanZero(aspectRatio, "aspectRatio");
        Require.argumentGreaterThanZero(near, "near");
        Require.argumentGreaterThanZero(far, "far");

        if (fovy >= 180)
        {
            throw new IllegalArgumentException("fovy >= 180");
        }

        final float[] params = this.projectionParams;
        params[0] = fovy;
        params[1] = aspectRatio;
        params[2] = near;
        params[3] = far;

        this.projectionNeedsUpdate = true;
        setProjectionType(PERSPECTIVE);
    }

    public void setScreen(float x, float y, float width, float height)
    {
        Require.argumentGreaterThanZero(width, "width");
        Require.argumentGreaterThanZero(height, "height");

        final float[] params = this.projectionParams;
        params[0] = x;
        params[1] = y;
        params[2] = width;
        params[3] = height;

        this.projectionNeedsUpdate = true;
        setProjectionType(SCREEN);
    }
}
