package m3x.translation;


import java.util.List;

import m3x.m3g.Object3D;
import m3x.m3g.VertexBuffer.TextureCoordinate;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.VertexBuffer.Texcoords;

public class VertexBufferTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.VertexBuffer vb = (m3x.xml.VertexBuffer) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int positionsIndex = searchObjectIndex(this.getXmlRootObject(), vb.getPositions());
        int normalsIndex = searchObjectIndex(this.getXmlRootObject(), vb.getNormals());
        int colorsIndex = searchObjectIndex(this.getXmlRootObject(), vb.getColors());

        List<Texcoords> list = vb.getTexcoords();
        TextureCoordinate[] textureCoordinates = new TextureCoordinate[list.size()];
        for (int i = 0; i < textureCoordinates.length; i++)
        {
            Texcoords tc = list.get(i);
            int index = searchObjectIndex(this.getXmlRootObject(), tc.getVertexArray());
            float[] bias = this.translateFloatArray(tc.getBias());
            float scale = tc.getScale().floatValue();
            textureCoordinates[i] = new TextureCoordinate(new ObjectIndex(index), bias, scale);
        }

        this.setBinaryObject(new m3x.m3g.VertexBuffer(animationTracks, userParameters, translateColorRGBA(vb.getDefaultColor()), new ObjectIndex(positionsIndex), this.translateFloatArray(vb.getPositions().getBias()), vb.getPositions().getScale().floatValue(), new ObjectIndex(normalsIndex), new ObjectIndex(colorsIndex), textureCoordinates));

        return this.getBinaryObject();
    }

    private float[] translateFloatArray(List<Float> floats)
    {
        float[] array = new float[floats.size()];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = floats.get(i).floatValue();
        }
        return array;
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
