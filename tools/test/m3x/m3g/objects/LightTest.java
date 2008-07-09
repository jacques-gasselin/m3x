package m3x.m3g.objects;

import junit.framework.TestCase;
import java.io.*;

import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class LightTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix transform = getMatrix();
    ColorRGB color = new ColorRGB(0.1f, 0.2f, 0.3f);
    Light light = new Light(animationTracks, 
                            userParameters, 
                            transform, 
                            true, 
                            true, 
                            (byte) 66, 
                            1, 
                            0.1f, 
                            0.2f, 
                            0.3f, 
                            color, 
                            Light.MODE_SPOT, 
                            0.5f, 
                            0.2f, 
                            2.0f);
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(light);
      Light deserialized = (Light)M3GSupport.bytesToObject(serialized, Light.class);
      float x = light.getAttenuationConstant();
      System.out.printf("%x %x", Float.floatToIntBits(x), Float.floatToIntBits(deserialized.getAttenuationConstant()));
      assertTrue(light.getAttenuationConstant() == deserialized.getAttenuationConstant());
      assertTrue(light.getAttenuationLinear() == deserialized.getAttenuationLinear());
      assertTrue(light.getAttenuationQuadratic() == deserialized.getAttenuationQuadratic());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
  public void testBla() throws Exception
  {
    float x = 666.0f;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    M3GSupport.writeFloat(dos, x);
    dos.close();
    byte[] serialized = baos.toByteArray();
    ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
    DataInputStream dis = new DataInputStream(bais);
    float y = M3GSupport.readFloat(dis);
    dis.close();
    assert(x == y);
  }
}
