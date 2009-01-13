package m3x.xml;

import javax.xml.bind.JAXB;

/**The Deserialiser class hides the complexities of binding a JAXB context
 * and unmarshalling the classes. 
 * The xml classes use refid references that need to be resolved in order to
 * make a properly referenced translation. 
 *
 * @author jgasseli
 */
public abstract class Deserialiser
{
    /**The JAXB unsmahaller that is responsible for converting
     * an XML document into m3x.xml classes.
     */
    private Deserialiser()
    {
        
    }
    
    /**Deserialise an input stream that contains an XML document.
     * 
     * @param stream - the inout stream to read from.
     * @return - the root M3G object
     */
    public static m3x.xml.M3G deserialise(java.io.InputStream stream)
    {
        try
        {
            return JAXB.unmarshal(stream, m3x.xml.M3G.class);
        }
        catch (javax.xml.bind.DataBindingException e)
        {
            throw new IllegalArgumentException("unable to parse stream: " + e.getMessage());
        }
    }
}
