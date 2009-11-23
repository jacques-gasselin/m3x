package m3x.collada.utils.swing;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import m3x.collada.COLLADA;
import m3x.collada.Deserializer;
import m3x.collada.VisualScene;

/**
 *
 */
public final class ColladaXMLDemo
{
    private ColladaXMLDemo()
    {

    }
    
    public static void main(String[] args)
    {
        final String daeFile =
                //"/Users/perarne/cvs_sandbox/dev_deadspace_iphone/res/master/high_base/models/fx_blood_spatter.dae";
                "/Users/perarne/cvs_sandbox/MonkeyAPI/J2SE/demo/collada/xml/corridor.dae";

        InputStream inputStream =
                ColladaXMLDemo.class.getResourceAsStream("corridor.dae");
        File file = new File(daeFile);
        if (!file.exists())
        {
            System.out.println(file + " does not exist");
            return;
        }

        // load the document from a file:
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();

        DocumentBuilder loader = null;
        try
        {
            loader = factory.newDocumentBuilder();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        org.w3c.dom.Document document = null;
        try
        {
            document = loader.parse(daeFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        org.w3c.dom.NodeList nl = document.getChildNodes();
        if (nl.getLength() != 1)
        {
            throw new RuntimeException();
        }

        //validation stuff
        inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        System.out.println(inputStream);
        COLLADA colladaRoot = null;
        try
        {
            colladaRoot = Deserializer.deserialize(inputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(colladaRoot);

        
        String testId1 = "corridor_corner3";
        String testId2 = "corridor_dark2Shape";
        String testId3 = "sune mangs";
        //System.out.println("getObjectById(" + testId1 + ") = " +
        //                    colladaRoot.getObjectByID(testId1));
        //System.out.println("getObjectById(" + testId2 + ") = " +
        //                    colladaRoot.getObjectByID(testId2));
        //System.out.println("getObjectById(" + testId3 + ") = " +
        //                    colladaRoot.getObjectByID(testId3));
        
        
        ColladaDemoFrame frame = new ColladaDemoFrame(nl.item(0));
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}

final class COLLADAUtil
{
    private COLLADAUtil()
    {

    }

    static VisualScene getVisualSceneFromURL(final String url)
    {
        return null;
    }
}

final class ColladaDemoFrame extends JFrame
{
    private Vector searchHits;
    private XMLViewJTree colladaTree;
    private JList searchHitList;

    ColladaDemoFrame(final org.w3c.dom.Node node)
    {
        searchHits = new Vector();
        searchHitList = new JList();
        setLayout(new GridLayout(1, 1, 1, 1));
        addWindowListener(
            new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    System.exit(0);
                }
            }
        );

        setTitle("ColladaTest");
        colladaTree = new XMLViewJTree(node);
        
        JScrollPane treeView = new JScrollPane(colladaTree);

        add(treeView);
        //add(m_searchHitList);
    }

    
}
