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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.imageio.ImageIO;

/**
 * @author jgasseli
 */
public final class Loader
{
    private static final class M3GDeserializer
    {
        private final ArrayList<Object3D> references = new ArrayList<Object3D>();
        private final ArrayList<Object3D> rootObjects = new ArrayList<Object3D>();
        private final ArrayList<InputStream> streamStack = new ArrayList<InputStream>();
        private InputStream currentStream;
        private Object3D[] internalReferenceObjects;

        M3GDeserializer(InputStream stream)
        {
            push(stream);
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
         * @throws EOFExcpetion if the stream is at EOF before the read.
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

        /**
         * Reads and decodes a reference to an already deserialized object.
         * @return the reference at the read index, or null if 0
         * @throws IOException if the stream hits EOF or any other error
         */
        Object3D readReference() throws IOException
        {
            final int index = readInt();
            Object3D ret = references.get(index - 2);
            rootObjects.remove(ret);
            return ret;
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
            return readChecked() & 0xff;
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
         * Checks the stream for the prescence of the M3G file identifier.
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
                final int compressionScheme = read();
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

                loadSection(section);

                pop();
            }
        }

        void loadSection(int sectionNumber)
        {
            throw new UnsupportedOperationException();
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
