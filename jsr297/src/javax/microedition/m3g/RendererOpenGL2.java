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
import java.util.ArrayList;
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
    private int maxLights;
    private float positionScale;
    private final float[] positionBias = new float[3];
    private final Transform viewTransform = new Transform();
    private final Transform modelTransform = new Transform();
    private final Transform modelViewTransform = new Transform();
    private Light[] lights;
    
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
        maxLights = glGet(gl, GL.GL_MAX_LIGHTS);
        lights = new Light[maxLights];
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

        gl.glDisable(GL.GL_DITHER);
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glEnable(GL.GL_RESCALE_NORMAL);
        gl.glDisable(GL.GL_NORMALIZE);
        gl.glDisable(GL.GL_LINE_STIPPLE);
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

    private static final float[] LIGHT_POSITIONAL = new float[]{ 0, 0, 0, 1 };
    private static final float[] LIGHT_DIRECTIONAL = new float[]{ 0, 0, 1, 0 };
    private static final float[] COLOR_BLACK_RGB = new float[]{ 0, 0, 0 };
    private static final Transform IDENTITY = new Transform();


    @Override
    public void resetLights()
    {
        final Light[] array = lights;
        final int count = array.length;
        for (int i = 0; i < count; ++i)
        {
            array[i] = null;
        }
    }
    
    @Override
    public void setLight(int index, Light light, Transform transform)
    {
        Require.indexInRange(index, maxLights);

        if (light != null)
        {
            final int mode = light.getMode();

            final GL gl = getGL();

            final int glLight = GL.GL_LIGHT0 + index;
            if (transform != null)
            {
                commitModelView(gl, transform);
            }
            else
            {
                commitModelView(gl, IDENTITY);
            }
            
            final float[] pos = (mode == Light.DIRECTIONAL) ?
                LIGHT_DIRECTIONAL : LIGHT_POSITIONAL;
            gl.glLightfv(glLight, GL.GL_POSITION, pos, 0);

            final float[] color = argbAsRGBVolatile(light.getColor(),
                    light.getIntensity());
            final float[] black = COLOR_BLACK_RGB;

            final float[] ambientColor = (mode == Light.AMBIENT) ?
                color : black;
            final float[] diffuseColor = (mode == Light.AMBIENT) ?
                black : color;
            final float[] specularColor = (mode == Light.AMBIENT) ?
                black : color;

            gl.glLightfv(glLight, GL.GL_AMBIENT, ambientColor, 0);
            gl.glLightfv(glLight, GL.GL_DIFFUSE, diffuseColor, 0);
            gl.glLightfv(glLight, GL.GL_SPECULAR, specularColor, 0);

            final float spotExponent = (mode == Light.SPOT) ?
                light.getSpotExponent() : 0;
            final float spotCutoff = (mode == Light.SPOT) ?
                light.getSpotAngle() : 180;

            gl.glLightf(glLight, GL.GL_SPOT_EXPONENT, spotExponent);
            gl.glLightf(glLight, GL.GL_SPOT_CUTOFF, spotCutoff);

            gl.glLightf(glLight, GL.GL_CONSTANT_ATTENUATION,
                    light.getConstantAttenuation());
            gl.glLightf(glLight, GL.GL_LINEAR_ATTENUATION,
                    light.getLinearAttenuation());
            gl.glLightf(glLight, GL.GL_QUADRATIC_ATTENUATION,
                    light.getQuadraticAttenuation());
        }

        lights[index] = light;
    }

    private final void selectLights(GL gl, int scope)
    {
        int numEnabled = 0;
        
        final Light[] array = lights;
        final int count = array.length;
        for (int i = 0; i < count; ++i)
        {
            final Light light = array[i];
            final boolean enable = (light != null) && light.isRenderingEnabled()
                    && (light.getScope() & scope) != 0;

            if (!enable)
            {
                gl.glDisable(GL.GL_LIGHT0 + i);
            }
            else
            {
                gl.glEnable(GL.GL_LIGHT0 + i);
                ++numEnabled;
            }
        }
        
        if (numEnabled == 0)
        {
            gl.glDisable(GL.GL_LIGHTING);
        }
        else
        {
            gl.glEnable(GL.GL_LIGHTING);
        }
    }

    @Override
    public void render(VertexBuffer vertices, IndexBuffer primitives, Appearance appearance, Transform transform, int scope, float alphaFactor)
    {
        Require.notNull(vertices, "vertices");
        Require.notNull(primitives, "primitives");
        Require.notNull(appearance, "appearance");

        final GL gl = getGL();
        selectLights(gl, scope);
        setModelTransform(transform);
        setAppearance(gl, appearance);
        setVertexBuffer(gl, vertices, alphaFactor);
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
                throw new UnsupportedOperationException("mode is not valid");
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

    private static final float[] argbAsRGBVolatile(int argb, float factor)
    {
        final float[] color = argbAsRGBAVolatile(argb);
        color[0] *= factor;
        color[1] *= factor;
        color[2] *= factor;
        color[3] = 1.0f;
        return color;
    }

    private static final float[] argbAsRGBAVolatile(int argb, float alphaFactor)
    {
        final float[] color = argbAsRGBAVolatile(argb);
        color[3] *= alphaFactor;
        return color;
    }

    private static final boolean isZero(float value)
    {
        //check for the 3 upper bits of the exponent
        //if they are all 0 the exponent is less than 2^(-64)
        return (Float.floatToIntBits(value) & 0x7000000) == 0;
    }

    private static final int testFuncAsGLEnum(int func)
    {
        switch (func)
        {
            case CompositingMode.NEVER:
            {
                return GL.GL_NEVER;
            }
            case CompositingMode.LESS:
            {
                return GL.GL_LESS;
            }
            case CompositingMode.EQUAL:
            {
                return GL.GL_EQUAL;
            }
            case CompositingMode.LEQUAL:
            {
                return GL.GL_LEQUAL;
            }
            case CompositingMode.GREATER:
            {
                return GL.GL_GREATER;
            }
            case CompositingMode.NOTEQUAL:
            {
                return GL.GL_NOTEQUAL;
            }
            case CompositingMode.GEQUAL:
            {
                return GL.GL_GEQUAL;
            }
            case CompositingMode.ALWAYS:
            {
                return GL.GL_ALWAYS;
            }
            default:
            {
                throw new UnsupportedOperationException();
            }
        }
    }

    private final void setCompositingMode(GL gl, CompositingMode compositingMode)
    {
        if (compositingMode != null)
        {
            final float alphaThreshold = compositingMode.getAlphaThreshold();
            if (!isZero(alphaThreshold))
            {
                gl.glAlphaFunc(testFuncAsGLEnum(compositingMode.getAlphaTest()),
                        alphaThreshold);
                gl.glEnable(GL.GL_ALPHA_TEST);
            }
            else
            {
                gl.glDisable(GL.GL_ALPHA_TEST);
            }

            if (compositingMode.isDepthTestEnabled())
            {
                gl.glDepthFunc(testFuncAsGLEnum(compositingMode.getDepthTest()));
                gl.glEnable(GL.GL_DEPTH_TEST);
            }
            else
            {
                gl.glDisable(GL.GL_DEPTH_TEST);
            }

            gl.glDepthMask(compositingMode.isDepthWriteEnabled());

            final float offsetFactor = compositingMode.getDepthOffsetFactor();
            final float offsetUnits = compositingMode.getDepthOffsetUnits();

            if (!isZero(offsetFactor) || !isZero(offsetUnits))
            {
                gl.glPolygonOffset(offsetFactor, offsetUnits);
                gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
            }
            else
            {
                gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
            }

            final Stencil stencil = compositingMode.getStencil();
            if (stencil != null)
            {
                throw new UnsupportedOperationException("stencil not supported yet");
            }
            else
            {
                gl.glDisable(GL.GL_STENCIL_TEST);
            }

            final int colorMask = compositingMode.getColorWriteMask();
            gl.glColorMask(
                    (colorMask & 0x00ff0000) != 0,
                    (colorMask & 0x0000ff00) != 0,
                    (colorMask & 0x000000ff) != 0,
                    (colorMask & 0xff000000) != 0);

            final Blender blender = compositingMode.getBlender();
            if (blender != null)
            {
                throw new UnsupportedOperationException("blender not supported yet");
            }
            else
            {
                final int blending = compositingMode.getBlending();
                if (blending != CompositingMode.REPLACE)
                {
                    int src, dst;
                    switch (blending)
                    {
                        case CompositingMode.ALPHA:
                        {
                            src = GL.GL_SRC_ALPHA;
                            dst = GL.GL_ONE_MINUS_SRC_ALPHA;
                            break;
                        }
                        case CompositingMode.ALPHA_ADD:
                        {
                            src = GL.GL_SRC_ALPHA;
                            dst = GL.GL_ONE;
                            break;
                        }
                        case CompositingMode.MODULATE:
                        {
                            src = GL.GL_DST_COLOR;
                            dst = GL.GL_ZERO;
                            break;
                        }
                        case CompositingMode.MODULATE_X2:
                        {
                            src = GL.GL_DST_COLOR;
                            dst = GL.GL_SRC_COLOR;
                            break;
                        }
                        case CompositingMode.ADD:
                        {
                            src = GL.GL_ONE;
                            dst = GL.GL_ONE;
                            break;
                        }
                        case CompositingMode.ALPHA_DARKEN:
                        {
                            src = GL.GL_ZERO;
                            dst = GL.GL_ONE_MINUS_SRC_ALPHA;
                            break;
                        }
                        case CompositingMode.ALPHA_PREMULTIPLIED:
                        {
                            src = GL.GL_ONE;
                            dst = GL.GL_ONE_MINUS_SRC_ALPHA;
                            break;
                        }
                        case CompositingMode.MODULATE_INV:
                        {
                            src = GL.GL_ONE;
                            dst = GL.GL_ONE_MINUS_SRC_COLOR;
                            break;
                        }
                        default:
                        {
                            throw new UnsupportedOperationException("blend mode not supported yet");
                        }
                    }
                    gl.glBlendEquation(GL.GL_ADD);
                    gl.glBlendFunc(src, dst);
                    gl.glEnable(GL.GL_BLEND);
                }
                else
                {
                    gl.glDisable(GL.GL_BLEND);
                }
            }
        }
        else
        {
            gl.glColorMask(true, true, true, true);
            
            gl.glDisable(GL.GL_ALPHA_TEST);
            gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
            gl.glDisable(GL.GL_BLEND);
            gl.glDisable(GL.GL_STENCIL_TEST);
            
            gl.glDepthMask(true);
            gl.glDepthFunc(GL.GL_LEQUAL);
            gl.glEnable(GL.GL_DEPTH_TEST);
        }
    }

    private final void setPolygonMode(GL gl, PolygonMode polygonMode)
    {
        if (polygonMode != null)
        {
            switch (polygonMode.getCulling())
            {
                case PolygonMode.CULL_BACK:
                {
                    gl.glCullFace(GL.GL_BACK);
                    gl.glEnable(GL.GL_CULL_FACE);
                    break;
                }
                case PolygonMode.CULL_FRONT:
                {
                    gl.glCullFace(GL.GL_FRONT);
                    gl.glEnable(GL.GL_CULL_FACE);
                    break;
                }
                case PolygonMode.CULL_NONE:
                {
                    gl.glDisable(GL.GL_CULL_FACE);
                    break;
                }
            }

            gl.glLineWidth(polygonMode.getLineWidth());

            switch (polygonMode.getShading())
            {
                case PolygonMode.SHADE_FLAT:
                {
                    gl.glShadeModel(GL.GL_FLAT);
                    break;
                }
                case PolygonMode.SHADE_SMOOTH:
                {
                    gl.glShadeModel(GL.GL_SMOOTH);
                    break;
                }
            }

            switch (polygonMode.getWinding())
            {
                case PolygonMode.WINDING_CCW:
                {
                    gl.glFrontFace(GL.GL_CCW);
                    break;
                }
                case PolygonMode.WINDING_CW:
                {
                    gl.glFrontFace(GL.GL_CW);
                    break;
                }
            }

            if (polygonMode.isLocalCameraLightingEnabled())
            {
                gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
            }
            else
            {
                gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_FALSE);
            }

            if (polygonMode.isTwoSidedLightingEnabled())
            {
                gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
            }
            else
            {
                gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_FALSE);
            }
        }
        else
        {
            gl.glCullFace(GL.GL_BACK);
            gl.glShadeModel(GL.GL_SMOOTH);
            gl.glFrontFace(GL.GL_CCW);
            
            gl.glEnable(GL.GL_CULL_FACE);
            
            gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_FALSE);
            gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_FALSE);
        }
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

    private final void setMaterial(GL gl, Material material)
    {
        if (material != null)
        {
            if (material.isVertexColorTrackingEnabled())
            {
                gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
                //skip ambient and diffuse if color tracking
                gl.glEnable(GL.GL_COLOR_MATERIAL);
            }
            else
            {
                gl.glDisable(GL.GL_COLOR_MATERIAL);
                gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT,
                        argbAsRGBAVolatile(material.getColor(Material.AMBIENT)), 0);
                gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE,
                        argbAsRGBAVolatile(material.getColor(Material.DIFFUSE)), 0);
            }
            
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION,
                    argbAsRGBAVolatile(material.getColor(Material.EMISSIVE)), 0);
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR,
                    argbAsRGBAVolatile(material.getColor(Material.SPECULAR)), 0);
            gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS,
                    material.getShininess());
        }
        else
        {
            gl.glDisable(GL.GL_COLOR_MATERIAL);
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT,
                    argbAsRGBAVolatile(0x00333333), 0);
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE,
                    argbAsRGBAVolatile(0xFFCCCCCC), 0);
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION,
                    argbAsRGBAVolatile(0x00000000), 0);
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR,
                    argbAsRGBAVolatile(0x00000000), 0);
            gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS,
                    0.0f);
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

    private final void setAppearanceBase(GL gl, AppearanceBase appearance)
    {
        setCompositingMode(gl, appearance.getCompositingMode());
        setPolygonMode(gl, appearance.getPolygonMode());
    }

    private final void setAppearance(GL gl, Appearance appearance)
    {
        setAppearanceBase(gl, appearance);
        setFog(gl, appearance.getFog());
        setMaterial(gl, appearance.getMaterial());

        for (int i = 0; i < maxTextureUnits; ++i)
        {
            setTexture(gl, i, appearance.getTexture(i));
        }
    }

    private void setVertexBuffer(GL gl, VertexBuffer vertices, float alphaFactor)
    {
        gl.glColor4fv(argbAsRGBAVolatile(vertices.getDefaultColor(), alphaFactor), 0);

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

        //normals
        {
            final VertexArray normals = vertices.getNormals();
            if (normals != null)
            {
                if (normals.getComponentCount() != 3)
                {
                    throw new UnsupportedOperationException("normals must have 3 components");
                }
                int glType = GL.GL_FLOAT;
                switch (normals.getComponentType())
                {
                    case VertexArray.FLOAT:
                    {
                        glType = GL.GL_FLOAT;
                        break;
                    }
                    case VertexArray.BYTE:
                    {
                        glType = GL.GL_BYTE;
                        break;
                    }
                    default:
                    {
                        throw new UnsupportedOperationException("unsupported component type");
                    }
                }

                gl.glNormalPointer(glType, normals.getVertexByteStride(),
                        normals.getBuffer());
                gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
            }
            else
            {
                gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
            }
        }

        //colors
        {
            final VertexArray colors = vertices.getColors();
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

    private void commitModelView(GL gl, Transform modelTransform)
    {
        final Transform transform = modelViewTransform;
        transform.set(viewTransform);
        transform.postMultiply(modelTransform);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadMatrixf(transform.getColumnMajor(), 0);
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
