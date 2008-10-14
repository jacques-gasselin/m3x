package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.MorphingMesh.TargetBuffer;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.MorphingMesh.Morphtarget;

public class MorphingMeshTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.m3gObject != null)
        {
            return this.m3gObject;
        }

        // do translation
        m3x.xml.MorphingMesh mm = (m3x.xml.MorphingMesh) this.m3xObject;
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        m3x.xml.TransformableType transformable = (m3x.xml.TransformableType) mm;
        Matrix transform = getM3GTransformMatrix(transformable);
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        List<Morphtarget> list = mm.getMorphtarget();
        TargetBuffer[] morphTargets = new TargetBuffer[list.size()];
        for (int i = 0; i < morphTargets.length; i++)
        {
            Morphtarget target = list.get(i);
            morphTargets[i] = new TargetBuffer(new ObjectIndex(searchObjectIndex(this.m3xRoot, target.getVertexBufferInstance().getRef())),
                target.getWeight().floatValue());
        }
        try
        {
            this.m3gObject = new m3x.m3g.objects.MorphingMesh(animationTracks,
                userParameters,
                transform,
                mm.isRenderingEnabled(),
                mm.isPickingEnabled(),
                (byte) (mm.getAlphaFactor() * 255.0f + 0.5f),
                mm.getScope(),
                morphTargets);
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.m3gObject;
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
