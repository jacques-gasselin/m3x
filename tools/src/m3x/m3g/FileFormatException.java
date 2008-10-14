package m3x.m3g;

/**
 * Thrown when the M3G deserialization should fail because of bad input data.
 * 
 * @author jsaarinen
 */
public class FileFormatException extends M3GException
{
    /**
    *
    */
    private static final long serialVersionUID = 1L;

    public FileFormatException()
    {
    }

    public FileFormatException(String message)
    {
        super(message);
    }

    public FileFormatException(Throwable cause)
    {
        super(cause);
    }

    public FileFormatException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
