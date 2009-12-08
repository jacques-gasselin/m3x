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

import java.nio.ShortBuffer;
import javax.media.opengl.GL;

/**
 * @author jgasseli
 */
public class RendererOpenGL2 extends Renderer
{
    private GL lastInstanceGL;
    private GL instanceGL;
    private int width;
    private int height;
    
    private int maxTextureUnits;
    private float positionScale;
    private final float[] positionBias = new float[3];
    private final Transform viewTransform = new Transform();
    private final Transform modelTransform = new Transform();
    private final Transform modelViewTransform = new Transform();
    
    public RendererOpenGL2()
    {
    }

    public GL getGL()
    {
        return instanceGL;
    }

    private static final int[] GLGETINTEGER1 = new int[1];
    
    private static final int glGet(GL gl, int glenum)
    {
        gl.glGetIntegerv(glenum, GLGETINTEGER1, 0);
        return GLGETINTEGER1[0];
    }

    public void initialize(GL gl)
    {
        maxTextureUnits = glGet(gl, GL.GL_MAX_TEXTURE_UNITS);

        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
    }

    public void bind(GL gl, int width, int height)
    {
        this.instanceGL = gl;
        this.width = width;
        this.height = height;

        if (gl != this.lastInstanceGL)
        {
            initialize(gl);
            this.lastInstanceGL = gl;
        }
    }

    public void release()
    {
        this.instanceGL = null;

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
        int clearColorARGB = 0xff000000; //default to full alpha black
        int colorMask = 0xffffffff; //clear all color channels
        int clearStencil = 0;
        int stencilMask = 0xffffffff; //clear all stencil bits
        float clearDepth = 1.0f;
        int clearFlags = 0;

        if (background != null)
        {
            clearColorARGB = background.getColor();
            colorMask = background.getColorClearMask();

            clearStencil = background.getStencil();
            stencilMask = background.getStencilClearMask();
            
            clearDepth = background.getDepth();
            clearFlags |= (background.isDepthClearEnabled()
                    ? GL.GL_DEPTH_BUFFER_BIT : 0);
        }
        else
        {
            //clear all buffers if no background is given
            clearFlags |= GL.GL_DEPTH_BUFFER_BIT;
        }

        clearFlags |= (colorMask != 0 ? GL.GL_COLOR_BUFFER_BIT : 0);
        clearFlags |= (stencilMask != 0 ? GL.GL_STENCIL_BUFFER_BIT : 0);

        final boolean redMask = ((colorMask >> 16) & 0xff) != 0;
        final boolean greenMask = ((colorMask >> 8) & 0xff) != 0;
        final boolean blueMask = ((colorMask >> 0) & 0xff) != 0;
        final boolean alphaMask = ((colorMask >> 24) & 0xff) != 0;

        final float ubyteToFloat = 1.0f / 255;
        final float redColor = ubyteToFloat * ((clearColorARGB >> 16) & 0xff);
        final float greenColor = ubyteToFloat * ((clearColorARGB >> 8) & 0xff);
        final float blueColor = ubyteToFloat * ((clearColorARGB >> 0) & 0xff);
        final float alphaColor = ubyteToFloat * ((clearColorARGB >> 24) & 0xff);

        final GL gl = getGL();
        gl.glColorMask(redMask, greenMask, blueMask, alphaMask);
        gl.glClearColor(redColor, greenColor, blueColor, alphaColor);
        gl.glStencilMask(stencilMask);
        gl.glClearStencil(clearStencil);
        gl.glClearDepth(clearDepth);
        gl.glClear(clearFlags);
    }

    @Override
    public void setViewport(int x, int y, int width, int height)
    {
        final GL gl = getGL();
        //OpenGL uses lower-left as origin
        gl.glViewport(x, this.height - (y + height), width, height);
    }

    @Override
    public void setProjectionView(Transform projection, Transform view)
    {
        Require.notNull(projection, "projection");
        Require.notNull(view, "view");

        final GL gl = getGL();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadMatrixf(projection.getColumnMajor(), 0);

        this.viewTransform.set(view);
    }

    @Override
    public void render(VertexBuffer vertices, IndexBuffer primitives, Appearance appearance, Transform transform, int scope)
    {
        Require.notNull(vertices, "vertices");
        Require.notNull(primitives, "primitives");
        Require.notNull(appearance, "appearance");

        final GL gl = getGL();
        setModelTransform(transform);
        setAppearance(gl, appearance);
        setVertexBuffer(gl, vertices);
        render(gl, primitives);
    }

    private void setModelTransform(Transform transform)
    {
        if (transform != null)
        {
            this.modelTransform.set(transform);
        }
        else
        {
            this.modelTransform.setIdentity();
        }
    }

    private static final int fogModeAsGLEnum(int mode)
    {
        switch (mode)
        {
            case Fog.LINEAR:
                return GL.GL_LINEAR;
            case Fog.EXPONENTIAL:
                return GL.GL_EXP;
            case Fog.EXPONENTIAL_SQUARED:
                return GL.GL_EXP2;
            default:
                throw new IllegalArgumentException("mode is not valid");
        }
    }

    private static final float[] TEMP_FLOAT4 = new float[4];

    private static final float[] argbAsRGBAVolatile(int argb)
    {
        final float[] color = TEMP_FLOAT4;
        final float byteToUniform = 1.0f / 255;
        
        color[0] = ((argb >> 16) & 0xff) * byteToUniform;
        color[1] = ((argb >> 8) & 0xff) * byteToUniform;
        color[2] = ((argb >> 0) & 0xff) * byteToUniform;
        color[3] = ((argb >> 24) & 0xff) * byteToUniform;

        return color;
    }

    private final void setCompositingMode(GL gl, CompositingMode compositingMode)
    {
        {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDisable(GL.GL_ALPHA_TEST);
            gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
            gl.glDisable(GL.GL_BLEND);
            gl.glDepthMask(true);
            gl.glColorMask(true, true, true, true);
            gl.glDepthFunc(GL.GL_LEQUAL);
        }
    }

    private final void setPolygonMode(GL gl, PolygonMode polygonMode)
    {
        //TODO
    }

    private final void setFog(GL gl, Fog fog)
    {
        if (fog != null)
        {
            if (true)
            {
                gl.glFogf(GL.GL_FOG_MODE,
                        fogModeAsGLEnum(fog.getMode()));
                gl.glFogfv(GL.GL_FOG_COLOR,
                        argbAsRGBAVolatile(fog.getColor()), 0);
                gl.glFogf(GL.GL_FOG_START, fog.getNearDistance());
                gl.glFogf(GL.GL_FOG_END, fog.getFarDistance());
                gl.glFogf(GL.GL_FOG_DENSITY, fog.getDensity());
            }
            
            gl.glEnable(GL.GL_FOG);
        }
        else
        {
            gl.glDisable(GL.GL_FOG);
        }
    }

    private final void setTexture(GL gl, int index, Texture2D texture)
    {
        gl.glActiveTexture(GL.GL_TEXTURE0 + index);
        if (texture != null)
        {
            /*if (true)
            {
                //TODO
            }*/
            gl.glEnable(GL.GL_TEXTURE_2D);
        }
        else
        {
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
    }

    private void setAppearance(GL gl, Appearance appearance)
    {
        setCompositingMode(gl, appearance.getCompositingMode());
        setPolygonMode(gl, appearance.getPolygonMode());
        setFog(gl, appearance.getFog());

        for (int i = 0; i < maxTextureUnits; ++i)
        {
            setTexture(gl, i, appearance.getTexture(i));
        }
    }

    private void setVertexBuffer(GL gl, VertexBuffer vertices)
    {
        gl.glColor4fv(argbAsRGBAVolatile(vertices.getDefaultColor()), 0);

        //positions
        if (true)
        {
            float[] scaleBias = TEMP_FLOAT4;
            VertexArray positions = vertices.getPositions(scaleBias);
            positionScale = scaleBias[0];
            System.arraycopy(scaleBias, 1, positionBias, 0, 3);

            int glType = GL.GL_FLOAT;
            switch (positions.getComponentType())
            {
                case VertexArray.FLOAT:
                {
                    glType = GL.GL_FLOAT;
                    break;
                }
                case VertexArray.FIXED:
                {
                    //FIXED is not supported in GL
                    //rescale and treat like INT
                    positionScale *= 1.0f / 65556;
                    glType = GL.GL_INT;
                    break;
                }
                case VertexArray.SHORT:
                {
                    glType = GL.GL_SHORT;
                    break;
                }
                default:
                {
                    throw new IllegalStateException("unsupported component type");
                }
            }

            gl.glVertexPointer(positions.getComponentCount(), glType,
                    positions.getVertexByteStride(), positions.getBuffer());
        }

        //TODO normals

        //TODO colors
        {
            VertexArray colors = vertices.getColors();
            if (colors != null)
            {
                int glType = GL.GL_FLOAT;
                switch (colors.getComponentType())
                {
                    case VertexArray.FLOAT:
                    {
                        glType = GL.GL_FLOAT;
                        break;
                    }
                    case VertexArray.BYTE:
                    {
                        glType = GL.GL_UNSIGNED_BYTE;
                        break;
                    }
                    default:
                    {
                        throw new IllegalStateException("unsupported component type");
                    }
                }

                gl.glColorPointer(colors.getComponentCount(), glType,
                        colors.getVertexByteStride(), colors.getBuffer());
                gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            }
            else
            {
                gl.glDisableClientState(GL.GL_COLOR_ARRAY);
            }
        }

        //TODO texcoords
    }

    private static final int primitiveTypeAsGLEnum(int type, boolean isStripped)
    {
        switch (type)
        {
            case IndexBuffer.TRIANGLES:
            {
                if (isStripped)
                {
                    return GL.GL_TRIANGLE_STRIP;
                }
                else
                {
                    return GL.GL_TRIANGLES;
                }
            }
            case IndexBuffer.LINES:
            {
                if (isStripped)
                {
                    return GL.GL_LINE_STRIP;
                }
                else
                {
                    return GL.GL_LINES;
                }
            }
            case IndexBuffer.POINT_SPRITES:
            {
                return GL.GL_POINT;
            }
            default:
            {
                throw new IllegalArgumentException("unknown primitive type");
            }
        }
    }

    private void commitModelView(GL gl)
    {
        final Transform transform = modelViewTransform;
        transform.set(viewTransform);
        transform.postMultiply(modelTransform);
        final float[] bias = positionBias;
        transform.postTranslate(bias[0], bias[1], bias[2]);
        final float scale = positionScale;
        transform.postScale(scale, scale, scale);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadMatrixf(transform.getColumnMajor(), 0);
    }
    
    private void render(GL gl, IndexBuffer primitives)
    {
        //commit the modelview matrix
        commitModelView(gl);

        final boolean isStripped = primitives.isStripped();
        final int glType = primitiveTypeAsGLEnum(primitives.getPrimitiveType(), isStripped);

        if (isStripped)
        {
            final int[] stripLengths = primitives.getStripLengths();
            if (primitives.isImplicit())
            {
                int firstIndex = primitives.getFirstIndex();
                for (int stripLength : stripLengths)
                {
                    gl.glDrawArrays(glType, firstIndex, stripLength);
                    firstIndex += stripLength;
                }
            }
            else
            {
                final ShortBuffer indices = primitives.getIndexBuffer();
                for (int stripLength : stripLengths)
                {
                    gl.glDrawElements(glType, stripLength, GL.GL_UNSIGNED_SHORT,
                            indices);
                    indices.position(indices.position() + stripLength);
                }
                indices.rewind();
            }
        }
        else
        {
            final int indexCount = primitives.getIndexCount();
            if (primitives.isImplicit())
            {
                gl.glDrawArrays(glType, primitives.getFirstIndex(), indexCount);
            }
            else
            {
                gl.glDrawElements(glType, indexCount, GL.GL_UNSIGNED_SHORT,
                        primitives.getIndexBuffer());
            }
        }
    }

    
}
