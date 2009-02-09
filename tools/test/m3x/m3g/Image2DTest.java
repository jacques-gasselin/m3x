package m3x.m3g;

import java.io.ByteArrayOutputStream;


public class Image2DTest extends AbstractTestCase
{
    private Image2D image;

    public Image2DTest()
    {
        image = new Image2D();
        image.setFormat(Image2D.RGBA);
        image.setMutable(false);
        image.setSize(256, 256);
        image.setPalette(new byte[256 * 4]);
        image.setPixels(new byte[256 * 256]);
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ image };

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Saver.save(out, roots, "1.0", "Image2DTest");

            byte[] data = out.toByteArray();

            Object3D[] loadRoots = Loader.load(data);

            for (int i = 0; i < roots.length; ++i)
            {
                doTestAccessors(roots[i], loadRoots[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testSerializationAndDeseriliazation1()
    {
        assertSerialiseSingle(image);
    }
}
