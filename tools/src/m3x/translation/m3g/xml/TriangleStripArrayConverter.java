package m3x.translation.m3g.xml;

import java.util.List;
import m3x.translation.m3g.XmlToBinaryTranslator;


public class TriangleStripArrayConverter extends IndexBufferConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.TriangleStripArray to = new m3x.m3g.TriangleStripArray();
        setFromXML(translator, to, (m3x.xml.TriangleStripArray)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.TriangleStripArray to, m3x.xml.TriangleStripArray from)
    {
        super.setFromXML(translator, to, from);

        List<Integer> stripLengthList = from.getStripLengths();
        final int stripLengthCount = stripLengthList.size();
        final int[] stripLengths = new int[stripLengthCount];
        for (int i = 0; i < stripLengthCount; ++i)
        {
            stripLengths[i] = stripLengthList.get(i);
        }
        to.setStripLengths(stripLengths);

    }
}
