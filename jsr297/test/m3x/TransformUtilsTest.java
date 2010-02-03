package m3x;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;
import junit.framework.TestCase;
import m3x.microedition.m3g.TransformUtils;

/**
 * @author jgasseli
 */
public class TransformUtilsTest extends TestCase
{
    private final void assertEquals(float[] expected, float[] actual, float epsilon)
    {
        final int length = Math.max(expected.length, actual.length);
        for (int i = 0; i < length; ++i)
        {
            assertEquals(expected[i], actual[i], epsilon);
        }
    }
    
    public void testProjectUnproject()
    {
        final Camera camera = new Camera();
        camera.setPerspective(60, 1, 1.0f, 100.0f);
        
        final float[] point = new float[4];
        Vmath.vload4(point, -1, 2, -12, 1);

        final Transform localToCamera = new Transform();
        camera.getCompositeTransform(localToCamera);
        
        //project the local space point to NDC
        final float[] ndcPoint = new float[4];
        Vmath.vmov4(ndcPoint, point);
        TransformUtils.project(camera, localToCamera, ndcPoint);
        //the point above is definitely in the camera frustum
        assertTrue(ndcPoint[0] >= -1 && ndcPoint[0] <= 1);
        assertTrue(ndcPoint[1] >= -1 && ndcPoint[1] <= 1);
        assertTrue(ndcPoint[2] >= -1 && ndcPoint[2] <= 1);
        //unproject the ndc to local space
        final float[] unprojPoint = new float[4];
        Vmath.vmov4(unprojPoint, ndcPoint);
        unprojPoint[3] = 1.0f;
        Transform cameraToLocal = new Transform(localToCamera);
        cameraToLocal.invert();
        TransformUtils.unproject(camera, cameraToLocal, unprojPoint);

        //confirm they are indeed the same
        assertEquals(point, unprojPoint, 0.001f);
    }

    public void testScreenToNDCAndBack()
    {
        final int width = 800;
        final int height = 600;
        final float[] screenPoint = new float[4];
        Vmath.vload4(screenPoint, 200, 100, 0, 1);
        final float[] ndcPoint = new float[4];
        TransformUtils.screenToNDC(ndcPoint, screenPoint, width, height);
        //map back to screen
        final float[] screenPoint2 = new float[4];
        TransformUtils.ndcToScreen(screenPoint2, ndcPoint, width, height);

        //confirm they are indeed the same
        assertEquals(screenPoint, screenPoint2, 0.001f);
    }

    public void testNDCToScreenAndBack()
    {
        final int width = 800;
        final int height = 600;
        final float[] ndcPoint = new float[4];
        Vmath.vload4(ndcPoint, 200, 100, 0, 1);
        final float[] screenPoint = new float[4];
        TransformUtils.ndcToScreen(screenPoint, ndcPoint, width, height);
        //map back to ndc
        final float[] ndcPoint2 = new float[4];
        TransformUtils.screenToNDC(ndcPoint2, screenPoint, width, height);

        //confirm they are indeed the same
        assertEquals(ndcPoint, ndcPoint2, 0.001f);
    }
}
