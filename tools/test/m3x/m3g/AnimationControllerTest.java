package m3x.m3g;

public class AnimationControllerTest extends AbstractTestCase
{
    private AnimationController controller;
    
    public AnimationControllerTest()
    {
    }

    @Override
    protected void setUp() throws Exception
    {
        controller = new AnimationController();
    }

    public void testSerializationAndDeserialization()
    {
        assertSerialiseSingle(controller);
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ controller };
        assertSaveAndLoad(roots);
    }

}
