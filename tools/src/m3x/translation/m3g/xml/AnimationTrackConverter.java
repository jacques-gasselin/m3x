package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlTranslator;


public class AnimationTrackConverter extends Object3DConverter
{
    private static m3x.m3g.AnimationController getController(
        XmlTranslator translator, m3x.xml.AnimationTrack from)
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
        XmlTranslator translator, m3x.xml.AnimationTrack from)
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

    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.AnimationTrack from = (m3x.xml.AnimationTrack)originalFrom;
        m3x.m3g.AnimationTrack to = new m3x.m3g.AnimationTrack();
        translator.setObject(from, to);
        super.toBinary(translator, from);

        to.setTargetProperty(from.getTargetProperty().value());
        to.setController(getController(translator, from));
        to.setKeyframeSequence(getKeyframeSequence(translator, from));
    }
}
