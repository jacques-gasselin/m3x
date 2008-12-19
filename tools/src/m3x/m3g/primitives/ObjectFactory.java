package m3x.m3g.primitives;

import m3x.m3g.*;

/**
 * A factory class that creates M3G objects.
 * 
 * @author jsaarinen
 */
abstract class ObjectFactory
{
    /**
     * Hides the constructor for Utility class
     */
    private ObjectFactory()
    {

    }

    public static final M3GTypedObject getInstance(int objectType)
    {
        switch (objectType)
        {
            case ObjectTypes.ANIMATION_CONTROLLER:
                return new AnimationController();
            case ObjectTypes.ANIMATION_TRACK:
                return new AnimationTrack();
            case ObjectTypes.APPEARANCE:
                return new Appearance();
            case ObjectTypes.BACKGROUND:
                return new Background();
            case ObjectTypes.CAMERA:
                return new Camera();
            case ObjectTypes.COMPOSITING_MODE:
                return new CompositingMode();
            case ObjectTypes.EXTERNAL_REFERENCE:
                return new ExternalReference();
            case ObjectTypes.FOG:
                return new Fog();
            case ObjectTypes.GROUP:
                return new Group();
            case ObjectTypes.IMAGE_2D:
                return new Image2D();
            case ObjectTypes.KEYFRAME_SEQUENCE:
                return new KeyframeSequence();
            case ObjectTypes.LIGHT:
                return new Light();
            case ObjectTypes.MATERIAL:
                return new Material();
            case ObjectTypes.MESH:
                return new Mesh();
            case ObjectTypes.MORPHING_MESH:
                return new MorphingMesh();
            case ObjectTypes.OBJECT_HEADER:
                return new Header();
            case ObjectTypes.POLYGON_MODE:
                return new PolygonMode();
            case ObjectTypes.SKINNED_MESH:
                return new SkinnedMesh();
            case ObjectTypes.SPRITE:
                return new Sprite();
            case ObjectTypes.TEXTURE_2D:
                return new Texture2D();
            case ObjectTypes.TRIANGLE_STRIP_ARRAY:
                return new TriangleStripArray();
            case ObjectTypes.VERTEX_ARRAY:
                return new VertexArray();
            case ObjectTypes.VERTEX_BUFFER:
                return new VertexBuffer();
            case ObjectTypes.WORLD:
                return new World();
            default:
                throw new IllegalArgumentException("Unknown object type: " + objectType);
        }
    }
}
