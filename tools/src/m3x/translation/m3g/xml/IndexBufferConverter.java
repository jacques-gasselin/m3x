package m3x.translation.m3g.xml;

import java.util.List;
import m3x.translation.m3g.XmlToBinaryTranslator;

/**
 *
 * @author jgasseli
 */
public abstract class IndexBufferConverter extends Object3DConverter
{
    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.IndexBuffer to, m3x.xml.IndexBuffer from)
    {
        super.setFromXML(translator, to, from);

        //convert indices to implicit or explicit
        //depending on the index list.
        List<Integer> indexList = from.getIndices();
        int maxIndex = 0;
        final int indexCount = indexList.size();
        final int[] indices = new int[indexCount];
        boolean allImplicit = true;
        for (int i = 0; i < indexCount; ++i)
        {
            final int index = indexList.get(i);
            maxIndex = Math.max(index, maxIndex);
            indices[i] = index;
            
            if (i > 0 && allImplicit)
            {
                //it must be previous + 1 to be an implicit index
                if (index != (indices[i - 1] + 1))
                {
                    allImplicit = false;
                }
            }
        }

        //work out the smallest possible encoding needed.
        int encoding;
        if (maxIndex > 65535)
        {
            encoding = m3x.m3g.IndexBuffer.ENCODING_INT;
        }
        else if (maxIndex > 255)
        {
            encoding = m3x.m3g.IndexBuffer.ENCODING_SHORT;
        }
        else
        {
            encoding = m3x.m3g.IndexBuffer.ENCODING_BYTE;
        }
        
        if (!allImplicit)
        {
            to.setEncoding(encoding | m3x.m3g.IndexBuffer.ENCODING_EXPLICIT);
            to.setIndices(indices);
        }
        else
        {
            to.setEncoding(encoding);
            to.setFirstIndex(indices[0]);
        }
    }
}
