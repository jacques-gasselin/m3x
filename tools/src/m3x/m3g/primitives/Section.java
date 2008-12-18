package m3x.m3g.primitives;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import m3x.m3g.FileFormatException;
import m3x.m3g.M3GDeserialiser;
import m3x.m3g.M3GException;
import m3x.m3g.M3GObject;
import m3x.m3g.M3GObjectFactory;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.Object3D;


/**
 * See http://www.java2me.org/m3g/file-format.html#Section
 * for more information. This class model's the Section object
 * in the M3G file format.
 * 
 * @author jsaarinen
 */
public class Section implements M3GSerializable
{
    /**
     * Enum for this.objects being not compressed.
     */
    public static final byte COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 = 0;
    /**
     * Enum for this.objects being zlib compressed.
     */
    public static final byte COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32 = 1;
    /**
     * Either of the previous two "enums".
     */
    private byte compressionScheme;
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
     * If compression scheme was zlib, this array will contain the compressed bytes,
     * otherwise it is null.
     */
    private byte[] compressedData;

    /**
     * Creates a new Section object.
     *
     * @param compressionScheme
     *  Whether to compress or not?
     * @throws IOException
     */
    public Section(byte compressionScheme)
    {
        if (compressionScheme != COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 &&
            compressionScheme != COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
        {
            throw new IllegalArgumentException("invalid compression scheme");
        }
        this.compressionScheme = compressionScheme;
    }

    /**
     * Serializes a list of objects to a byte array.
     *
     * @param m3gVersion
     * @return
     * @throws IOException
     */
    private byte[] serialize(String m3gVersion) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        /*for (M3GSerializable object : this.objects)
        {
            object.serialize(dos, m3gVersion);
        }*/
        dos.close();
        byte[] serializedBytes = baos.toByteArray();
        this.uncompressedLength = serializedBytes.length;
        return serializedBytes;
    }

    /**
     * Compresses ObjectChunk objects one at a time and returns the data in array
     *
     * @param m3gVersion
     * @return
     *  The compressed data block.
     * @throws IOException
     *  When compression failed for some reason.
     */
    private byte[] serializeAndCompress(String m3gVersion)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(baos);
        /*for (M3GSerializable object : this.objects)
        {
            // compress one object at a time
            byte[] serializedObject = M3GSupport.objectToBytes(object);
            dos.write(serializedObject, 0, serializedObject.length);
        }*/
        dos.close();
        byte[] serializedBytes = baos.toByteArray();
        return serializedBytes;
    }

    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        this.compressionScheme = deserialiser.readByte();
        if (this.compressionScheme != COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 &&
            this.compressionScheme != COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
        {
            throw new FileFormatException("Invalid compression scheme: " + this.compressionScheme);
        }

        this.totalSectionLength = deserialiser.readInt();
        if (this.totalSectionLength <= 0)
        {
            throw new FileFormatException("Invalid total section length: " + this.totalSectionLength);
        }

        this.uncompressedLength = deserialiser.readInt();
        if (this.uncompressedLength <= 0)
        {
            throw new FileFormatException("Invalid uncompressed length: " + this.uncompressedLength);
        }

        // read Section.objects bytes
        byte[] objectsAsBytes = new byte[this.uncompressedLength];
        if (this.compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
        {
            int compressedLength = this.totalSectionLength - 1 - 4 - 4 - 4;
            this.compressedData = new byte[compressedLength];
            deserialiser.readFully(this.compressedData);
            this.calculateChecksum(this.compressedData);
            Inflater inflater = new Inflater();
            inflater.setInput(this.compressedData);
            try
            {
                int n = inflater.inflate(objectsAsBytes);
                if (n != objectsAsBytes.length)
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
            // uncompressed, just read the array
            deserialiser.readFully(objectsAsBytes);
            this.calculateChecksum(objectsAsBytes);
        }

        int checksumFromStream = deserialiser.readInt();
        if (this.checksum != checksumFromStream)
        {
            throw new FileFormatException("Invalid checksum, was " + checksumFromStream + ", should have been " + checksum);
        }

        // build ObjectChunk[] this.objectsAsBytes
        deserialiser.pushInputStream(new ByteArrayInputStream(objectsAsBytes));
        while (true)
        {
            try
            {
                final int objectType = deserialiser.readUnsignedByte();
                final int length = deserialiser.readInt();
                try
                {
                    Object3D obj = (Object3D)M3GObjectFactory.getInstance(objectType);
                    obj.deserialize(deserialiser);
                    if (objectType == 0)
                    {
                        //add a null object instead of the Header object.
                        deserialiser.addObject(null);
                    }
                    else
                    {
                        deserialiser.addObject(obj);
                    }
                }
                catch (IllegalArgumentException e)
                {
                    throw new FileFormatException(e);
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
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
    {
        dataOutputStream.write(this.compressionScheme);
        M3GSupport.writeInt(dataOutputStream, this.totalSectionLength);
        byte[] objectsAsBytes;
        if (this.compressionScheme == COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32)
        {
            // don't compress, just serialize
            objectsAsBytes = this.serialize(m3gVersion);
        }
        else
        {
            // if the data hasn't been compressed yet, compress it now
            // otherwise use the cached reference to compressed data
            // (in this case compression was done in the constructor
            if (this.compressedData == null)
            {
                this.compressedData = this.serializeAndCompress(M3GObject.M3G_VERSION);
            }
            objectsAsBytes = this.compressedData;
        }
        this.uncompressedLength = 0;
        /*for (ObjectChunk objectChunk : this.objects)
        {
            this.uncompressedLength += objectChunk.getTotalLength();
        }*/
        M3GSupport.writeInt(dataOutputStream, this.uncompressedLength);
        dataOutputStream.write(objectsAsBytes);
        this.calculateChecksum(objectsAsBytes);
        M3GSupport.writeInt(dataOutputStream, this.checksum);
    }

    /**
     * Calculates the Adler 32 checksum from this object's data.
     *
     * @return
     *  32 bits of Adler 32 checksum.
     *
     * @param objects
     *  The array containing compressed or uncompressed object(s) data
     * @throws IOException
     */
    private void calculateChecksum(byte[] objects) throws IOException
    {
        Adler32 adler32 = new Adler32();
        adler32.update(this.compressionScheme);
        adler32.update(M3GSupport.intToBytes(this.totalSectionLength));
        adler32.update(M3GSupport.intToBytes(this.uncompressedLength));
        adler32.update(objects);
        this.checksum = (int) adler32.getValue();
    }

    public Section()
    {
        super();
    }

    public byte getCompressionScheme()
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
