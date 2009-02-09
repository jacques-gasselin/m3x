package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;

public class Vector3DTest extends AbstractTestCase
{

    public void testSerializationAndDeserialization()
    {
        Vector3D vector = new Vector3D(10, 20, 30);
        assertSerialiseSingle(vector);
    }
}
