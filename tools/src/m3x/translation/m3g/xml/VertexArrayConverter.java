package m3x.translation.m3g.xml;

import java.util.List;
import m3x.translation.m3g.XmlToBinaryTranslator;


public class VertexArrayConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        final m3x.m3g.VertexArray to = new m3x.m3g.VertexArray();
        setFromXML(translator, to, (m3x.xml.VertexArray)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.VertexArray to, m3x.xml.VertexArray from)
    {
        super.setFromXML(translator, to, from);
        final List<Integer> values = from.getIntArray();
        
        final int arraySize = values.size();
        final int componentCount = from.getComponentCount();
        final int vertexCount = arraySize / componentCount;
        final String componentTypeStr = from.getComponentType().value();
        final int componentType = to.getComponentType(componentTypeStr);
        //System.out.println("componentType: " + componentTypeStr
        //    + " -> " + componentType);

        to.setSizeAndType(vertexCount, componentCount, componentType);
        to.set(0, vertexCount, values);
    }
}
