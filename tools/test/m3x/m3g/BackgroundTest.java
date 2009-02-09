package m3x.m3g;


public class BackgroundTest extends AbstractTestCase
{
    private Background background;

    public BackgroundTest()
    {
    }

    @Override
    protected void setUp() throws Exception
    {
        background = new Background();
    }

    public void testSerializationAndDeserialization()
    {
        assertSerialiseSingle(background);
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ background };
        assertSaveAndLoad(roots);
    }
}
