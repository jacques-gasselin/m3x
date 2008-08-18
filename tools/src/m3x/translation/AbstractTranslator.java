package m3x.translation;

import m3x.m3g.objects.Object3D;
import m3x.xml.Deserialiser;
import m3x.xml.Object3DType;

public abstract class AbstractTranslator implements Translator
{

  protected Object3DType m3xObject;
  protected Object3D m3gObject;
  protected Deserialiser deserializer;

  /**
   * Sets the values from an XML object May throw java.lang.ClassCastException -
   * if object is not of a valid type.
   * 
   * @param object
   *          - the object to set from
   * @param deserialiser
   *          - the deserialiser used to resolve references.
   */
  public void set(Object3DType object, Deserialiser deserialiser)
  {
    this.m3xObject = object;
    this.deserializer = deserialiser;
  }

  /**
   * Sets the values from an M3G object May throw java.lang.ClassCastException -
   * if object is not of a valid type.
   * 
   * @param object
   *          - the object to set from
   */
  public void set(Object3D object)
  {
    this.m3gObject = object;

  }

}
