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
package org.cirdles.topsoil.app.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import static javax.xml.transform.OutputKeys.DOCTYPE_PUBLIC;
import static javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM;
import static javax.xml.transform.OutputKeys.INDENT;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 * The purpose of SVGSaver.java is to save a vector image displayed in Topsoil's
 * js-charts with the .svg file extension
 *
 * @author John Zeringue
 */
public class SVGSaver {

    private static final Logger LOGGER
            = Logger.getLogger(SVGSaver.class.getName());

    private static final String SVG_DOCTYPE_PUBLIC = "-//W3C//DTD SVG 1.1//EN";

    private static final String SVG_DOCTYPE_SYSTEM
            = "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd";

    private static final File FILE_CHOOSER_INITIAL_DIRECTORY
            = new File(System.getProperty("user.home"));

    /**
     * Saves the vector image in Topsoil with the .svg file extension
     *
     * @param svgDocument Document to be saved
     */
    public void save(Document svgDocument) {
        // If there is no file specified, then there is nothing to do.
        // Otherwise, write the file with the SVG format.
        generateSaveUI().ifPresent(stream -> {
            try {
                writeSVGToOutputStream(svgDocument, stream);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        });
    }

    private FileChooser getFileChooser() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Export to SVG");
        fileChooser.setInitialDirectory(FILE_CHOOSER_INITIAL_DIRECTORY);

        fileChooser.getExtensionFilters().setAll(
                new ExtensionFilter("All Files", "*"),
                new ExtensionFilter("SVG Image", "*.svg")
        );

        return fileChooser;
    }

    /**
     * Create a save dialog window and prompt user for file path
     *
     * @return Optional<File> the file path
     */
    private Optional<OutputStream> generateSaveUI() {
        return Optional.of(getFileChooser().showSaveDialog(null))
                .map(file -> {
                    try {
                        return new FileOutputStream(file);
                    } catch (FileNotFoundException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                        return null;
                    }
                });
    }

    /**
     * Writes the SVG file to the given path
     *
     * @param svgDocument Vector image to be saved
     * @param outputStream Given Path
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
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            outputStream.close();
        }
    }
}
