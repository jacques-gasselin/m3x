package m3x.translation;

import java.util.List;

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.xml.M3G;

import junit.framework.TestCase;

public abstract class TranslatorSupport extends TestCase
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
  protected static m3x.xml.Object3D searchObjectIndex(M3G root, int searchKey)
  {
    int index = 1;
    for (m3x.xml.Section section : root.getSection())
    {
      for (m3x.xml.Object3D object : section.getObjects())
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
  
  protected static void assertColorRGBEquals(ColorRGB m3gColor, List<Short> m3xColor)
  {
    assertTrue(m3xColor.size() == 3);
    assertTrue(m3xColor.get(0).byteValue() == m3gColor.getR());
    assertTrue(m3xColor.get(1).byteValue() == m3gColor.getG());
    assertTrue(m3xColor.get(2).byteValue() == m3gColor.getB());
  }

  protected static void assertColorRGBAEquals(ColorRGBA m3gColor, List<Short> m3xColor)
  {
    assertTrue(m3xColor.size() == 4);
    assertTrue(m3xColor.get(0).byteValue() == m3gColor.getR());
    assertTrue(m3xColor.get(1).byteValue() == m3gColor.getG());
    assertTrue(m3xColor.get(2).byteValue() == m3gColor.getB());
    assertTrue(m3xColor.get(3).byteValue() == m3gColor.getA());
  }
  
  protected static void assertArraysEquals(byte[] list1, List<? extends Number> list2)
  {
    assertTrue(list1.length == list2.size());
    for (int i = 0; i < list1.length; i++)
    {
      assertTrue(list1[i] == list2.get(i).byteValue());
    }
  }

  protected static void assertArraysEquals(short[] list1, List<? extends Number> list2)
  {
    assertTrue(list1.length == list2.size());
    for (int i = 0; i < list1.length; i++)
    {
      assertTrue(list1[i] == list2.get(i).shortValue());
    }
  }
  
  protected static void assertArraysEquals(int[] list1, List<? extends Number> list2)
  {
    assertTrue(list1.length == list2.size());
    for (int i = 0; i < list1.length; i++)
    {
      assertTrue(list1[i] == list2.get(i).intValue());
    }
  }
  
  protected static void assertArraysEquals(float[] list1, List<? extends Number> list2)
  {
    assertTrue(list1.length == list2.size());
    for (int i = 0; i < list1.length; i++)
    {
      assertTrue(list1[i] == list2.get(i).floatValue());
    }
  }
  
  protected static void assertArraysEquals(double[] list1, List<? extends Number> list2)
  {
    assertTrue(list1.length == list2.size());
    for (int i = 0; i < list1.length; i++)
    {
      assertTrue(list1[i] == list2.get(i).doubleValue());
    }
  }

  protected void fillTransformableData(m3x.xml.TransformableType tt)
  {
    tt.getScale().clear();
    tt.getScale().add(0.1f);
    tt.getScale().add(0.2f);
    tt.getScale().add(0.3f);
    
    tt.getTransform().clear();
    tt.getTransform().add(0.1f);
    tt.getTransform().add(0.2f);
    tt.getTransform().add(0.3f);
    tt.getTransform().add(0.4f);
    tt.getTransform().add(0.5f);
    tt.getTransform().add(0.6f);
    tt.getTransform().add(0.7f);
    tt.getTransform().add(0.8f);
    tt.getTransform().add(0.9f);
    tt.getTransform().add(1.0f);
    tt.getTransform().add(1.1f);
    tt.getTransform().add(1.2f);
    tt.getTransform().add(1.3f);
    tt.getTransform().add(1.4f);
    tt.getTransform().add(1.5f);
    tt.getTransform().add(1.6f);
    /*
    tt.getOrientation().setAngle((float)Math.PI);
    tt.getOrientation().getValue().clear();
    tt.getOrientation().getValue().add(1.0f);
    tt.getOrientation().getValue().add(-1.0f);
    tt.getOrientation().getValue().add(0.0f);
    */
  }
}
