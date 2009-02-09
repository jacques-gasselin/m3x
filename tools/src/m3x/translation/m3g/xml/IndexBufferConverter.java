/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
