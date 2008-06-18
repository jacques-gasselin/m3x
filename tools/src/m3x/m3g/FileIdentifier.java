package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * See http://www.java2me.org/m3g/file-format.html#FileIdentifier
 * for more information.
 * 
 * @author jsaarinen
 */
public class FileIdentifier implements M3GSerializable
{
  /**
   * The immutable bytes of the M3G file format header.
   */
  private static final byte[] fileIdentifier = 
  { 
    (byte)0xAB, 0x4A, 0x53, 0x52, 
    0x31, 0x38, 0x34, (byte)0xBB, 
    0x0D, 0x0A, 0x1A, 0x0A 
  };

  /**
   * The file header bytes are written to the output stream.
   */
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.write(fileIdentifier);
  }
}
