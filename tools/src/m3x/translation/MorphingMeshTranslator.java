package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.Object3D;
import m3x.m3g.MorphingMesh.TargetBuffer;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.MorphingMesh.Morphtarget;

public class MorphingMeshTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.MorphingMesh mm = (m3x.xml.MorphingMesh) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        m3x.xml.TransformableType transformable = (m3x.xml.TransformableType) mm;
        Matrix transform = getM3GTransformMatrix(transformable);
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        List<Morphtarget> list = mm.getMorphtarget();
        TargetBuffer[] morphTargets = new TargetBuffer[list.size()];
        for (int i = 0; i < morphTargets.length; i++)
        {
            Morphtarget target = list.get(i);
            morphTargets[i] = new TargetBuffer(new ObjectIndex(searchObjectIndex(this.getXmlRootObject(), target.getVertexBufferInstance().getRef())),
                target.getWeight().floatValue());
        }
        try
        {
            this.setBinaryObject(new m3x.m3g.MorphingMesh(animationTracks, userParameters, transform, mm.isRenderingEnabled(), mm.isPickingEnabled(), (byte) (mm.getAlphaFactor() * 255.0f + 0.5f), mm.getScope(), morphTargets));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
