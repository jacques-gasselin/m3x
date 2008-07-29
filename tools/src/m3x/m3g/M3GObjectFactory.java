package m3x.m3g;

/**
 * 
 * @author jsaarinen
 */
public interface M3GObjectFactory
{
  /**
   * Returns an empty (should be deserialized then) 
   * M3G object specified by type byte.
   * 
   * @param objectType
   *  The desired object type. 
   * @return
   * @throws M3GException
   */
  M3GTypedObject getInstance(byte objectType) throws M3GException;
}
