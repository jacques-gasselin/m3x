package m3x.translation;

/**
 *
 * @author jgasseli
 */
public interface Translator
{
    /**Sets the values from an XML object
     * May throw java.lang.ClassCastException - if object is not of a valid type.
     * @param object - the object to set from
     * @param deserialiser - the deserialiser used to resolve references.
     */
    public void set(m3x.xml.Object3DType object, m3x.xml.Deserialiser deserialiser);

    /**Sets the values from an M3G object
     * May throw java.lang.ClassCastException - if object is not of a valid type.
     * @param object - the object to set from
     */
    public void set(m3x.m3g.objects.Object3D object);
    
    /**Convert to an XML object.
     * 
     * @return an XML representation
     */
    public m3x.xml.Object3DType toXML();
    
    /**Convert to an M3G object.
     * 
     * @return an M3G representation
     */
    public m3x.m3g.objects.Object3D toM3G();
}
