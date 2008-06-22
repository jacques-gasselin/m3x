package m3x.translation;

/**
 *
 * @author jgasseli
 */
public interface Translator
{
    /**Sets the values from an XML object
     * 
     * @param object - the object to set from
     * @throw java.lang.ClassCastException - if object is not of a valid type.
     */
    public void set(m3x.jaxb.Object3DType object);

    /**Sets the values from an M3G object
     * 
     * @param object - the object to set from
     * @throw java.lang.ClassCastException - if object is not of a valid type.
     */
    public void set(m3x.m3g.objects.Object3D object);
    
    /**Convert to an XML object.
     * 
     * @return an XML representation
     */
    public m3x.jaxb.Object3DType toXML();
    
    /**Convert to an M3G object.
     * 
     * @return an M3G representation
     */
    public m3x.m3g.objects.Object3D toM3G();
}
