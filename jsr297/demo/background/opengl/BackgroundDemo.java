package background.opengl;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.RenderTarget;
import javax.microedition.m3g.opengl.GLRenderTarget;

/**
 * @author jgasseli
 */
public class BackgroundDemo extends Frame implements WindowListener
{
    private static class BackgroundCanvas extends GLCanvas implements Runnable
    {
        Background background;
        RenderTarget renderTarget;
        int color;

        public BackgroundCanvas()
        {
            renderTarget = new GLRenderTarget(this);
            background = new Background();

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
                color += 1;
                background.setColor(color);
                g3d.clear(background);
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
                    Thread.sleep(25);
                }
                catch (InterruptedException e)
                {
                    //e.printStackTrace();
                }
                repaint();
            }
        }
    }

    BackgroundDemo()
    {
        super("BackgroundDemo");
        setSize(800, 600);
        add(new BackgroundCanvas());

        addWindowListener(this);
    }

    public static void main(String[] args)
    {
        Frame frame = new BackgroundDemo();

        if (false)
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();

            try
            {
                if (gd.isFullScreenSupported())
                {
                    frame.setUndecorated(true);
                    gd.setFullScreenWindow(frame);
                }
            }
            finally
            {
                gd.setFullScreenWindow(null);
            }
        }
        
        frame.setVisible(true);
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        System.exit(0);
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }
}
