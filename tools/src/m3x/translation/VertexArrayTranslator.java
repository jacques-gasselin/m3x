package m3x.translation;

import java.util.List;

import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.Deserialiser;
import m3x.xml.M3G;
;

public class VertexArrayTranslator extends AbstractTranslator
{
    public void set(m3x.xml.Object3D object, M3G root, Deserialiser deserialiser)
    {
        super.set((m3x.xml.VertexArray) object, root, deserialiser);
    }

    public void set(Object3D object)
    {
        super.set((m3x.m3g.objects.VertexArray) object);
    }

    public Object3D toM3G()
    {
        if (this.getBinaryObject() == null)
        {
            m3x.xml.VertexArray va = (m3x.xml.VertexArray) this.getXmlObject();
            ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
            Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
            List<Integer> ints = va.getIntArray();
            switch (va.getComponentType())
            {
                case BYTE:
                    System.out.println(va.getComponentCount());
                    byte[] byteComponents = new byte[ints.size()];
                    for (int i = 0; i < ints.size(); i++)
                    {
                        byteComponents[i] = ints.get(i).byteValue();
                    }
                    setBinaryObject(new m3x.m3g.objects.VertexArray(animationTracks, userParameters, byteComponents, false));
                case SHORT:
                    short[] shortComponents = new short[ints.size()];
                    for (int i = 0; i < ints.size(); i++)
                    {
                        shortComponents[i] = ints.get(i).shortValue();
                    }
                    setBinaryObject(new m3x.m3g.objects.VertexArray(animationTracks, userParameters, shortComponents, false));
            }
        }
        // else translation is done already
        return getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return this.getXmlObject();
    }
}
