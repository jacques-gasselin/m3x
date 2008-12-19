package m3x.m3g;

import java.io.FileInputStream;

import junit.framework.TestCase;

public class LoaderTest extends TestCase
{
    private final static String[] dataFiles =
    {
        "data/helloworld.m3g",
        "data/teapot.m3g",
        "data/nokia_on_ice.m3g",
        "data/otokka_jump2.m3g",
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
            Object3D objects[] = Loader.load(fis);
            assertTrue(objects.length > 0);
            /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Loader.save(baos, object1);
            byte[] serializedBytes = baos.toByteArray();
            assertTrue(Arrays.equals(fileBytes, serializedBytes));
            M3GObject object2 = Loader.load(serializedBytes);
            assertTrue(object1.equals(object2));*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
