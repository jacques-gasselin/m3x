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
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import m3x.translation.m3g.xml.AnimationControllerConverter;
import m3x.translation.m3g.xml.AnimationTrackConverter;
import m3x.translation.m3g.xml.AppearanceConverter;
import m3x.translation.m3g.xml.BackgroundConverter;
import m3x.translation.m3g.xml.CameraConverter;
import m3x.translation.m3g.xml.CompositingModeConverter;
import m3x.translation.m3g.xml.FogConverter;
import m3x.translation.m3g.xml.GroupConverter;
import m3x.translation.m3g.xml.Image2DConverter;
import m3x.translation.m3g.xml.KeyframeSequenceConverter;
import m3x.translation.m3g.xml.LightConverter;
import m3x.translation.m3g.xml.MaterialConverter;
import m3x.translation.m3g.xml.MeshConverter;
import m3x.translation.m3g.xml.MorphingMeshConverter;
import m3x.translation.m3g.xml.PolygonModeConverter;
import m3x.translation.m3g.xml.SkinnedMeshConverter;
import m3x.translation.m3g.xml.Texture2DConverter;
import m3x.translation.m3g.xml.TriangleStripArrayConverter;
import m3x.translation.m3g.xml.VertexArrayConverter;
import m3x.translation.m3g.xml.VertexBufferConverter;
import m3x.translation.m3g.xml.WorldConverter;

/**
 *
 * @author jgasseli
 */
public class XmlToBinaryTranslator extends BinaryTranslator
{
    private Map<Class, Class> converterMap;
    private String version;

    public XmlToBinaryTranslator(String version)
    {
        converterMap = new Hashtable<Class, Class>();
        this.version = version;

        //add supported class converters
        converterMap.put(m3x.xml.AnimationController.class,
                AnimationControllerConverter.class);
        converterMap.put(m3x.xml.AnimationTrack.class,
                AnimationTrackConverter.class);
        converterMap.put(m3x.xml.Appearance.class,
                AppearanceConverter.class);
        converterMap.put(m3x.xml.Background.class,
                BackgroundConverter.class);
        converterMap.put(m3x.xml.Camera.class,
                CameraConverter.class);
        converterMap.put(m3x.xml.CompositingMode.class,
                CompositingModeConverter.class);
        converterMap.put(m3x.xml.Fog.class,
                FogConverter.class);
        //For the skeleton node
        converterMap.put(m3x.xml.GroupType.class,
                GroupConverter.class);
        converterMap.put(m3x.xml.Group.class,
                GroupConverter.class);
        converterMap.put(m3x.xml.Image2D.class,
                Image2DConverter.class);
        converterMap.put(m3x.xml.KeyframeSequence.class,
                KeyframeSequenceConverter.class);
        converterMap.put(m3x.xml.Light.class,
                LightConverter.class);
        converterMap.put(m3x.xml.Material.class,
                MaterialConverter.class);
        converterMap.put(m3x.xml.Mesh.class,
                MeshConverter.class);
        converterMap.put(m3x.xml.MorphingMesh.class,
                MorphingMeshConverter.class);
        converterMap.put(m3x.xml.PolygonMode.class,
                PolygonModeConverter.class);
        converterMap.put(m3x.xml.SkinnedMesh.class,
                SkinnedMeshConverter.class);
        converterMap.put(m3x.xml.Texture2D.class,
                Texture2DConverter.class);
        converterMap.put(m3x.xml.TriangleStripArray.class,
                TriangleStripArrayConverter.class);
        converterMap.put(m3x.xml.VertexArray.class,
                VertexArrayConverter.class);
        converterMap.put(m3x.xml.VertexBuffer.class,
                VertexBufferConverter.class);
        converterMap.put(m3x.xml.World.class,
                WorldConverter.class);
    }

    public static void convert(File source, File target)
    {
        if (source == null)
        {
            throw new NullPointerException("source is null");
        }
        if (!source.exists())
        {
            throw new IllegalArgumentException("source file does not exist");
        }
        if (target == null)
        {
            throw new NullPointerException("target is null");
        }
        if (target.exists() && !target.canWrite())
        {
            throw new IllegalArgumentException(
                    "target file exists but is write protected");
        }
        
        //deserialise XML stream
        m3x.xml.M3G xmlRoot = null;
        try
        {
            final m3x.xml.Deserializer deserializer = new m3x.xml.Deserializer();
            xmlRoot = deserializer.deserialize(new FileReader(source));
        }
        catch (FileNotFoundException ex)
        {
            throw new IllegalArgumentException("source " + source
                + " is not a valid file", ex);
        }

        //translate
        final m3x.m3g.Object3D[] binRoots = convertRoot(xmlRoot);

        //serialise the binary stream
        try
        {
            m3x.m3g.Saver.save(new FileOutputStream(target),
                binRoots,
                xmlRoot.getVersion(), xmlRoot.getAuthor());
        }
        catch (FileNotFoundException ex)
        {
            throw new IllegalArgumentException("target " + target
                + " is not a valid path", ex);
        }
        catch (IOException ex)
        {
            throw new IllegalArgumentException(
                    "unable to serialise xml objects", ex);
        }
    }

    /**
     * Converts an M3X xml document root to an M3G root object array.
     * @param root
     * @throws NullPointerException if root is null
     * @return
     */
    public static m3x.m3g.Object3D[] convertRoot(m3x.xml.M3G root)
    {
        if (root == null)
        {
            throw new NullPointerException("root is null");
        }
        
        XmlToBinaryTranslator translator = new XmlToBinaryTranslator(root.getVersion());
        for (m3x.xml.Section section : root.getSections())
        {
            for (m3x.xml.Object3D object : section.getObjects())
            {
                translator.getObject(object);
            }
        }

        //collect the result vector to an array
        Vector<m3x.m3g.Object3D> rootVector = translator.getRootVector();
        m3x.m3g.Object3D[] arr = new m3x.m3g.Object3D[rootVector.size()];
        return rootVector.toArray(arr);
    }

    public m3x.m3g.Object3D getObject(m3x.xml.Object3D key)
    {
        return super.getObject((java.lang.Object)key);
    }

    public m3x.m3g.Object3D getReference(m3x.xml.Object3D key)
    {
        return super.getReference((java.lang.Object)key);
    }

    public void setObject(m3x.xml.Object3D key, m3x.m3g.Object3D value)
    {
        super.setObject(key, value);
    }

    @Override
    protected Class getConverterClass(Class objectClass)
    {
        return converterMap.get(objectClass);
    }

    private enum CommandOption
    {
        OutputFile,
        InputFile;
    }
    
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Usage: " + XmlToBinaryTranslator.class.getSimpleName()
                + " -o binary_outfile xml_infile");
        }


        File inputFile = null;
        File outputFile = null;
        
        CommandOption option = CommandOption.InputFile;
        for (String value : args)
        {
            //establish which option is selected
            if (value.equals("-o") || value.equals("--output"))
            {
                option = CommandOption.OutputFile;
                continue;
            }

            if (option == CommandOption.InputFile)
            {
                inputFile = new File(value);
            }
            else if (option == CommandOption.OutputFile)
            {
                outputFile = new File(value);
            }

            //default back to input file arguments
            option = CommandOption.InputFile;
        }

        System.out.println("inputFile: " + inputFile);
        System.out.println("outputFile: " + outputFile);
        convert(inputFile, outputFile);
    }
}
