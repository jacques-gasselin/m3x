package m3x.m3g;

import java.io.ByteArrayOutputStream;

public class AnimationControllerTest extends AbstractTestCase
{
    private AnimationController controller;
    
    public AnimationControllerTest()
    {
        controller = new AnimationController();
        controller.setActiveInterval(1, 10);
        controller.setPosition(0.1f, 100);
        controller.setSpeed(2.0f, 100);
        controller.setWeight(3.0f);
    }


    public void testSerializationAndDeserialization()
    {
        assertSerialised(controller);
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ controller };

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Saver.save(out, roots, "1.0", "AnimationControllerTest");

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

}
