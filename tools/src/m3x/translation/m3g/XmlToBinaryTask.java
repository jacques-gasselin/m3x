/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
