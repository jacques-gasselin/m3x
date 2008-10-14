package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Camera;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.CameraProjectionModeType;

public class CameraTranslator extends AbstractTranslator
{
    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Camera camera = (m3x.xml.Camera) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        m3x.xml.TransformableType transformable = (m3x.xml.TransformableType) camera;
        Matrix transform = getM3GTransformMatrix(transformable);
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        try
        {
            if (camera.getProjectionTransform().size() == 0)
            {
                this.setBinaryObject(new m3x.m3g.objects.Camera(animationTracks, userParameters, transform, camera.isRenderingEnabled(), camera.isPickingEnabled(), (byte) (camera.getAlphaFactor() * 255.0f + 0.5f), camera.getScope(), toM3G(camera.getProjectionType()), camera.getFovy().floatValue(), camera.getAspectRatio().floatValue(), camera.getNear().floatValue(), camera.getFar().floatValue()));
            }
            else
            {
                this.setBinaryObject(new m3x.m3g.objects.Camera(animationTracks, userParameters, transform, camera.isRenderingEnabled(), camera.isPickingEnabled(), (byte) (camera.getAlphaFactor() * 255.0f + 0.5f), camera.getScope(), toM3G(camera.getProjectionTransform())));
            }
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    private int toM3G(CameraProjectionModeType projectionType)
    {
        if (projectionType.equals(CameraProjectionModeType.GENERIC))
        {
            return Camera.PROJECTION_TYPE_GENERIC;
        }
        if (projectionType.equals(CameraProjectionModeType.PARALLEL))
        {
            return Camera.PROJECTION_TYPE_PARALLEL;
        }
        if (projectionType.equals(CameraProjectionModeType.PERSPECTIVE))
        {
            return Camera.PROJECTION_TYPE_PERSPECTIVE;
        }
        if (projectionType.equals(CameraProjectionModeType.SCREEN))
        {
            throw new IllegalArgumentException(projectionType.toString());
        // TODO: what to return here?
        //return Camera.PROJECTION_TYPE_GENERIC;
        }
        throw new IllegalArgumentException(projectionType.toString());
    }

    private Matrix toM3G(List<Float> projectionTransform)
    {
        Float[] floatObjects = projectionTransform.toArray(new Float[16]);
        float[] floats = new float[16];
        for (int i = 0; i < floats.length; i++)
        {
            floats[i] = floatObjects[i].floatValue();
        }
        return new Matrix(floats);
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
