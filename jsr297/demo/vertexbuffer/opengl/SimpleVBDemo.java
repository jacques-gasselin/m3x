package vertexbuffer.opengl;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.opengl.GLRenderTarget;

/**
 * @author jgasseli
 */
public class SimpleVBDemo extends Frame
{
    private static final boolean FULLSCREEN = false;
    
    private static class SimpleVBCanvas extends GLCanvas implements Runnable
    {
        private Background background;
        private AbstractRenderTarget renderTarget;

        private VertexBuffer vertexBuffer;
        private IndexBuffer primitives;
        private Appearance appearance;
        
        public SimpleVBCanvas()
        {
            renderTarget = new GLRenderTarget(this);
            background = new Background();
            background.setColor(0x1f1f1f);

            vertexBuffer = new VertexBuffer();
            VertexArray positions = new VertexArray(3, 3, VertexArray.BYTE);
            positions.set(0, 1, new byte[]{ 0, 0, 0 });
            positions.set(1, 1, new byte[]{ 1, 0, 0 });
            positions.set(2, 1, new byte[]{ 1, 1, 0 });
            vertexBuffer.setPositions(positions, 1.0f, null);

            primitives = new IndexBuffer(IndexBuffer.TRIANGLES, 1,
                    new int[]{0, 1, 2});

            appearance = new Appearance();
            
            new Thread(this).start();
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

                //TODO
                g3d.render(vertexBuffer, primitives, appearance, null);

            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
            finally
            {
                g3d.releaseTarget();
            }
        }

        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(40);
                }
                catch (InterruptedException e)
                {
                    //e.printStackTrace();
                }
                repaint();
            }
        }
    }

    private final class WindowAdapter extends java.awt.event.WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }

    SimpleVBDemo()
    {
        super("SimpleVBDemo");
        add(new SimpleVBCanvas());
        addWindowListener(new WindowAdapter());
    }

    public static void main(String[] args)
    {
        Frame frame = new SimpleVBDemo();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        assert(ge != null);

        GraphicsDevice gd = ge.getDefaultScreenDevice();
        assert(gd != null);

        if (FULLSCREEN)
        {
            try
            {
                //try setting fullscreen mode
                DisplayMode dm = gd.getDisplayMode();
                if (gd.isFullScreenSupported())
                {
                    frame.setSize(dm.getWidth(), dm.getHeight());
                    frame.setUndecorated(true);
                    gd.setFullScreenWindow(frame);
                }
            }
            catch (Throwable t)
            {
                //unable to set full screen
                gd.setFullScreenWindow(null);
                frame.setUndecorated(true);
            }
        }
        else
        {
            frame.setSize(800, 600);
        }

        frame.setVisible(true);
    }

}
