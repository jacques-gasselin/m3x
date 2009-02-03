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
        to.setNormals(getNormals(translator, from.getNormals()));
        to.setColors(getColors(translator, from.getColors()));
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

    private static final m3x.m3g.VertexArray getNormals(
        XmlToBinaryTranslator translator, m3x.xml.VertexBuffer.Normals from)
    {
        if (from == null)
        {
            return null;
        }
        m3x.xml.VertexArray xmlVA = getObjectOrInstance(
            from.getVertexArray(), from.getVertexArrayInstance());
        return (m3x.m3g.VertexArray) translator.getReference(xmlVA);
    }

    private static final m3x.m3g.VertexArray getColors(
        XmlToBinaryTranslator translator, m3x.xml.VertexBuffer.Colors from)
    {
        if (from == null)
        {
            return null;
        }
        m3x.xml.VertexArray xmlVA = getObjectOrInstance(
            from.getVertexArray(), from.getVertexArrayInstance());
        return (m3x.m3g.VertexArray) translator.getReference(xmlVA);
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
