package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import junit.framework.TestCase;

public class M3GSupportTest extends TestCase
{
  public void testFloats() throws Exception
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
    assertTrue(x == y);
  }
}
