package m3x.m3g;

/**
 * Thrown when the M3G deserialization fails because of bad input data.
 * 
 * @author jsaarinen
 */
public class FileFormatException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public FileFormatException()
  {
    // TODO Auto-generated constructor stub
  }

  public FileFormatException(String message)
  {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public FileFormatException(Throwable cause)
  {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  public FileFormatException(String message, Throwable cause)
  {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

}
