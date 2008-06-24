/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package m3x.xml;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**The Deserialiser class hides the complexities of binding a JAXB context
 * and unmarshalling the classes. 
 * The xml classes use refid references that need to be resolved in order to
 * make a properly referenced translation. 
 *
 * @author jgasseli
 */
public class Deserialiser
{
    /**The JAXB unsmahaller that is responsible for converting
     * an XML document into m3x.xml classes.
     */
    private Unmarshaller xmlUnmarshaller = null;

    /**Creates a new Deserialser that is bound to the m3x.xml JAXB context.
     * 
     */
    public Deserialiser()
    {
        //create the unmarshaller
        JAXBContext context = null;
        try
        {
            context = JAXBContext.newInstance("m3x.xml");
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to bind schema: " + e.getMessage());
        }
        
        try
        {
            xmlUnmarshaller = context.createUnmarshaller();
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to create unmarshaller: " + e.getMessage());
        }
    }
    
    /**Deserialise an input stream that contains an XML document.
     * 
     * @param stream - the inout stream to read from.
     * @return - the root M3G object
     */
    public m3x.xml.M3G deserialise(InputStream stream)
    {
        try
        {
            return (m3x.xml.M3G)xmlUnmarshaller.unmarshal(stream);
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to parse infile: " + e.getMessage());
        }       
    }
}
