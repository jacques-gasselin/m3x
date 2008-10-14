package m3x.translation;


import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Background;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.BackgroundRepeatType;
import m3x.xml.Object3DType;

public class BackgroundTranslator extends AbstractTranslator
{
    public Object3D toM3G()
    {
        if (this.m3gObject != null)
        {
            return this.m3gObject;
        }

        // do translation
        m3x.xml.Background background = (m3x.xml.Background) this.m3xObject;
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int backgroundIndex = searchObjectIndex(this.m3xRoot, background.getImage2DInstance().getRef());
        try
        {
            this.m3gObject = new m3x.m3g.objects.Background(animationTracks,
                userParameters,
                translateColorRGBA(background.getBackgroundColor()),
                new ObjectIndex(backgroundIndex),
                toM3G(background.getBackgroundImageModeX()),
                toM3G(background.getBackgroundImageModeY()),
                background.getCropX().intValue(),
                background.getCropY().intValue(),
                background.getCropWidth().intValue(),
                background.getCropHeight().intValue(),
                background.isDepthClearEnabled().booleanValue(),
                background.isColorClearEnabled().booleanValue());
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.m3gObject;
    }

    private int toM3G(BackgroundRepeatType backgroundImageMode)
    {
        if (backgroundImageMode.equals(BackgroundRepeatType.BORDER))
        {
            return Background.MODE_BORDER;
        }
        if (backgroundImageMode.equals(BackgroundRepeatType.REPEAT))
        {
            return Background.MODE_REPEAT;
        }
        throw new IllegalArgumentException(backgroundImageMode.toString());
    }

    public Object3DType toXML()
    {
        return null;
    }
}
