package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;

public class MatrixTest extends AbstractTestCase
{

    public void testSerializationAndDeserialization()
    {
        Matrix matrix = new Matrix();
        matrix.setMatrix(new float[]{
            0,   1,   2,   3,
            4,   5,   6,   7,
            8,   9,  10,  11,
           12,  13,  14,  15
        });
        assertSerialiseSingle(matrix);
    }
}
