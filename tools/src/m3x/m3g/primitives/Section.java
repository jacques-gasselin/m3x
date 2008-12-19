package m3x.m3g.primitives;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;
import m3x.m3g.primitives.Serializable;
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
    /**
     * Enum for this.objects being not compressed.
     */
    public static final int COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 = 0;
    /**
     * Enum for this.objects being zlib compressed.
     */
    public static final int COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32 = 1;
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

    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        this.compressionScheme = deserialiser.readUnsignedByte();
        if (this.compressionScheme != COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 &&
            this.compressionScheme != COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
        {
            throw new IllegalStateException("Invalid compression scheme: " + this.compressionScheme);
        }

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
        byte[] objectsAsBytes = new byte[this.uncompressedLength];
        if (this.compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
        {
            int compressedLength = this.totalSectionLength - 1 - 4 - 4 - 4;
            byte[] compressedData = new byte[compressedLength];
            deserialiser.readFully(compressedData);
            //this.calculateChecksum(compressedData);
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);
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
            //this.calculateChecksum(objectsAsBytes);
        }

        int checksumFromStream = deserialiser.readInt();
        /*if (this.checksum != checksumFromStream)
        {
            throw new IllegalStateException("Invalid checksum, was " + checksumFromStream + ", should have been " + checksum);
        }*/

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
                    Object3D obj = (Object3D)ObjectFactory.getInstance(objectType);
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
                    throw new IllegalStateException(e);
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
    public void serialize(Serialiser serialiser, Serializable[] objects)
        throws IOException
    {
        //serialise the objects to a byte array
        ByteArrayOutputStream objectStream = new ByteArrayOutputStream();
        serialiser.pushOutputStream(objectStream);
        for (Serializable obj : objects)
        {
            obj.serialize(serialiser);
        }
        serialiser.popOutputStream();
        byte[] objectData = objectStream.toByteArray();
        this.uncompressedLength = objectData.length;
        //compress the data if needed.
        if (compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
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
                this.compressionScheme = COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32;
            }
        }
        this.totalSectionLength = objectData.length + 1 + 4 + 4 + 4;
        //write out the section
        Adler32 adler = new Adler32();
        serialiser.startChecksum(adler);
        serialiser.write(this.compressionScheme);
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

    public Section(int compressionScheme)
    {
        super();
        this.compressionScheme = compressionScheme;

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
