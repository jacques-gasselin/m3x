package m3x.translation;


import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.Object3DType;

public class AnimationControllerTranslator extends AbstractTranslator
{
    public Object3D toM3G()
    {
        if (this.m3gObject != null)
        {
            return this.m3gObject;
        }

        // do translation
        m3x.xml.AnimationController ac = (m3x.xml.AnimationController) this.m3xObject;
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        this.m3gObject = new m3x.m3g.objects.AnimationController(animationTracks,
            userParameters,
            ac.getSpeed(),
            ac.getWeight(),
            ac.getActiveIntervalStart(),
            ac.getActiveIntervalEnd(),
            ac.getReferenceSequenceTime(),
            ac.getReferenceWorldTime());

        return this.m3gObject;
    }

    public Object3DType toXML()
    {
        return null;
    }
}
