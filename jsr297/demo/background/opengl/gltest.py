import os
import os.path
import sys

#---Setup the classpath
sys.path.append(os.path.abspath("../../../../lib/jogl/jogl.jar"))
sys.path.append(os.path.abspath("../../../../lib/jogl/gluegen-rt.jar"))
sys.path.append(os.path.abspath("../../../../lib/vecmath/vecmath.jar"))
sys.path.append(os.path.abspath("../../../dist/m3x-jsr297.jar"))
#print sys.path

from java.awt import Frame
from javax.media.opengl import GL, GLCanvas;

class BackgroundCanvas(GLCanvas):
    def __init_(self):
        GLCanvas.__init__(self)
        self.renderTarget = GLRenderTarget(self)
        self.background = Background()
        #Magenta color
        self.background.setColor(0xffff00ff)
        self.setAutoSwapBufferMode(True)

    def paint(self, g):
        GLCanvas.paint(self, g)
        self.getContext().makeCurrent()
        gl = self.getGL()
        gl.glEnable(GL.GL_DEPTH_TEST)
        gl.glClear(GL.GL_COLOR_BUFFER_BIT)

class BackgroundDemo(Frame):
    def __init__(self):
        Frame.__init__(self)
        self.add(BackgroundCanvas())

    def main(cls):
        frame = cls();
        frame.setSize(800, 600);
        frame.setVisible(True);
    main = classmethod(main)

if __name__ == "__main__":
    BackgroundDemo.main()
