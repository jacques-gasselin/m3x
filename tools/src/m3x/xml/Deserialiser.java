package m3x.xml;

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
    private javax.xml.bind.Unmarshaller xmlUnmarshaller = null;

    /**Creates a new Deserialser that is bound to the m3x.xml JAXB context.
     * 
     */
    public Deserialiser()
    {
        //create the unmarshaller
        javax.xml.bind.JAXBContext context = null;
        try
        {
            context = javax.xml.bind.JAXBContext.newInstance("m3x.xml");
        }
        catch (javax.xml.bind.JAXBException e)
        {
            throw new IllegalArgumentException("unable to bind schema: " + e.getMessage());
        }
        
        try
        {
            xmlUnmarshaller = context.createUnmarshaller();
        }
        catch (javax.xml.bind.JAXBException e)
        {
            throw new IllegalArgumentException("unable to create unmarshaller: " + e.getMessage());
        }
    }
    
    /**Deserialise an input stream that contains an XML document.
     * 
     * @param stream - the inout stream to read from.
     * @return - the root M3G object
     */
    public m3x.xml.M3G deserialise(java.io.InputStream stream)
    {
        try
        {
            return (m3x.xml.M3G)xmlUnmarshaller.unmarshal(stream);
        }
        catch (javax.xml.bind.JAXBException e)
        {
            throw new IllegalArgumentException("unable to parse infile: " + e.getMessage());
        }       
    }
}
