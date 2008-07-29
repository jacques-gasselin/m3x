package m3x.m3g;

public class M3GException extends Exception
{

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
