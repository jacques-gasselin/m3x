#!BPY
"""
Name: 'm3x'
Blender: 249
Group: 'Export'
Tooltip: 'Export to m3x, the xml interchange format for the mobile 3d graphics API M3G'

Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

- Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."""

__author__="jgasseli"
__date__ ="$Dec 31, 2009 11:39:06 AM$"

import Blender
import Blender.Camera
import Blender.Image
import Blender.Lamp
import Blender.Mathutils
import Blender.Mesh
import Blender.Texture
import Blender.Types
import Blender.Window
import Blender.BGL as GL
import sys
import re
import traceback
import math


class Object3D(object):
    def __init__(self, idValue):
        object.__init__(self)
        self.id = idValue
        self.userID = 0
        if self.id:
            regex = r'#(?P<userID>\d+)'
            match = re.search(regex, self.id)
            if match and match.group('userID'):
                self.userID = int(match.group('userID'))
            self.id = self.id.replace("#", "-")

    def fillAttributes(self, attrs):
        if self.id:
            attrs["id"] = self.id
        if self.userID > 0:
            attrs["userID"] = self.userID
        return

    def serializeChildren(self, serializer):
        #TODO animation tracks
        pass


class Transformable(Object3D):
    def __init__(self, idValue):
        Object3D.__init__(self, idValue)
        self.translation = None
        self.scale = None
        self.orientation = None

    def setTranslation(self, x, y, z):
        self.translation = [x, y, z]

    def setScale(self, x, y, z):
        self.scale = [x, y, z]

    def setOrientation(self, angle, x, y, z):
        self.orientation = [angle, x, y, z]
        
    def serializeChildren(self, serializer):
        Object3D.serializeChildren(self, serializer)
        if self.translation:
            trans = ["%.6f" % x for x in self.translation]
            serializer.writeDataTag("translation", self.translation)
        if self.orientation:
            orientation = ["%.6f" % x for x in self.orientation]
            serializer.writeDataTag("orientation", orientation[1:],
                {"angle": orientation[0]})
        if self.scale:
            scale = ["%.6f" % x for x in self.scale]
            serializer.writeDataTag("scale", scale)


class Node(Transformable):
    def __init__(self, idValue):
        Transformable.__init__(self, idValue)

    def serializeChildren(self, serializer):
        Transformable.serializeChildren(self, serializer)


class AppearanceBase(Object3D):
    def __init__(self, idValue):
        Object3D.__init__(self, idValue)
        self.compositingMode = None
        self.polygonMode = None

    def setCompositingMode(self, cm):
        self.compositingMode = cm

    def setPolygonMode(self, pm):
        self.polygonMode = pm

    SHARED_BY_MATERIAL = {}
    def getSharedAppearanceBase(cm, pm, material, version):
        shared = AppearanceBase.SHARED_BY_MATERIAL
        key = material
        if key not in shared:
            shared[key] = {}
        appearances = shared[key]
        key = (cm, pm)
        if key not in appearances:
            if version == 2:
                #TODO support Shaders
                pass
            else:
                a = Appearance("Appearance-" + material.name + "-%d" % len(appearances.keys()))
                a.setCompositingMode(cm)
                a.setPolygonMode(pm)
                #get lighting material
                a.setMaterial(Material.getSharedMaterial(material))
                #get the texture 2D elements from the material
                m3xTextures = []
                #print "material", material
                #print "textures:", material.getTextures()
                for mtex in material.getTextures():
                    if mtex:
                        texture = mtex.tex
                        m3xImage = None
                        m3xTexture = None
                        if texture:
                            #convert from Blender textures
                            m3xImage = ImageBase.getSharedImageBase(texture.image, version)
                        if m3xImage:
                            m3xTexture = Texture.getSharedTexture(material, mtex, m3xImage, version)
                        if m3xTexture:
                            m3xTextures.append(m3xTexture)
                a.setTextures(m3xTextures)
            appearances[key] = a
        return appearances[key]

    getSharedAppearanceBase = staticmethod(getSharedAppearanceBase)

    def serializeChildren(self, serializer):
        Object3D.serializeChildren(self, serializer)
        serializer.writeReference(self.compositingMode)
        serializer.writeReference(self.polygonMode)


class Appearance(AppearanceBase):
    def __init__(self, idValue):
        AppearanceBase.__init__(self, idValue)
        self.material = None
        self.textures = None

    def setMaterial(self, material):
        self.material = material
        
    def setTextures(self, textures):
        self.textures = textures[:]
        
    def serializeInstance(self, serializer):
        serializer.closedTag("AppearanceInstance", {"ref" : self.id})

    def serializeChildren(self, serializer):
        AppearanceBase.serializeChildren(self, serializer)
        serializer.writeReference(self.material)
        for t in self.textures:
            serializer.writeReference(t)

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Appearance", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class BlenderMesh(object):
    """A collection of m3x objects that a Blender Mesh object converts to.
    As the data in each BlenderMesh will need to be duplicated for m3x Mesh
    objects it is good to keep it origanized in a container class such as
    this.
    """
    def __init__(self):
        object.__init__(self)

    def setVertexBuffer(self, vertexBuffer):
        self.vertexBuffer = vertexBuffer

    def getSubmeshCount(self):
        return len(self.indexBuffers)

    def setIndexBuffers(self, indexBuffers):
        self.indexBuffers = indexBuffers[:]

    def setAppearanceComponents(self, appearanceComponents):
        self.appearanceComponents = appearanceComponents[:]


class CompositingMode(Object3D):
    ADD = "ADD"
    ALPHA = "ALPHA"
    ALPHA_ADD = "ALPHA_ADD"
    ALPHA_DARKEN = "ALPHA_DARKEN"
    ALPHA_PREMULTIPLIED = "ALPHA_PREMULTIPLIED"
    ALWAYS = "ALWAYS"
    EQUAL = "EQUAL"
    GEQUAL = "GEQUAL"
    GREATER = "GREATER"
    LEQUAL = "LEQUAL"
    LESS = "LESS"
    MODULATE = "MODULATE"
    MODULATE_INV = "MODULATE_INV"
    MODULATE_X2 = "MODULATE_X2"
    NEVER = "NEVER"
    NOTEQUAL = "NOTEQUAL"
    REPLACE = "REPLACE"
    
    def __init__(self, idValue, version):
        Object3D.__init__(self, idValue)
        self.version = version
        self.blending = CompositingMode.REPLACE
        self.alphaThreshold = 0.0
        self.alphaFunc = CompositingMode.GEQUAL
        self.depthOffsetUnits = 0.0
        self.depthOffsetFactor = 0.0
        self.depthFunc = CompositingMode.LEQUAL
        self.depthWrite = True
        self.depthTest = True
        self.colorWriteMask = 0xffffffff
        self.blender = None
        self.stencil = None

    SHARED_MODES = {}
    
    def getSharedCompositingMode(blending, alphaThreshold, version):
        modes = CompositingMode.SHARED_MODES
        key = (blending, alphaThreshold)
        if key not in modes:
            name = "CompositingMode-" + blending
            if alphaThreshold > 0.0:
                name = name + "-t%f" % alphaThreshold
            mode = CompositingMode(name, version)
            mode.setBlending(blending)
            mode.setAlphaThreshold(alphaThreshold)
            modes[key] = mode
        return modes[key]
    
    getSharedCompositingMode = staticmethod(getSharedCompositingMode)

    def setBlending(self, blending):
        self.blending = blending

    def setAlphaThreshold(self, alphaThreshold):
        self.alphaThreshold = alphaThreshold

    def serializeInstance(self, serializer):
        serializer.closedTag("CompositingModeInstance", {"ref" : self.id})

    def serializeChildren(self, serializer):
        Object3D.serializeChildren(self, serializer)
        if serializer.version == 2:
            #TODO add blender and stencil
            pass

    def fillAttributes(self, attr):
        Object3D.fillAttributes(self, attr)
        attr["blending"] = self.blending

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        if serializer.version == 2:
            #TODO add some more bits to attr
            pass
        serializer.startTag("CompositingMode", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class Group(Node):
    def __init__(self, idValue):
        Node.__init__(self, idValue)
        self.children = []

    def addChild(self, child):
        self.children.append(child)

    def serializeChildren(self, serializer):
        Node.serializeChildren(self, serializer)
        for child in self.children:
            serializer.writeReference(child)

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        if serializer.version == 2:
            #TODO add some more bits to attr
            pass
        serializer.startTag("Group", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class IndexBuffer(Object3D):
    TRIANGLES = "TRIANGLES"
    def __init__(self, idValue, type):
        Object3D.__init__(self, idValue)
        self.type = type
        self.indices = None

    def setIndices(self, indices):
        self.indices = indices[:]

    def serializeChildren(self, serializer):
        Object3D.serializeChildren(self, serializer)
        serializer.writeDataTag("indices", self.indices)


class ImageBase(Object3D):
    LUMINANCE = "LUMINANCE"
    LUMINANCE_ALPHA = "LUMINANCE_ALPHA"
    RGB = "RGB"
    RGBA = "RGBA"

    def __init__(self, idValue):
        Object3D.__init__(self, idValue)
        self.format = None
        self.width = None
        self.height = None

    SHARED_IMAGES = {}

    def getSharedImageBase(bimage, version):
        if bimage is None:
            return None
        images = ImageBase.SHARED_IMAGES
        key = bimage
        if key not in images:
            name = bimage.name
            stype = bimage.source
            if stype == Blender.Image.Sources["STILL"]:
                #TODO support Cube images for version 2
                im = Image2D.createImage2D(name, bimage)
            elif stype == Blender.Image.Sources["MOVIE"]:
                if version == 2:
                    #TODO support Dynamic images
                    pass
                else:
                    im = Image2D.createImage2D(name, bimage)
            images[key] = im
        return images[key]

    getSharedImageBase = staticmethod(getSharedImageBase)

    def fillAttributes(self, attr):
        Object3D.fillAttributes(self, attr)
        attr["format"] = self.format
        attr["width"] = self.width
        attr["height"] = self.height


class Image2D(ImageBase):
    def __init__(self, idValue):
        ImageBase.__init__(self, idValue)

    def set(self, format, width, height, pixels):
        self.format = format
        self.width = width
        self.height = height
        self.pixels = pixels
        
    def createImage2D(idValue, bimage):
        im = Image2D("Image2D-" + idValue)
        width, height = bimage.size
        depth = bimage.depth
        if depth == 8:
            #LUMINANCE ?
            pass
        if depth == 16:
            #LUMINANCE_ALPHA ?
            pass
        if depth in (24, 32):
            #RGB
            if depth == 24:
                format = ImageBase.RGB
                bpp = 3
            elif depth == 32:
                format = ImageBase.RGBA
                bpp = 4
            getPixel = bimage.getPixelI
            pixels = [0] * (width * height * bpp)
            for y in xrange(height):
                yOffset = width * bpp * y
                for x in xrange(width):
                    rgba = getPixel(x, y)
                    offset = x * bpp + yOffset
                    pixels[offset:offset + bpp] = rgba[:bpp]
            im.set(format, width, height, pixels)
        return im

    createImage2D = staticmethod(createImage2D)

    def serializeChildren(self, serializer):
        ImageBase.serializeChildren(self, serializer)
        serializer.writeDataTag("pixels", self.pixels)

    def serializeInstance(self, serializer):
        serializer.closedTag("Image2DInstance", {"ref" : self.id})

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Image2D", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class Material(Object3D):
    def __init__(self, idValue):
        Object3D.__init__(self, idValue)
        self.ambientColor = None
        self.diffuseColor = None
        self.emissiveColor = None
        self.specularColor = None
        self.shininess = None
        self.vertexColorTrackingEnabled = None

    def setAmbient(self, colorRGB):
        self.ambientColor = colorRGB

    def setDiffuse(self, colorRGB):
        self.diffuseColor = colorRGB

    def setEmissive(self, colorRGB):
        self.emissiveColor = colorRGB

    def setSpecular(self, colorRGB):
        self.specularColor = colorRGB

    def setShininess(self, shininess):
        self.shininess = shininess

    def setVertexColorTrackingEnabled(self, enabled):
        self.vertexColorTrackingEnabled = enabled

    SHARED_MATERIALS = {}

    def getSharedMaterial(bmat):
        if bmat is None:
            return None
        materials = Material.SHARED_MATERIALS
        key = bmat
        if key not in materials:
            name = bmat.name
            mat = Material(name)
            mat.setDiffuse(tuple(bmat.rgbCol))
            mat.setSpecular(tuple(bmat.specCol))
            mat.setShininess(bmat.hard)
            #TODO implement emission and ambient
            materials[key] = mat
        return materials[key]

    getSharedMaterial = staticmethod(getSharedMaterial)

    def fillAttributes(self, attr):
        Object3D.fillAttributes(self, attr)
        attr["vertexColorTrackingEnabled"] = self.vertexColorTrackingEnabled

    def serializeChildren(self, serializer):
        Object3D.serializeChildren(self, serializer)
        serializer.writeDataTag("ambientColor", self.ambientColor)
        serializer.writeDataTag("diffuseColor", self.diffuseColor)
        serializer.writeDataTag("specularColor", self.specularColor,
            {"shininess": self.shininess})

    def serializeInstance(self, serializer):
        serializer.closedTag("MaterialInstance", {"ref" : self.id})

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Material", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class Mesh(Node):
    def __init__(self, idValue, version):
        Node.__init__(self, idValue)
        self.version = version
        self.vertexBuffer = None
        self.submeshCount = 0
        self.indexBuffers = []
        self.appearances = []

    def setVertexBuffer(self, vertexBuffer):
        self.vertexBuffer = vertexBuffer

    def addSubmesh(self, indexBuffer, appearance):
        self.indexBuffers.append(indexBuffer)
        self.appearances.append(appearance)
        self.submeshCount += 1

    def serializeChildren(self, serializer):
        Node.serializeChildren(self, serializer)
        serializer.writeReference(self.vertexBuffer)
        for i in xrange(self.submeshCount):
            ib = self.indexBuffers[i]
            ap = self.appearances[i]
            serializer.startTag("submesh")
            serializer.writeReference(ib)
            serializer.writeReference(ap)
            serializer.endTag()
        
    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Mesh", attr)
        self.serializeChildren(serializer)
        serializer.endTag()
    

class PolygonMode(Object3D):
    CULL_BACK = "CULL_BACK"
    CULL_FRONT = "CULL_FRONT"
    CULL_NONE = "CULL_NONE"

    def __init__(self, idValue, version):
        Object3D.__init__(self, idValue)
        self.version = version
        self.culling  = PolygonMode.CULL_BACK

    def setCulling(self, culling):
        self.culling = culling

    SHARED_MODES = {}

    def getSharedPolygonMode(culling, version):
        modes = PolygonMode.SHARED_MODES
        key = (culling)
        if key not in modes:
            name = "PolygonMode-" + culling
            mode = PolygonMode(name, version)
            mode.setCulling(culling)
            modes[key] = mode
        return modes[key]

    getSharedPolygonMode = staticmethod(getSharedPolygonMode)

    def serializeInstance(self, serializer):
        serializer.closedTag("PolygonModeInstance", {"ref" : self.id})

    def serializeChildren(self, serializer):
        Object3D.serializeChildren(self, serializer)

    def serialize(self, serializer):
        attr = {
            "culling": self.culling
            }
        self.fillAttributes(attr)
        if serializer.version == 2:
            #TODO add some more bits
            pass
        serializer.startTag("PolygonMode", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class Texture(Transformable):
    FILTER_BASE_LEVEL = "FILTER_BASE_LEVEL"
    FILTER_LINEAR = "FILTER_LINEAR"
    FILTER_NEAREST = "FILTER_NEAREST"

    def __init__(self, idValue):
        Transformable.__init__(self, idValue)
        self.image = None
        self.imageFilter = Texture.FILTER_NEAREST
        self.levelFilter = Texture.FILTER_BASE_LEVEL

    def setImage(self, image):
        self.image = image

    def setFiltering(self, levelFilter, imageFilter):
        self.levelFilter = levelFilter
        self.imageFilter = imageFilter
        
    SHARED_TEXTURES = {}

    def getSharedTexture(material, mtex, m3xImage, version):
        textures = Texture.SHARED_TEXTURES
        key = (material.name, mtex.tex.name, m3xImage)
        if key not in textures:
            if isinstance(m3xImage, Image2D):
                tex = Texture2D.createTexture2D(material, mtex, m3xImage, version)
            textures[key] = tex
        return textures[key]

    getSharedTexture = staticmethod(getSharedTexture)

    def fillAttributes(self, attr):
        Transformable.fillAttributes(self, attr)
        attr["levelFilter"] = self.levelFilter
        attr["imageFilter"] = self.imageFilter

    def serializeChildren(self, serializer):
        Transformable.serializeChildren(self, serializer)
        serializer.writeReference(self.image)


class Texture2D(Texture):
    def __init__(self, idValue):
        Texture.__init__(self, idValue)

    def createTexture2D(material, mtex, m3xImage, version):
        name = "%s-%s-%s" % (material.name, mtex.tex.name, m3xImage.id)
        #TODO wrap/clamp tranforms
        tex = Texture2D(name)
        if mtex.tex.mipmap:
            levelFilter = Texture.FILTER_LINEAR
        else:
            levelFilter = Texture.FILTER_BASE_LEVEL
        tex.setFiltering(levelFilter, Texture.FILTER_LINEAR)
        tex.setImage(m3xImage)
        return tex

    createTexture2D = staticmethod(createTexture2D)

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Texture2D", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class TriangleStripArray(IndexBuffer):
    def __init__(self, idValue):
        IndexBuffer.__init__(self, idValue, IndexBuffer.TRIANGLES)
        self.stripLengths = None

    def setLengths(self, lengths):
        self.stripLengths = lengths[:]

    def serializeInstance(self, serializer):
        serializer.closedTag("TriangleStripArrayInstance", {"ref" : self.id})

    def serializeChildren(self, serializer):
        IndexBuffer.serializeChildren(self, serializer)
        serializer.writeDataTag("stripLengths", self.stripLengths)
        
    def serialize(self, serializer):
        attr = {"id": self.id}
        serializer.startTag("TriangleStripArray", attr)
        self.serializeChildren(serializer)
        serializer.endTag()


class VertexArray(Object3D):
    BYTE = "BYTE"
    SHORT = "SHORT"
    FIXED = "FIXED"
    HALF = "HALF"
    FLOAT = "FLOAT"
    
    def __init__(self, idValue):
        Object3D.__init__(self, idValue)
        self.components = []
        self.componentType = VertexArray.FLOAT
        self.scale = None
        self.bias = None
        self.vertexCount = None
        self.componentCount = None

    def setId(self, idValue):
        self.id = idValue

    def setVertexCount(self, vertexCount):
        """setVertexCount(self, int vertexCount) -> None
        Sets the vertex count of the array and implicitly
        calculates the componentCount."""
        self.vertexCount = vertexCount
        self.componentCount = len(self.components) / vertexCount
        self.bias = [0.0] * self.componentCount

    def extend(self, iterable):
        """extend(self, iterable) -> None
        Extends the array with components from the iterable.
        This invalidates vertexCount and componentCount."""
        self.components.extend(iterable)

    def __calcScaleBias(self, qMin, qMax):
        componentCount = self.componentCount
        components = self.components
        minValues = list(components[0:componentCount])
        maxValues = list(components[0:componentCount])
        vertex = [0.0] * componentCount
        for v in xrange(1, self.vertexCount):
            start = componentCount * v
            vertex[:] = components[start:start + componentCount]
            for c in xrange(componentCount):
                value = vertex[c]
                minValues[c] = min(minValues[c], value)
                maxValues[c] = max(maxValues[c], value)
        maxDist = 0
        bias = [0.0] * componentCount
        for c in xrange(componentCount):
            dist = maxValues[c] - minValues[c]
            maxDist = max(maxDist, dist)
            median = (maxValues[c] + minValues[c]) / 2.0
            bias[c] = median
        scale = maxDist / (qMax - qMin)
        return scale, bias

    def __compress(self, qMin, qMax, scale, bias):
        componentCount = self.componentCount
        components = self.components
        if scale is None:
            scale = 1.0
        if bias is None:
            bias = [0.0] * componentCount
        vertex = [0.0] * componentCount
        for v in xrange(self.vertexCount):
            start = componentCount * v
            vertex[:] = components[start:start + componentCount]
            for c in xrange(componentCount):
                #quantize
                q = int(((vertex[c] - bias[c]) / scale) + 0.5)
                #clamp and write back
                vertex[c] = max(qMin, min(q, qMax))
            components[start:start + componentCount] = vertex
        self.scale = scale
        self.bias = bias

    def compressShort(self, scale = None, bias = None):
        """compressShort(self, float scale, float[] bias) -> None
        Compresses the vertex array to fit in a set of scaled and
        biased shorts"""
        qMin = -32768
        qMax = 32767
        if scale is None:
            scale, bias = self.__calcScaleBias(qMin, qMax)
        self.__compress(qMin, qMax, scale, bias)
        self.componentType = VertexArray.SHORT

    def compressByte(self, scale = None, bias = None):
        """compressByte(self, float scale, float[] bias) -> None
        Compresses the vertex array to fit in a set of scaled and
        biased byte"""
        qMin = -128
        qMax = 127
        if scale is None:
            scale, bias = self.__calcScaleBias(qMin, qMax)
        self.__compress(qMin, qMax, scale, bias)
        self.componentType = VertexArray.BYTE

    def compressUnsignedByte(self, scale = None, bias = None):
        """compressUnsignedByte(self, float scale, float[] bias) -> None
        Compresses the vertex array to fit in a set of scaled and
        biased unsigned bytes"""
        qMin = 0
        qMax = 255
        if scale is None:
            scale, bias = self.__calcScaleBias(qMin, qMax)
        self.__compress(qMin, qMax, scale, bias)
        self.componentType = VertexArray.BYTE

    def __repr__(self):
        return str(self.__dict__)

    def fillAttributes(self, attr):
        Object3D.fillAttributes(self, attr)
        attr["componentCount"] = self.componentCount
    
    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("VertexArray", attr)
        if self.componentType == VertexArray.BYTE:
            serializer.writeDataTag("byteComponents", self.components)
        elif self.componentType == VertexArray.SHORT:
            serializer.writeDataTag("shortComponents", self.components)
        serializer.endTag()


class VertexBuffer(Object3D):
    def __init__(self, idValue):
        Object3D.__init__(self, idValue)
        self.positions = None
        self.normals = None
        self.texcoords = []

    def setPositions(self, positions):
        self.positions = positions

    def setNormals(self, normals):
        self.normals = normals

    def addTexcoords(self, texcoords):
        self.texcoords.append(texcoords)

    def serializeInstance(self, serializer):
        serializer.closedTag("VertexBufferInstance", {"ref" : self.id})
        
    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("VertexBuffer", attr)
        pos = self.positions
        if pos:
            serializer.startTag("positions", {"scale": pos.scale})
            serializer.writeDataTag("bias", pos.bias)
            serializer.writeReference(pos)
            serializer.endTag()
        norm = self.normals
        if norm:
            serializer.startTag("normals")
            serializer.writeReference(norm)
            serializer.endTag()
        for tex in self.texcoords:
            serializer.startTag("texcoords", {"scale": tex.scale})
            serializer.writeDataTag("bias", tex.bias)
            serializer.writeReference(tex)
            serializer.endTag()
        serializer.endTag()


class VertexSeq(object):
    def __init__(self, hasColors, hasUV):
        object.__init__(self)
        self.positions = []
        self.normals = []
        if hasColors:
            self.colors = []
        else:
            self.colors = None
        if hasUV:
            self.uvs = []
        else:
            self.uvs = None
        self.indexByComponents = {}
        self.vertexCount = 0

    def getIndex(self, pos, norm, col, uv):
        key = (pos, norm, col, uv)
        if key not in self.indexByComponents:
            self.positions.extend(pos)
            self.normals.extend(norm)
            if self.colors is not None:
                self.colors.extend(col)
            if self.uvs is not None:
                self.uvs.extend(uv)
            self.indexByComponents[key] = self.vertexCount
            self.vertexCount += 1
        return self.indexByComponents[key]


class Serializer(object):
    def __init__(self, version, writer):
        object.__init__(self)
        self.version = version
        self.writer = writer
        self.tags = []
        self.indents = [""]
        self.addindent = "  "
        self.serializedObjects = set()

    def startTag(self, name, attr = None):
        self.tags.append(name)
        indent = self.indents[-1]
        self.indents.append(indent + self.addindent)
        write = self.writer.write
        write("%s<%s" % (indent, name))
        if attr:
            for n, v in attr.iteritems():
                if v is not None:
                    write(" %s=\"%s\"" % (n, str(v)))
        write(">\n")

    def closedTag(self, name, attr = None):
        indent = self.indents[-1]
        write = self.writer.write
        write("%s<%s" % (indent, name))
        if attr:
            for n, v in attr.iteritems():
                if v is not None:
                    write(" %s=\"%s\"" % (n, str(v)))
        write(" />\n")
        
    def endTag(self):
        name = self.tags.pop()
        self.indents.pop()
        indent = self.indents[-1]
        self.writer.write("%s</%s>\n" % (indent, name))

    def writeDataTag(self, name, data, attr = None):
        if data is None and attr is None:
            return
        indent = self.indents[-1]
        write = self.writer.write
        write("%s<%s" % (indent, name))
        if attr:
            for n, v in attr.iteritems():
                if v is not None:
                    write(" %s=\"%s\"" % (n, str(v)))
        if data:
            write(">")
            strData = (str(x) for x in data)
            write(" ".join(strData))
            write("</%s>\n" % name)
        else:
            write(" />\n")

    def writeReference(self, object):
        if not object:
            return
        if object in self.serializedObjects:
            object.serializeInstance(self)
        else:
            object.serialize(self)
            self.serializedObjects.add(object)
        
    def serialize(self, sectionsWithRootObjects):
        self.writer.write("""<?xml version="1.0" encoding="utf-8"?>\n""")
        versions = [None, "1.0", "2.0"]
        self.startTag("m3g", {"version" : versions[self.version]})
        for rootObjects in sectionsWithRootObjects:
            self.startTag("section")
            for object in rootObjects:
                object.serialize(self)
                self.serializedObjects.add(object)
            self.endTag()
        self.endTag()

class M3XConverter(object):
    ZEROS_3 = (0.0, 0.0, 0.0)
    ONES_3 = (1.0, 1.0, 1.0)

    def __init__(self):
        object.__init__(self)
        self.convertedDataObjects = {}
        self.objects = []

    def setVersion(self, version):
        self.version = version

    def clearCaches(self):
        self.convertedDataObjects = {}
        AppearanceBase.SHARED_BY_MATERIAL = {}
        CompositingMode.SHARED_MODES = {}
        ImageBase.SHARED_IMAGES = {}
        Material.SHARED_MATERIALS = {}
        PolygonMode.SHARED_MODES = {}
        Texture.SHARED_TEXTURES = {}

    def getFaceCompositingMode(self, face, hasPerFaceUV):
        modes = Blender.Mesh.FaceTranspModes
        if hasPerFaceUV:
            bmode = face.transp
        else:
            bmode = modes["SOLID"]
        alphaThreshold = 0.0
        if bmode == modes["SOLID"]:
            blending = CompositingMode.REPLACE
        elif bmode == modes["ADD"]:
            blending = CompositingMode.ADD
        elif bmode == modes["ALPHA"]:
            blending = CompositingMode.ALPHA
        elif blending == modes["SUB"]:
            if self.version == 1:
                blending = CompositingMode.MODULATE
            elif self.version == 2:
                #TODO make a blender with subtract blending
                pass
        elif blending == modes["CLIP"]:
            blending = CompositingMode.ALPHA
            alphaThreshold = 0.75
        return CompositingMode.getSharedCompositingMode(blending, alphaThreshold, self.version)

    def getFacePolygonMode(self, face, hasPerFaceUV):
        modes = Blender.Mesh.FaceModes
        if not hasPerFaceUV:
            culling = PolygonMode.CULL_BACK
        elif face.mode & modes["TWOSIDE"]:
            culling = PolygonMode.CULL_NONE
        else:
            culling = PolygonMode.CULL_BACK
        return PolygonMode.getSharedPolygonMode(culling, self.version)

    def getFaceAppearanceComponents(self, face, mesh):
        hasPerFaceUV = mesh.faceUV
        #get CompositingMode
        cm = self.getFaceCompositingMode(face, hasPerFaceUV)
        #get PolygonMode
        pm = self.getFacePolygonMode(face, hasPerFaceUV)
        #TODO get Fog
        #TODO get PointSpriteMode
        #actual appearance has to be linked up later with the
        #per instance materials
        return cm, pm, face.mat

    def getAppearance(self, cm, pm, material):
        return AppearanceBase.getSharedAppearanceBase(cm, pm, material, self.version)

    def convertMesh(self, mesh):
        version = self.version
        hasPerVertexColor = mesh.vertexColors
        #otherwise they are per face UVs
        hasPerVertexUV = mesh.vertexUV
        hasPerFaceUV = mesh.faceUV

        #sort faces by material
        facesByAppearanceComponents = {}
        for face in mesh.faces:
            key = self.getFaceAppearanceComponents(face, mesh)
            #print "key:", key
            lst = facesByAppearanceComponents.setdefault(key, [])
            lst.append(face)
        appearanceComponents = facesByAppearanceComponents.keys()
        #print "appearanceComponents:", appearanceComponents
        #TODO sort appearances on texture and blend mode
        #extract submesh data
        vseq = VertexSeq(hasPerVertexColor, hasPerVertexUV or hasPerFaceUV)
        indexBuffers = []
        for submeshIndex, components in enumerate(appearanceComponents):
            faces = facesByAppearanceComponents[components]
            submeshTriStrips = []
            stripLengths = []
            for face in faces:
                #get the indices
                faceIndices = []
                for i, v in enumerate(face.verts):
                    pos = tuple(v.co)
                    norm = tuple(v.no)
                    if hasPerVertexColor:
                        col = tuple(face.col[i])
                    else:
                        col = None
                    #per face goes first
                    if hasPerFaceUV:
                        uv = tuple(face.uv[i])
                    elif hasPerVertexUV:
                        uv = tuple(v.uvco)
                    else:
                        uv = None
                    faceIndices.append(vseq.getIndex(pos, norm, col, uv))
                #Make TriangleStripArrays
                if (len(faceIndices) == 3):
                    submeshTriStrips.extend(faceIndices)
                    stripLengths.append(3)
                else: #len is 4
                    #triangulate and add, converting to backward Z with CCW winding
                    submeshTriStrips.append(faceIndices[0])
                    submeshTriStrips.append(faceIndices[1])
                    submeshTriStrips.append(faceIndices[3])
                    submeshTriStrips.append(faceIndices[2])
                    stripLengths.append(4)
            if version == 1:
                #Make TriangleStripArrays
                #TODO clean this up, it is a naive solution
                #triangles could be merged
                indexBuffer = TriangleStripArray(mesh.name + "-submesh%d" % submeshIndex)
                indexBuffer.setIndices(submeshTriStrips)
                indexBuffer.setLengths(stripLengths)
            else:
                #Make plain IndexBuffers in TRIANGLES mode
                #TODO clean this up, it is a naive solution
                #triangles could be merged
                indexBuffer = IndexBuffer(mesh.name + "-submesh%d" % submeshIndex, IndexBuffer.TRIANGLES)
                indexBuffer.setIndices(submeshTriStrips)
                indexBuffer.setLengths(stripLengths)
            indexBuffers.append(indexBuffer)

        vertexBuffer = VertexBuffer(mesh.name + "-vertexBuffer")

        #create lists representing the normal and position vertex arrays
        positionArray = VertexArray(mesh.name + "-positions")
        positionArray.extend(vseq.positions)
        positionArray.setVertexCount(vseq.vertexCount)
        if version == 1:
            #version 1 has SHORT as the highest fidelity type
            #therefore FLOAT data must be compressed
            positionArray.compressShort()
        vertexBuffer.setPositions(positionArray)
        del positionArray
        
        normalArray = VertexArray(mesh.name + "-normals")
        normalArray.extend(vseq.normals)
        normalArray.setVertexCount(vseq.vertexCount)
        if version == 1:
            #ensure it fits in a byte and normals are always in
            #the range [-1, 1]
            normalArray.compressByte(2 / 255.0, [1 / 255.0] * 3)
        vertexBuffer.setNormals(normalArray)
        del normalArray

        if vseq.colors is not None:
            colorArray = VertexArray(mesh.name + "-colors")
            colorArray.extend(vseq.colors)
            colorArray.setVertexCount(vseq.vertexCount)
            if version == 1:
                #version 1 has UNSIGNED_BYTE as the highest fidelity type
                #therefore FLOAT data must be compressed.
                #ensure data fits in [0, 255]
                colorArray.compressUnsignedByte(1 / 255.0)
            vertexBuffer.setColors(colorArray)
            del colorArray

        if vseq.uvs is not None:
            uvArray = VertexArray(mesh.name + "-texcoords0")
            uvArray.extend(vseq.uvs)
            uvArray.setVertexCount(vseq.vertexCount)
            if version == 1:
                #version 1 has SHORT as the highest fidelity type
                #therefore FLOAT data must be compressed
                uvArray.compressShort()
            vertexBuffer.addTexcoords(uvArray)
            del uvArray

        #store the BlenderMesh for later conversions
        bmesh = BlenderMesh()
        bmesh.setVertexBuffer(vertexBuffer)
        bmesh.setAppearanceComponents(appearanceComponents)
        bmesh.setIndexBuffers(indexBuffers)

        return bmesh

    def convertObject(self, object, childrenByObject):
        #print "objectName:", object.getName()
        hasChildren =  object in childrenByObject
        returnObject = None
        if hasChildren:
            #traverse them next
            #this is Group in M3X terms, or perhaps a SkinnedMesh
            #TODO discern if it is a SkinnedMesh
            returnObject = Group(object.name)
            for child in childrenByObject[object]:
                returnObject.addChild(
                    self.convertObject(child, childrenByObject))

        data = object.getData(mesh=True)
        if data:
            #print "objectData:", data, data.__class__
            #is it a mesh?
            if type(data) == Blender.Types.MeshType:
                if data not in self.convertedDataObjects:
                    self.convertedDataObjects[data] = self.convertMesh(data)
                bmesh = self.convertedDataObjects[data]
                #get the materials, supporting the per object override
                objectMaterials = object.getMaterials(1)
                meshMaterials = data.materials
                materials = []
                for i in xrange(16):
                    if object.colbits & (1 << i):
                        materials.append(objectMaterials[i])
                    elif meshMaterials and i < len(meshMaterials):
                        materials.append(meshMaterials[i])
                    else:
                        materials.append(None)
                del objectMaterials
                del meshMaterials
                #add a mesh object
                mesh = Mesh(object.name + "-mesh", self.version)
                mesh.setVertexBuffer(bmesh.vertexBuffer)
                for i in xrange(bmesh.getSubmeshCount()):
                    ib = bmesh.indexBuffers[i]
                    cm, pm, materialIndex = bmesh.appearanceComponents[i]
                    material = materials[materialIndex]
                    appearance = self.getAppearance(cm, pm, material)
                    mesh.addSubmesh(ib, appearance)
                if returnObject:
                    #TODO what happens for SkinnedMesh
                    returnObject.addChild(mesh)
                else:
                    returnObject = mesh
        elif returnObject is None:
            #Empty node
            returnObject = Group(object.name)
        if returnObject:
            loc = tuple(object.loc)
            if loc != M3XConverter.ZEROS_3:
                returnObject.setTranslation(*loc)
            scale = tuple(object.size)
            if scale != M3XConverter.ONES_3:
                returnObject.setScale(*scale)
            rot = tuple(object.rot)
            if rot != M3XConverter.ZEROS_3:
                euler = Blender.Mathutils.Euler(rot)
                quat = euler.toQuat()
                #TODO potential documentation error in Blender python docs
                #this is said to be in degrees already but seems to be in radians
                angle = math.degrees(quat.angle)
                x, y, z = quat.axis
                returnObject.setOrientation(angle, x, y, z)
        return returnObject


    def convert(self, objectsToConvert):
        #clear all caches from previous conversions
        self.clearCaches()
        #sort the objects into root first order
        #identify the non-root set
        closedSet = set()
        rootSet = set()
        #also compile the list of children per object
        childrenByObject = {}
        roots = []
        openList = []
        for candidate in objectsToConvert:
            if candidate not in closedSet:
                openList.append(candidate)
            while len(openList) > 0:
                object = openList.pop()
                closedSet.add(object)
                parent = object.parent
                if parent:
                    children = childrenByObject.setdefault(parent, [])
                    children.append(object)
                    if parent not in closedSet:
                        openList.append(parent)
                elif object not in rootSet:
                    roots.append(object)
                    rootSet.add(object)

        #traverse the roots as a stack
        #TODO add a world as the root if needed
        for object in roots:
            converted = self.convertObject(object, childrenByObject)
            if converted:
                self.objects.append(converted)

    def serialize(self, writer):
        s = Serializer(self.version, writer)
        sections = []
        #TODO make this a proper roots only list
        sections.append(self.objects)
        s.serialize(sections)


class GUI:
    COL_WIDTH = 100
    ROW_HEIGHT = 20
    BOTTOM_MARIGIN = 10
    LEFT_MARGIN = 10

    EVENT_EXPORT_WORLD_TOGGLE = 0
    EVENT_EXPORT_SELECTION_TOGGLE = 1
    EVENT_EXPORT_10 = 2
    EVENT_EXPORT_20 = 3

    #
    def __init__(self):
        self.__exportToWorld = 0
        self.__exportToWorldButton = None
        self.__exportSelectionOnly = 0
        self.__exportSelectionOnlyButton = None
        self.__exportVersion1Button = None
        self.__exportVersion2Button = None
        self.__converter = M3XConverter()
        self.objectsToConvert = None

    #
    def gridCoords(self, col, row):
        return [GUI.LEFT_MARGIN + col * GUI.COL_WIDTH,
                GUI.BOTTOM_MARIGIN + row * GUI.ROW_HEIGHT]

    #
    def draw(self):
        width, height = (GUI.COL_WIDTH, GUI.ROW_HEIGHT)
        #x, y = self.gridCoords(0, 2)
        #self.__exportToWorldButton = Blender.Draw.Toggle(
        #    "export to world",
        #    GUI.EVENT_EXPORT_WORLD_TOGGLE,
        #    x, y, width, height,
        #    self.__exportToWorld,
        #    "Tooltip")
        x, y = self.gridCoords(1, 2)
        self.__exportSelectionOnlyButton = Blender.Draw.Toggle(
            "export selection only",
            GUI.EVENT_EXPORT_SELECTION_TOGGLE,
            x, y, width, height,
            self.__exportSelectionOnly,
            "Tooltip")
        x, y = self.gridCoords(0, 0)
        self.__exportVersion1Button = Blender.Draw.PushButton(
            "export v1.0",
            GUI.EVENT_EXPORT_10,
            x, y, width, height,
            "Tooltip")
        #x, y = self.gridCoords(1, 0)
        #self.__exportVersion2Button = Blender.Draw.PushButton(
        #    "export v2.0",
        #    GUI.EVENT_EXPORT_20,
        #    x, y, width, height,
        #    "Tooltip")
        GL.glRasterPos2i(*self.gridCoords(0, 4))
        Blender.Draw.Text("m3x Export", 'large')

    def event(self, evt, val):
        if evt == Blender.Draw.ESCKEY: # Example if esc key pressed
            Blender.Draw.Exit()    # then exit script
            return                 # return from the function

    def button(self, evt):
        export = False
        exportVersion = 1
        if evt == GUI.EVENT_EXPORT_WORLD_TOGGLE:
            self.__exportToWorld = 1 - self.__exportToWorld
        elif evt == GUI.EVENT_EXPORT_SELECTION_TOGGLE:
            self.__exportSelectionOnly = 1 - self.__exportSelectionOnly
        elif evt == GUI.EVENT_EXPORT_10:
            export = True
            self.__converter.setVersion(1)
        elif evt == GUI.EVENT_EXPORT_20:
            export = True
            self.__converter.setVersion(2)
        if export:
            if self.__exportSelectionOnly:
                self.objectsToConvert = Blender.Object.GetSelected()
            else:
                self.objectsToConvert = Blender.Scene.GetCurrent().objects
            if True:
                Blender.Window.FileSelector(self.fileSelectedForConversion)
            else:
                self.__converter.convert(self.objectsToConvert)
                writer = sys.stdout
                self.__converter.serialize(writer)
                writer.flush()

    def fileSelectedForConversion(self, filename):
        try:
            pbar = Blender.Window.DrawProgressBar
            pbar(0.0, "Converting Blender Objects to M3X")
            self.__converter.convert(self.objectsToConvert)
            pbar(0.2, "Opening destination file")
            writer = open(filename, "wb")
            pbar(0.3, "Serializing to destination file")
            self.__converter.serialize(writer)
            pbar(0.8, "Flushing serializing buffers")
            writer.flush()
            pbar(0.9, "Saving and closing destination file")
            writer.close()
            pbar(1.0, "Finished")
        except:
            traceback.print_exc()
#
gui = GUI()
def draw(): # Define the draw function (which draws your GUI).
    gui.draw()

def event(evt, val):  # Define mouse and keyboard press events
    gui.event(evt, val)

def button(evt):     # Define what to do if a button is pressed, for example:
    gui.button(evt)

if __name__ == '__main__':
    Blender.Draw.Register(draw, event, button)