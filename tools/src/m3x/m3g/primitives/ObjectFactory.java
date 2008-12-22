package m3x.m3g.primitives;

import m3x.m3g.AnimationController;
import m3x.m3g.AnimationTrack;
import m3x.m3g.Appearance;
import m3x.m3g.Background;
import m3x.m3g.Camera;
import m3x.m3g.CompositingMode;
import m3x.m3g.ExternalReference;
import m3x.m3g.Fog;
import m3x.m3g.Group;
import m3x.m3g.Image2D;
import m3x.m3g.KeyframeSequence;
import m3x.m3g.Light;
import m3x.m3g.Material;
import m3x.m3g.Mesh;
import m3x.m3g.MorphingMesh;
import m3x.m3g.PolygonMode;
import m3x.m3g.SkinnedMesh;
import m3x.m3g.Sprite;
import m3x.m3g.Texture2D;
import m3x.m3g.TriangleStripArray;
import m3x.m3g.VertexArray;
import m3x.m3g.VertexBuffer;
import m3x.m3g.World;

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

    public static final SectionSerialisable getInstance(int objectType)
    {
        switch (objectType)
        {
            case ObjectTypes.HEADER:
                return new Header();
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
