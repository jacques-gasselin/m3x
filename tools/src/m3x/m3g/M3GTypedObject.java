package m3x.m3g;

public interface M3GTypedObject extends M3GSerializable
{
  /**
   * Returns the object type byte specified in the M3G specification.
   * 
   * @return
   */
  byte getObjectType();
}
