package m3x.m3g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * All M3G objects that can be serialized will implement
 * this interface for object serialization.
 * 
 * @author jsaarinen
 */
public interface M3GSerializable
{  
  /**
   * Constructs a M3G object from a stream.
   * The user is responsible for closing the stream.
   * 
   * @param dataInputStream
   *  The stream which from the M3G object is read.
   * 
   * @param m3gVersion
   * 
   * @throws IOException
   * @throws FileFormatException
   *  When the input data was somehow invalid from the M3G specification point of view.
   */
  void deserialize(DataInputStream dataInputStream, String m3gVersion) throws IOException, FileFormatException;
  
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
