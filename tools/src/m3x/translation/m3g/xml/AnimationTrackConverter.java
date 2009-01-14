package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class AnimationTrackConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.AnimationTrack to = new m3x.m3g.AnimationTrack();
        setFromXML(translator, to, (m3x.xml.AnimationTrack)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.AnimationTrack to, m3x.xml.AnimationTrack from)
    {
        super.setFromXML(translator, to, from);
        to.setTargetProperty(from.getTargetProperty().value());
        to.setController(getController(translator, from));
        to.setKeyframeSequence(getKeyframeSequence(translator, from));
    }
    
    private static m3x.m3g.AnimationController getController(
        XmlToBinaryTranslator translator, m3x.xml.AnimationTrack from)
    {
        m3x.xml.AnimationController controller = null;
        controller = from.getController();
        if (controller == null)
        {
            if (from.getControllerInstance() != null)
            {
                controller = (m3x.xml.AnimationController)
                    from.getControllerInstance().getRef();
            }
        }
        if (controller == null)
        {
            return null;
        }
        return (m3x.m3g.AnimationController) translator.getObject(controller);
    }

    private static m3x.m3g.KeyframeSequence getKeyframeSequence(
        XmlToBinaryTranslator translator, m3x.xml.AnimationTrack from)
    {
        m3x.xml.KeyframeSequence sequence = null;
        sequence = from.getKeyframeSequence();
        if (sequence == null)
        {
            if (from.getControllerInstance() != null)
            {
                sequence = (m3x.xml.KeyframeSequence)
                    from.getKeyframeSequenceInstance().getRef();
            }
        }
        if (sequence == null)
        {
            return null;
        }
        return (m3x.m3g.KeyframeSequence) translator.getObject(sequence);
    }
}
