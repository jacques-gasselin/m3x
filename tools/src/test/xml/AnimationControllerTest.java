/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.xml;

import junit.framework.TestCase;
import m3x.xml.AnimationController;

/**
 *
 * @author jgasseli
 */
public class AnimationControllerTest extends TestCase
{
    private AnimationController animationController;
    
    /**Sets up the test case fixture.
     * Creates a new instance of an m3x.jaxb.AnimationController.
     */
    @Override
    public void setUp()
    {
        animationController = new AnimationController();
    }

    public void testGetSpeed()
    {
        //assert on the default value.
        assertEquals(animationController.getSpeed(), 1.0f, 0);
    }
    
    public void testSetSpeed()
    {
        final float positiveSpeed = 2.0f;
        animationController.setSpeed(positiveSpeed);
        assertEquals(animationController.getSpeed(), positiveSpeed, 0);
        final float negativeSpeed = -1.0f;
        animationController.setSpeed(negativeSpeed);
        assertEquals(animationController.getSpeed(), negativeSpeed, 0);
    }
    
    public void testGetWeight()
    {
        assertEquals(animationController.getWeight(), 1.0f, 0);
    }
    
    public void testSetWeight()
    {
        final float positiveWeight = 2.0f;
        animationController.setWeight(positiveWeight);
        assertEquals(animationController.getWeight(), positiveWeight, 0);
        final float negativeWeight = -1.0f;
        animationController.setWeight(negativeWeight);
        assertEquals(animationController.getWeight(), negativeWeight, 0);
    }    
}
