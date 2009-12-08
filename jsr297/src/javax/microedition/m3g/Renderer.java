package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public abstract class Renderer
{
    public abstract void clear(Background background);
    public abstract void setViewport(int x, int y, int width, int height);
    public abstract void setProjectionView(Transform projection, Transform view);
    public abstract void render(VertexBuffer vertices, IndexBuffer primitives, Appearance appearance, Transform transform, int scope);

}
