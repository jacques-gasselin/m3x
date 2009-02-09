/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
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

/**
 * Object identifiers enumerated. See
 * http://www.java2me.org/m3g/file-format.html#ObjectTypeValues
 * for more information.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public interface ObjectTypes
{
    public static final int HEADER = 0;
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
