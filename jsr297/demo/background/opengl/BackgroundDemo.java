package background.opengl;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
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
public class BackgroundDemo extends Frame
{
    private static class BackgroundCanvas extends GLCanvas implements Runnable
    {
        Background background;
        RenderTarget renderTarget;
        float hue;
        final float saturation = 1.0f;
        final float brightness = 1.0f;

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
                final int color = Color.HSBtoRGB(hue, saturation, brightness);
                background.setColor(color);
                hue += 0.01f;
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

    BackgroundDemo()
    {
        super("BackgroundDemo");
        setSize(800, 600);
        add(new BackgroundCanvas());

        addWindowListener(new WindowAdapter());
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

}
