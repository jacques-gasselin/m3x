package m3x.translation.m3g.xml;

import java.util.List;
import m3x.translation.m3g.XmlToBinaryTranslator;


public class VertexArrayConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.VertexArray to = new m3x.m3g.VertexArray();
        setFromXML(translator, to, (m3x.xml.VertexArray)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.VertexArray to, m3x.xml.VertexArray from)
    {
        super.setFromXML(translator, to, from);
        List<Integer> values = from.getIntArray();
        
        final int vertexCount = values.size();
        final int componentCount = from.getComponentCount();
        final int componentType = to.getComponentType(
            from.getComponentType().value());

        to.setSizeAndType(vertexCount, componentCount, componentType);
        to.set(0, vertexCount, values);
    }
}
