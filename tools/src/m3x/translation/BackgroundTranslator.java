package m3x.translation;


import m3x.m3g.FileFormatException;
import m3x.m3g.Background;
import m3x.m3g.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.BackgroundRepeatType;

public class BackgroundTranslator extends AbstractTranslator
{
    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Background background = (m3x.xml.Background) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int backgroundIndex = searchObjectIndex(this.getXmlRootObject(), background.getImage2DInstance().getRef());
        try
        {
            this.setBinaryObject(new m3x.m3g.Background(animationTracks, userParameters, translateColorRGBA(background.getBackgroundColor()), new ObjectIndex(backgroundIndex), toM3G(background.getBackgroundImageModeX()), toM3G(background.getBackgroundImageModeY()), background.getCropX().intValue(), background.getCropY().intValue(), background.getCropWidth().intValue(), background.getCropHeight().intValue(), background.isDepthClearEnabled(), background.isColorClearEnabled()));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
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

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
