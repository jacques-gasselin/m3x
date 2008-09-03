package m3x.translation;

import m3x.xml.M3G;
import m3x.xml.Object3DType;
import m3x.xml.SectionType;

public class TranslationTestSupport
{
  /**
   * Searches an XML element (Object3D subclass instance) with given index.
   * 
   * @param root
   *  Root of the M3X document.
   *  
   * @param searchKey
   *  Element to be found
   *  
   * @return
   *  Found Object3D inside Sections list or null if not found. 
   */
  protected static Object3DType searchObjectIndex(M3G root, int searchKey)
  {
    int index = 1;
    for (SectionType section : root.getSection())
    {
      for (Object3DType object : section.getObjects())
      {
        if (index == searchKey)
        {
          return object;
        }
        index++;
      }
    }
    return null;
  }
}
