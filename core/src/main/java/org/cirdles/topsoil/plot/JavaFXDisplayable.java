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
package org.cirdles.topsoil.plot;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import java.util.concurrent.ExecutionException;

/**
 * An interface for objects that are displayable in a couple of common formats, with a default method specified for
 * displaying as a {@link JComponent}.
 *
 * @author John Zeringue
 */
public interface JavaFXDisplayable extends Displayable {

    /**{@inheritDoc}*/
    @Override
    default JComponent displayAsJComponent() {
        JFXPanel jfxPanel = new JFXPanel();

        Task<Void> initializeJFXPanel = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                Node node = displayAsNode();
                Parent parent;

                // cast/wrap node as appropriate
                if (node instanceof Parent) {
                    parent = (Parent) node;
                } else {
                    parent = new VBox(node);
                }

                Scene scene = new Scene(parent);
                jfxPanel.setScene(scene);

                return null;
            }

        };

        Platform.runLater(initializeJFXPanel);

        // synchronize initializeJFXPanel
        try {
            initializeJFXPanel.get();
        } catch (InterruptedException | ExecutionException ex) {
            LoggerFactory.getLogger(JavaFXDisplayable.class).error(null, ex);
        }

        return jfxPanel;
    }

}
