/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package m3x.xml;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;

/**The Serialiser class hides the complexities of binding a JAXB context
 * and marshalling the classes. 
 * This is a reversed version of the Deserialiser class that was written earlier.
 *
 * @author imsteve
 */
public class Serialiser
{
    /**The JAXB mashaller that is responsible for converting
     * an XML document into m3x.xml classes.
     */
    private Marshaller xmlMarshaller = null;

    /**Creates a new Serialser that is bound to the m3x.xml JAXB context.
     * 
     */
    public Serialiser()
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
            xmlMarshaller = context.createMarshaller();
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to create marshaller: " + e.getMessage());
        }
    }
    
    /**Serialise an M3G object and write it to the output stream given.
     * 
     * @param object - the object to write.
     * @param stream - the inout stream to write to.
     */
    public void serialise(m3x.xml.M3G object, OutputStream stream)
    {
        try
        {
            xmlMarshaller.setProperty("jaxb.formatted.output", new Boolean(false));
            xmlMarshaller.marshal(object, stream);
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to write outfile: " + e.toString());
        }       
    }
    
    /**Serialise an M3G object and write it to the output stream given in a way that is more readable.
     * 
     * @param object - the object to write.
     * @param stream - the inout stream to write to.
     */
    public void serialiseFormatted(m3x.xml.M3G object, OutputStream stream)
    {
        try
        {
            xmlMarshaller.setProperty("jaxb.formatted.output", new Boolean(true));
            xmlMarshaller.marshal(object, stream);
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to write outfile: " + e.toString());
        }       
    }
}
