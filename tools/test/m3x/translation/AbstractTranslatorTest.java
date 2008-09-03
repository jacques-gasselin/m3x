package m3x.translation;

import java.util.List;

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import junit.framework.TestCase;

public abstract class AbstractTranslatorTest extends TestCase
{
  protected void assertColorRGBEquals(ColorRGB m3gColor, List<Short> m3xColor)
  {
    assertTrue(m3xColor.size() == 3);
    assertTrue(m3xColor.get(0).byteValue() == m3gColor.getB());
    assertTrue(m3xColor.get(1).byteValue() == m3gColor.getG());
    assertTrue(m3xColor.get(2).byteValue() == m3gColor.getB());
  }

  protected void assertColorRGBAEquals(ColorRGBA m3gColor, List<Short> m3xColor)
  {
    assertTrue(m3xColor.size() == 4);
    assertTrue(m3xColor.get(0).byteValue() == m3gColor.getB());
    assertTrue(m3xColor.get(1).byteValue() == m3gColor.getG());
    assertTrue(m3xColor.get(2).byteValue() == m3gColor.getB());
    assertTrue(m3xColor.get(3).byteValue() == m3gColor.getA());
  }
}
