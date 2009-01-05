package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlTranslator;


public class AppearanceConverter extends Object3DConverter
{
    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.Appearance from = (m3x.xml.Appearance)originalFrom;
        m3x.m3g.Appearance to = new m3x.m3g.Appearance();
        translator.setObject(from, to);
        super.toBinary(translator, from);

        //FIXME Fog mode
        //FIXME Compositing mode.
        //FIXME Polygon mode.
    }
}
