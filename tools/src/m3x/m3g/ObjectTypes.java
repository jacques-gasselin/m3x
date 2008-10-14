package m3x.m3g;

/**
 * Object identifiers enumerated. See
 * http://www.java2me.org/m3g/file-format.html#ObjectTypeValues
 * for more information.
 * 
 * @author jsaarinen
 */
public interface ObjectTypes
{
    public static final byte OBJECT_HEADER = 0;
    public static final byte ANIMATION_CONTROLLER = 1;
    public static final byte ANIMATION_TRACK = 2;
    public static final byte APPEARANCE = 3;
    public static final byte BACKGROUND = 4;
    public static final byte CAMERA = 5;
    public static final byte COMPOSITING_MODE = 6;
    public static final byte FOG = 7;
    public static final byte POLYGON_MODE = 8;
    public static final byte GROUP = 9;
    public static final byte IMAGE_2D = 10;
    public static final byte TRIANGLE_STRIP_ARRAY = 11;
    public static final byte LIGHT = 12;
    public static final byte MATERIAL = 13;
    public static final byte MESH = 14;
    public static final byte MORPHING_MESH = 15;
    public static final byte SKINNED_MESH = 16;
    public static final byte TEXTURE_2D = 17;
    public static final byte SPRITE = 18;
    public static final byte KEYFRAME_SEQUENCE = 19;
    public static final byte VERTEX_ARRAY = 20;
    public static final byte VERTEX_BUFFER = 21;
    public static final byte WORLD = 22;
    public static final byte EXTERNAL_REFERENCE = (byte) 255;
}
