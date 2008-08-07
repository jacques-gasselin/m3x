package m3x.m3g.primitives;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;


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
   * The actual contents in this section
   */
  private ObjectChunk[] objects;
    
  /**
   * ObjectChunks serialized, possibly compressed
   */
  private byte[] objectsAsBytes;
  
  /**
   * Creates a new Section object from ObjectChunks. A Section
   * contains 1..N ObjectChunks.
   * 
   * @param compressionScheme
   *  Whether to compress or not?
   *  
   * @param m3gObjects
   *  The actual data in this section.
   */
  public Section(byte compressionScheme, ObjectChunk[] objects)
  {
    assert(compressionScheme == COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 || compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32);
    assert(objects != null);
    
    this.compressionScheme = compressionScheme;
    
    // loop through object chunks to calculate length
    this.uncompressedLength = 0;
    for (ObjectChunk objectChunk : objects)
    {
      this.uncompressedLength += objectChunk.getLength();
    }
    
    this.objects = objects;
    
    // length of a section is compression scheme, total section length
    // uncompressed length, objects array length and Adler32 checksum
    this.totalSectionLength = 1 + 4 + 4 + this.uncompressedLength + 4;
  }
  
  /**
   * Serializes a list of objects to a byte array.
   * 
   * @param m3gObjects
   * @param m3gVersion
   * @return
   * @throws IOException
   */
  private byte[] serialize(M3GSerializable[] m3gObjects, String m3gVersion) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(this.uncompressedLength);
    DataOutputStream dos = new DataOutputStream(baos);
    for (M3GSerializable object : m3gObjects)
    {
      object.serialize(dos, m3gVersion);
    }
    dos.close();
    return baos.toByteArray();
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
    for (M3GSerializable object : this.objects)
    {
      // compress one object at a time
      byte[] serializedObject = M3GSupport.objectToBytes(object);
      dos.write(serializedObject, 0, serializedObject.length);
    }
    dos.close();
    return baos.toByteArray();
  }
  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    this.compressionScheme = dataInputStream.readByte();
    if (this.compressionScheme != COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 &&
        this.compressionScheme != COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
    {
      throw new FileFormatException("Invalid compression scheme: " + this.compressionScheme);
    }
    
    this.totalSectionLength = M3GSupport.readInt(dataInputStream);
    if (this.totalSectionLength <= 0)
    {
      throw new FileFormatException("Invalid total section length: " + this.totalSectionLength);
    }
    
    this.uncompressedLength = M3GSupport.readInt(dataInputStream);
    if (this.uncompressedLength <= 0)
    {
      throw new FileFormatException("Invalid uncompressed length: " + this.uncompressedLength);
    }
    
    // read 
    this.objectsAsBytes = new byte[this.uncompressedLength];
    int objectsLengthInBytes = this.totalSectionLength - 1 - 4 - 4 - 4;
    int checksum;
    if (this.compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
    {
      byte[] compressedObjects = new byte[objectsLengthInBytes];
      dataInputStream.read(compressedObjects);
      checksum = this.calculateChecksum(compressedObjects);
      Inflater inflater = new Inflater();
      inflater.setInput(compressedObjects);
      try
      {
        inflater.inflate(this.objectsAsBytes);
      }
      catch (DataFormatException e)
      {
        //avoiding Java SE 6 style Throwable copy constructor
        IOException ioe = new IOException(e.toString());
        ioe.initCause(e);
        throw ioe;
      }
      finally
      {
        inflater.end();
      }
    }
    else
    {
      // uncompressed, just read the array
      dataInputStream.read(this.objectsAsBytes);
      checksum = this.calculateChecksum(this.objectsAsBytes);
    }
    
    int checksumFromStream = M3GSupport.readInt(dataInputStream);
    if (checksum != checksumFromStream)
    {
      throw new FileFormatException("Invalid checksum, was " + checksumFromStream + ", should have been " + checksum);
    }
    
    // build ObjectChunk[] this.objectsAsBytes
    List<ObjectChunk> list = new ArrayList<ObjectChunk>();
    dataInputStream = new DataInputStream(new ByteArrayInputStream(this.objectsAsBytes));
    while (true)
    {
      try
      {
        ObjectChunk objectChunk = new ObjectChunk();
        objectChunk.deserialize(dataInputStream, m3gVersion);
        list.add(objectChunk);
      }
      catch (EOFException e)
      {
        // end of stream, quit deserialization
        break;
      }
    }
    this.objects = list.toArray(new ObjectChunk[list.size()]);
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
    M3GSupport.writeInt(dataOutputStream, this.uncompressedLength);
    byte[] objectsAsBytes;
    if (this.compressionScheme == COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32)
    {
      // don't compress, just serialize
      objectsAsBytes = this.serialize(this.objects, m3gVersion);
    }
    else
    {
      // first compress and then serialize
      objectsAsBytes = this.serializeAndCompress(m3gVersion);
    }
    int checksum = calculateChecksum(objectsAsBytes);
    M3GSupport.writeInt(dataOutputStream, checksum);
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
  private int calculateChecksum(byte[] objects) throws IOException
  {
    Adler32 adler32 = new Adler32();
    adler32.update(this.compressionScheme);
    adler32.update(M3GSupport.intToBytes(this.totalSectionLength));
    adler32.update(M3GSupport.intToBytes(this.uncompressedLength));
    adler32.update(objects);
    int checksum = (int)adler32.getValue();
    return checksum;
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

  public ObjectChunk[] getObjects()
  {
    return this.objects;
  }
}
