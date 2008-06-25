package m3x.m3g;

/**
 * An interface which all concrete M3G classes implement.
 * 
 * @author jsaarinen
 */
public interface M3GTypedObject extends M3GSerializable
{
  /**
   * Returns the object type byte specified in the M3G specification.
   * 
   * @return
   */
  byte getObjectType();
}
