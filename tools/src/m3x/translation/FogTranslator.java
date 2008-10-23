package m3x.translation;


import m3x.m3g.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.FogEquationType;

public class FogTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Fog fog = (m3x.xml.Fog) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        if (fog.getMode().equals(FogEquationType.EXPONENTIAL))
        {
            this.setBinaryObject(new m3x.m3g.Fog(animationTracks, userParameters, AbstractTranslator.translateColorRGB(fog.getColor()), fog.getDensity().floatValue()));
        }
        else if (fog.getMode().equals(FogEquationType.LINEAR))
        {
            this.setBinaryObject(new m3x.m3g.Fog(animationTracks, userParameters, AbstractTranslator.translateColorRGB(fog.getColor()), fog.getNear().floatValue(), fog.getFar().floatValue()));
        }
        else if (fog.getMode().equals(FogEquationType.EXPONENTIAL_SQUARED))
        {
            throw new IllegalArgumentException("exp^2 fog is not supported by M3G 1.0.");
        }
        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
