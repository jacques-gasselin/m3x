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
import Blender.Lamp
import Blender.Mesh
import Blender.Types
import Blender.BGL as GL
import sys
import re

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

    def fillAttributes(self, attrs):
        if self.id:
            attrs["id"] = self.id
        if self.userID > 0:
            attrs["userID"] = self.userID
        return


class Transformable(Object3D):
    def __init__(self, idValue):
        Object3D.__init__(self, idValue)


class Node(Transformable):
    def __init__(self, idValue):
        Transformable.__init__(self, idValue)


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
        if material not in shared:
            shared[material] = {}
        appearances = shared[material]
        key = (cm, pm)
        if key not in appearances:
            if version == 2:
                #TODO support Shaders
                pass
            else:
                a = Appearance("Appearance-" + material.name + "-%d" % len(appearances.keys()))
                a.setCompositingMode(cm)
                a.setPolygonMode(pm)
                #TODO get lighting material
                #get the texture 2D elements from the material
                textures = []
                for mtex in material.textures:
                    #TODO convert from Blender textures
                    pass
            appearances[key] = a
        return appearances[key]

    getSharedAppearanceBase = staticmethod(getSharedAppearanceBase)

class Appearance(AppearanceBase):
    def __init__(self, idValue):
        AppearanceBase.__init__(self, idValue)

    def serializeInstance(self, serializer):
        serializer.closedTag("AppearanceInstance", {"ref" : self.id})

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Appearance", attr)
        serializer.writeReference(self.compositingMode)
        serializer.writeReference(self.polygonMode)
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

    def serialize(self, serializer):
        attr = {
            "blending": self.blending
            }
        self.fillAttributes(attr)
        if serializer.version == 2:
            #TODO add some more bits to attr
            serializer.startTag("CompositingMode", attr)
            #TODO add blender and stencil
            serializer.endTag()
        else:
            serializer.closedTag("CompositingMode", attr)


class Group(Node):
    def __init__(self, idValue):
        Node.__init__(self, idValue)
        self.children = []

    def addChild(self, child):
        self.children.append(child)

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        if serializer.version == 2:
            #TODO add some more bits to attr
            pass
        if len(self.children) > 0:
            serializer.startTag("Group", attr)
            for child in self.children:
                serializer.writeReference(child)
            serializer.endTag()
        else:
            serializer.closedTag("Group", attr)


class IndexBuffer(Object3D):
    TRIANGLES = "TRIANGLES"
    def __init__(self, idValue, type):
        Object3D.__init__(self, idValue)
        self.type = type
        self.indices = None

    def setIndices(self, indices):
        self.indices = indices[:]


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

    def serialize(self, serializer):
        attr = {}
        self.fillAttributes(attr)
        serializer.startTag("Mesh", attr)
        serializer.writeReference(self.vertexBuffer)
        for i in xrange(self.submeshCount):
            ib = self.indexBuffers[i]
            ap = self.appearances[i]
            serializer.startTag("submesh")
            serializer.writeReference(ib)
            serializer.writeReference(ap)
            serializer.endTag()
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

    def serialize(self, serializer):
        attr = {
            "culling": self.culling
            }
        self.fillAttributes(attr)
        if serializer.version == 2:
            #TODO add some more bits
            pass
        serializer.closedTag("PolygonMode", attr)


class TriangleStripArray(IndexBuffer):
    def __init__(self, idValue):
        IndexBuffer.__init__(self, idValue, IndexBuffer.TRIANGLES)
        self.stripLengths = None

    def setLengths(self, lengths):
        self.stripLengths = lengths[:]

    def serializeInstance(self, serializer):
        serializer.closedTag("TriangleStripArrayInstance", {"ref" : self.id})

    def serialize(self, serializer):
        attr = {"id": self.id}
        serializer.startTag("TriangleStripArray", attr)
        serializer.writeDataTag("indices", self.indices)
        serializer.writeDataTag("stripLengths", self.stripLengths)
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

    def serialize(self, serializer):
        attr = {
            "componentCount" : self.componentCount
            }
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
        self.writer.write("%s<%s" % (indent, name))
        if attr:
            for n, v in attr.iteritems():
                if v is not None:
                    self.writer.write(" %s=\"%s\"" % (n, str(v)))
        self.writer.write(">\n")

    def closedTag(self, name, attr = None):
        indent = self.indents[-1]
        self.writer.write("%s<%s" % (indent, name))
        if attr:
            for n, v in attr.iteritems():
                if v is not None:
                    self.writer.write(" %s=\"%s\"" % (n, str(v)))
        self.writer.write(" />\n")
        
    def endTag(self):
        name = self.tags.pop()
        self.indents.pop()
        indent = self.indents[-1]
        self.writer.write("%s</%s>\n" % (indent, name))

    def writeDataTag(self, name, data):
        indent = self.indents[-1]
        self.writer.write("%s<%s>%s</%s>\n" % (indent, name, " ".join(map(str, data)), name))

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

    def __init__(self):
        object.__init__(self)
        self.convertedDataObjects = {}
        self.objects = []

    def setVersion(self, version):
        self.version = version

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
            submeshTris = []
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
                    if hasPerVertexUV:
                        uv = tuple(v.uvco)
                    elif hasPerFaceUV:
                        uv = tuple(face.uv[i])
                    else:
                        uv = None
                    faceIndices.append(vseq.getIndex(pos, norm, col, uv))
                #triangulate and add
                for i in xrange(len(faceIndices) - 2):
                    submeshTris.extend(faceIndices[i:i+3])
            if version == 1:
                #Make TriangleStripArrays
                #TODO clean this up, it is a naive solution
                #triangles could be merged
                lengths = [3] * (len(submeshTris) / 3)
                indexBuffer = TriangleStripArray(mesh.name + "-submesh%d" % submeshIndex)
                indexBuffer.setIndices(submeshTris)
                indexBuffer.setLengths(lengths)
            else:
                #Make plain IndexBuffers in TRIANGLES mode
                indexBuffer = IndexBuffer(mesh.name + "-submesh%d" % submeshIndex, IndexBuffer.TRIANGLES)
                indexBuffer.setIndices(submeshTris)
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
        print "objectName:", object.getName()
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
            print "objectData:", data, data.__class__
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
        return returnObject


    def convert(self, objectsToConvert):
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
        self.__exportSelectionOnly = 0
        self.__converter = M3XConverter()

    #
    def gridCoords(self, col, row):
        return [GUI.LEFT_MARGIN + col * GUI.COL_WIDTH,
                GUI.BOTTOM_MARIGIN + row * GUI.ROW_HEIGHT]

    #
    def draw(self):
        width, height = (GUI.COL_WIDTH, GUI.ROW_HEIGHT)
        x, y = self.gridCoords(0, 2)
        Blender.Draw.Toggle("export to world",
                            GUI.EVENT_EXPORT_WORLD_TOGGLE,
                            x, y, width, height,
                            self.__exportToWorld,
                            "Tooltip")
        x, y = self.gridCoords(1, 2)
        Blender.Draw.Toggle("export selection only",
                            GUI.EVENT_EXPORT_SELECTION_TOGGLE,
                            x, y, width, height,
                            self.__exportSelectionOnly,
                            "Tooltip")
        x, y = self.gridCoords(0, 0)
        Blender.Draw.PushButton("export v1.0",
                            GUI.EVENT_EXPORT_10,
                            x, y, width, height,
                            "Tooltip")
        x, y = self.gridCoords(1, 0)
        Blender.Draw.PushButton("export v2.0",
                            GUI.EVENT_EXPORT_20,
                            x, y, width, height,
                            "Tooltip")
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
                objects = Blender.Object.GetSelected()
            else:
                objects = Blender.Scene.GetCurrent().objects
            self.__converter.convert(objects)
            writer = sys.stdout
            self.__converter.serialize(writer)
            writer.flush()
            
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