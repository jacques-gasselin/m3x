package m3x.m3g;

import java.io.DataInputStream;
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
      loadObject("tools/test/data/teapot.m3g");
      loadObject("tools/test/data/helloworld.m3g");
      loadObject("tools/test/data/nokia_on_ice.m3g");
      loadObject("tools/test/data/otokka_jump2.m3g");
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

  private void loadObject(String filename) throws FileNotFoundException, IOException,
      M3GException
  {
    M3GObject object = new M3GObject();
    DataInputStream dataInputStream = new DataInputStream(new FileInputStream(filename));
    object.deserialize(dataInputStream, "1.0");
    System.out.println(object.getFileIdentifier());
    System.out.println(object.getHeader());
    for (M3GTypedObject typedObject : object.getObjects())
    {
      assertNotNull(typedObject);
      System.out.println(typedObject);
    }
  }
}
