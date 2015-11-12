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
package org.cirdles.topsoil.chart;

import org.cirdles.topsoil.dataset.Dataset;

import java.util.List;
import java.util.Optional;

/**
 * A generalized chart that can express itself as a {@link javafx.scene.Node}.
 *
 * @author John Zeringue
 */
public interface Chart extends Displayable {

    /**
     * Returns an {@link Optional} that contains this {@link Chart}'s if it has
     * been set and is empty otherwise.
     *
     * @return an {@link Optional} containing data of type <code>T</code>
     */
    Optional<Dataset> getDataset();

    Optional<VariableContext> getVariableContext();

    /**
     * Sets this {@link Chart}'s data to the object specified.
     *
     * @param variableContext
     */
    void setData(VariableContext variableContext);

    List<Variable> getVariables();

    Object getProperty(String key);

    void setProperty(String key, Object value);

    Displayable getPropertiesPanel();

}
