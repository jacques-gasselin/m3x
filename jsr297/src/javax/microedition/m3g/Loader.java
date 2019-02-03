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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * @author jgasseli
 */
public final class Loader
{
    private static final class M3GDeserializer
    {
        private static final int TYPE_HEADEROBJECT = 0;
        private static final int TYPE_ANIMATIONCONTROLLER = 1;
        private static final int TYPE_ANIMATIONTRACK = 2;
        private static final int TYPE_APPEARANCE = 3;
        private static final int TYPE_BACKGROUND = 4;
        private static final int TYPE_CAMERA = 5;
        private static final int TYPE_COMPOSITINGMODE = 6;
        private static final int TYPE_FOG = 7;
        private static final int TYPE_POLYGONMODE = 8;
        private static final int TYPE_GROUP = 9;
        private static final int TYPE_IMAGE2D = 10;
        private static final int TYPE_TRIANGLESTRIPARRAY = 11;
        private static final int TYPE_LIGHT = 12;
        private static final int TYPE_MATERIAL = 13;
        private static final int TYPE_MESH = 14;
        private static final int TYPE_MORPHINGMESH = 15;
        private static final int TYPE_SKINNEDMESH = 16;
        private static final int TYPE_TEXTURE2D = 17;
        private static final int TYPE_SPRITE3D = 18;
        private static final int TYPE_KEYFRAMESEQUENCE = 19;
        private static final int TYPE_VERTEXARRAY = 20;
        private static final int TYPE_VERTEXBUFFER = 21;
        private static final int TYPE_WORLD = 22;
        private static final int TYPE_BLENDER = 23;
        private static final int TYPE_DYNAMICIMAGE2D = 24;
        private static final int TYPE_FRAGMENTSHADER = 25;
        private static final int TYPE_IMAGECUBE = 26;
        private static final int TYPE_INDEXBUFFER = 27;
        private static final int TYPE_POINTSPRITEMODE = 28;
        private static final int TYPE_RENDERPASS = 29;
        private static final int TYPE_RENDERTARGET = 30;
        private static final int TYPE_SHADERAPPEARANCE = 31;
        private static final int TYPE_SHADERPROGRAM = 32;
        private static final int TYPE_SHADERUNIFORMS = 33;
        private static final int TYPE_STENCIL = 34;
        private static final int TYPE_TEXTURECOMBINER = 35;
        private static final int TYPE_TEXTURECUBE = 36;
        private static final int TYPE_VERTEXSHADER = 37;

        private static final int TYPE_EXTERNALOBJECT = 253;
        private static final int TYPE_EXTERNALIMAGE = 254;
        private static final int TYPE_EXTERNAL = 255;
        
        private final ArrayList<Object3D> references = new ArrayList<Object3D>();
        private final ArrayList<Object3D> rootObjects = new ArrayList<Object3D>();
        private final ArrayList<InputStream> streamStack = new ArrayList<InputStream>();
        private InputStream currentStream;
        private Object3D[] internalReferenceObjects;
        private int versionMajor, versionMinor;

        M3GDeserializer(InputStream stream)
        {
            push(stream);
        }

        final boolean isFileFormat1()
        {
            return versionMajor == 1;
        }

        final boolean isFileFormat2()
        {
            return versionMajor == 2;
        }

        final void setVersion(int major, int minor)
        {
            this.versionMajor = major;
            this.versionMinor = minor;
        }

        void push(Checksum checksum)
        {
            CheckedInputStream cstream = new CheckedInputStream(currentStream, checksum);
            push(cstream);
        }

        void push(InputStream stream)
        {
            streamStack.add(stream);
            currentStream = stream;
        }

        void pop()
        {
            streamStack.remove(streamStack.size() - 1);
            currentStream = streamStack.get(streamStack.size() - 1);
        }

        void addReference(Object3D obj)
        {
            Require.notNull(obj, "obj");

            if (references.contains(obj))
            {
                throw new IllegalStateException("reference to " + obj +
                        " already exists");
            }
            
            references.add(obj);
            rootObjects.add(obj);
        }

        /**
         * Reads an unsigned byte value.
         * @return the value, or -1 if EOF was reached
         * @throws IOException if the stream hits any error
         */
        int read() throws IOException
        {
            return currentStream.read();
        }

        /**
         * Reads an unsigned byte value.
         * @return the value.
         * @throws IOException if the stream hits any error
         * @throws EOFException if the stream is at EOF before the read.
         */
        private int readChecked() throws IOException
        {
            final int b = currentStream.read();
            if (b == -1)
            {
                throw new EOFException();
            }
            return b;
        }

        /**
         * Reads a little-endian boolean value. This is equavalent to
         * {@code readByte() != 0}
         * @return the boolean value of the next byte in the stream.
         * @throws IOException if the stream hits EOF or any other error
         */
        boolean readBoolean() throws IOException
        {
            return readByte() != 0;
        }

        /**
         * Reads a little-endian signed byte value.
         * @return the byte value of the next byte in the stream.
         * @throws IOException if the stream hits EOF or any other error
         */
        byte readByte() throws IOException
        {
            return (byte) readChecked();
        }

        /**
         * Reads a little-endian signed float value.
         * @return the float value of the next 4 bytes in the stream.
         * @throws IOException if the stream hits EOF or any other error
         */
        float readFloat() throws IOException
        {
            return Float.intBitsToFloat(readInt());
        }

        void readFully(byte[] b, int off, int len) throws IOException
        {
            for (int i = 0; i < len; ++i)
            {
                b[off + i] = (byte) readChecked();
            }
        }

        void readFully(float[] f, int off, int len) throws IOException
        {
            for (int i = 0; i < len; ++i)
            {
                f[off + i] = readFloat();
            }
        }
        
        /**
         * Reads a little-endian signed integer value.
         * @return the int value of the next 4 bytes in the stream.
         * @throws IOException if the stream hits EOF or any other error
         */
        int readInt() throws IOException
        {
            final int byte0 = readChecked();
            final int byte1 = readChecked();
            final int byte2 = readChecked();
            final int byte3 = readChecked();

            return (byte0) | (byte1 << 8) | (byte2 << 16) | (byte3 << 24);
        }

        int readRGBasARGB() throws IOException
        {
            final int red = readChecked();
            final int green = readChecked();
            final int blue = readChecked();

            return (0xff << 24) | (red << 16) | (green << 8) | (blue << 0);
        }

        int readRGBAasARGB() throws IOException
        {
            final int red = readChecked();
            final int green = readChecked();
            final int blue = readChecked();
            final int alpha = readChecked();

            return (alpha << 24) | (red << 16) | (green << 8) | (blue << 0);
        }

        /**
         * Reads and decodes a reference to an already deserialized object.
         * @return the reference at the read index, or null if 0
         * @throws IOException if the stream hits EOF or any other error
         */
        @SuppressWarnings("unchecked")
        <Object3DDerived extends Object3D>
        Object3DDerived readReference() throws IOException
        {
            final int index = readInt();
            if (index == 0)
            {
                return null;
            }
            Object3D ret = references.get(index - 2);
            rootObjects.remove(ret);
            return (Object3DDerived) ret;
        }

        /**
         * Reads a little-endian signed short value.
         * @return the short value of the next 2 bytes in the stream.
         * @throws IOException if the stream hits EOF or any other error
         */
        short readShort() throws IOException
        {
            return (short) readUnsignedShort();
        }

        /**
         * Reads a little-endian unsigned byte value.
         * @return the byte value of the next byte in the stream.
         * @throws IOException if the stream hits EOF or any other error
         */
        int readUnsignedByte() throws IOException
        {
            return readChecked();
        }

        /**
         * Reads a little-endian unsigned short value.
         * @return the short value of the next 2 bytes in the stream.
         * @throws IOException if the stream hits EOF or any other error
         */
        int readUnsignedShort() throws IOException
        {
            final int byte0 = readChecked();
            final int byte1 = readChecked();

            return (byte0) | (byte1 << 8);
        }

        /**
         * Checks the stream for the presence of the M3G file identifier.
         * @throws IOException if the identifier is missing.
         */
        private void checkIdentifier() throws IOException
        {
            final byte[] expected = { 
                (byte)0xAB, 'J', 'S', 'R', '1', '8', '4', (byte)0xBB,
                '\r', '\n', 0x1A, '\n'
            };

            final int length = expected.length;
            final byte[] actual = new byte[length];

            readFully(actual, 0, length);
            
            int i;
            for (i = 0; i < length; ++i)
            {
                if (actual[i] != expected[i])
                {
                    break;
                }
            }

            if (i == length)
            {
                return;
            }

            //assume the identifier was not correct here
            String message;
            switch (i)
            {
                case 0:
                {
                    if (actual[i] != 0x2B)
                    {
                        message = "Byte " + i + " is not " + (char)expected[i] +
                                " as expected. Perhaps this is not an M3G file.";
                    }
                    else
                    {
                        message = "Byte " + i + " is not " + (char)expected[i] +
                                " as expected." +
                                "However it is 0x2B as one would expect if the " +
                                "file had been subjected to 7bit conversion.";
                    }
                    break;
                }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                {
                    final char uppercase = (char)expected[i];
                    final char lowercase = Character.toLowerCase(uppercase);
                    if (actual[i] != lowercase)
                    {
                        message = "Byte " + i + " is not " + uppercase +
                                " as expected. Perhaps this is not an M3G file.";
                    }
                    else
                    {
                        message = "Byte " + i + " is not " + uppercase +
                                " as expected." +
                                "However it is " + lowercase + " as one would" +
                                " expect if the file had been subjected to" +
                                "upper to lower case conversion.";
                    }
                    break;
                }
                case 8:
                {
                    if (actual[i] != '\n')
                    {
                        message = "Byte " + i + " is not '\\r'" +
                                " as expected. Perhaps this is not an M3G file.";
                    }
                    else
                    {
                        message = "Byte " + i + " is not '\\r' as expected." +
                                "However it is '\\n' as one would" +
                                " expect if the file had been subjected to" +
                                "CR-LF to LF conversion. This can often occur" +
                                " if the file has been treated as a text file" +
                                " and transfered to a UNIX-like OS.";
                    }
                    break;
                }
                case 11:
                {
                    if (actual[i] != '\r')
                    {
                        message = "Byte " + i + " is not '\\n'" +
                                " as expected. Perhaps this is not an M3G file.";
                    }
                    else
                    {
                        message = "Byte " + i + " is not '\\n' as expected." +
                                "However it is '\\r' as one would" +
                                " expect if the file had been subjected to" +
                                "LF to CR-LF conversion. This can often occur" +
                                " if the file has been treated as a text file" +
                                " and transfered to a Windows-like OS.";
                    }
                    break;
                }
                default:
                {
                    message = "Actual byte read " + "(" + actual[i] + ")" +
                            "at index " + i + " does not equal the expected" +
                            " value " + expected[i];
                }
            }

            throw new IOException("Invalid file identifier: " + message);
        }

        private void setInternalReferenceObjects(Object3D[] referenceObjects)
        {
            this.internalReferenceObjects = referenceObjects;
        }

        private void load() throws IOException
        {
            Adler32 adler32 = new Adler32();
            Inflater inflater = new Inflater();
            //read sections
            for (int section = 0; true; ++section)
            {
                adler32.reset();
                push(adler32);
                final int compressionScheme;
                try
                {
                    compressionScheme = read();
                }
                catch (EOFException e)
                {
                    break;
                }

                //EOF?
                if (compressionScheme == -1)
                {
                    break;
                }

                final int totalSectionLength = readInt();
                final int uncompressedLength = readInt();
                final byte[] objects = new byte[totalSectionLength - 13];
                readFully(objects, 0, objects.length);
                pop();
                final long checksum = readInt() & 0x00000000ffffffffL;
                //check that the checksum is correct
                if (adler32.getValue() != checksum)
                {
                    throw new IOException("checksum in section " + section +
                            " does not match the data." +
                            " Expected " + checksum + " but calculated " +
                            adler32.getValue() + ".");
                }


                ByteArrayInputStream uncompressedStream =
                        new ByteArrayInputStream(objects);
                if (compressionScheme == 1)
                {
                    //zlib compressed, 32k buffer
                    final int bufferSize = 32 * 1024;
                    push(new InflaterInputStream(uncompressedStream, inflater,
                            bufferSize));
                }
                else
                {
                    push(uncompressedStream);
                }

                loadSection(section, uncompressedLength);

                pop();
            }
        }

        void loadSection(int sectionNumber, int sectionLength) throws IOException
        {
            int offset = 0;
            while (offset < sectionLength)
            {
                final int objectType = readUnsignedByte();
                final int objectLength = readInt();
                offset += objectLength + 5;

                //decode the object
                switch (objectType)
                {
                    case TYPE_HEADEROBJECT:
                    {
                        if (sectionNumber != 0)
                        {
                            throw new IOException(
                                    "Header Object is not in section 0");
                        }
                        final int major = readUnsignedByte();
                        final int minor = readUnsignedByte();
                        setVersion(major, minor);
                        //TODO support the external reference sections
                        final boolean hasExternalReferences = readBoolean();
                        final int totalFileSize = readInt();
                        final int approximateContentSize = readInt();
                        final int utf8Length = objectLength - 11;
                        final byte[] utf8 = new byte[utf8Length];
                        readFully(utf8, 0, utf8Length);
                        //TODO may need to scan for the first 0 byte and truncate
                        final String authoringField;
                        if (utf8Length > 1)
                        {
                            authoringField = new String(utf8, 0, utf8Length - 1,
                                    "UTF-8");
                        }
                        else
                        {
                            authoringField = "";
                        }
                        break;
                    }
                    case TYPE_ANIMATIONCONTROLLER:
                    {
                        AnimationController obj = new AnimationController();

                        loadAnimationController(obj);
                        
                        addReference(obj);
                        break;
                    }
                    case TYPE_ANIMATIONTRACK:
                    {
                        AnimationTrack obj = new AnimationTrack();
                        
                        loadAnimationTrack(obj);
                        
                        addReference(obj);
                        break;
                    }
                    case TYPE_APPEARANCE:
                    {
                        Appearance obj = new Appearance();

                        loadAppearance(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_BACKGROUND:
                    {
                        Background obj = new Background();

                        loadBackground(obj);

                        addReference(obj);
                        break;                        
                    }
                    case TYPE_CAMERA:
                    {
                        Camera obj = new Camera();

                        loadCamera(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_COMPOSITINGMODE:
                    {
                        CompositingMode obj = new CompositingMode();

                        loadCompositingMode(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_FOG:
                    {
                        Fog obj = new Fog();
                        
                        loadFog(obj);
                        
                        addReference(obj);
                        break;
                    }
                    case TYPE_GROUP:
                    {
                        Group obj = new Group();

                        loadGroup(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_IMAGE2D:
                    {
                        Image2D obj = new Image2D();

                        loadImage2D(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_LIGHT:
                    {
                        Light obj = new Light();

                        loadLight(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_MATERIAL:
                    {
                        Material obj = new Material();

                        loadMaterial(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_MESH:
                    {
                        Mesh obj = new Mesh();

                        loadMesh(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_MORPHINGMESH:
                    {
                        throw new IOException("Unsupported object type "
                                + objectType + " (MorphingMesh)");
                    }
                    case TYPE_SKINNEDMESH:
                    {
                        SkinnedMesh obj = new SkinnedMesh();
                        
                        loadSkinnedMesh(obj);
                        
                        addReference(obj);
                        break;
                    }
                    case TYPE_POLYGONMODE:
                    {
                        PolygonMode obj = new PolygonMode();

                        loadPolygonMode(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_TEXTURE2D:
                    {
                        Texture2D obj = new Texture2D();

                        loadTexture2D(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_SPRITE3D:
                    {
                        throw new IOException("Unsupported object type "
                                + objectType + " (Sprite3D)");
                    }
                    case TYPE_KEYFRAMESEQUENCE:
                    {
                        KeyframeSequence obj = new KeyframeSequence();

                        loadKeyframeSequence(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_TRIANGLESTRIPARRAY:
                    {
                        TriangleStripArray obj = new TriangleStripArray();

                        loadTriangleStripArray(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_VERTEXARRAY:
                    {
                        VertexArray obj = new VertexArray();

                        loadVertexArray(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_VERTEXBUFFER:
                    {
                        VertexBuffer obj = new VertexBuffer();

                        loadVertexBuffer(obj);

                        addReference(obj);
                        break;
                    }
                    case TYPE_WORLD:
                    {
                        World obj = new World();

                        loadWorld(obj);

                        addReference(obj);
                        break;
                    }
                    default:
                    {
                        throw new IOException("Unsupported object type "
                                + objectType);
                    }
                }
            }
        }

        private void loadAnimationController(AnimationController obj)
            throws IOException
        {
            loadObject3D(obj);

            final float speed = readFloat();
            final float weight = readFloat();
            final int activeIntervalStart = readInt();
            final int activeIntervalEnd = readInt();
            final float referenceSequenceTime = readFloat();
            final int referenceWorldTime = readInt();
            
            obj.setPosition(referenceSequenceTime, referenceWorldTime);
            obj.setWeight(weight);
            obj.setSpeed(speed, referenceWorldTime);
            obj.setActiveInterval(activeIntervalStart, activeIntervalEnd);
        }

        private void loadAnimationTrack(AnimationTrack obj)
            throws IOException
        {
            loadObject3D(obj);
            
            KeyframeSequence sequence = readReference();
            AnimationController controller = readReference();
            
            final int property;
            if (isFileFormat2())
            {
                property = readUnsignedShort();
                if (property >= ShaderVariable.FLOAT || property <= ShaderVariable.SAMPLER_CUBE)
                {
                    boolean normalized = readBoolean();
                }
            }
            else 
            {
                property = readInt();
            }
            
            obj.setTargetProperty(property);
            obj.setKeyframeSequence(sequence);
            obj.setController(controller);
        }
        
        private void loadAppearanceBase(AppearanceBase obj)
            throws IOException
        {
            loadObject3D(obj);

            if (isFileFormat1())
            {
                obj.setLayer(readUnsignedByte());
            }
            else
            {
                obj.setLayer(readByte());
            }

            obj.setCompositingMode(readReference());

            if (isFileFormat2())
            {
                obj.setPolygonMode(readReference());
                obj.setDepthSortEnabled(readBoolean());
            }
        }

        private void loadAppearance(Appearance obj)
            throws IOException
        {
            loadAppearanceBase(obj);

            obj.setFog(readReference());
            if (isFileFormat1())
            {
                obj.setPolygonMode(readReference());
            }
            else
            {
                obj.setPointSpriteMode(readReference());
            }
            obj.setMaterial(readReference());
            final int textureCount = readInt();
            for (int i = 0; i < textureCount; ++i)
            {
                obj.setTexture(i, readReference());
            }
        }

        @SuppressWarnings("deprecation")
        private void loadBackground(Background obj)
            throws IOException
        {
            loadObject3D(obj);
            
            obj.setColor(readRGBAasARGB());
            obj.setImage(readReference());
            
            int modeX = readUnsignedByte();
            int modeY = readUnsignedByte();
            obj.setImageMode(modeX, modeY);
            
            int cropX = readInt();
            int cropY = readInt();
            int cropWidth = readInt();
            int cropHeight = readInt();
            obj.setCrop(cropX, cropY, cropWidth, cropHeight);
            
            obj.setDepthClearEnabled(readBoolean());

            if (isFileFormat2())
            {
                obj.setDepth(readFloat());
                obj.setStencil(readInt());
                obj.setStencilClearMask(readInt());
                obj.setColorClearMask(readRGBAasARGB());
            }
            else
            {
                obj.setColorClearEnabled(readBoolean());
            }

        }
        
        private void loadCamera(Camera obj)
            throws IOException
        {
            loadNode(obj);
            
            int projectionType = readUnsignedByte();
            if (projectionType == Camera.GENERIC)
            {
                final float[] matrix = new float[16];
                for (int i = 0; i < 16; ++i)
                {
                    matrix[i] = readFloat();
                }
                final Transform transform = new Transform();
                transform.set(matrix);
                
                obj.setGeneric(transform);
            }
            else if (isFileFormat2() && projectionType == Camera.SCREEN)
            {
                float x = readFloat();
                float y = readFloat();
                float width = readFloat();
                float height = readFloat();
                
                obj.setScreen(x, y, width, height);
            }
            else
            {
                float fovy = readFloat();
                float aspect = readFloat();
                float near = readFloat();
                float far = readFloat();
                
                obj.setPerspective(fovy, aspect, near, far);
            }
        }

        private void loadCompositingMode(CompositingMode obj)
            throws IOException
        {
            loadObject3D(obj);

            obj.setDepthTestEnabled(readBoolean());
            obj.setDepthWriteEnabled(readBoolean());
            if (isFileFormat1())
            {
                int mask = 0;
                mask |= readBoolean() ?
                    0x00ffffff : 0;
                mask |= readBoolean() ?
                    0xff000000 : 0;
                obj.setColorWriteMask(mask);
            }

            obj.setBlending(readUnsignedByte());
            final float byteToUniform = 1.0f / 255;
            obj.setAlphaThreshold(readUnsignedByte() * byteToUniform);
            final float factor = readFloat();
            final float units = readFloat();
            obj.setDepthOffset(factor, units);

            if (!isFileFormat1())
            {
                obj.setDepthTest(readUnsignedShort());
                obj.setAlphaTest(readUnsignedShort());
                obj.setBlender(readReference());
                obj.setStencil(readReference());
                obj.setColorWriteMask(readRGBAasARGB());
            }
        }

        private void loadFog(Fog obj)
            throws IOException
        {
            loadObject3D(obj);
            
            obj.setColor(readRGBasARGB());
            final int mode = readUnsignedByte();
            obj.setMode(mode);
            
            if (mode == Fog.EXPONENTIAL || (isFileFormat2() && mode == Fog.EXPONENTIAL_SQUARED))
            {
                obj.setDensity(readFloat());
            }
            else if (mode == Fog.LINEAR)
            {
                final float near = readFloat();
                final float far = readFloat();
                obj.setLinear(near, far);
            }
        }
        
        private void loadGroup(Group obj)
            throws IOException
        {
            loadNode(obj);

            final int childCount = readInt();
            for (int i = 0; i < childCount; ++i)
            {
                obj.addChild(readReference());
            }

            if (!isFileFormat1())
            {
                final boolean enable = readBoolean();
                final float hysteresis = readFloat();
                obj.setLODEnable(enable, hysteresis);
                obj.setLODOffset(readFloat());
            }
        }

        private void loadIndexBuffer(IndexBuffer obj)
            throws IOException
        {
            loadObject3D(obj);

            final int encoding = readUnsignedByte();
            final int indexType = encoding & 0x7f;
            final boolean explicit = (encoding & 0x80) != 0;

            int indexCount = 0;

            if (isFileFormat2())
            {
                throw new UnsupportedOperationException();
            }
            else
            {
                if (!(obj instanceof TriangleStripArray))
                {
                    throw new IOException("File format 1.0 only allows triangle strips");
                }
                //Bue to an oversight in the format, stripped index buffers
                //don't already know their index count though the sum of the
                //strip lengths would be that value.
                //Because the stripLengths are read after the indices in
                //format 1.0 we just have to set this to 0 and read it in
                //later.
                indexCount = 0;
            }

            int firstIndex = 0;
            final int[] explicitIndices;
            if (explicit)
            {
                //redundant deserialization of indexCount here is an
                //oversight in the format and not a bug in this deserializer.
                indexCount = readInt();
                explicitIndices = new int[indexCount];
            }
            else
            {
                explicitIndices = null;
            }

            switch (encoding)
            {
                case 0:
                {
                    firstIndex = readInt();
                    break;
                }
                case 1:
                {
                    firstIndex = readUnsignedByte();
                    break;
                }
                case 2:
                {
                    firstIndex = readUnsignedShort();
                    break;
                }
                case 128:
                {
                    for (int i = 0; i < indexCount; ++i)
                    {
                        explicitIndices[i] = readInt();
                    }
                    break;
                }
                case 129:
                {
                    for (int i = 0; i < indexCount; ++i)
                    {
                        explicitIndices[i] = readUnsignedByte();
                    }
                    break;
                }
                case 130:
                {
                    for (int i = 0; i < indexCount; ++i)
                    {
                        explicitIndices[i] = readUnsignedShort();
                    }
                    break;
                }
                case 192:
                {
                    if (!isFileFormat2())
                    {
                        throw new IOException("Unsupported index encoding");
                    }
                    throw new UnsupportedOperationException();
                }
                case 193:
                {
                    if (!isFileFormat2())
                    {
                        throw new IOException("Unsupported index encoding");
                    }
                    throw new UnsupportedOperationException();
                }
                case 194:
                {
                    if (!isFileFormat2())
                    {
                        throw new IOException("Unsupported index encoding");
                    }
                    throw new UnsupportedOperationException();
                }
                default:
                {
                    throw new IOException("Unsupported index encoding");
                }
            }

            if (explicit)
            {
                obj.setExplicitIndices(explicitIndices, indexCount);
            }
            else
            {
                obj.setImplicitIndex(firstIndex, indexCount);
            }
        }

        private void loadImage2D(Image2D obj)
            throws IOException
        {
            final boolean isMutable = loadImageBase(obj);

            if (!isMutable)
            {
                final int paletteLength = readInt();
                byte[] palette = null;
                if (paletteLength > 0)
                {
                    palette = new byte[paletteLength];
                    readFully(palette, 0, paletteLength);
                }
                final int basePixelsLength = readInt();
                final byte[] basePixels = new byte[basePixelsLength];
                readFully(basePixels, 0, basePixelsLength);

                if (palette != null)
                {
                    obj.set(0,
                            0, 0,
                            obj.getWidth(), obj.getHeight(),
                            palette, basePixels);
                }
                else
                {
                    obj.set(0,
                            0, 0,
                            obj.getWidth(), obj.getHeight(),
                            basePixels);
                }
                
                if (isFileFormat1())
                {
                    obj.createMipmapLevels();
                }
                else
                {
                    final int mipmapCount = readUnsignedByte();
                    if (mipmapCount == 0)
                    {
                        obj.createMipmapLevels();
                    }
                    else
                    {
                        //read in explicit mipmap levels
                        throw new UnsupportedOperationException();
                    }
                }

                obj.commit();
            }
        }

        private boolean loadImageBase(ImageBase obj)
            throws IOException
        {
            loadObject3D(obj);

            final int format;
            if (isFileFormat1())
            {
                //always make version 1.0 images lossless
                format = readUnsignedByte() | ImageBase.LOSSLESS;
            }
            else
            {
                format = readInt();
            }

            final boolean isMutable = readBoolean();

            final int width = readInt();
            final int height = readInt();

            obj.set(format, width, height, 1, true);

            return isMutable;
        }

        private void loadLight(Light obj)
            throws IOException
        {
            loadNode(obj);

            final float constant = readFloat();
            final float linear = readFloat();
            final float quadratic = readFloat();

            obj.setAttenuation(constant, linear, quadratic);
            obj.setColor(readRGBasARGB());
            obj.setMode(readUnsignedByte());
            obj.setIntensity(readFloat());
            obj.setSpotAngle(readFloat());
            obj.setSpotExponent(readFloat());
        }

        private void loadMaterial(Material obj)
            throws IOException
        {
            loadObject3D(obj);

            obj.setColor(Material.AMBIENT, readRGBasARGB());
            obj.setColor(Material.DIFFUSE, readRGBAasARGB());
            obj.setColor(Material.EMISSIVE, readRGBasARGB());
            obj.setColor(Material.SPECULAR, readRGBasARGB());
            obj.setShininess(readFloat());
            obj.setVertexColorTrackingEnabled(readBoolean());
        }

        private void loadMesh(Mesh obj)
            throws IOException
        {
            loadNode(obj);

            obj.setVertexBuffer(readReference());

            final int submeshCount = readInt();

            obj.setSubmeshCount(submeshCount);

            for (int i = 0; i < submeshCount; ++i)
            {
                obj.setIndexBuffer(i,readReference());
                AppearanceBase appearance = readReference();
                if (appearance instanceof Appearance)
                {
                    obj.setAppearance(i, (Appearance) appearance);
                }
                else
                {
                    obj.setShaderAppearance(i, (ShaderAppearance) appearance);
                }
            }

            if (isFileFormat2())
            {
                throw new UnsupportedOperationException();
            }
        }

        @SuppressWarnings("deprecation")
        private void loadSkinnedMesh(SkinnedMesh obj)
            throws IOException
        {
            loadMesh(obj);
            
            obj.setSkeleton(readReference());
            
            boolean isLegacy = true;
            if (isFileFormat2())
            {
                isLegacy = readBoolean();
            }
            
            obj.setLegacy(isLegacy);
            
            final int boneCount = readInt();
            final Node[] bones = new Node[boneCount];
            for (int i = 0; i < boneCount; ++i)
            {
                bones[i] = readReference();
                if (isLegacy)
                {
                    int firstVertex = readInt();
                    int numVertices = readInt();
                    int weight = readInt();
                    obj.addTransform(bones[i], weight, firstVertex, numVertices);
                }
            }
            
            if (!isLegacy)
            {
                obj.setBones(bones);
            }
        }
        
        private void loadNode(Node obj)
            throws IOException
        {
            loadTransformable(obj);

            obj.setRenderingEnabled(readBoolean());
            obj.setPickingEnabled(readBoolean());
            obj.setAlphaFactor(readUnsignedByte() / 255.0f);
            obj.setScope(readInt());

            final boolean hasAlignment = readBoolean();

            if (hasAlignment)
            {
                final int zTarget = readUnsignedByte();
                final int yTarget = readUnsignedByte();

                final int zReferenceIndex = readInt();
                final int yReferenceIndex = readInt();

                //TODO link up the references
                throw new UnsupportedOperationException();
            }

            if (isFileFormat2())
            {
                //TODO bounding volumes etc
                throw new UnsupportedOperationException();
            }
        }

        private void loadObject3D(Object3D obj)
            throws IOException
        {
            obj.setUserID(readInt());

            final int animationTrackCount = readInt();
            for (int i = 0; i < animationTrackCount; ++i)
            {
                AnimationTrack animationTrack = readReference();
                int channel = 0;
                if (isFileFormat2())
                {
                    channel = readInt();
                }
                obj.addAnimationTrack(animationTrack, channel);
            }

            final int userParameterCount = readInt();
            if (userParameterCount > 0)
            {
                Hashtable<Integer, byte[]> userObject =
                        new Hashtable<Integer, byte[]>();
                for (int i = 0; i < userParameterCount; ++i)
                {
                    final int parameterID = readInt();
                    final int parameterValueLength = readInt();
                    final byte[] parameterValue = new byte[parameterValueLength];
                    readFully(parameterValue, 0, parameterValueLength);
                    userObject.put(Integer.valueOf(parameterID),
                            parameterValue);
                }
                obj.setUserObject(userObject);
            }
            else
            {
                obj.setUserObject(null);
            }

            if (isFileFormat2())
            {
                obj.setAnimationEnabled(readBoolean());
            }
        }

        private void loadPolygonMode(PolygonMode obj)
            throws IOException
        {
            loadObject3D(obj);

            obj.setCulling(readUnsignedByte());
            obj.setShading(readUnsignedByte());
            obj.setWinding(readUnsignedByte());
            obj.setTwoSidedLightingEnabled(readBoolean());
            obj.setLocalCameraLightingEnabled(readBoolean());
            obj.setPerspectiveCorrectionEnabled(readBoolean());

            if (!isFileFormat1())
            {
                obj.setLineWidth(readFloat());
            }
        }

        private void loadTexture(Texture obj)
            throws IOException
        {
            loadTransformable(obj);

            obj.setImageBase(readReference());

            if (!isFileFormat1())
            {
                final int levelFilter = readUnsignedByte();
                final int imageFilter = readUnsignedByte();
                obj.setFiltering(levelFilter, imageFilter);
            }
        }

        private void loadTexture2D(Texture2D obj)
            throws IOException
        {
            loadTexture(obj);

            final int blendColor;
            if (isFileFormat1())
            {
                blendColor = readRGBasARGB();
            }
            else
            {
                blendColor = readRGBAasARGB();
            }
            obj.setBlendColor(blendColor);
            obj.setBlending(readUnsignedByte());
            final int wrappingS = readUnsignedByte();
            final int wrappingT = readUnsignedByte();
            obj.setWrapping(wrappingS, wrappingT);

            if (isFileFormat1())
            {
                final int levelFilter = readUnsignedByte();
                final int imageFilter = readUnsignedByte();
                obj.setFiltering(levelFilter, imageFilter);
            }
            else
            {
                obj.setCombiner(readReference());
            }
        }

        private void loadKeyframeSequence(KeyframeSequence obj)
            throws IOException
        {
            loadObject3D(obj);
            
            int interpolation = readUnsignedByte();
            int repeatMode = readUnsignedByte();
            int encoding = readUnsignedByte();
            int duration = readInt();
            int validRangeFirst = readInt();
            int validRangeLast = readInt();
            
            int componentCount = readInt();
            int keyframeCount = readInt();

            int channelCount = 1;
            if (isFileFormat2())
            {
                channelCount = readInt();
            }
            
            obj.set(channelCount, keyframeCount, componentCount, interpolation);
            obj.setDuration(duration);
            obj.setRepeatMode(repeatMode);
            obj.setValidRange(validRangeFirst, validRangeLast);
            
            if (encoding == 0)
            {
                // raw float keyframes
                
                float values[] = new float[componentCount];
                for (int k = 0; k < keyframeCount; ++k)
                {
                    final int time = readInt();
                    obj.setKeyframeTime(k, time);
                    for (int c = 0; c < channelCount; ++c)
                    {
                        readFully(values, 0, componentCount);
                        obj.setKeyframeValue(c, k, values);
                    }
                }
            }
            else if (encoding == 1)
            {
                // scale and bias compressed byte keyframes
                float bias[] = new float[componentCount];
                float scale[] = new float[componentCount];
                readFully(bias, 0, componentCount);
                readFully(scale, 0, componentCount);
                
                float values[] = new float[componentCount];
                for (int k = 0; k < keyframeCount; ++k)
                {
                    final int time = readInt();
                    obj.setKeyframeTime(k, time);
                    for (int c = 0; c < channelCount; ++c)
                    {
                        for (int i = 0; i < componentCount; ++i)
                        {
                            float v = bias[i] + scale[i]
                                    * readUnsignedByte() / 255.0f;
                            values[i] = v;
                        }
                        obj.setKeyframeValue(c, k, values);
                    }
                }
            }
            else if (encoding == 2)
            {
                // scale and bias compressed short keyframes
                float bias[] = new float[componentCount];
                float scale[] = new float[componentCount];
                readFully(bias, 0, componentCount);
                readFully(scale, 0, componentCount);
                
                float values[] = new float[componentCount];
                for (int k = 0; k < keyframeCount; ++k)
                {
                    final int time = readInt();
                    obj.setKeyframeTime(k, time);
                    for (int c = 0; c < channelCount; ++c)
                    {
                        for (int i = 0; i < componentCount; ++i)
                        {
                            float v = bias[i] + scale[i]
                                    * readUnsignedShort() / 65535.0f;
                            values[i] = v;
                        }
                        obj.setKeyframeValue(c, k, values);
                    }
                }
            }
            else 
            {
                throw new IOException("unsupported KeyframeSequence encoding "
                        + encoding);
            }
            
            if (isFileFormat2())
            {
                final int eventCount = readInt();
                final int eventTimes[] = new int[eventCount];
                final int eventIDs[] = new int[eventCount];
                for (int i = 0; i < eventCount; ++i)
                {
                    eventTimes[i] = readInt();
                    eventIDs[i] = readInt();
                }
            }
        }
        
        private void loadTransformable(Transformable obj)
            throws IOException
        {
            loadObject3D(obj);

            final boolean hasComponentTransform = readBoolean();

            if (hasComponentTransform)
            {
                final float tx = readFloat();
                final float ty = readFloat();
                final float tz = readFloat();

                obj.setTranslation(tx, ty, tz);

                final float sx = readFloat();
                final float sy = readFloat();
                final float sz = readFloat();

                obj.setScale(sx, sy, sz);

                final float angle = readFloat();
                final float ax = readFloat();
                final float ay = readFloat();
                final float az = readFloat();

                obj.setOrientation(angle, ax, ay, az);
            }

            final boolean hasGeneralTransform = readBoolean();

            if (hasGeneralTransform)
            {
                final float[] matrix = new float[16];
                for (int i = 0; i < 16; ++i)
                {
                    matrix[i] = readFloat();
                }
                final Transform transform = new Transform();
                transform.set(matrix);

                obj.setTransform(transform);
            }
        }

        private void loadTriangleStripArray(TriangleStripArray obj)
            throws IOException
        {
            loadIndexBuffer(obj);

            if (isFileFormat1())
            {
                final int stripLengthsCount = readInt();
                final int[] stripLengths = new int[stripLengthsCount];
                for (int i = 0; i < stripLengthsCount; ++i)
                {
                    stripLengths[i] = readInt();
                }

                obj.setStripLengths(stripLengths);
            }
        }

        private void loadVertexArray(VertexArray obj)
            throws IOException
        {
            loadObject3D(obj);

            final int componentType = readUnsignedByte();
            final int componentCount = readUnsignedByte();
            final int encoding = readUnsignedByte();

            final int vertexCount = readUnsignedShort();

            obj.set(vertexCount, componentCount, componentType);

            //read the data

            switch (componentType)
            {
                case VertexArray.BYTE:
                {
                    if (encoding == 0)
                    {
                        final byte[] components = new byte[componentCount];
                        for (int i = 0; i < vertexCount; ++i)
                        {
                            for (int c = 0; c < componentCount; ++c)
                            {
                                components[c] = readByte();
                            }
                            obj.set(i, 1, components);
                        }
                    }
                    else
                    {
                        throw new UnsupportedOperationException();
                    }
                    break;
                }
                case VertexArray.SHORT:
                {
                    if (encoding == 0)
                    {
                        final short[] components = new short[componentCount];
                        for (int i = 0; i < vertexCount; ++i)
                        {
                            for (int c = 0; c < componentCount; ++c)
                            {
                                components[c] = readShort();
                            }
                            obj.set(i, 1, components);
                        }
                    }
                    else
                    {
                        throw new UnsupportedOperationException();
                    }
                    break;
                }
                case VertexArray.FIXED:
                {
                    if (!isFileFormat2())
                    {
                        throw new IOException("FIXED type requires version >= 2.0");
                    }

                    if (encoding == 0)
                    {
                        final int[] components = new int[componentCount];
                        for (int i = 0; i < vertexCount; ++i)
                        {
                            for (int c = 0; c < componentCount; ++c)
                            {
                                components[c] = readInt();
                            }
                            obj.set(i, 1, components);
                        }
                    }
                    else
                    {
                        throw new UnsupportedOperationException();
                    }
                    break;
                }
                case VertexArray.HALF:
                {
                    if (!isFileFormat2())
                    {
                        throw new IOException("HALF type requires version >= 2.0");
                    }

                    throw new UnsupportedOperationException();
                }
                case VertexArray.FLOAT:
                {
                    if (!isFileFormat2())
                    {
                        throw new IOException("FLOAT type requires version >= 2.0");
                    }

                    if (encoding == 0)
                    {
                        final float[] components = new float[componentCount];
                        for (int i = 0; i < vertexCount; ++i)
                        {
                            for (int c = 0; c < componentCount; ++c)
                            {
                                components[c] = readFloat();
                            }
                            obj.set(i, 1, components);
                        }
                    }
                    else
                    {
                        throw new UnsupportedOperationException();
                    }
                    break;
                }
                default:
                {
                    throw new UnsupportedOperationException();
                }
            }
        }

        private void loadVertexBuffer(VertexBuffer obj)
            throws IOException
        {
            loadObject3D(obj);
            
            final float[] bias = new float[3];

            obj.setDefaultColor(readRGBAasARGB());

            {
                VertexArray positions = readReference();
                final float[] positionBias = bias;
                for (int i = 0; i < 3; ++i)
                {
                    positionBias[i] = readFloat();
                }
                final float positionScale = readFloat();
                obj.setPositions(positions, positionScale, positionBias);
            }

            {
                final VertexArray normals = readReference();
                obj.setNormals(normals);
            }

            {
                final VertexArray colors = readReference();
                obj.setColors(colors);
            }

            final int texcoordArrayCount = readInt();
            for (int unit = 0; unit < texcoordArrayCount; ++unit)
            {
                VertexArray texCoords = readReference();
                final float[] texCoordBias = bias;
                for (int i = 0; i < 3; ++i)
                {
                    texCoordBias[i] = readFloat();
                }
                final float texCoordScale = readFloat();
                obj.setTexCoords(unit, texCoords, texCoordScale, texCoordBias);
            }

            if (isFileFormat2())
            {
                throw new UnsupportedOperationException();
            }
        }

        private void loadWorld(World obj)
            throws IOException
        {
            loadGroup(obj);

            obj.setActiveCamera(readReference());
            obj.setBackground(readReference());
        }

        private Object3D[] getRootObjects()
        {
            final int rootObjectCount = rootObjects.size();
            Object3D[] ret = new Object3D[rootObjectCount];
            return rootObjects.toArray(ret);
        }
    }
    
    /**
     * Static utility class
     */
    private Loader()
    {
        
    }

    @Deprecated
    public static final Object3D[] load(byte[] data, int offset)
        throws IOException
    {
        Require.notNull(data, "stream");
        Require.indexInRange(offset, data.length);

        final InputStream stream = new ByteArrayInputStream(data);
        stream.skip(offset);
        return load(stream);
    }

    public static final Object3D[] load(InputStream stream)
        throws IOException
    {
        return load(stream, null);
    }

    public static final Object3D[] load(InputStream stream,
            Object3D[] referenceObjects)
        throws IOException
    {
        Require.notNull(stream, "stream");

        M3GDeserializer deserializer = new M3GDeserializer(stream);

        deserializer.checkIdentifier();
        
        deserializer.setInternalReferenceObjects(referenceObjects);
        
        deserializer.load();

        return deserializer.getRootObjects();
    }

    public static final Object3D[] load(String name)
        throws IOException
    {
        return load(name, null);
    }

    public static final Object3D[] load(String name, Object3D[] referenceObjects)
        throws IOException
    {
        Require.notNull(name, "name");
        
        //TODO verify that this is the correct class loader
        final ClassLoader cl = Loader.class.getClassLoader();
        final InputStream is = cl.getResourceAsStream(name);

        try
        {
            return load(is, referenceObjects);
        }
        finally
        {
            is.close();
        }
    }

    public static final ImageBase loadImage(int format, InputStream stream)
        throws IOException
    {
        Require.notNull(stream, "stream");
        
        BufferedImage bufferedImage = ImageIO.read(stream);
        if (bufferedImage == null)
        {
            throw new IOException("unable to load image");
        }

        //TODO properly decode the image data with regards to format

        ImageBase image = new Image2D(format, bufferedImage);

        return image;
    }

    public static final ImageBase loadImage(int format, String name)
        throws IOException
    {
        Require.notNull(name, "name");

        //TODO verify that this is the correct class loader
        final ClassLoader cl = Loader.class.getClassLoader();
        final InputStream is = cl.getResourceAsStream(name);

        try
        {
            return loadImage(format, is);
        }
        finally
        {
            is.close();
        }
    }
}
