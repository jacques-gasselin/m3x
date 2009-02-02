package m3x.translation.m3g.xml;

import java.util.List;
import m3x.translation.m3g.XmlToBinaryTranslator;


public class VertexBufferConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.VertexBuffer to = new m3x.m3g.VertexBuffer();
        setFromXML(translator, to, (m3x.xml.VertexBuffer)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, m3x.xml.VertexBuffer from)
    {
        super.setFromXML(translator, to, from);

        setPositions(translator, to, from.getPositions());
        setNormals(translator, to, from.getNormals());
        setColors(translator, to, from.getColors());
        setTexCoords(translator, to, from.getTexcoords());
    }


    private static final float[] getBias(List<Float> list)
    {
        if (list == null)
        {
            return null;
        }

        float[] bias = new float[3];
        final int size = Math.min(bias.length, list.size());
        for (int i = 0; i < size; ++i)
        {
            bias[i] = list.get(i);
        }
        return bias;
    }

    private static final void setPositions(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, m3x.xml.VertexBuffer.Positions from)
    {
        final float[] bias = getBias(from.getBias());
        final float scale = from.getScale();
        m3x.xml.VertexArray xmlVA = getObjectOrInstance(
            from.getVertexArray(), from.getVertexArrayInstance());
        m3x.m3g.VertexArray va = (m3x.m3g.VertexArray)
            translator.getReference(xmlVA);
        to.setPositions(va, scale, bias);
    }

    private static final void setNormals(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, m3x.xml.VertexBuffer.Normals from)
    {
        m3x.xml.VertexArray xmlVA = getObjectOrInstance(
            from.getVertexArray(), from.getVertexArrayInstance());
        m3x.m3g.VertexArray va = (m3x.m3g.VertexArray)
            translator.getReference(xmlVA);
        to.setNormals(va);
    }

    private static final void setColors(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, m3x.xml.VertexBuffer.Colors from)
    {
        m3x.xml.VertexArray xmlVA = getObjectOrInstance(
            from.getVertexArray(), from.getVertexArrayInstance());
        m3x.m3g.VertexArray va = (m3x.m3g.VertexArray)
            translator.getReference(xmlVA);
        to.setColors(va);
    }

    private static final void setTexCoords(XmlToBinaryTranslator translator,
        m3x.m3g.VertexBuffer to, List<m3x.xml.VertexBuffer.Texcoords> fromList)
    {
        final int texCoordCount = fromList.size();
        to.setTexCoordCount(texCoordCount);
        for (int i = 0; i < texCoordCount; ++i)
        {
            m3x.xml.VertexBuffer.Texcoords from = fromList.get(i);
            final float[] bias = getBias(from.getBias());
            final float scale = from.getScale();
            m3x.xml.VertexArray xmlVA = getObjectOrInstance(
                from.getVertexArray(), from.getVertexArrayInstance());
            m3x.m3g.VertexArray va = (m3x.m3g.VertexArray)
                translator.getReference(xmlVA);
            to.setTexCoords(i, va, scale, bias);
        }
    }

}
