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
package org.cirdles.javafx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.cirdles.topsoil.app.builder.TopsoilBuilderFactory;

/**
 * A class that's responsible for initializing a custom JavaFX component by
 * loading the FXML file that corresponds to the component's class name with the
 * component set as both the root and the controller of the FXML layout.
 *
 * @author John Zeringue
 */
public class CustomComponentInitializer {
    
    private static final ResourceBundle resources = ResourceBundle.getBundle("Resources");

    /**
     * Initializes the node by setting it as the root and controller of the FXML
     * file <code>[classname].FXML</code> and then loading that FXML file.
     * <p>
     * This is roughly the same as
     *
     * <pre><code>String fxmlFilename = node.getClass().getSimpleName() + ".fxml";
     *URL fxmlLocation = node.getClass().getResource(fxmlFilename);
     *
     *FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
     *fxmlLoader.setRoot(node);
     *fxmlLoader.setController(node);
     *
     *fxmlLoader.load();</code></pre>
     *
     * @param node a node that's type corresponds to that of the root element of
     * the corresponding FXML file.
     */
    public void initialize(Node node) {
        // prepare the FXML URL
        String fxmlFilename = node.getClass().getSimpleName() + ".fxml";
        URL fxmlLocation = node.getClass().getResource(fxmlFilename);

        // prepare the FXMLLoader
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        fxmlLoader.setRoot(node);
        fxmlLoader.setController(node);
        fxmlLoader.setResources(resources);
        
        // this package should be nice and general, but this is necessary
        // for some components at the moment
        // this should be refactored away later
        // you should be able to build a generalized BuilderFactory using
        // reflection
        fxmlLoader.setBuilderFactory(new TopsoilBuilderFactory());

        // load the FXML
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
