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

package javax.microedition.m3g;

import m3x.Require;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    private boolean supportsAnisotropy;
    private boolean supportsS3TC;
    private float maxAnisotropy;
    private int maxTextureSize;
    private int maxTextureUnits;
    private int maxLights;

    private float positionScale;
    private final float[] positionBias = new float[3];
    private float[] texcoordScale;
    private float[][] texcoordBias;
    private Transform[] texcoordTransform;
    private final Transform viewTransform = new Transform();
    private final Transform modelTransform = new Transform();
    private final Transform modelViewTransform = new Transform();
    private final Transform textureTransform = new Transform();
    private Light[] lights;
    private ShortBuffer positionShortBuffer;
    private ShortBuffer[] texcoordShortBuffer;
    
    public RendererOpenGL2()
    {
    }

    public GL getGL()
    {
        return instanceGL;
    }

    private static final float[] GLGETFLOAT1 = new float[1];
    private static final int[] GLGETINTEGER1 = new int[1];
    private static final byte[] GLGETBOOLEAN1 = new byte[1];

    private static final float glGetFloat(GL gl, int glenum)
    {
        gl.glGetFloatv(glenum, GLGETFLOAT1, 0);
        return GLGETFLOAT1[0];
    }
    
    private static final int glGetInteger(GL gl, int glenum)
    {
        gl.glGetIntegerv(glenum, GLGETINTEGER1, 0);
        return GLGETINTEGER1[0];
    }

    private static final boolean glGetBoolean(GL gl, int glenum)
    {
        gl.glGetBooleanv(glenum, GLGETBOOLEAN1, 0);
        return GLGETBOOLEAN1[0] != 0;
    }

    public void initialize(GL gl)
    {
        supportsAnisotropy = gl.isExtensionAvailable(
                "GL_EXT_texture_filter_anisotropic");
        supportsS3TC = gl.isExtensionAvailable(
                "GL_EXT_texture_compression_s3tc");
        
        if (supportsAnisotropy)
        {
            maxAnisotropy = glGetFloat(gl, GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        }
        else
        {
            maxAnisotropy = 1.0f;
        }
        maxTextureSize = glGetInteger(gl, GL.GL_MAX_TEXTURE_SIZE);
        maxTextureUnits = glGetInteger(gl, GL.GL_MAX_TEXTURE_UNITS);
        texcoordScale = new float[maxTextureUnits];
        texcoordBias = new float[maxTextureUnits][3];
        texcoordTransform = new Transform[maxTextureUnits];
        for (int texunit = 0; texunit < maxTextureUnits; ++texunit)
        {
            texcoordTransform[texunit] = new Transform();
        }
        texcoordShortBuffer = new ShortBuffer[maxTextureUnits];
        
        maxLights = glGetInteger(gl, GL.GL_MAX_LIGHTS);
        lights = new Light[maxLights];
    }

    public void bindContext(GL gl, int width, int height)
    {
        if (this.instanceGL != null)
        {
            throw new IllegalStateException("another context is already bound");
        }

        this.instanceGL = gl;
        this.width = width;
        this.height = height;

        if (gl != this.lastInstanceGL)
        {
            initialize(gl);
            this.lastInstanceGL = gl;
        }

        gl.glDisable(GL.GL_NORMALIZE);
        gl.glDisable(GL.GL_LINE_STIPPLE);
        gl.glDisable(GL.GL_DITHER);

        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glEnable(GL.GL_RESCALE_NORMAL);
        gl.glEnable(GL.GL_MULTISAMPLE);

    }

    public void releaseContext()
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
                commitModelViewMatrix(gl, transform);
            }
            else
            {
                commitModelViewMatrix(gl, IDENTITY);
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

    private static final float[] argbAsRGBA(int argb, float[] color)
    {
        final float byteToUniform = 1.0f / 255;

        color[0] = ((argb >> 16) & 0xff) * byteToUniform;
        color[1] = ((argb >> 8) & 0xff) * byteToUniform;
        color[2] = ((argb >> 0) & 0xff) * byteToUniform;
        color[3] = ((argb >> 24) & 0xff) * byteToUniform;

        return color;
    }

    private static final float[] TEMP_FLOAT4 = new float[4];

    private static final float[] argbAsRGBAVolatile(int argb)
    {
        return argbAsRGBA(argb, TEMP_FLOAT4);
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

            if (polygonMode.isPerspectiveCorrectionEnabled())
            {
                gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
            }
            else
            {
                gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
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

            //Default to off to emulate GLES
            gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
            //Desktop defaults to always perspective correct
            gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

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

    private ShortBuffer getPositionShortBuffer(int limit)
    {
        if (this.positionShortBuffer == null ||
            this.positionShortBuffer.capacity() < limit)
        {
            this.positionShortBuffer = ByteBuffer.allocateDirect(
                    limit).order(ByteOrder.nativeOrder()).asShortBuffer();
        }

        return (ShortBuffer) this.positionShortBuffer.rewind();
    }

    private ShortBuffer getTexcoordShortBuffer(int unit, int limit)
    {
        if (this.texcoordShortBuffer[unit] == null ||
            this.texcoordShortBuffer[unit].capacity() < limit)
        {
            this.texcoordShortBuffer[unit] = ByteBuffer.allocateDirect(
                    limit).order(ByteOrder.nativeOrder()).asShortBuffer();
        }

        return (ShortBuffer) this.texcoordShortBuffer[unit].rewind();
    }

    private static abstract class ImageBaseRendererData extends ImageBase.RendererData
    {
        abstract void upload(RendererOpenGL2 renderer, GL gl);
    }

    private static final class Image2DRendererData extends ImageBaseRendererData
    {
        private final Image2D image;
        private boolean needsUpdate = true;
        private int glTextureName;

        public Image2DRendererData(Image2D image)
        {
            this.image = image;
        }

        void upload(RendererOpenGL2 renderer, GL gl)
        {
            boolean firstUpload = false;
            if (glTextureName == 0)
            {
                final int[] names = new int[1];
                gl.glGenTextures(1, names, 0);
                glTextureName = names[0];
                firstUpload = true;
            }

            gl.glBindTexture(GL.GL_TEXTURE_2D, glTextureName);

            if (needsUpdate)
            {
                //don't use a palette during the decode
                gl.glPixelTransferi(GL.GL_MAP_COLOR, GL.GL_FALSE);
                //always use 4 byte aligned data
                gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);

                int glInternalFormat;
                int glFormat;
                int glDataType;

                final boolean undersized = (image.getWidth() <= 64) ||
                        (image.getHeight() <= 64);

                //avoid auto-compression on undersized image
                final boolean lossless = image.isLossless() || undersized;

                switch (image.getColorFormat())
                {
                    case ImageBase.ALPHA:
                    {
                        glInternalFormat = lossless ?
                            GL.GL_ALPHA : GL.GL_COMPRESSED_ALPHA;
                        glFormat = GL.GL_ALPHA;
                        glDataType = GL.GL_UNSIGNED_BYTE;
                        break;
                    }
                    case ImageBase.LUMINANCE:
                    {
                        glInternalFormat = lossless ?
                            GL.GL_LUMINANCE : GL.GL_COMPRESSED_LUMINANCE;
                        glFormat = GL.GL_LUMINANCE;
                        glDataType = GL.GL_UNSIGNED_BYTE;
                        break;
                    }
                    case ImageBase.LUMINANCE_ALPHA:
                    {
                        glInternalFormat = lossless ?
                            GL.GL_LUMINANCE_ALPHA :
                            GL.GL_COMPRESSED_LUMINANCE_ALPHA;
                        glFormat = GL.GL_LUMINANCE_ALPHA;
                        glDataType = GL.GL_UNSIGNED_BYTE;
                        break;
                    }
                    case ImageBase.RGB:
                    {
                        if (lossless)
                        {
                            glInternalFormat = GL.GL_RGB;
                        }
                        else
                        {
                            if (renderer.supportsS3TC)
                            {
                                glInternalFormat = GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
                            }
                            else
                            {
                                glInternalFormat = GL.GL_COMPRESSED_RGB;
                            }
                        }
                        glFormat = GL.GL_RGB;
                        glDataType = GL.GL_UNSIGNED_BYTE;
                        break;
                    }
                    case ImageBase.RGBA:
                    {
                        if (lossless)
                        {
                            glInternalFormat = GL.GL_RGBA;
                        }
                        else
                        {
                            if (renderer.supportsS3TC)
                            {
                                glInternalFormat = GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
                            }
                            else
                            {
                                glInternalFormat = GL.GL_COMPRESSED_RGBA;
                            }
                        }
                        glFormat = GL.GL_RGBA;
                        glDataType = GL.GL_UNSIGNED_BYTE;
                        break;
                    }
                    case ImageBase.RGB565:
                    {
                        glInternalFormat = lossless ?
                            GL.GL_RGB : GL.GL_COMPRESSED_RGB;
                        glFormat = GL.GL_RGB;
                        glDataType = GL.GL_UNSIGNED_SHORT_5_6_5;
                        break;
                    }
                    case ImageBase.RGBA5551:
                    {
                        glInternalFormat = lossless ?
                            GL.GL_RGB5_A1 : GL.GL_COMPRESSED_RGBA;
                        glFormat = GL.GL_RGBA;
                        glDataType = GL.GL_UNSIGNED_SHORT_5_5_5_1;
                        break;
                    }
                    case ImageBase.RGBA4444:
                    {
                        glInternalFormat = lossless ?
                            GL.GL_RGBA4 : GL.GL_COMPRESSED_RGBA;
                        glFormat = GL.GL_RGBA;
                        glDataType = GL.GL_UNSIGNED_SHORT_4_4_4_4;
                        break;
                    }
                    default:
                    {
                        throw new UnsupportedOperationException();
                    }
                }

                final int numLevels = image.getLevelCount();
                final int width = image.getWidth();
                final int height = image.getHeight();

                //find the start level
                int startLevel = 0;
                final int maxSize = renderer.maxTextureSize;
                for (; startLevel < numLevels; ++startLevel)
                {
                    final int w = Math.max(1, width >> startLevel);
                    final int h = Math.max(1, height >> startLevel);
                    if (w <= maxSize && h <= maxSize)
                    {
                        break;
                    }
                }


                //prep the texture objects
                if (firstUpload)
                {
                    for (int level = startLevel; level < numLevels; ++level)
                    {
                        final int w = Math.max(1, width >> level);
                        final int h = Math.max(1, height >> level);
                        gl.glTexImage2D(GL.GL_TEXTURE_2D,
                                level, glInternalFormat,
                                w, h,
                                0, glFormat, glDataType, null);
                    }
                }

                //upload the data, if there is any
                final int face = 0;
                for (int level = startLevel; level < numLevels; ++level)
                {
                    if (image.hasLevelBuffer(face, level))
                    {
                        final ByteBuffer buffer = image.getLevelBuffer(face, level);
                        final int w = Math.max(1, width >> level);
                        final int h = Math.max(1, height >> level);
                        gl.glTexSubImage2D(GL.GL_TEXTURE_2D,
                                level, 0, 0,
                                w, h,
                                glFormat, glDataType, buffer.rewind());
                    }
                }
                needsUpdate = false;
            }
        }

        @Override
        void sourceDataChanged()
        {
            //TODO support caching
        }
    }

    private static abstract class TextureRendererData extends Texture.RendererData
    {
        abstract void upload(RendererOpenGL2 renderer, GL gl);
    }

    private static final class Texture2DRendererData extends TextureRendererData
    {
        private final Texture2D texture;
        private boolean needsUpdate = true;
        private int glMinFilter, glMagFilter;
        private float glMaxAnisotropy;
        private int glWrapS, glWrapT;
        private final float[] glTexEnvColor = new float[4];
        private int glTexEnvMode;
        private int glCombineRGB, glCombineAlpha;
        private int glRGBScale, glAlphaScale;
        private final int[] operandRGB = new int[3];
        private final int[] srcRGB = new int[3];
        private final int[] operandAlpha = new int[3];
        private final int[] srcAlpha = new int[3];
        
        
        Texture2DRendererData(Texture2D texture)
        {
            this.texture = texture;
        }

        private void updateFiltering(RendererOpenGL2 renderer,
                int levelFilter, int imageFilter)
        {
            int mag, min;
            float anisotropy = 1.0f;
            switch (imageFilter)
            {
                case Texture.FILTER_LINEAR:
                {
                    mag = GL.GL_LINEAR;
                    switch (levelFilter)
                    {
                        case Texture.FILTER_BASE_LEVEL:
                        {
                            min = GL.GL_LINEAR;
                            break;
                        }
                        case Texture.FILTER_LINEAR:
                        {
                            min = GL.GL_LINEAR_MIPMAP_LINEAR;
                            break;
                        }
                        case Texture.FILTER_NEAREST:
                        {
                            min = GL.GL_LINEAR_MIPMAP_NEAREST;
                            break;
                        }
                        default:
                        {
                            throw new UnsupportedOperationException();
                        }
                    }
                    break;
                }
                case Texture.FILTER_NEAREST:
                {
                    mag = GL.GL_NEAREST;
                    switch (levelFilter)
                    {
                        case Texture.FILTER_BASE_LEVEL:
                        {
                            min = GL.GL_NEAREST;
                            break;
                        }
                        case Texture.FILTER_LINEAR:
                        {
                            min = GL.GL_NEAREST_MIPMAP_LINEAR;
                            break;
                        }
                        case Texture.FILTER_NEAREST:
                        {
                            min = GL.GL_NEAREST_MIPMAP_NEAREST;
                            break;
                        }
                        default:
                        {
                            throw new UnsupportedOperationException();
                        }
                    }
                    break;
                }
                case Texture.FILTER_ANISOTROPIC:
                {
                    anisotropy = renderer.maxAnisotropy;
                    mag = GL.GL_LINEAR;
                    switch (levelFilter)
                    {
                        case Texture.FILTER_BASE_LEVEL:
                        {
                            min = GL.GL_LINEAR;
                            break;
                        }
                        case Texture.FILTER_LINEAR:
                        {
                            min = GL.GL_LINEAR_MIPMAP_LINEAR;
                            break;
                        }
                        case Texture.FILTER_NEAREST:
                        {
                            min = GL.GL_LINEAR_MIPMAP_NEAREST;
                            break;
                        }
                        default:
                        {
                            throw new UnsupportedOperationException();
                        }
                    }
                    break;
                }
                default:
                {
                    throw new UnsupportedOperationException();
                }
            }

            this.glMagFilter = mag;
            this.glMinFilter = min;
            this.glMaxAnisotropy = anisotropy;
        }

        private static final int wrappingAsGLenum(int wrapping)
        {
            switch (wrapping)
            {
                case Texture2D.WRAP_CLAMP:
                {
                    return GL.GL_CLAMP_TO_EDGE;
                }
                case Texture2D.WRAP_REPEAT:
                {
                    return GL.GL_CLAMP_TO_EDGE;
                }
                case Texture2D.WRAP_MIRROR:
                {
                    return GL.GL_MIRRORED_REPEAT;
                }
                default:
                {
                    throw new UnsupportedOperationException();
                }
            }
        }
        private void updateWrapping(int wrappingS, int wrappingT)
        {
            glWrapS = wrappingAsGLenum(wrappingS);
            glWrapT = wrappingAsGLenum(wrappingT);
        }

        private static final int combinerFuncAsGLEnum(int func)
        {
            switch (func)
            {
                case TextureCombiner.ADD:
                {
                    return GL.GL_ADD;
                }
                case TextureCombiner.ADD_SIGNED:
                {
                    return GL.GL_ADD_SIGNED;
                }
                case TextureCombiner.DOT3_RGB:
                {
                    return GL.GL_DOT3_RGB;
                }
                case TextureCombiner.DOT3_RGBA:
                {
                    return GL.GL_DOT3_RGBA;
                }
                case TextureCombiner.INTERPOLATE:
                {
                    return GL.GL_INTERPOLATE;
                }
                case TextureCombiner.MODULATE:
                {
                    return GL.GL_MODULATE;
                }
                case TextureCombiner.REPLACE:
                {
                    return GL.GL_REPLACE;
                }
                case TextureCombiner.SUBTRACT:
                {
                    return GL.GL_SUBTRACT;
                }
                default:
                {
                    throw new UnsupportedOperationException();
                }
            }
        }

        private static final int combinerSourceAsGLenum(int source)
        {
            switch (source & (~(TextureCombiner.ALPHA | TextureCombiner.INVERT)))
            {
                case TextureCombiner.CONSTANT:
                {
                    return GL.GL_CONSTANT;
                }
                case TextureCombiner.PRIMARY:
                {
                    return GL.GL_PRIMARY_COLOR;
                }
                case TextureCombiner.PREVIOUS:
                {
                    return GL.GL_PREVIOUS;
                }
                case TextureCombiner.TEXTURE:
                {
                    return GL.GL_TEXTURE;
                }
                default:
                {
                    throw new UnsupportedOperationException();
                }
            }
        }
        private static final int combinerOpAsGLenum(boolean alpha, boolean invert)
        {
            if (!invert)
            {
                return alpha ?
                    GL.GL_SRC_ALPHA :
                    GL.GL_SRC_COLOR;
            }
            else
            {
                return alpha ?
                    GL.GL_ONE_MINUS_SRC_ALPHA :
                    GL.GL_ONE_MINUS_SRC_COLOR;
            }
        }
        
        private void updateCombiner(TextureCombiner combiner)
        {
            glCombineRGB = combinerFuncAsGLEnum(combiner.getColorFunction());
            glCombineAlpha = combinerFuncAsGLEnum(combiner.getAlphaFunction());
            glRGBScale = combiner.getColorScale();
            glAlphaScale = combiner.getAlphaScale();

            for (int i = 0; i < 3; ++i)
            {
                final int colorSrc = combiner.getColorSource(i);
                operandRGB[i] = combinerOpAsGLenum(
                        (colorSrc & TextureCombiner.ALPHA) != 0,
                        (colorSrc & TextureCombiner.INVERT) != 0);
                srcRGB[i] = combinerSourceAsGLenum(colorSrc);
                
                final int alphaSrc = combiner.getAlphaSource(i);
                operandAlpha[i] = combinerOpAsGLenum(true,
                        (alphaSrc & TextureCombiner.INVERT) != 0);
                srcAlpha[i] = combinerSourceAsGLenum(alphaSrc);
            }
            
            glTexEnvMode = GL.GL_COMBINE;
        }

        private void updateBlending(int blending)
        {
            int envMode;
            switch (blending)
            {
                case Texture2D.FUNC_ADD:
                {
                    envMode = GL.GL_ADD;
                    break;
                }
                case Texture2D.FUNC_BLEND:
                {
                    envMode = GL.GL_BLEND;
                    break;
                }
                case Texture2D.FUNC_DECAL:
                {
                    envMode = GL.GL_DECAL;
                    break;
                }
                case Texture2D.FUNC_MODULATE:
                {
                    envMode = GL.GL_MODULATE;
                    break;
                }
                default:
                {
                    envMode = GL.GL_REPLACE;
                }
            }
            this.glTexEnvMode = envMode;
        }

        private static final void glTexEnv(GL gl, int pname, int param)
        {
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, pname, param);
        }

        private static final void glTexEnv(GL gl, int pname, float[] param)
        {
            gl.glTexEnvfv(GL.GL_TEXTURE_ENV, pname, param, 0);
        }

        private static final void glTexParameter(GL gl, int pname, float param)
        {
            gl.glTexParameterf(GL.GL_TEXTURE_2D, pname, param);
        }

        private static final void glTexParameter(GL gl, int pname, int param)
        {
            gl.glTexParameteri(GL.GL_TEXTURE_2D, pname, param);
        }

        @Override
        void upload(RendererOpenGL2 renderer, GL gl)
        {
            if (needsUpdate)
            {
                updateFiltering(renderer,
                        texture.getLevelFilter(), texture.getImageFilter());

                updateWrapping(texture.getWrappingS(), texture.getWrappingT());

                argbAsRGBA(texture.getBlendColor(), this.glTexEnvColor);
                
                final TextureCombiner combiner = texture.getCombiner();
                if (combiner != null)
                {
                    updateCombiner(combiner);
                }
                else
                {
                    updateBlending(texture.getBlending());
                }

                //TODO support caching
                //needsUpdate = false;
            }

            Image2D image = texture.getImage2D();
            Image2DRendererData rendererData = (Image2DRendererData)
                    image.getRendererData();
            if (rendererData == null)
            {
                //allocate one
                rendererData = new Image2DRendererData(image);
                image.setRendererData(rendererData);
            }
            rendererData.upload(renderer, gl);

            glTexParameter(gl, GL.GL_TEXTURE_MIN_FILTER, glMinFilter);
            glTexParameter(gl, GL.GL_TEXTURE_MAG_FILTER, glMagFilter);
            if (renderer.supportsAnisotropy)
            {
                glTexParameter(gl, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                        glMaxAnisotropy);
            }
            glTexParameter(gl, GL.GL_TEXTURE_WRAP_S, glWrapS);
            glTexParameter(gl, GL.GL_TEXTURE_WRAP_T, glWrapT);

            glTexEnv(gl, GL.GL_TEXTURE_ENV_COLOR, glTexEnvColor);
            glTexEnv(gl, GL.GL_TEXTURE_ENV_MODE, glTexEnvMode);
            if (glTexEnvMode == GL.GL_COMBINE)
            {
                glTexEnv(gl, GL.GL_COMBINE_RGB, glCombineRGB);
                glTexEnv(gl, GL.GL_COMBINE_ALPHA, glCombineAlpha);
                glTexEnv(gl, GL.GL_RGB_SCALE, glRGBScale);
                glTexEnv(gl, GL.GL_ALPHA_SCALE, glAlphaScale);

                for (int i = 0; i < 3; ++i)
                {
                    glTexEnv(gl, GL.GL_OPERAND0_RGB + i, operandRGB[i]);
                    glTexEnv(gl, GL.GL_SRC0_RGB + i, srcRGB[i]);
                    glTexEnv(gl, GL.GL_OPERAND0_ALPHA + i, operandAlpha[i]);
                    glTexEnv(gl, GL.GL_SRC0_ALPHA + i, srcAlpha[i]);
                }
            }

            gl.glEnable(GL.GL_TEXTURE_2D);
        }

        @Override
        void sourceDataChanged()
        {
            //TODO support caching
        }
    }

    private final void setTexture(GL gl, int texunit, Texture texture)
    {
        gl.glActiveTexture(GL.GL_TEXTURE0 + texunit);
        if (texture != null)
        {
            texture.getCompositeTransform(texcoordTransform[texunit]);
            TextureRendererData rendererData = (TextureRendererData) texture.getRendererData();
            if (rendererData == null)
            {
                //create the renderer data and set
                if (texture instanceof Texture2D)
                {
                    rendererData = new Texture2DRendererData((Texture2D) texture);
                }
                else
                {
                    throw new UnsupportedOperationException();
                }

                texture.setRendererData(rendererData);
            }
            rendererData.upload(this, gl);
        }
        else
        {
            gl.glDisable(GL.GL_TEXTURE_2D);
            gl.glDisable(GL.GL_TEXTURE_CUBE_MAP);
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

            int stride = positions.getVertexByteStride();
            Buffer buffer = positions.getBuffer();
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
                case VertexArray.BYTE:
                {
                    final ByteBuffer bytes = (ByteBuffer) buffer;
                    final int limit = bytes.limit();
                    final ShortBuffer shorts = getPositionShortBuffer(limit * 2);
                    for (int i = 0; i < limit; ++i)
                    {
                        shorts.put(bytes.get());
                    }
                    glType = GL.GL_SHORT;
                    stride = 0;
                    buffer = shorts.flip();
                    break;
                }
                default:
                {
                    throw new IllegalStateException("unsupported component type");
                }
            }

            gl.glVertexPointer(positions.getComponentCount(), glType,
                    stride, buffer);
        }

        //normals
        if (true)
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
        if (true)
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

        //texcoords
        if (true)
        {
            float[] scaleBias = TEMP_FLOAT4;
            final int maxUnits = maxTextureUnits;
            for (int texunit = 0; texunit < maxUnits; ++texunit)
            {
                VertexArray texcoords = vertices.getTexCoords(texunit, scaleBias);
                gl.glClientActiveTexture(GL.GL_TEXTURE0 + texunit);
                if (texcoords != null)
                {
                    texcoordScale[texunit] = scaleBias[0];
                    System.arraycopy(scaleBias, 1, texcoordBias[texunit], 0, 3);
                    int stride = texcoords.getVertexByteStride();
                    Buffer buffer = texcoords.getBuffer();
                    int glType = GL.GL_FLOAT;
                    switch (texcoords.getComponentType())
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
                            texcoordScale[texunit] *= 1.0f / 65556;
                            glType = GL.GL_INT;
                            break;
                        }
                        case VertexArray.SHORT:
                        {
                            glType = GL.GL_SHORT;
                            break;
                        }
                        case VertexArray.BYTE:
                        {
                            final ByteBuffer bytes = (ByteBuffer) buffer;
                            final int limit = bytes.limit();
                            final ShortBuffer shorts =
                                    getTexcoordShortBuffer(texunit, limit * 2);
                            for (int i = 0; i < limit; ++i)
                            {
                                shorts.put(bytes.get());
                            }
                            glType = GL.GL_SHORT;
                            stride = 0;
                            buffer = shorts.flip();
                            break;
                        }
                        default:
                        {
                            throw new IllegalStateException("unsupported component type");
                        }
                    }

                    gl.glTexCoordPointer(texcoords.getComponentCount(), glType,
                            stride, buffer);
                    gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                }
                else
                {
                    gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                }
            }
        }
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

    private void commitModelViewMatrix(GL gl, Transform modelTransform)
    {
        final Transform transform = modelViewTransform;
        transform.set(viewTransform);
        transform.postMultiply(modelTransform);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadMatrixf(transform.getColumnMajor(), 0);
    }

    private void commitModelViewMatrix(GL gl)
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

    private void commitTextureMatrices(GL gl)
    {
        gl.glMatrixMode(GL.GL_TEXTURE);
        final int maxUnits = maxTextureUnits;
        final Transform transform = textureTransform;
        for (int texunit = 0; texunit < maxUnits; ++texunit)
        {
            gl.glActiveTexture(GL.GL_TEXTURE0 + texunit);
            transform.set(texcoordTransform[texunit]);
            final float[] bias = texcoordBias[texunit];
            transform.postTranslate(bias[0], bias[1], bias[2]);
            final float scale = texcoordScale[texunit];
            transform.postScale(scale, scale, scale);
            gl.glLoadMatrixf(transform.getColumnMajor(), 0);
        }
    }
    
    private void render(GL gl, IndexBuffer primitives)
    {
        //commit the texture unit matrices
        commitTextureMatrices(gl);
        
        //commit the modelview matrix
        commitModelViewMatrix(gl);

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
