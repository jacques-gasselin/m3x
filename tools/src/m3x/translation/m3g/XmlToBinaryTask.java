package m3x.translation.m3g;

import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author jgasseli
 */
public class XmlToBinaryTask extends Task
{
    private List<FileSet> filesetList;

    public XmlToBinaryTask()
    {
        filesetList = new Vector<FileSet>();
    }

    public void addConfiguredFileSet(FileSet fileset)
    {
        filesetList.add(fileset);
    }

    @Override
    public void execute()
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
