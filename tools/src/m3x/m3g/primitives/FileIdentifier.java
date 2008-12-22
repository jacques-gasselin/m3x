package m3x.m3g.primitives;

/**
 * See http://www.java2me.org/m3g/file-format.html#FileIdentifier
 * for more information.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public abstract class FileIdentifier
{
    private FileIdentifier()
    {
        
    }
    
    /**
     * The immutable bytes of the M3G file format header.
     */
    public static final byte[] BYTES =
    {
        (byte) 0xAB, 0x4A, 0x53, 0x52,
        0x31, 0x38, 0x34, (byte) 0xBB,
        0x0D, 0x0A, 0x1A, 0x0A
    };
}
