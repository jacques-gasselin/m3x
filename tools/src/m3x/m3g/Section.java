package m3x.m3g;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


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
   * @param objects
   *  The actual data in this section.
   */
  public Section(byte compressionScheme, byte[] objects)
  {
    assert(compressionScheme == COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32 || compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32);
    assert(objects != null);
    
    this.compressionScheme = compressionScheme;
    this.uncompressedLength = objects.length;
    if (this.compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
    {
      byte[] compressedObjects = new byte[objects.length];
      Deflater deflater = new Deflater();
      deflater.setInput(objects);
      deflater.finish();
      int compressedLength = deflater.deflate(compressedObjects);
      this.objects = new byte[compressedLength];
      System.arraycopy(compressedObjects, 0, this.objects, 0, this.objects.length);
    }
    else
    {
      this.objects = objects;
    }
    // length of a section is compression scheme, total section length
    // uncompressed length, objects array length and Adler32 checksum
    this.totalSectionLength = 1 + 4 + 4 + this.objects.length + 4;
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
    if (this.compressionScheme == COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32)
    {
      byte[] compressedObjects = new byte[objectsLengthInBytes];
      dataInputStream.read(compressedObjects);
      Inflater inflater = new Inflater();
      inflater.setInput(compressedObjects);
      try
      {
        inflater.inflate(this.objects);
      }
      catch (DataFormatException e)
      {
        //avoiding Java SE 6 style Throwable copy constructor
        throw new IOException(e.getMessage());
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
    }
    
    int checksum = this.calculateChecksum();
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
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.totalSectionLength));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.uncompressedLength));
    dataOutputStream.write(this.objects);
    int checksum = calculateChecksum();
    dataOutputStream.writeInt(M3GSupport.swapBytes(checksum));
  }

  /**
   * Calculates the Adler 32 checksum from this object's data.
   * 
   * @return
   *  32 bits of Adler 32 checksum.
   *  
   * @throws IOException
   */
  private int calculateChecksum() throws IOException
  {
    Adler32 adler32 = new Adler32();
    adler32.update(this.compressionScheme);
    adler32.update(M3GSupport.intToBytes(this.totalSectionLength));
    adler32.update(M3GSupport.intToBytes(this.uncompressedLength));
    adler32.update(this.objects);
    int checksum = (int)adler32.getValue();
    return checksum;
  }
  
  public static void main(String[] args) throws Exception
  {
    int n = 100;
    byte[] data = new byte[n];
    for (int i = 0; i < n; i++)
    {
      data[i] = 123;
    }
    Section section = new Section(COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32, data);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    section.serialize(dos, null);
    dos.close();
    data = baos.toByteArray();
    System.out.println(new BigInteger(1, data).toString(16));
  }
}
