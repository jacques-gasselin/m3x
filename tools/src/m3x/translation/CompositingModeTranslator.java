package m3x.translation;


import m3x.m3g.FileFormatException;
import m3x.m3g.objects.CompositingMode;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.CompositingModeType;

public class CompositingModeTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.CompositingMode cm = (m3x.xml.CompositingMode) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        try
        {
            this.setBinaryObject(
                new m3x.m3g.objects.CompositingMode(
                    animationTracks,
                    userParameters,
                    cm.isDepthTestEnabled(),
                    cm.isDepthWriteEnabled(),
                    cm.isColorWriteEnabled(),
                    cm.isAlphaWriteEnabled(),
                    toM3G(cm.getBlending()),
                    (int) (cm.getAlphaThreshold() * 255.0f + 0.5f),
                    cm.getDepthOffsetFactor(),
                    cm.getDepthOffsetUnits()));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    private int toM3G(CompositingModeType blending)
    {
        if (blending.equals(CompositingModeType.ALPHA))
        {
            return CompositingMode.ALPHA;
        }
        if (blending.equals(CompositingModeType.ALPHA_ADD))
        {
            return CompositingMode.ALPHA_ADD;
        }
        if (blending.equals(CompositingModeType.ALPHA_DARKEN))
        {
            // TODO: what to return here?
            //return CompositingMode.ALPHA_ADD;
            throw new IllegalArgumentException(blending.toString());
        }
        if (blending.equals(CompositingModeType.ALPHA_PREMULTIPLY))
        {
            // TODO: what to return here?
            //return CompositingMode.ALPHA_ADD;
            throw new IllegalArgumentException(blending.toString());
        }
        if (blending.equals(CompositingModeType.MODULATE))
        {
            return CompositingMode.MODULATE;
        }
        if (blending.equals(CompositingModeType.MODULATE_INV))
        {
            // TODO: what to return here?
            //return CompositingMode.MODULATE_INV;
            throw new IllegalArgumentException(blending.toString());
        }
        if (blending.equals(CompositingModeType.MODULATE_X_2))
        {
            return CompositingMode.MODULATE_X2;
        }
        if (blending.equals(CompositingModeType.REPLACE))
        {
            return CompositingMode.REPLACE;
        }
        throw new IllegalArgumentException(blending.toString());
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
