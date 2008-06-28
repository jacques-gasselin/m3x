package m3x.m3g;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * This class includes support methods for swapping the byte
 * order. M3G uses little-endian byte order, so this class
 * is needed.
 * 
 * @author jsaarinen
 */
public class M3GSupport
{
  private M3GSupport()
  {  
  }
  
  /**
   * Converts a float from big-endian to little-endian or vice versa.
   * 
   * @param x
   * @return
   */
  public static int swapBytes(float x)
  {
    int asInt = Float.floatToRawIntBits(x);
    return swapBytes(asInt);
  }
  
  /**
   * Converts an 32-bit integer from big-endian to little-endian or vice versa.
   * 
   * @param x
   * @return
   */
  public static int swapBytes(int x)
  {
    int b1 = (x >>  0) & 0x000000FF;
    int b2 = (x >>  8) & 0x000000FF;
    int b3 = (x >> 16) & 0x000000FF;
    int b4 = (x >> 24) & 0x000000FF;
    return (b1 << 24) | (b2 << 16) | (b3 << 8) | (b4 << 0);
  }

  /**
   * Converts a 16-bit integer from big-endian to little-endian or vice versa.
   * 
   * @param x
   * @return
   */
  public static short swapBytes(short x)
  {
    int b1 = (x >> 0) & 0x000000FF;
    int b2 = (x >> 8) & 0x000000FF;
    return (short)((b1 << 8) | (b2 << 0));
  }

  /**
   * Returns an integer in little-endian bytes.
   * 
   * @param x
   *  The integer.
   *  
   * @return
   *  The integer bytes, LSB(yte) first.
   *  
   * @throws IOException
   */
  public static byte[] intToBytes(int x) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(swapBytes(x));
    dos.close();
    return baos.toByteArray();
  }
  
  /**
   * Returns the M3G object as a byte array.
   * 
   * @param object
   * @return
   * @throws IOException
   */
  public static byte[] objectToBytes(M3GSerializable object) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    object.serialize(dos, null);
    dos.close();
    return baos.toByteArray();    
  }
  
  /**
   * A helper method for read an integer from the stream saved as little-endian byte order. 
   * @param dataInputStream
   * @return
   * @throws IOException
   */
  public static int readInt(DataInputStream dataInputStream) throws IOException
  {
    return swapBytes(dataInputStream.readInt());
  }
  
  /**
   * A helper method to write an integer as little-endian to the stream.
   * @param dataOutputStream
   * @param x
   * @throws IOException
   */
  public static void writeInt(DataOutputStream dataOutputStream, int x) throws IOException
  {
    dataOutputStream.writeInt(swapBytes(x));
  }

  /**
   * A helper method for read a float from the stream saved as little-endian byte order. 
   * @param dataInputStream
   * @return
   * @throws IOException
   */
  public static float readFloat(DataInputStream dataInputStream) throws IOException
  {
    return Float.intBitsToFloat(swapBytes(dataInputStream.readInt()));
  }
  
  /**
   * A helper method to write a float as little-endian to the stream.
   * @param dataOutputStream
   * @param x
   * @throws IOException
   */
  public static void writeInt(DataOutputStream dataOutputStream, float x) throws IOException
  {
    dataOutputStream.writeInt(swapBytes(x));
  }
}
