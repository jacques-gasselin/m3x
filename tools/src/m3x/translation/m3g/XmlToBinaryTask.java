package m3x.translation.m3g;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;

/**
 *
 * @author jgasseli
 */
public class XmlToBinaryTask extends Task
{
    private List<FileSet> filesetList;
    private FileNameMapper filenameMapper;
    private File executeDirectory;

    public XmlToBinaryTask()
    {
        filesetList = new Vector<FileSet>();
    }

    public void addConfiguredFileSet(FileSet fileset)
    {
        filesetList.add(fileset);
    }

    public void addConfiguredFilenameMapper(FileNameMapper mapper)
    {
        if (filenameMapper == null)
        {
            filenameMapper = mapper;
        }
        else
        {
            throw new IllegalArgumentException("only one mapper allowed");
        }
    }

    public void setDir(File dir)
    {
        executeDirectory = dir;
    }

    @Override
    public void execute()
    {
        for (FileSet fs : filesetList)
        {
            Iterator it = fs.iterator();
            while (it.hasNext())
            {
                File source = ((FileResource)it.next()).getFile();
                try
                {
                    String preMappingPath = source.getCanonicalPath();
                    String postMappingPath = preMappingPath;
                    if (filenameMapper != null)
                    {
                        postMappingPath = filenameMapper.mapFileName(preMappingPath)[0];
                    }
                    //apply the conversion for the given file names.
                    File target = new File(postMappingPath);
                    XmlToBinaryTranslator.convert(source, target);
                }
                catch (IOException ex)
                {
                    throw new IllegalArgumentException("file " + source
                        + " does not exist");
                }
            }
        }
    }
}
