package m3x.m3g;

public class M3GException extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public M3GException()
    {
    }

    public M3GException(String message)
    {
        super(message);
    }

    public M3GException(Throwable cause)
    {
        super(cause);
    }

    public M3GException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
