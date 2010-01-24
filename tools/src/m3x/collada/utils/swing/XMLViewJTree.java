package m3x.collada.utils.swing;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class XMLTreeNode
{
    private org.w3c.dom.Node domNode;

    XMLTreeNode(final org.w3c.dom.Node domNode)
    {
        if (domNode == null)
        {
            throw new NullPointerException();
        }

        this.domNode = domNode;
    }

    @Override
    public String toString()
    {
        return XMLTreeModel.getTreeNodeLabel(domNode);
    }

    org.w3c.dom.Node getDomNode()
    {
        return domNode;
    }

}

class XMLTreeModel implements TreeModel
{
    private org.w3c.dom.Node rootNode;
    
    XMLTreeModel(final org.w3c.dom.Node rootNode)
    {
        this.rootNode = rootNode;
    }
    
    public void addTreeModelListener(TreeModelListener l)
    {

    }

    public Object getChild(Object parent, int index)
    {
        org.w3c.dom.Node parentNode =
                ((XMLTreeNode) parent).getDomNode();
        final NodeList nodeList = parentNode.getChildNodes();
        int j = 0;
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            if (!isEmpty(nodeList.item(i)))
            {
                if (j == index)
                {
                    return new XMLTreeNode(nodeList.item(i));
                }
                j++;
            }
        }

        return new XMLTreeNode(parentNode.getChildNodes().item(index));
    }

    public int getChildCount(Object parent)
    {
        org.w3c.dom.Node parentNode =
                ((XMLTreeNode) parent).getDomNode();
        final NodeList nodeList = parentNode.getChildNodes();
        int j = 0;
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            if (!isEmpty(nodeList.item(i)))
            {
                j++;
            }
        }

        return j;
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if (parent == null || child == null)
        {
            return -1;
        }
        org.w3c.dom.Node parentNode =
                ((XMLTreeNode) parent).getDomNode();
        org.w3c.dom.Node childNode =
                ((XMLTreeNode) child).getDomNode();

        final NodeList nodeList = parentNode.getChildNodes();
        int j = 0;
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            if (!isEmpty(nodeList.item(i)))
            {
                if (nodeList.item(i) == childNode)
                {
                    return j;
                }
                j++;
            }
        }

        return -1;
    }

    boolean isEmpty(final org.w3c.dom.Node domNode)
    {
        return XMLTreeModel.getTreeNodeLabel(domNode).length() == 0;
    }

    public Object getRoot()
    {
        return new XMLTreeNode(rootNode);
    }

    public boolean isLeaf(Object node)
    {
        org.w3c.dom.Node domNode =
                ((XMLTreeNode) node).getDomNode();
        final NodeList nodeList = domNode.getChildNodes();
        int j = 0;
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            if (!isEmpty(nodeList.item(i)))
            {
                j++;
            }
        }
        return j == 0;
    }

    public void removeTreeModelListener(TreeModelListener l)
    {

    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {

    }

    /**
     * Creates a tree node label corresponding to a dom node.
     * @param domNode
     * @return
     */
    static String getTreeNodeLabel(final org.w3c.dom.Node domNode)
    {
        final boolean isTextNode =
                    domNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE;

        String treeNodeLabel = "";

        if (isTextNode)
        {
            String trimmedText = domNode.getTextContent().trim();

            final int maxTextLength = 100;
            if (trimmedText.length() > maxTextLength)
            {
                trimmedText = trimmedText.substring(0, maxTextLength - 1) +
                        " ...";
            }

            treeNodeLabel = trimmedText;
        }
        else
        {
            treeNodeLabel += "<" + domNode.getNodeName();
            Element el = null;
            try
            {
                el = (Element)domNode;
                final int numAttr = el.getAttributes().getLength();
                for (int j = 0; j < numAttr ; j++)
                {
                    Attr attr = (Attr)el.getAttributes().item(j);
                    final String attrName = attr.getName();
                    if (j == 0)
                    {
                        treeNodeLabel += " ";
                    }
                    else
                    {
                        treeNodeLabel += " ";
                    }

                    treeNodeLabel += attrName + "=\"" +
                            el.getAttribute(attrName) + "\"";
                }

                if (numAttr > 0)
                {
                    treeNodeLabel += "";
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            treeNodeLabel += ">";

        }
        return treeNodeLabel;

    }
}

/**
 * A Swing tree controller providing a visual representation
 * of XML files.
 * @author perarne
 */
public class XMLViewJTree extends JTree
{
    public XMLViewJTree(final org.w3c.dom.Node rootNode)
    {
        removeAll();
        if (rootNode != null)
        {
            setRootNode(rootNode);
        }
    }

    /**
     *
     * @param rootDomNode
     */
    public void setRootNode(final org.w3c.dom.Node rootDomNode)
    {
        if (rootDomNode == null)
        {
            throw new NullPointerException("rootNode is null");
        }

        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(
               XMLTreeModel.getTreeNodeLabel(rootDomNode));

        setModel(new XMLTreeModel(rootDomNode));
        buildTree(rootDomNode, rootTreeNode);
    }

    /**
     * 
     * @param domNode
     * @param treeNode
     */
    private void buildTree(final org.w3c.dom.Node domNode,
                           final DefaultMutableTreeNode treeNode)
    {
        final NodeList children = domNode.getChildNodes();
        final int numChildren = children.getLength();
        
        for (int i = 0; i < numChildren; i++)
        {
            org.w3c.dom.Node childDomNode = children.item(i);

            String treeNodeLabel = XMLTreeModel.getTreeNodeLabel(childDomNode);
            boolean addChild = treeNodeLabel.length() > 0;

            if (addChild)
            {
                DefaultMutableTreeNode childTreeNode =
                        new DefaultMutableTreeNode(treeNodeLabel);
                treeNode.add(childTreeNode);
                buildTree(childDomNode,
                          childTreeNode);
            }
        }
    }

    
}
