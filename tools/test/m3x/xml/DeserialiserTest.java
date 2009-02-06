package m3x.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import junit.framework.TestCase;

/**Test case for m3x.xml.Deserialiser
 *
 * @author jgasseli
 */
public class DeserialiserTest extends TestCase
{
   
    /**Sets up the test case fixture.
     * Creates a new instance of an m3x.xml.Deserialiser.
     */
    @Override
    public void setUp()
    {

    }
    
    public void testDeserialiseEmptyFile()
    {
        String xmlString = "<?xml version=\"1.0\"?>\n";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            //Should throw here
            Deserialiser.deserialise(in);
            fail("Should throw here because the file is empty");
        }
        catch (Deserialiser.ValidationException e)
        {
            
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            fail();
        }
    }

    public void testDeserialiseEmptySection()
    {
        String xmlString = "<?xml version=\"1.0\"?>\n"
                + "<m3g>\n"
                + "    <section/>\n"
                + "</m3g>";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            Deserialiser.deserialise(in);
            fail("Should throw here because the section is empty");
        }
        catch (Deserialiser.ValidationException e)
        {
            assertTrue(e.getLine() == 3);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            fail();
        }
    }

    public void testDeserialiseStringAnimationController()
    {
        String xmlString =
              "<?xml version=\"1.0\"?>\n"
            + "<m3g>\n"
            + "    <section>\n"
            + "        <AnimationController/>\n"
            + "    </section>\n"
            + "</m3g>";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            //Should not throw here
            m3x.xml.M3G root = Deserialiser.deserialise(in);
            assertNotNull("deserialised root must not be null",
                root);
            List<m3x.xml.Section> sections = root.getSection();
            assertEquals("Should have 1 section only",
                1, sections.size());
            Section section = sections.get(0);
            List<Object3D> objects = section.getObjects();
            assertEquals("Should have 1 object only",
                1, objects.size());
            assertTrue("object should be an animation controller",
                objects.get(0) instanceof m3x.xml.AnimationController);
        }
        catch (Throwable t)
        {
            //must not throw
            fail(t.toString());
        }
    }

    public void testDeserialiseStringInvalidAnimationControllerInstance()
    {
        String xmlString =
              "<?xml version=\"1.0\"?>\n"
            + "<m3g>\n"
            + "    <section>\n"
            + "        <AnimationController id=\"ac0\"/>\n"
            + "        <AnimationControllerInstance ref=\"ac0\"/>\n"
            + "    </section>\n"
            + "</m3g>";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            Deserialiser.deserialise(in);
            fail("Should throw here because an instance can't be a root object");
        }
        catch (Deserialiser.ValidationException e)
        {
            assertTrue(e.getLine() == 5);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            fail();
        }
    }

    public void testDeserialiseStringInvalidAnimationTrack()
    {
        String xmlString =
              "<?xml version=\"1.0\"?>\n"
            + "<m3g>\n"
            + "    <section>\n"
            + "        <AnimationTrack id=\"at0\"/>\n"
            + "    </section>\n"
            + "</m3g>";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            Deserialiser.deserialise(in);
            fail("Should throw here because a track needs a sequence");
        }
        catch (Deserialiser.ValidationException e)
        {
            assertTrue(e.getLine() == 4);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            fail();
        }
    }

}
