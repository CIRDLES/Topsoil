/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.plot.internal;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertTrue;

import org.cirdles.topsoil.plot.internal.SVGSaver;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import com.google.common.jimfs.Jimfs;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author parizotclement
 */
public class SVGSaverTest {

    private SVGSaver svg;
    private Document svgDocument;

    @Before
    public void setUp() throws ParserConfigurationException {
        svg = new SVGSaver();
        //Create a SVG Document
        svgDocument = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
    }

    /**
     * Test of method writeSVGToFile
     * Write a file and asserts if it exists
     *
     * @throws java.io.IOException
     */
    @Test
    public void testWriteToOutputStream() throws IOException {

        // create filesystem and get a "virtual path"
        FileSystem virtualFileSystem = Jimfs.newFileSystem();
        Path virtualPath = virtualFileSystem.getPath("testWriteSVGToFile");
        OutputStream out = Files.newOutputStream(virtualPath);

        svg.writeSVGToOutputStream(svgDocument, out);

        assertTrue(Files.exists(virtualPath));
    }

    @Test
    public void testSVGDocumentContent() throws IOException {

        // create filesystem and get a "virtual path"
        FileSystem virtualFileSystem = Jimfs.newFileSystem();
        Path virtualPath = virtualFileSystem.getPath("testWriteSVGToFile");
        OutputStream out = Files.newOutputStream(virtualPath);

        svg.writeSVGToOutputStream(svgDocument, out);

        assertTrue(Files.lines(virtualPath).count() > 1);
    }
}
