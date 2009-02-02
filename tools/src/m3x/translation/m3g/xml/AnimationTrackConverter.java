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
        m3x.xml.AnimationController controller = getObjectOrInstance(
            from.getController(), from.getControllerInstance());
        return (m3x.m3g.AnimationController) translator.getObject(controller);
    }

    private static m3x.m3g.KeyframeSequence getKeyframeSequence(
        XmlToBinaryTranslator translator, m3x.xml.AnimationTrack from)
    {
        m3x.xml.KeyframeSequence sequence = getObjectOrInstance(
            from.getKeyframeSequence(), from.getKeyframeSequenceInstance());
        return (m3x.m3g.KeyframeSequence) translator.getObject(sequence);
    }
}
