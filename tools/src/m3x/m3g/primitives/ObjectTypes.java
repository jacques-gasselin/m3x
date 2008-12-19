package m3x.m3g.primitives;

/**
 * Object identifiers enumerated. See
 * http://www.java2me.org/m3g/file-format.html#ObjectTypeValues
 * for more information.
 * 
 * @author jsaarinen
 */
public interface ObjectTypes
{
    public static final int OBJECT_HEADER = 0;
    public static final int ANIMATION_CONTROLLER = 1;
    public static final int ANIMATION_TRACK = 2;
    public static final int APPEARANCE = 3;
    public static final int BACKGROUND = 4;
    public static final int CAMERA = 5;
    public static final int COMPOSITING_MODE = 6;
    public static final int FOG = 7;
    public static final int POLYGON_MODE = 8;
    public static final int GROUP = 9;
    public static final int IMAGE_2D = 10;
    public static final int TRIANGLE_STRIP_ARRAY = 11;
    public static final int LIGHT = 12;
    public static final int MATERIAL = 13;
    public static final int MESH = 14;
    public static final int MORPHING_MESH = 15;
    public static final int SKINNED_MESH = 16;
    public static final int TEXTURE_2D = 17;
    public static final int SPRITE = 18;
    public static final int KEYFRAME_SEQUENCE = 19;
    public static final int VERTEX_ARRAY = 20;
    public static final int VERTEX_BUFFER = 21;
    public static final int WORLD = 22;
    public static final int EXTERNAL_REFERENCE = 255;
}
