/**
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

package m3x.m3g.primitives;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import m3x.m3g.Deserializer;
import m3x.m3g.Serializer;
import m3x.m3g.Object3D;


/**
 * See http://www.java2me.org/m3g/file-format.html#Section
 * for more information. This class model's the Section object
 * in the M3G file format.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public class Section
{
    private static final boolean DEBUG_SERIALIZE = false;

    /**
     * Enum for this.objects being not compressed.
     */
    public static final int UNCOMPRESSED_ADLER32 = 0;
    /**
     * Enum for this.objects being zlib compressed.
     */
    public static final int ZLIB_32K_COMPRESSED_ADLER32 = 1;
    /**
     * Either of the previous two "enums".
     */
    private int compressionScheme;
    /**
     * Total length of the section data in bytes. Includes all fields.
     */
    private int totalSectionLength;
    /**
     * The uncompressed length of this.objects.
     */
    private int uncompressedLength;
    /**
     * Adler32 checksum of the section.
     */
    private int checksum;

    /**
     * Creates a new Section object.
     *
     * @param compressionScheme
     *  Whether to compress or not?
     * @throws IOException
     */
    public Section(int compressionScheme)
    {
        verifyCompressionScheme(compressionScheme);
        this.compressionScheme = compressionScheme;
    }

    private void verifyCompressionScheme(int compressionScheme)
    {
        if (compressionScheme != UNCOMPRESSED_ADLER32 &&
            compressionScheme != ZLIB_32K_COMPRESSED_ADLER32)
        {
            throw new IllegalArgumentException("invalid compression scheme");
        }
    }

    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        this.compressionScheme = deserialiser.readUnsignedByte();
        verifyCompressionScheme(compressionScheme);
        this.totalSectionLength = deserialiser.readInt();
        if (this.totalSectionLength <= 0)
        {
            throw new IllegalStateException("Invalid total section length: " + this.totalSectionLength);
        }
        this.uncompressedLength = deserialiser.readInt();
        if (this.uncompressedLength <= 0)
        {
            throw new IllegalStateException("Invalid uncompressed length: " + this.uncompressedLength);
        }

        // read Section.objects bytes
        final int compressedLength = this.totalSectionLength - 1 - 4 - 4 - 4;
        byte[] objectData = new byte[this.uncompressedLength];
        if (this.compressionScheme == ZLIB_32K_COMPRESSED_ADLER32)
        {
            byte[] compressedData = new byte[compressedLength];
            deserialiser.readFully(compressedData);
            //this.calculateChecksum(compressedData);
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);
            try
            {
                final int n = inflater.inflate(objectData);
                if (n != this.uncompressedLength)
                {
                    throw new IOException("Decompression failed!");
                }
            }
            catch (DataFormatException e)
            {
                throw new IOException(e.toString());
            }
            inflater.end();
        }
        else
        {
            if (compressedLength != uncompressedLength)
            {
                throw new IllegalStateException("uncompressed length is not section length - 13");
            }
            // uncompressed, just read the array
            deserialiser.readFully(objectData);
            //this.calculateChecksum(objectsAsBytes);
        }

        final int checksumFromStream = deserialiser.readInt();
        /*if (this.checksum != checksumFromStream)
        {
            throw new IllegalStateException("Invalid checksum, was " + checksumFromStream + ", should have been " + checksum);
        }*/

        // build ObjectChunk[] this.objectsAsBytes
        deserialiser.pushInputStream(new ByteArrayInputStream(objectData));
        while (true)
        {
            try
            {
                final int objectType = deserialiser.readUnsignedByte();
                final int length = deserialiser.readInt();
                Serializable obj = ObjectFactory.getInstance(objectType);
                if (DEBUG_SERIALIZE)
                {
                    System.err.printf("Deserialized %s: type %d of length %d\n",
                            obj.getClass().getSimpleName(), objectType, length);
                    System.err.flush();
                }
                obj.deserialise(deserialiser);
                if (objectType == 0)
                {
                    //add a null object instead of the Header object.
                    deserialiser.addObject(null);
                }
                else
                {
                    deserialiser.addObject((Object3D)obj);
                }
            }
            catch (EOFException e)
            {
                // end of stream, quit deserialization
                break;
            }
        }
        deserialiser.popInputStream();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof Section))
        {
            return false;
        }
        Section another = (Section) obj;
        return this.compressionScheme == another.compressionScheme &&
            this.totalSectionLength == another.totalSectionLength &&
            this.uncompressedLength == another.uncompressedLength &&
            this.checksum == another.checksum;
    }

    /**
     * See http://www.java2me.org/m3g/file-format.html#Section for more
     * information about how serialization is done. Basically all
     * fields of this object are written into the output stream,
     * integers are converted into little endian format.
     */
    public void serialise(Serializer serialiser, SectionSerializable[] objects)
        throws IOException
    {
        //serialise the objects to a byte array
        ByteArrayOutputStream objectStream = new ByteArrayOutputStream();
        serialiser.pushOutputStream(objectStream);
        {
            final ByteArrayOutputStream perObjectStream = new ByteArrayOutputStream();
            final BufferedOutputStream bufferedPerObjectStream = new BufferedOutputStream(perObjectStream);
            //wrap each object in a SectionObject
            for (SectionSerializable obj : objects)
            {
                perObjectStream.reset();
                serialiser.pushOutputStream(bufferedPerObjectStream);
                obj.serialise(serialiser);
                serialiser.popOutputStream();
                perObjectStream.flush();
                byte[] objData = perObjectStream.toByteArray();

                //write out the object type and the length
                //Byte          ObjectType
                //UInt32        Length
                //Byte[Length]  Data
                final int objectType = obj.getSectionObjectType();
                final int length = objData.length;
                if (DEBUG_SERIALIZE)
                {
                    System.err.printf("Serialized %s: type %d of length %d\n",
                            obj.getClass().getSimpleName(), objectType, length);
                    System.err.flush();
                }
                serialiser.writeUnsignedByte(objectType);
                serialiser.writeInt(length);
                serialiser.write(objData);
                serialiser.addObject(obj);
            }
        }
        serialiser.popOutputStream();
        byte[] objectData = objectStream.toByteArray();
        this.uncompressedLength = objectData.length;
        //compress the data if needed.
        if (compressionScheme == ZLIB_32K_COMPRESSED_ADLER32)
        {
            // Compress the bytes
            Deflater compresser = new Deflater();
            compresser.setInput(objectData);
            compresser.finish();
            byte[] compressedData = new byte[objectData.length];
            int compressedDataLength = compresser.deflate(compressedData);
            if (compressedDataLength < this.uncompressedLength)
            {
                //compression saved space. Use it
                objectData = compressedData;
            }
            else
            {
                //compression did not save space. Don't use it
                //set the section to uncompressed
                this.compressionScheme = UNCOMPRESSED_ADLER32;
            }
        }
        this.totalSectionLength = objectData.length + 1 + 4 + 4 + 4;
        //write out the section
        Adler32 adler = new Adler32();
        serialiser.startChecksum(adler);
        serialiser.writeUnsignedByte(this.compressionScheme);
        serialiser.writeInt(this.totalSectionLength);
        serialiser.writeInt(this.uncompressedLength);
        serialiser.write(objectData);
        serialiser.endChecksum();
        serialiser.writeInt((int)adler.getValue());
    }

    public Section()
    {
        super();
    }

    public int getCompressionScheme()
    {
        return this.compressionScheme;
    }

    public int getTotalSectionLength()
    {
        return this.totalSectionLength;
    }

    public int getUncompressedLength()
    {
        return this.uncompressedLength;
    }
}
