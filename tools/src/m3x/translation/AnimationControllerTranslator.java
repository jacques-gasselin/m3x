package m3x.translation;


import m3x.m3g.Object3D;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationControllerTranslator extends AbstractTranslator
{
    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.AnimationController ac = (m3x.xml.AnimationController) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        this.setBinaryObject(new m3x.m3g.AnimationController(animationTracks, userParameters, ac.getSpeed(), ac.getWeight(), ac.getActiveIntervalStart(), ac.getActiveIntervalEnd(), ac.getReferenceSequenceTime(), ac.getReferenceWorldTime()));

        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
