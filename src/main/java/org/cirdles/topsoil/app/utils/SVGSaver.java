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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author John Zeringue
 */
public class SVGSaver {

    public static final String SVG_DOCTYPE_PUBLIC = "-//W3C//DTD SVG 1.1//EN";
    private static final String SVG_DOCTYPE_SYSTEM = "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd";

    public void save(Document svgDocument) {
        // get the path for the new SVG file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to SVG");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG Image", "*.svg"));
        File svgFile = fileChooser.showSaveDialog(null);

        // can't convert if the file isn't specified
        if (svgFile == null) {
            return;
        }

        // write the SVG to the given path
        try {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();

            // configure the resulting SVG document
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, SVG_DOCTYPE_PUBLIC);
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, SVG_DOCTYPE_SYSTEM);
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            transformer.transform(new DOMSource(svgDocument), new StreamResult(svgFile));
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(SVGSaver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(SVGSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
