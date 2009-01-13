package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlTranslator;

public class Image2DConverter extends Object3DConverter
{
    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.Image2D from = (m3x.xml.Image2D)originalFrom;
        m3x.m3g.Image2D to = new m3x.m3g.Image2D();
        translator.setObject(originalFrom, to);
        super.toBinary(translator, from);

        to.setFormat(from.getFormat().value());
        to.setMutable(from.isMutable());
        to.setSize(from.getWidth(), from.getHeight());
        to.setPixels(from.getPixels());
        to.setPalette(from.getPalette());
    }
}
