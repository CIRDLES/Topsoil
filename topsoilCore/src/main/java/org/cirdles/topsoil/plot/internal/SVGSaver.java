/*
 * Copyright 2014 CIRDLES.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static javax.xml.transform.OutputKeys.DOCTYPE_PUBLIC;
import static javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM;
import static javax.xml.transform.OutputKeys.INDENT;

/**
 * {@code SVGSaver} saves the vector image displayed in Topsoil's JavaScript plots with the .svg file extension
 *
 * @author John Zeringue
 */
public class SVGSaver {

    //***********************
    // Attributes
    //***********************

    private static final Logger LOGGER
            = LoggerFactory.getLogger(SVGSaver.class);
    private static final String SVG_DOCTYPE_PUBLIC = "-//W3C//DTD SVG 1.1//EN";

    private static final String SVG_DOCTYPE_SYSTEM
            = "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd";

    //***********************
    // Methods
    //***********************

    /**
     * Saves the vector image in Topsoil with the .svg file extension, to the provided file.
     * <p>
     * Note: Use this method if the project is in Swing, because it doesn't require the use of the JavaFX FileChooser.
     *
     * @param svgDocument   the Document to save
     * @param file  the File destination
     */
    public void save(Document svgDocument, File file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            writeSVGToOutputStream(svgDocument, out);
        } catch (IOException e) {
            LOGGER.error(null, e);
            e.printStackTrace();
        }
    }

    /**
     * Writes the SVG file to the given path.
     *
     * @param svgDocument Vector image to be saved
     * @param outputStream Given Path
     * @throws  IOException if transforming fails
     */
    void writeSVGToOutputStream(Document svgDocument, OutputStream outputStream)
            throws IOException {
        try {
            Transformer transformer
                    = TransformerFactory.newInstance().newTransformer();

            // configure the resulting SVG document
            transformer.setOutputProperty(DOCTYPE_PUBLIC, SVG_DOCTYPE_PUBLIC);
            transformer.setOutputProperty(DOCTYPE_SYSTEM, SVG_DOCTYPE_SYSTEM);
            transformer.setOutputProperty(INDENT, "yes");

            transformer.transform(
                    new DOMSource(svgDocument), new StreamResult(outputStream));
        } catch (TransformerException ex) {
            LOGGER.error(null, ex);
        } finally {
            outputStream.close();
        }
    }
}
