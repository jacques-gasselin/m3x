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

    public void testGetUserID()
    {
        assertEquals("user id should be " + USERID,
            USERID, object.getUserID());
    }
    
}
