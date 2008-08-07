package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.Section;

public class MatrixTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    Matrix matrix = this.getMatrix();                                                              
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(matrix);
      Matrix deserialized = (Matrix)M3GSupport.bytesToObject(serialized, Matrix.class);
      this.doTestAccessors(matrix, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
