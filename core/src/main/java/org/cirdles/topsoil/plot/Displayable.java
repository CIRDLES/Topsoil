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

import javafx.scene.Node;
import org.w3c.dom.Document;

import javax.swing.JComponent;
import java.io.File;

/**
 * An interface for objects that are displayable in a couple of common formats.
 *
 * @author John Zeringue
 */
public interface Displayable {

    /**
     * Returns a {@link JComponent} representing this {@code Displayable}. This
     * method must not be called before the AWT/Swing runtime has been
     * initialized.
     *
     * @return a {@code JComponent} that may or may not be unique
     */
    JComponent displayAsJComponent();

    /**
     * Returns a {@link Node} representing this {@code Displayable}. This method
     * must not be called before the FX runtime has been initialized.
     *
     * @return a {@code Node} that may or may not be unique
     * @see javafx.application.Platform
     */
    Node displayAsNode();

    /**
     * Returns a {@code Document} representing this {@code Displayable}.
     *
     * @return a {@code Document} that may or may not be unique
     */
    default Document displayAsSVGDocument() {
        throw new UnsupportedOperationException();
    }

    /**
     * Saves a {@code Document} representing this {@code Displayable} to the specified {@code File}.
     *
     * @param file  the File destination
     */
    default void saveAsSVGDocument(File file) {
        throw new UnsupportedOperationException();
    }

}
