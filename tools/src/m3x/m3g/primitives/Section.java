package m3x.m3g.primitives;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
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
   * Either of the previous two enums.
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
   * The actual contents in this section, may be compressed.
   */
  private byte[] objects;
    
  /**
   * Creates a new Section object. 
   * Data compression - if TBD - is done here.
   * 
   * @param compressionScheme
   *  Whether to compress or not?
   *  
   * @param m3gObjects
   *  The actual data in this section.
   */
  public Section(byte compressionScheme, byte[] m3gObjects)
  {
    assert(compressionScheme == COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 || compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32);
    assert(m3gObjects != null);
    
    this.compressionScheme = compressionScheme;
    this.uncompressedLength = m3gObjects.length;
    if (this.compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
    {
      compress(m3gObjects);
    }
    else
    {
      this.objects = m3gObjects;
    }
    // length of a section is compression scheme, total section length
    // uncompressed length, objects array length and Adler32 checksum
    this.totalSectionLength = 1 + 4 + 4 + this.objects.length + 4;
  }

  /**
   * Creates a new Section object from given M3GSerializable objects.
   *  
   * @param compressionScheme
   * @param m3gObjects
   * @param m3gVersion
   * @throws IOException
   */
  public Section(byte compressionScheme, M3GSerializable[] m3gObjects, String m3gVersion) throws IOException
  {
    assert(compressionScheme == COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 || compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32);
    assert(m3gObjects != null);
    
    this.compressionScheme = compressionScheme;
    if (this.compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
    {
      serializeAndCompress(m3gObjects, m3gVersion); 
    }
    else
    {
      serialize(m3gObjects, m3gVersion);
    }
    // length of a section is compression scheme, total section length
    // uncompressed length, objects array length and Adler32 checksum
    this.totalSectionLength = 1 + 4 + 4 + this.objects.length + 4;
  }

  private void serialize(M3GSerializable[] m3gObjects, String m3gVersion) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    for (M3GSerializable object : m3gObjects)
    {
      object.serialize(dos, m3gVersion);
    }
    dos.close();
    this.objects = baos.toByteArray();
    this.uncompressedLength = this.objects.length;
  }

  /**
   * Compresses M3GSerializable objects one at a time to the this.objects;
   * 
   * @param m3gObjects
   * @param m3gVersion
   * @throws IOException
   */
  private void serializeAndCompress(M3GSerializable[] m3gObjects, String m3gVersion)
      throws IOException
  {
    List<byte[]> buffers = new ArrayList<byte[]>();
    this.uncompressedLength = 0;
    Deflater deflater = new Deflater();
    for (M3GSerializable object : m3gObjects)
    {
      // compress one object at a time
      byte[] serializedObject = M3GSupport.objectToBytes(object);
      // we allocate maximum buffer size for compression,
      // deflate doesn't grow data in any case, so the max.
      // required amount of memory is the same as the M3G object
      // is when serialized
      byte[] buffer = new byte[serializedObject.length];
      this.uncompressedLength += serializedObject.length;
      deflater.setInput(serializedObject);
      int compressedLength = deflater.deflate(buffer);
      deflater.finish();
      byte[] compressedData = new byte[compressedLength];
      System.arraycopy(buffer, 0, compressedData, 0, compressedLength);
      buffers.add(compressedData);
    }
    deflater.end();
    // allocate space for the compressed data
    int compressedLength = 0;
    for (byte[] buf : buffers)
    {
      compressedLength += buf.length;
    }
    // allocate buffer for all individual buffers
    this.objects = new byte[compressedLength];
    int index = 0;
    // "concatenate" individual buffers
    for (byte[] buf : buffers)
    {
      System.arraycopy(buf, 0, this.objects, index, buf.length);
      index += buf.length;
    }
  }
  
  private void compress(byte[] m3gObjects)
  {
    byte[] buffer = new byte[m3gObjects.length];
    Deflater deflater = new Deflater();
    deflater.setInput(m3gObjects);
    int compressedLength = deflater.deflate(buffer);
    deflater.finish();
    this.objects = new byte[compressedLength];
    System.arraycopy(buffer, 0, this.objects, 0, this.objects.length);
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
    
    this.objects = new byte[this.uncompressedLength];
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
        inflater.inflate(this.objects);
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
      dataInputStream.read(this.objects);
      checksum = this.calculateChecksum(this.objects);
    }
    
    int checksumFromStream = M3GSupport.readInt(dataInputStream);
    if (checksum != checksumFromStream)
    {
      throw new FileFormatException("Invalid checksum, was " + checksumFromStream + ", should have been " + checksum);
    }
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
    dataOutputStream.write(this.objects);
    int checksum = calculateChecksum(this.objects);
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

  public byte[] getObjects()
  {
    return this.objects;
  }
}
