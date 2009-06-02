package background.opengl;

import java.awt.Frame;
import java.awt.Graphics;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.RenderTarget;
import javax.microedition.m3g.opengl.GLRenderTarget;

/**
 * @author jgasseli
 */
public class BackgroundDemo extends Frame
{
    private static class BackgroundCanvas extends GLCanvas
    {
        Background background = new Background();
        RenderTarget renderTarget;

        public BackgroundCanvas()
        {
            renderTarget = new GLRenderTarget(this);
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);

            Graphics3D g3d = Graphics3D.getInstance();

            try
            {
                g3d.bindTarget(renderTarget);
                g3d.clear(background);
            }
            finally
            {
                g3d.releaseTarget();
            }
        }
    }

    BackgroundDemo()
    {
        add(new BackgroundCanvas());
    }

    public static void main(String[] args)
    {
        Frame frame = new BackgroundDemo();
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
