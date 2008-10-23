package m3x.translation;


import java.util.ArrayList;
import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.Object3D;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.NodeType;

public class GroupTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Group group = (m3x.xml.Group) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        m3x.xml.TransformableType transformable = (m3x.xml.TransformableType) group;
        Matrix transform = getM3GTransformMatrix(transformable);
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        List<NodeType> childNodes = group.getChildNodes();
        List<ObjectIndex> childObjectIndices = new ArrayList<ObjectIndex>();
        for (NodeType node : childNodes)
        {
            int index = searchObjectIndex(this.getXmlRootObject(), node);
            childObjectIndices.add(new ObjectIndex(index));
        }
        ObjectIndex[] children = childObjectIndices.toArray(new ObjectIndex[childObjectIndices.size()]);

        try
        {
            this.setBinaryObject(new m3x.m3g.Group(animationTracks, userParameters, transform, group.isRenderingEnabled(), group.isPickingEnabled(), (byte) (group.getAlphaFactor() * 255.0f + 0.5f), group.getScope(), children));
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
