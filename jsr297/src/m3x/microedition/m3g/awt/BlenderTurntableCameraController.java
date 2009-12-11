package m3x.microedition.m3g.awt;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

/**
 * A turntable controller operating like the one in Blender 2.4+
 * 
 * @author jgasseli
 */
public class BlenderTurntableCameraController extends MouseAndKeyInputTransformController
{
    private final Camera camera;
    
    private float yaw;
    private float pitch;
    private float dolly;

    private float x, y, z;
    private int mx, my;

    private boolean leftDown;
    private boolean wheelDown;

    public BlenderTurntableCameraController(Camera camera, Component component)
    {
        this(camera, component, 0, 0, 20);
    }

    public BlenderTurntableCameraController(Camera camera, Component component,
            float yaw, float pitch, float dolly)
    {
        super(component);
        
        if (camera == null)
        {
            throw new NullPointerException("camera is null");
        }

        setTransform(new Transform());

        this.camera = camera;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dolly = dolly;
    }

    @Override
    public synchronized void mouseDragged(MouseEvent e)
    {
        final int eventX = e.getX();
        final int eventY = e.getY();
        final int dx = eventX - mx;
        final int dy = eventY - my;
        mx = eventX;
        my = eventY;

        final boolean emulateWheelDown = (leftDown && e.isAltDown());
        if (emulateWheelDown || wheelDown)
        {
            if (e.isShiftDown())
            {
                //pan
                //TODO use the camera to unproject the pan and move properly
                final float[] vector = new float[]{ -dx, dy, 0, 0 };
                this.getTransform().transform(vector);

                final float factor = 0.005f;
                this.x += vector[0] * factor;
                this.y += vector[1] * factor;
                this.z += vector[2] * factor;
            }
            else if (e.isControlDown())
            {
                //dolly
                dolly += dy * 0.02f;
            }
            else
            {
                yaw += -dx * 0.1f;
                pitch += -dy * 0.1f;
            }
        }
    }

    @Override
    public synchronized void mousePressed(MouseEvent e)
    {
        //reset the delta movement origin
        mx = e.getX();
        my = e.getY();

        switch (e.getButton())
        {
            case MouseEvent.BUTTON1:
            {
                leftDown = true;
                break;
            }
            case MouseEvent.BUTTON2:
            {
                wheelDown = true;
                break;
            }
        }
    }

    @Override
    public synchronized void mouseReleased(MouseEvent e)
    {
        switch (e.getButton())
        {
            case MouseEvent.BUTTON1:
            {
                leftDown = false;
                break;
            }
            case MouseEvent.BUTTON2:
            {
                wheelDown = false;
                break;
            }
        }
    }

    @Override
    public synchronized void mouseWheelMoved(MouseWheelEvent e)
    {
        dolly += e.getWheelRotation() * 0.25f;
    }

    @Override
    public synchronized void update(double seconds)
    {
        Transform t = getTransform();
        if (t != null)
        {
            t.setIdentity();
            t.postTranslate(x, y, z);
            t.postRotate(yaw, 0, 1, 0);
            t.postRotate(pitch, 1, 0, 0);
            t.postTranslate(0, 0, dolly);
        }
    }

    
}
