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


class VertexArray(object):
    BYTE = "BYTE"
    SHORT = "SHORT"
    FIXED = "FIXED"
    HALF = "HALF"
    FLOAT = "FLOAT"
    
    def __init__(self, idValue):
        object.__init__(self)
        self.id = idValue
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

    def __compress(self, qMin, qMax, scale, bias):
        if scale is None:
            scale = 1.0
        if bias is None:
            bias = [0.0] * self.componentCount
        componentCount = self.componentCount
        for v in xrange(self.vertexCount):
            start = componentCount * v
            vertex = self.components[start:start + componentCount]
            for c in xrange(componentCount):
                #quantize
                q = round((vertex[c] - bias[c]) / scale)
                #clamp and write back
                vertex[c] = max(qMin, min(q, qMax))
        self.scale = scale
        self.bias = bias

    def compressShort(self, scale = None, bias = None):
        """compressShort(self, float scale, float[] bias) -> None
        Compresses the vertex array to fit in a set of scaled and
        biased shorts"""
        qMin = -32768
        qMax = 32768
        if scale is None:
            #TODO
            pass
        self.__compress(qMin, qMax, scale, bias)
        self.componentType = VertexArray.SHORT

    def compressByte(self, scale = None, bias = None):
        """compressByte(self, float scale, float[] bias) -> None
        Compresses the vertex array to fit in a set of scaled and
        biased byte"""
        qMin = -128
        qMax = 127
        if scale is None:
            #TODO
            pass
        self.__compress(qMin, qMax, scale, bias)
        self.componentType = VertexArray.BYTE

    def compressUnsignedByte(self, scale = None, bias = None):
        """compressUnsignedByte(self, float scale, float[] bias) -> None
        Compresses the vertex array to fit in a set of scaled and
        biased unsigned bytes"""
        qMin = 0
        qMax = 255
        if scale is None:
            #TODO
            pass
        self.__compress(qMin, qMax, scale, bias)
        self.componentType = VertexArray.BYTE

    def __repr__(self):
        return str(self.__dict__)

    def write(self, writer):
        writer.write("  <VertexArray")
        if self.id:
            writer.write(" id=\"%s\"" % self.id)
        writer.write(" componentCount=\"%d\"" % self.componentCount)
        writer.write(">\n")
        if self.componentType == VertexArray.BYTE:
            writer.write("    <byteComponents>")
            writer.write("\n    ")
            for c in self.components:
                writer.write("%i " % c)
            writer.write("\n")
            writer.write("    </byteComponents>\n")
        elif self.componentType == VertexArray.SHORT:
            writer.write("    <shortComponents>")
            writer.write("\n    ")
            for c in self.components:
                writer.write("%i " % c)
            writer.write("\n")
            writer.write("    </shortComponents>\n")
        writer.write("  </VertexArray>\n")


class VertexBuffer(object):
    def __init__(self, idValue):
        object.__init__(self)
        self.id = idValue
        self.positions = None
        self.normals = None
        self.texcoords = []

    def setPositions(self, positions):
        self.positions = positions

    def setNormals(self, normals):
        self.normals = normals

    def addTexcoords(self, texcoords):
        self.texcoords.append(texcoords)

    def write(self, writer):
        writer.write("  <VertexBuffer")
        if self.id:
            writer.write(" id=\"%s\"" % self.id)
        writer.write(">\n")
        if self.positions:
            writer.write("      <positions scale=\"%f\">\n" % self.positions.scale)
            writer.write("        <bias>%s</bias>\n" % " ".join(map(str, self.positions.bias)))
            writer.write("        <VertexArrayInstance ref=\"%s\" />\n" % self.positions.id)
            writer.write("      </positions>\n")
        writer.write("  </VertexBuffer>\n")
        
class Section(object):
    def __init__(self):
        object.__init__(self)
        self.objects = []

    def append(self, object):
        self.objects.append(object)

    def extend(self, iterable):
        self.objects.extend(iterable)

    def write(self, writer):
        writer.write("""  <section>\n""")
        for object in self.objects:
            object.write(writer)
        writer.write("""  </section>\n""")

class M3XConverter(object):

    def __init__(self):
        object.__init__(self)
        self.convertedObjects = {}
        self.sections = []

    def setVersion(self, version):
        self.version = version

    def convertMesh(self, mesh, section):
        version = self.version
        hasPerVertexColor = mesh.vertexColors
        #otherwise they are per face UVs
        hasPerVertexUV = mesh.vertexUV
        hasPerFaceUV = mesh.faceUV

        #create lists representing the normal and position vertex arrays
        positionArray = VertexArray(mesh.name + "-positions")
        normalArray = VertexArray(mesh.name + "-normal")
        #TODO colors
        #TODO face UVs
        if hasPerVertexUV:
            uvArray = VertexArray(mesh.name + "-texcoords0")
        verts = mesh.verts
        vertexCount = len(verts)
        for v in verts:
            positionArray.extend(v.co)
            normalArray.extend(v.no)
            if hasPerVertexUV:
                uvArray.extend(v.uvco)
        positionArray.setVertexCount(vertexCount)
        normalArray.setVertexCount(vertexCount)
        if hasPerVertexUV:
            uvArray.setVertexCount(vertexCount)

        if version == 1:
            #version 1 has SHORT as the highest fidelity type
            #therefore FLOAT data must be compressed
            positionArray.compressShort()
            #ensure it fits in a byte and normals are always in
            #the range [-1, 1]
            normalArray.compressByte(2 / 255.0, [1 / 255.0] * 3)
            if hasPerVertexUV:
                #version 1 has SHORT as the highest fidelity type
                #therefore FLOAT data must be compressed
                uvArray.compressShort()
                
        vertexBuffer = VertexBuffer(mesh.name + "-vertexBuffer")

        objects = []
        objects.append(positionArray)
        objects.append(normalArray)
        vertexBuffer.setPositions(positionArray)
        vertexBuffer.setNormals(normalArray)
        if hasPerVertexUV:
            objects.append(uvArray)
            vertexBuffer.addTexcoords(uvArray)
        objects.append(vertexBuffer)

        section.extend(objects)
        self.convertedObjects[mesh] = objects

    def convert(self, objectsToConvert):
        section = Section()
        for object in objectsToConvert:
            print "objectName:", object.getName()
            data = object.getData(mesh=True)
            print "objectData:", data, data.__class__

            #is it a mesh?
            if type(data) == Blender.Types.MeshType:
                if data not in self.convertedObjects:
                    self.convertMesh(data, section)
            else:
                continue
        self.sections.append(section)
    
    def write(self, writer):
        writer.write("""<?xml version="1.0" encoding="utf-8"?>\n""")
        writer.write("""<m3g version="%d.0">\n""" % self.version)
        for section in self.sections:
            section.write(writer)
        writer.write("""</m3g>""")

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
            self.__converter.write(writer)
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