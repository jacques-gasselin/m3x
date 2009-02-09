package m3x.m3g;


public class AppearanceTest extends AbstractTestCase
{
    private Appearance appearance;

    public AppearanceTest()
    {
    }

    @Override
    protected void setUp() throws Exception
    {
        appearance = new Appearance();
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ appearance };
        assertSaveAndLoad(roots);
    }

    public void testGetLayer()
    {
        assertEquals(0, appearance.getLayer());
    }

    public void testSetLayer()
    {
        final int MIN = -63;
        final int MAX = 63;

        try
        {
            appearance.setLayer(MIN);
            assertEquals(MIN, appearance.getLayer());

            appearance.setLayer(MAX);
            assertEquals(MAX, appearance.getLayer());
        }
        catch (Throwable t)
        {
            fail("setting valid values must not throw");
        }

        try
        {
            appearance.setLayer(MIN - 1);
            fail("should throw if the layer is below the minimum");
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof IllegalArgumentException);
        }

        try
        {
            appearance.setLayer(MAX + 1);
            fail("should throw if the layer is above the maximum");
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof IllegalArgumentException);
        }
    }
}
