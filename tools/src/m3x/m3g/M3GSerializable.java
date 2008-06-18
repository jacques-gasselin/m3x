package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * All M3G objects that can be serialized will implement
 * this interface for object serialization.
 * 
 * @author jsaarinen
 */
public interface M3GSerializable
{
  /**
   * Implementations of this method should output the corresponding
   * M3G object data to the output stream.
   * 
   * It is up to the caller of this method to set up and clean up
   * the output stream.
   * 
   * @param dataOutputStream
   *  Where data will be written to.
   * 
   * @param m3gVersion TODO
   *  
   * @throws IOException
   *  When something nasty happened.
   */
  void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException;
}
