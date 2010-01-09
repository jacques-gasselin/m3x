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


class VertexArray(object):
    def __init__(self):
        object.__init__(self)
        self.components = []

    def setVertexCount(self, vertexCount):
        self.vertexCount = vertexCount
        self.componentCount = len(self.components) / vertexCount

    def extend(self, iterable):
        self.components.extend(iterable)

    def __repr__(self):
        return str(self.__dict__)

class M3XConverter:

    def __init__(self):
        pass

    def convertMesh(self, mesh):
        hasPerVertexColor = mesh.vertexColors
        #otherwise they are per face UVs
        hasPerVertexUV = mesh.vertexUV
        hasPerFaceUV = mesh.faceUV

        #create lists representing the normal and position vertex arrays
        positionArray = VertexArray()
        normalArray = VertexArray()
        if hasPerVertexUV:
            uvArray = VertexArray()
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

        print positionArray
        print normalArray
        if hasPerVertexUV:
            print uvArray

    def convert(self, objectsToConvert):
        for object in objectsToConvert:
            print "objectName:", object.getName()
            data = object.getData(mesh=True)
            print "objectData:", data, data.__class__

            #is it a mesh?
            if type(data) == Blender.Types.MeshType:
                self.convertMesh(data)
            else:
                continue
    
    def toXML(self):
        #TODO implement
        return """<?xml version="1.0" encoding="utf-8"?>"""

class GUI:
    COL_WIDTH = 100
    ROW_HEIGHT = 20
    BOTTOM_MARIGIN = 10
    LEFT_MARGIN = 10

    EVENT_EXPORT_WORLD_TOGGLE = 0
    EVENT_EXPORT_SELECTION_TOGGLE = 1
    EVENT_EXPORT = 2

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
        Blender.Draw.PushButton("export",
                            GUI.EVENT_EXPORT,
                            x, y, width, height,
                            "Tooltip")
        GL.glRasterPos2i(*self.gridCoords(0, 4))
        Blender.Draw.Text("m3x Export", 'large')

    def event(self, evt, val):
        if evt == Blender.Draw.ESCKEY: # Example if esc key pressed
            Blender.Draw.Exit()    # then exit script
            return                 # return from the function

    def button(self, evt):
        if evt == GUI.EVENT_EXPORT_WORLD_TOGGLE:
            self.__exportToWorld = 1 - self.__exportToWorld
        elif evt == GUI.EVENT_EXPORT_SELECTION_TOGGLE:
            self.__exportSelectionOnly = 1 - self.__exportSelectionOnly
        elif evt == GUI.EVENT_EXPORT:
            if self.__exportSelectionOnly:
                print "converting selection only"
                self.__converter.convert(Blender.Object.GetSelected())
            else:
                print "converting all objects from the current scene"
                self.__converter.convert(Blender.Scene.GetCurrent().objects)
            print self.__converter.toXML()

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