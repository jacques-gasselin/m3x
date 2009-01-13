package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlTranslator;



public class GroupConverter extends NodeConverter
{
    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.Group from = (m3x.xml.Group)originalFrom;
        m3x.m3g.Group to = new m3x.m3g.Group();
        translator.setObject(from, to);
        super.toBinary(translator, from);

        
    }
}
