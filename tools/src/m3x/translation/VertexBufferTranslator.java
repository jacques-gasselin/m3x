package m3x.translation;


import java.util.List;

import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.VertexBuffer.TextureCoordinate;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.VertexBuffer.Texcoords;

public class VertexBufferTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.m3gObject != null)
        {
            return this.m3gObject;
        }

        // do translation
        m3x.xml.VertexBuffer vb = (m3x.xml.VertexBuffer) this.m3xObject;
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int positionsIndex = searchObjectIndex(this.m3xRoot, vb.getPositions());
        int normalsIndex = searchObjectIndex(this.m3xRoot, vb.getNormals());
        int colorsIndex = searchObjectIndex(this.m3xRoot, vb.getColors());

        List<Texcoords> list = vb.getTexcoords();
        TextureCoordinate[] textureCoordinates = new TextureCoordinate[list.size()];
        for (int i = 0; i < textureCoordinates.length; i++)
        {
            Texcoords tc = list.get(i);
            int index = searchObjectIndex(this.m3xRoot, tc.getVertexArray());
            float[] bias = this.translateFloatArray(tc.getBias());
            float scale = tc.getScale().floatValue();
            textureCoordinates[i] = new TextureCoordinate(new ObjectIndex(index), bias, scale);
        }

        this.m3gObject = new m3x.m3g.objects.VertexBuffer(animationTracks,
            userParameters,
            translateColorRGBA(vb.getDefaultColor()),
            new ObjectIndex(positionsIndex),
            this.translateFloatArray(vb.getPositions().getBias()),
            vb.getPositions().getScale().floatValue(),
            new ObjectIndex(normalsIndex),
            new ObjectIndex(colorsIndex),
            textureCoordinates);

        return this.m3gObject;
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
