package m3x.translation;


import m3x.m3g.FileFormatException;
import m3x.m3g.Object3D;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationTrackTranslator extends AbstractTranslator
{
    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.AnimationTrack at = (m3x.xml.AnimationTrack) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int animationController = searchObjectIndex(this.getXmlRootObject(), at.getAnimationControllerInstance().getRef());
        int keyframeSequence = searchObjectIndex(this.getXmlRootObject(), at.getKeyframeSequenceInstance().getRef());
        try
        {
            this.setBinaryObject(new m3x.m3g.AnimationTrack(animationTracks, userParameters, new ObjectIndex(keyframeSequence), new ObjectIndex(animationController), (int) at.getUserID()));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
