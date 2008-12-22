package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;

public class ColorRGBTest extends AbstractTestCase
{

    public void testSerializationAndDeserialization()
    {
        ColorRGB color = new ColorRGB(10, 20, 30);
        assertSerialised(color);
    }
}
