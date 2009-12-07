/**
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import m3x.m3g.Sprite3D;
import m3x.m3g.Texture2D;
import m3x.m3g.TriangleStripArray;
import m3x.m3g.VertexArray;
import m3x.m3g.VertexBuffer;
import m3x.m3g.World;

/**
 * A factory class that creates M3G objects.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
abstract class ObjectFactory
{
    /**
     * Hides the constructor for Utility class
     */
    private ObjectFactory()
    {

    }

    public static final SectionSerializable getInstance(int objectType)
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
                return new Sprite3D();
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
