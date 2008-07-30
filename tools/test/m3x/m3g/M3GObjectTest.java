package m3x.m3g;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import m3x.m3g.M3GException;

public class M3GObjectTest extends TestCase
{
  public void testLoader()
  {
    try
    {
      M3GObject object = new M3GObject(new FileInputStream("tools/test/data/teapot.m3g"));
      System.out.println(object.getFileIdentifier());
      System.out.println(object.getHeader());
      for (M3GTypedObject typedObject : object.getObjects())
      {
        assertNotNull(typedObject);
        System.out.println(typedObject);
      }
    }
    catch (IOException e)
    {
      fail("Unable to load test data.");
    }
    catch (M3GException e)
    {
      fail(e.getMessage());
    }
  }
}
