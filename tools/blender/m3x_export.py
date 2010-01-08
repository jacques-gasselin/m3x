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
import Blender.BGL as GL

class M3XConverter:

    def __init__(self, objectsToConvert):
        for object in objectsToConvert:
            data = object.getData()
            try:
                vertices = data.verts
                faces = data.faces
            except:
                continue

            objectName = object.getName()

            #create lists representing the normal and position vertex arrays
            positionVertexArray = []
            normalVertexArray = []
            uvVertexArray = []
            for vertex in vertices:
                for x in vertex.co:
                    positionVertexArray.append(x)

                for x in vertex.no:
                    normalVertexArray.append(x)

                for x in vertex.uvco:
                    uvVertexArray.append(x)

            print 'pos', positionVertexArray
            print 'n  ', normalVertexArray
            print 'uv ', uvVertexArray

    def toXML(self):
        print "<XML here>"

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
        self.__converter = None

    #
    def gridCoords(self, row, col):
        return [GUI.LEFT_MARGIN + col * GUI.COL_WIDTH,
                GUI.BOTTOM_MARIGIN + row * GUI.ROW_HEIGHT]

    #
    def draw(self):
        width, height = (GUI.COL_WIDTH, GUI.ROW_HEIGHT)
        x, y = self.gridCoords(2, 0)
        self.__exportToWorld = Blender.Draw.Toggle("export to world",
                            GUI.EVENT_EXPORT_WORLD_TOGGLE,
                            x, y, width, height,
                            0,
                            "Tooltip")
        x, y = self.gridCoords(2, 1)
        self.__exportSelectionOnly = Blender.Draw.Toggle("export selection only",
                            GUI.EVENT_EXPORT_SELECTION_TOGGLE,
                            x, y, width, height,
                            0,
                            "Tooltip")
        x, y = self.gridCoords(0, 0)
        Blender.Draw.PushButton("export",
                            GUI.EVENT_EXPORT,
                            x, y, width, height,
                            "Tooltip")
        GL.glRasterPos2i(*self.gridCoords(4, 0))
        Blender.Draw.Text("M3X Export", 'large')

    def event(self, evt, val):
        if evt == Blender.Draw.ESCKEY: # Example if esc key pressed
            Blender.Draw.Exit()    # then exit script
            return                 # return from the function


    def button(self, evt):
        if evt == GUI.EVENT_EXPORT_WORLD_TOGGLE:
            print "EVENT_EXPORT_WORLD_TOGGLE"
        elif evt == GUI.EVENT_EXPORT_SELECTION_TOGGLE:
            print "EVENT_EXPORT_SELECTION_TOGGLE"
        elif evt == GUI.EVENT_EXPORT:
            self.__converter = M3XConverter(Blender.Object.GetSelected())
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