"""
    Name: 'm3x'
    Blender: 276
    Group: 'Export'
    Tooltip: 'Export to m3x, the xml interchange format for the mobile 3d graphics API M3G'
    
    Copyright (c) 2008-2015, Jacques Gasselin de Richebourg
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

bl_info = {
    "name": "Mobile 3D xml interchange format (.m3x)",
    "author": "jgasseli",
    "version": (1, 1, 1),
    "blender": (2, 76, 0),
    "location": "File > Export > Mobile 3D xml (.m3x)",
    "description": "Exports to Mobile 3D xml interchange format",
    "category": "Import-Export",
}


import bpy
from . import m3x

class UTF8Wrapper:
    def __init__(self, writer):
        self.__writer = writer
    
    def write(self, contents):
        self.__writer.write(bytes(contents, 'UTF-8'))


class Operator_M3XExport( bpy.types.Operator ):
    """Export to M3x"""
    bl_idname   = "object.m3x_export"
    bl_label    = "Export"
    
    filename_ext = ".m3x"
    filepath = bpy.props.StringProperty( subtype='FILE_PATH' )
    filter_glob = bpy.props.StringProperty( default="*.m3x", options={'HIDDEN'} )

    option_export_world = bpy.props.BoolProperty(
                                name        = "Export as World",
                                description = "Ensures all the objects are inside a world container on export",
                                default     = False)

    option_selection_only = bpy.props.BoolProperty(
                                name        = "Export Selection only",
                                description = "Only export the selected objects.",
                                default     = False)

    option_version = bpy.props.EnumProperty(
                                name        = "Version",
                                description = "Version 1.0 (JSR184) or 2.0 (JSR297)",
                                items       = [ ('1', "Version 1.0 (JSR184+)",    "Exports to the format suitable for JSR184 and JSR297"),
                                               ('2', "Version 2.0 (JSR297)",     "Exports to the format suitable only for JSR297") ],
                                default     = '1')

    def __init__(self):
        bpy.types.Operator.__init__(self)

    def draw(self, context):
        layout = self.layout
        layout.prop(self, "option_export_world")
        layout.prop(self, "option_selection_only")
        layout.prop(self, "option_version")

    def execute(self, context):
        export = False
        converter = m3x.M3XConverter()
        self.objectsToConvert = None
        if self.option_version == '1':
            export = True
            converter.setVersion(1)
        elif self.option_version == '2':
            export = True
            converter.setVersion(2)
        if export:
            if self.option_selection_only:
                self.objectsToConvert = [o.select for o in context.scene.objects]
            else:
                self.objectsToConvert = context.scene.objects
            if True:
                #pbar = Blender.Window.DrawProgressBar
                #pbar(0.0, "Converting Blender Objects to M3X")
                converter.convert(self.objectsToConvert)
                if self.option_export_world:
                    converter.wrapInWorld(context.scene)
                #pbar(0.2, "Opening destination file")
                writer = open(self.filepath, "wb")
                #pbar(0.3, "Serializing to destination file")
                converter.serialize(UTF8Wrapper(writer))
                #pbar(0.8, "Flushing serializing buffers")
                writer.flush()
                #pbar(0.9, "Saving and closing destination file")
                writer.close()
                #pbar(1.0, "Finished")
            else:
                converter.convert(self.objectsToConvert)
                if self.option_export_world:
                    converter.wrapInWorld(Blender.Scene.GetCurrent())
                writer = sys.stdout
                converter.serialize(writer)
                writer.flush()
        
        return{'FINISHED'}

    def invoke(self, context, event):
        wm = context.window_manager
        wm.fileselect_add(self)
        return {'RUNNING_MODAL'}



def menu_func(self, context):
    self.layout.operator(Operator_M3XExport.bl_idname, text="Mobile 3D xml interchange (.m3x)")

def register():
    bpy.utils.register_module(__name__)
    bpy.types.INFO_MT_file_export.append(menu_func)

def unregister():
    bpy.utils.unregister_module(__name__)
    bpy.types.INFO_MT_file_export.remove(menu_func)

if __name__ == "__main__":
    register()
