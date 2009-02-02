package m3x.translation.m3g.xml;

import java.util.List;
import java.util.Vector;
import m3x.translation.m3g.XmlToBinaryTranslator;



public class GroupConverter extends NodeConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Group to = new m3x.m3g.Group();
        setFromXML(translator, to, (m3x.xml.Group)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Group to, m3x.xml.Group from)
    {
        super.setFromXML(translator, to, from);
        to.setAlphaFactor(from.getAlphaFactor());
        to.setPickingEnabled(from.isPickingEnabled());
        to.setRenderingEnabled(from.isRenderingEnabled());
        to.setScope(from.getScope());
        to.setChildNodes(getChildNodes(translator, from));
    }
    
    private static List<m3x.m3g.Node> getChildNodes(
        XmlToBinaryTranslator translator, m3x.xml.Group from)
    {
        List<m3x.m3g.Node> childNodes = null;
        if (from.getChildNodes() != null)
        {
            childNodes = new Vector<m3x.m3g.Node>();
            for (m3x.xml.Node node : from.getChildNodes())
            {
                childNodes.add((m3x.m3g.Node) translator.getReference(node));
            }
        }
        return childNodes;
    }
    
}
