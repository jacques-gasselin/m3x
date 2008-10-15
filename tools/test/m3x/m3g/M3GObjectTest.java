package m3x.m3g;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import m3x.m3g.M3GException;
import m3x.m3g.primitives.Section;

public class M3GObjectTest extends TestCase
{
    public void testLoader()
    {
        try
        {
            loadObject("data/teapot.m3g");
            loadObject("data/helloworld.m3g");
            loadObject("data/nokia_on_ice.m3g");
            loadObject("data/otokka_jump2.m3g");
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
        for (Section section : object.getObjects())
        {
            assertNotNull(section);
            System.out.println(section);
        }
    }
}
