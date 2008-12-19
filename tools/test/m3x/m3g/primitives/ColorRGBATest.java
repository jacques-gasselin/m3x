package m3x.m3g.primitives;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import m3x.m3g.AbstractTestCase;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;

public class ColorRGBATest extends AbstractTestCase
{
    public void testSerializationAndDeserialization()
    {
        ColorRGBA color = new ColorRGBA(10, 20, 30, 255);
        try
        {
            //Serialise
            Serialiser serialiser = new Serialiser("1.0", null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            serialiser.pushOutputStream(out);
            color.serialize(serialiser);
            serialiser.popOutputStream();

            byte[] serialized = out.toByteArray();

            //Deserialise
            Deserialiser deserialiser = new Deserialiser();
            ByteArrayInputStream in = new ByteArrayInputStream(serialized);
            deserialiser.pushInputStream(in);
            ColorRGBA deserialized = new ColorRGBA();
            deserialized.deserialize(deserialiser);
            deserialiser.popInputStream();
            
            doTestAccessors(color, deserialized);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
