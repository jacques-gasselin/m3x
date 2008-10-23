package m3x.translation;


import m3x.m3g.Object3D;
import m3x.m3g.primitives.ObjectIndex;
;

public class TriangleStripArrayTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.TriangleStripArray tsa = (m3x.xml.TriangleStripArray) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int[] indices = new int[tsa.getIndices().size()];
        for (int i = 0; i < indices.length; i++)
        {
            indices[i] = tsa.getIndices().get(i);
        }
        int[] stripLengths = new int[tsa.getStripLengths().size()];
        for (int i = 0; i < stripLengths.length; i++)
        {
            stripLengths[i] = tsa.getStripLengths().get(i);
        }

        this.setBinaryObject(new m3x.m3g.TriangleStripArray(animationTracks, userParameters, indices, stripLengths));
        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
