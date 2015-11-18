#!BPY
"""
Name: 'm3x'
Blender: 249
Group: 'Export'
Tooltip: 'Export to m3x, the xml interchange format for the mobile 3d graphics API M3G'

Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

import bpy
import sys
import re
import traceback
import math
import mathutils


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
                for i, mtex in enumerate(material.textures):
                    if material.enabledTextures:
                        enabled = i in material.enabledTextures
                    else:
                        enabled = True
                    if enabled and mtex:
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


class BlenderLamp(object):
    def __init__(self):
        object.__init__(self)
        self.mode = None
        self.color = None

    def setMode(self, mode):
        self.mode = mode

    def setColor(self, color):
        self.color = color

    def setIntensity(self, intensity):
        self.intensity = intensity
    
    def setSpot(self, angle, exponent):
        self.spotAngle = angle
        self.spotExponent = exponent

    def setAttenuation(self, cAttn, lAttn, qAttn):
        self.constantAttenuation = cAttn
        self.linearAttenuation = lAttn
        self.quadraticAttenuation = qAttn


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
            #RGB or RGBA
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


class Light(Node):
    AMBIENT = "AMBIENT"
    DIRECTIONAL = "DIRECTIONAL"
    OMNI = "OMNI"
    SPOT = "SPOT"
    
    def __init__(self, idValue):
        Node.__init__(self, idValue)
        self.mode = None
        self.color = None
        self.intensity = None
        self.spotAngle = None
        self.spotExponent = None
        self.constantAttenuation = None
        self.linearAttenuation = None
        self.quadraticAttenuation = None
    
    def setMode(self, mode):
        self.mode = mode

    def setColor(self, color):
        self.color = color

    def setIntensity(self, intensity):
        self.intensity = intensity
    
    def setSpotAngle(self, angle):
        self.spotAngle = angle

    def setSpotExponent(self, exponent):
        self.spotExponent = exponent
        
    def setAttenuation(self, constant, linear, quadratic):
        self.constantAttenuation = constant
        self.linearAttenuation = linear
        self.quadraticAttenuation = quadratic

    def fillAttributes(self, attr):
        Node.fillAttributes(self, attr)
        attr["mode"] = self.mode
        attr["intensity"] = self.intensity

    def serializeChildren(self, serializer):
        Node.serializeChildren(self, serializer)
        serializer.writeDataTag("color", self.color)
        serializer.writeDataTag("attenuation", None,
            {"constant" : self.constantAttenuation,
            "linear" : self.linearAttenuation,
            "quadratic" : self.quadraticAttenuation})
        serializer.writeDataTag("spot", None,
            {"angle": self.spotAngle, "exponent": self.spotExponent})

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Light", attr)
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

    def setDiffuse(self, colorRGBA):
        self.diffuseColor = colorRGBA

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
        if (bmat.mode & Blender.Material.Modes.SHADELESS):
            return None
        materials = Material.SHARED_MATERIALS
        key = bmat
        if key not in materials:
            name = bmat.name
            mat = Material(name)
            diffuseRGBA = [int(round(bmat.ref * x * 255)) for x in bmat.rgbCol]
            diffuseRGBA.append(int(round(bmat.alpha * 255)))
            mat.setDiffuse(diffuseRGBA)
            mat.setSpecular([int(round(bmat.spec * x * 255)) for x in bmat.specCol])
            #remap [1, 511] into the range [0, 128]
            mat.setShininess(128 * (bmat.hard - 1) / 511.0)
            mat.setEmissive([int(round(0.5 * bmat.emit * x * 255)) for x in bmat.rgbCol])
            #TODO ambient
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
        self.culling = PolygonMode.CULL_BACK

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

    def serializeInstance(self, serializer):
        serializer.closedTag("Texture2DInstance", {"ref" : self.id})

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
        self.colors = None
        self.normals = None
        self.texcoords = []

    def setPositions(self, positions):
        self.positions = positions

    def setColors(self, colors):
        self.colors = colors

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
        colors = self.colors
        if colors:
            serializer.startTag("colors")
            serializer.writeReference(colors)
            serializer.endTag()
        for tex in self.texcoords:
            serializer.startTag("texcoords", {"scale": tex.scale})
            serializer.writeDataTag("bias", tex.bias)
            serializer.writeReference(tex)
            serializer.endTag()
        serializer.endTag()


class World(Group):
    def __init__(self, idValue):
        Group.__init__(self, idValue)
        self.activeCamera = None
        self.background = None

    def fillAttributes(self, attr):
        Group.fillAttributes(self, attr)
        if self.activeCamera:
            attr["activeCamera"] = self.activeCamera.id

    def serializeChildren(self, serializer):
        Group.serializeChildren(self, serializer)
        serializer.writeReference(self.background)

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("World", attr)
        self.serializeChildren(serializer)
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
        #normalize the normals
        if norm:
            normLen = math.sqrt(sum([x * x for x in norm]))
            norm = tuple([x / normLen for x in norm])
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

    def getFacePolygonMode(self, face, twoSided, hasPerFaceUV):
        modes = Blender.Mesh.FaceModes
        if not hasPerFaceUV:
            if twoSided:
                culling = PolygonMode.CULL_NONE
            else:
                culling = PolygonMode.CULL_BACK
        elif face.mode & modes.TWOSIDE:
            culling = PolygonMode.CULL_NONE
        else:
            culling = PolygonMode.CULL_BACK
        return PolygonMode.getSharedPolygonMode(culling, self.version)

    def getFaceAppearanceComponents(self, face, mesh):
        hasPerFaceUV = mesh.faceUV
        twoSided = mesh.mode & Blender.Mesh.Modes.TWOSIDED
        #get CompositingMode
        cm = self.getFaceCompositingMode(face, hasPerFaceUV)
        #get PolygonMode
        pm = self.getFacePolygonMode(face, twoSided, hasPerFaceUV)
        #TODO get Fog
        #TODO get PointSpriteMode
        #actual appearance has to be linked up later with the
        #per instance materials
        return cm, pm, face.mat

    def getAppearance(self, cm, pm, material):
        return AppearanceBase.getSharedAppearanceBase(cm, pm, material, self.version)

    def convertLamp(self, lamp):
        mode = Light.DIRECTIONAL
        spotAngle = 45
        spotExponent = 0
        if lamp.type == Blender.Lamp.Types['Lamp']:
            mode = Light.OMNI
        elif lamp.type == Blender.Lamp.Types['Sun']:
            mode = Light.DIRECTIONAL
        elif lamp.type == Blender.Lamp.Types['Spot']:
            mode = Light.SPOT
            #Blender uses double angle
            spotAngle = lamp.spotSize * 0.5
            spotExponent = lamp.spotBlend * 128.0
        elif lamp.type == Blender.Lamp.Types['Hemi']:
            mode = Light.AMBIENT
        elif lamp.type == Blender.Lamp.Types['Area']:
            #TODO this should actually create an array of lights
            pass
        elif lamp.type == Blender.Lamp.Types['Photon']:
            #TODO how do we emulate this type?
            return None
        dist = lamp.dist
        falloffType = lamp.falloffType
        cAttn = 1.0
        lAttn = 0.0
        qAttn = 0.0
        if falloffType == Blender.Lamp.Falloffs['INVLINEAR']:
            #dist is the distance where intensity will be half
            #thus 1 / (0 + l * dist + 0) = .5 gives l = 2 / dist
            cAttn = 0.0
            lAttn = 2 / dist
            qAttn = 0.0
        elif falloffType == Blender.Lamp.Falloffs['INVSQUARE']:
            #dist is the distance where intensity will be half
            #thus 1 / (0 + 0 + q * dist * dist) = .5 gives q = 2 / (dist * dist)
            cAttn = 0.0
            lAttn = 0.0
            qAttn = 2 / (dist * dist)
        elif falloffType == Blender.Lamp.Falloffs['LINQUAD']:
            #mixed linear & quadratic
            cAttn = 0.0
            lAttn = lamp.quad1 * 2 / dist
            qAttn = lamp.quad2 * 2 / (dist * dist)
        #TODO support other falloffs
        intensity = lamp.energy
        if lamp.mode & Blender.Lamp.Modes['Negative']:
            intensity = -intensity
        blamp = BlenderLamp()
        blamp.setMode(mode)
        blamp.setColor([int(round(x * 255)) for x in lamp.col])
        blamp.setIntensity(intensity)
        blamp.setSpot(spotAngle, spotExponent)
        blamp.setAttenuation(cAttn, lAttn, qAttn)
        return blamp

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

        data = object.data
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
            elif type(data) == Blender.Types.LampType:
                if data not in self.convertedDataObjects:
                    self.convertedDataObjects[data] = self.convertLamp(data)
                blamp = self.convertedDataObjects[data]
                light = Light(object.name + "-light")
                light.setMode(blamp.mode)
                light.setColor(blamp.color)
                light.setIntensity(blamp.intensity)
                light.setSpotAngle(blamp.spotAngle)
                light.setSpotExponent(blamp.spotExponent)
                light.setAttenuation(blamp.constantAttenuation,
                    blamp.linearAttenuation, blamp.quadraticAttenuation)
                if returnObject:
                    returnObject.addChild(light)
                else:
                    returnObject = light
        elif returnObject is None:
            #Empty node
            returnObject = Group(object.name)
        if returnObject:
            transform = object.matrixLocal.copy()
            loc = tuple(transform.translationPart())
            if loc != M3XConverter.ZEROS_3:
                returnObject.setTranslation(*loc)
            scale = tuple(transform.scalePart())
            if scale != M3XConverter.ONES_3:
                returnObject.setScale(*scale)
            rot = tuple(object.rot)
            if rot != M3XConverter.ZEROS_3:
                quat = transform.rotationPart().toQuat()
                angle = quat.angle
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

    def wrapInWorld(self, scene):
        world = World(scene.name)
        #TODO link in the background
        #TODO set the active camera
        objects = [world]
        for o in self.objects:
            if isinstance(o, Node):
                world.addChild(o)
            else:
                objects.append(o)
        self.objects = objects
    
    def serialize(self, writer):
        s = Serializer(self.version, writer)
        sections = []
        sections.append(self.objects)
        s.serialize(sections)

