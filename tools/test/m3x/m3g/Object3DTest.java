package m3x.m3g;

/**
 *
 * @author jgasseli
 */
public class Object3DTest extends AbstractTestCase
{
    private static final int USERID = 10;
    private Object3D object;

    public Object3DTest()
    {
        object = new AnimationController();
        object.setUserID(USERID);
    }

    public void testEquality()
    {
        Object3D another = new AnimationController();
        //user id should affect equality
        assertFalse(object.equals(another));

        //equal user id
        another.setUserID(USERID);
        assertTrue(object.equals(another));

        //different classes should not be equal
        another = new Background();
        assertFalse(object.equals(another));

        //null is never equal to a non null object
        assertFalse(object.equals(null));
    }
}
