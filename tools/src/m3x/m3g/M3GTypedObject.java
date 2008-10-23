package m3x.m3g;

/**
 * An interface which all concrete (which have object type field)
 * M3G classes implement.
 * 
 * @author jsaarinen
 */
public interface M3GTypedObject extends M3GSerializable
{
    /**
     * Returns the object type byte specified in the M3G specification.
     *
     * @return
     *  The object type byte.
     */
    int getObjectType();
} 
