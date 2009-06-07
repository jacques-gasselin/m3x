package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public abstract class Renderer
{
    public abstract void clear(Background background);
    public abstract void setViewport(int x, int y, int width, int height);

}
