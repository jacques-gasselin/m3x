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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;

/**
 *
 * @author jgasseli
 */
public class XmlToBinaryTask extends Task
{
    private List<FileSet> filesetList;
    private Path classpath;
    private Mapper mapper;
    private File executeDirectory;
    
    public XmlToBinaryTask()
    {
        filesetList = new Vector<FileSet>();
    }

    @Override
    public void init()
    {
        super.init();
    }

    public void addFileSet(FileSet fileset)
    {
        filesetList.add(fileset);
    }

    public void setClasspath(Path p)
    {
        this.classpath = p;
    }

    public Path createClasspath()
    {
        this.classpath = new Path(getProject());
        return this.classpath;
    }

    public Mapper createMapper() throws BuildException
    {
        if (mapper == null)
        {
            mapper = new Mapper(getProject());
        }
        else
        {
            throw new IllegalArgumentException("only one mapper allowed");
        }
        return mapper;
    }

    public void setDir(File dir)
    {
        executeDirectory = dir;
    }

    @Override
    public void execute()
    {
        AntClassLoader loader = null;
        Method convertMethod = null;
        final String classname = "m3x.translation.m3g.XmlToBinaryTranslator";

        {
            Class targetClass = null;
            try
            {
                if (classpath == null)
                {
                    targetClass = Class.forName(classname);
                }
                else
                {
                    loader = getProject().createClassLoader(classpath);
                    loader.setParent(getProject().getCoreLoader());
                    loader.setParentFirst(false);
                    loader.addJavaLibraries();
                    loader.setIsolated(true);
                    loader.setThreadContextLoader();
                    loader.forceLoadClass(classname);
                    targetClass = Class.forName(classname, true, loader);
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new BuildException("Could not find " + classname + "."
                                         + " Make sure you have it in your"
                                         + " classpath");
            }
            try
            {
                convertMethod = targetClass.getMethod("convert",
                        new Class[] {File.class, File.class});
            }
            catch (NoSuchMethodException e)
            {
                throw new BuildException("No method convert(File, File) in "
                                         + classname);
            }
            if (convertMethod == null)
            {
                throw new BuildException("Could not find convert(File, File) method in "
                                         + classname);
            }
            if ((convertMethod.getModifiers() & Modifier.STATIC) == 0)
            {
                throw new BuildException("convert(File, File) method in " + classname
                    + " is not declared static");
            }
        }

        FileNameMapper fnmapper = mapper.getImplementation();
        for (FileSet fs : filesetList)
        {
            Iterator it = fs.iterator();
            while (it.hasNext())
            {
                File sourceFile = ((FileResource)it.next()).getFile();
                try
                {
                    String preMappingPath = sourceFile.getCanonicalPath();
                    String postMappingPath = fnmapper.mapFileName(preMappingPath)[0];
                    //apply the conversion for the given file names.
                    final File targetFile = new File(postMappingPath);
                    convertMethod.invoke(null, 
                            new Object[] {sourceFile, targetFile});
                }
                catch (InvocationTargetException ex)
                {
                    String message = ex.getMessage();
                    final Throwable cause = ex.getCause();
                    if (cause != null)
                    {
                        message = cause.getMessage();
                    }
                    throw new BuildException("convert(File, File) failed on\n"
                            + sourceFile + ": "+ message);
                }
                catch (IllegalAccessException ex)
                {
                    throw new BuildException("invoking convert(File, File) failed"
                        + " due to an access violation", ex);
                }
                catch (IOException ex)
                {
                    throw new BuildException("file " + sourceFile
                        + " does not exist");
                }
            }
        }
    }
}
