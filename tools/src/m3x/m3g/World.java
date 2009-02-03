package m3x.m3g;

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

/**
 * See http://java2me.org/m3g/file-format.html#World<br>
  ObjectIndex   activeCamera;<br>
  ObjectIndex   background;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class World extends Group implements SectionSerialisable
{
    private Camera activeCamera;
    private Background background;

    public World()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setActiveCamera((Camera)deserialiser.readReference());
        setBackground((Background)deserialiser.readReference());
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(getActiveCamera());
        serialiser.writeReference(getBackground());
    }

    @Override
    public int getSectionObjectType()
    {
        return ObjectTypes.WORLD;
    }

    public Camera getActiveCamera()
    {
        return this.activeCamera;
    }

    public Background getBackground()
    {
        return this.background;
    }

    public void setActiveCamera(Camera activeCamera)
    {
        this.activeCamera = activeCamera;
    }

    public void setBackground(Background background)
    {
        this.background = background;
    }
}
