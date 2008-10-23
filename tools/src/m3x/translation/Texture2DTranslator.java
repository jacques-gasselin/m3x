package m3x.translation;


import m3x.m3g.Object3D;
import m3x.m3g.Texture2D;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.primitives.Vector3D;
import m3x.xml.Texture2DBlendModeType;
import m3x.xml.Texture2DWrapModeType;
import m3x.xml.TextureFilterModeType;
import m3x.xml.TextureMipmapModeType;

/**
 * Translator for Texture2D object.
 * 
 * @author jsaarinen
 */
public class Texture2DTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Texture2D texture = (m3x.xml.Texture2D) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int textureIndex = searchObjectIndex(this.getXmlRootObject(), texture.getImage2DInstance().getRef());

        float x, y, z;
        x = texture.getTranslation().get(0).floatValue();
        y = texture.getTranslation().get(1).floatValue();
        z = texture.getTranslation().get(2).floatValue();
        Vector3D translation = new Vector3D(x, y, z);

        x = texture.getScale().get(0).floatValue();
        y = texture.getScale().get(1).floatValue();
        z = texture.getScale().get(2).floatValue();
        Vector3D scale = new Vector3D(x, y, z);

        float orientationAngle = texture.getOrientation().getAngle().floatValue();

        x = texture.getOrientation().getValue().get(0).floatValue();
        y = texture.getOrientation().getValue().get(1).floatValue();
        z = texture.getOrientation().getValue().get(2).floatValue();
        Vector3D orientationAxis = new Vector3D(x, y, z);

        ColorRGB blendColor = AbstractTranslator.translateColorRGB(texture.getBlendColor());

        this.setBinaryObject(new m3x.m3g.Texture2D(animationTracks, userParameters, translation, scale, orientationAngle, orientationAxis, new ObjectIndex(textureIndex), blendColor, toM3G(texture.getBlending()), toM3G(texture.getWrappingS()), toM3G(texture.getWrappingT()), toM3G(texture.getLevelFilter()), toM3G(texture.getImageFilter())));
        return this.getBinaryObject();
    }

    private int toM3G(TextureFilterModeType imageFilter)
    {
        if (imageFilter.toString().equals(TextureFilterModeType.ANISOTROPIC))
        {
            // TODO: what to return here?
            //return Texture2D.
            throw new IllegalArgumentException(imageFilter.toString());
        }
        if (imageFilter.toString().equals(TextureFilterModeType.LINEAR))
        {
            return Texture2D.FILTER_LINEAR;
        }
        if (imageFilter.toString().equals(TextureFilterModeType.NEAREST))
        {
            return Texture2D.FILTER_NEAREST;
        }
        throw new IllegalArgumentException(imageFilter.toString());
    }

    private int toM3G(TextureMipmapModeType levelFilter)
    {
        if (levelFilter.equals(TextureMipmapModeType.BASE_LEVEL))
        {
            return Texture2D.FILTER_BASE_LEVEL;
        }
        if (levelFilter.equals(TextureMipmapModeType.LINEAR))
        {
            return Texture2D.FILTER_LINEAR;
        }
        if (levelFilter.equals(TextureMipmapModeType.NEAREST))
        {
            return Texture2D.FILTER_NEAREST;
        }
        throw new IllegalArgumentException(levelFilter.toString());
    }

    private int toM3G(Texture2DWrapModeType wrapping)
    {
        if (wrapping.equals(Texture2DWrapModeType.CLAMP))
        {
            return Texture2D.WRAP_CLAMP;
        }
        if (wrapping.equals(Texture2DWrapModeType.MIRROR))
        {
            // TODO: what to return here? Is this M3G 2.0 feature?
            //return Texture2D.WRAP_MIRROR;
            throw new IllegalArgumentException(wrapping.toString());
        }
        if (wrapping.equals(Texture2DWrapModeType.REPEAT))
        {
            return Texture2D.WRAP_REPEAT;
        }
        throw new IllegalArgumentException(wrapping.toString());
    }

    private int toM3G(Texture2DBlendModeType blending)
    {
        if (blending.equals(Texture2DBlendModeType.ADD))
        {
            return Texture2D.FUNC_ADD;
        }
        if (blending.equals(Texture2DBlendModeType.BLEND))
        {
            return Texture2D.FUNC_BLEND;
        }
        if (blending.equals(Texture2DBlendModeType.DECAL))
        {
            return Texture2D.FUNC_DECAL;
        }
        if (blending.equals(Texture2DBlendModeType.DOT_3))
        {
            // TODO: what to return here?
            throw new IllegalArgumentException(blending.toString());
        }
        if (blending.equals(Texture2DBlendModeType.MODULATE))
        {
            return Texture2D.FUNC_MODULATE;
        }
        if (blending.equals(Texture2DBlendModeType.REPLACE))
        {
            return Texture2D.FUNC_REPLACE;
        }
        throw new IllegalArgumentException(blending.toString());
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
