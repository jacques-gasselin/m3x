package m3x.translation;


import java.util.ArrayList;
import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.NodeType;
;

public class WorldTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.m3gObject != null)
        {
            return this.m3gObject;
        }

        // do translation
        m3x.xml.World world = (m3x.xml.World) this.m3xObject;
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        m3x.xml.TransformableType transformable = (m3x.xml.TransformableType) world;
        Matrix transform = getM3GTransformMatrix(transformable);
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        List<NodeType> childNodes = world.getChildNodes();
        List<ObjectIndex> childObjectIndices = new ArrayList<ObjectIndex>();
        for (NodeType node : childNodes)
        {
            Object toBeFound = node.getId();
            int index = AbstractTranslator.searchObjectIndex(this.m3xRoot, toBeFound);
            childObjectIndices.add(new ObjectIndex(index));
        }
        ObjectIndex[] children = childObjectIndices.toArray(new ObjectIndex[childObjectIndices.size()]);
        int activeCameraIndex = searchObjectIndex(this.m3xRoot, world.getActiveCamera());
        int backgroundIndex = searchObjectIndex(this.m3xRoot, world.getBackground());

        try
        {
            this.m3gObject = new m3x.m3g.objects.World(animationTracks,
                userParameters,
                transform,
                world.isRenderingEnabled(),
                world.isPickingEnabled(),
                (byte) (world.getAlphaFactor() * 255.0f + 0.5f),
                world.getScope(),
                children,
                new ObjectIndex(activeCameraIndex),
                new ObjectIndex(backgroundIndex));
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
