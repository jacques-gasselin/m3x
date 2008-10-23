package m3x.translation;


import m3x.m3g.FileFormatException;
import m3x.m3g.Object3D;
import m3x.m3g.PolygonMode;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.PolygonCullingModeType;
import m3x.xml.PolygonShadingModeType;
import m3x.xml.PolygonWindingModeType;

public class PolygonModeTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.PolygonMode polygonMode = (m3x.xml.PolygonMode) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        try
        {
            this.setBinaryObject(new m3x.m3g.PolygonMode(animationTracks, userParameters, toM3G(polygonMode.getCulling()), toM3G(polygonMode.getShading()), toM3G(polygonMode.getWinding()), polygonMode.isTwoSidedLightingEnabled().booleanValue(), polygonMode.isLocalCameraLightingEnabled().booleanValue(), polygonMode.isPerspectiveCorrectionEnabled().booleanValue()));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    private int toM3G(PolygonWindingModeType winding)
    {
        if (winding.equals(PolygonWindingModeType.CW))
        {
            return PolygonMode.WINDING_CW;
        }
        if (winding.equals(PolygonWindingModeType.CCW))
        {
            return PolygonMode.WINDING_CCW;
        }
        throw new IllegalArgumentException(winding.toString());
    }

    private int toM3G(PolygonShadingModeType shading)
    {
        if (shading.equals(PolygonShadingModeType.SMOOTH))
        {
            return PolygonMode.SHADE_SMOOTH;
        }
        if (shading.equals(PolygonShadingModeType.FLAT))
        {
            return PolygonMode.SHADE_FLAT;
        }
        throw new IllegalArgumentException(shading.toString());
    }

    private int toM3G(PolygonCullingModeType culling)
    {
        if (culling.equals(PolygonCullingModeType.FRONT))
        {
            return PolygonMode.CULL_FRONT;
        }
        if (culling.equals(PolygonCullingModeType.BACK))
        {
            return PolygonMode.CULL_BACK;
        }
        if (culling.equals(PolygonCullingModeType.NONE))
        {
            return PolygonMode.CULL_NONE;
        }
        throw new IllegalArgumentException(culling.toString());
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
