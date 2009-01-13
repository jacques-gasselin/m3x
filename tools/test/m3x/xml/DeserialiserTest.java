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
    
    public void testDeserialiseString1()
    {
        String xmlString = "<?xml version=\"1.0\"?>\n";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            //Should throw here
            m3x.xml.M3G root = Deserialiser.deserialise(in);
        }
        catch (IllegalArgumentException unused)
        {
            return;
        }
        catch (Exception e)
        {
            //must be IllegalArgumentException
            fail();
        }
        //should not get here
        fail();
    }

    public void testDeserialiseString2()
    {
        String xmlString = "<?xml version=\"1.0\"?>\n"
                + "<m3g>\n"
                + "    <section/>\n"
                + "</m3g>";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            //Should not throw here
            m3x.xml.M3G root = Deserialiser.deserialise(in);
            assertNotNull("deserialised root must not be null", root);
            List<m3x.xml.Section> sections = root.getSection();
        }
        catch (Exception e)
        {
            //must not throw
            fail(e.toString());
        }
    }

    public void testDeserialiseStringAnimationController()
    {
        String xmlString =
              "<?xml version=\"1.0\"?>\n"
            + "<m3g>\n"
            + "    <section>\n"
            + "        <AnimationController/>"
            + "    </section>\n"
            + "</m3g>";
        InputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try
        {
            //Should not throw here
            m3x.xml.M3G root = Deserialiser.deserialise(in);
            assertNotNull("deserialised root must not be null", root);
            List<m3x.xml.Section> sections = root.getSection();
        }
        catch (Exception e)
        {
            //must not throw
            fail(e.toString());
        }
    }
}
