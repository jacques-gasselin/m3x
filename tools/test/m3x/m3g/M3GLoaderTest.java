package m3x.m3g;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;

import junit.framework.TestCase;

public class M3GLoaderTest extends TestCase
{
  private final static String[] dataFiles =
  {
    "tools/test/data/helloworld.m3g",
    "tools/test/data/teapot.m3g",
    "tools/test/data/nokia_on_ice.m3g",
    "tools/test/data/otokka_jump2.m3g",
  };
  
  public void testLoader()
  {
    for (String fileName : dataFiles)
    {
      fileTest(fileName);
    }
  }
  
  private void fileTest(String fileName)
  {
    try
    {
      FileInputStream fis = new FileInputStream(fileName);
      int length = fis.available();
      byte[] fileBytes = new byte[length];
      fis.read(fileBytes);
      fis.close();
      M3GObject object1 = M3GLoader.load(fileBytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      M3GLoader.save(baos, object1);
      byte[] serializedBytes = baos.toByteArray();
      assertTrue(Arrays.equals(fileBytes, serializedBytes));
      M3GObject object2 = M3GLoader.load(serializedBytes);
      assertTrue(object1.equals(object2));
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }
}
