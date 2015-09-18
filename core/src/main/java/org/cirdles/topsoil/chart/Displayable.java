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
package org.cirdles.topsoil.chart;

import javafx.scene.Node;
import javax.swing.JComponent;
import org.w3c.dom.Document;

/**
 * An interface for objects that are displayable in a couple of common formats.
 *
 * @author John Zeringue
 */
public interface Displayable {

    /**
     * Returns a {@link JComponent} representing this {@code Displayable}.
     *
     * @return a {@code JComponent} that may or may not be unique
     */
    public JComponent displayAsJComponent();

    /**
     * Returns a {@link Node} representing this {@code Displayable}. This method
     * should only be called from the JavaFX Application Thread.
     *
     * @return a {@code Node} that may or may not be unique
     * @see javafx.application.Platform
     */
    public Node displayAsNode();

    /**
     * Returns a {@link Document} representing this {@code Displayable}.
     *
     * @return a {@code Document} that may or may not be unique
     */
    public Document displayAsSVGDocument();

}
