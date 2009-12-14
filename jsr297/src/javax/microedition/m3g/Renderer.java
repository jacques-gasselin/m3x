package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public abstract class Renderer
{
    public abstract void clear(Background background);
    public abstract void setViewport(int x, int y, int width, int height);
    public abstract void setProjectionView(Transform projection, Transform view);
    public abstract void resetLights();
    public abstract void setLight(int index, Light light, Transform transform);
    public abstract void render(VertexBuffer vertices, IndexBuffer primitives, Appearance appearance, Transform transform, int scope, float alphaFactor);

}
