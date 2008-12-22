package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;

public class ColorRGBATest extends AbstractTestCase
{
    public void testSerializationAndDeserialization()
    {
        ColorRGBA color = new ColorRGBA(10, 20, 30, 255);
        assertSerialised(color);
    }
}
