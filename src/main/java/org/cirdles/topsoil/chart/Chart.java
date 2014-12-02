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

import java.util.Optional;
import javafx.scene.Node;
import org.cirdles.topsoil.chart.setting.SettingScope;

/**
 * A generalized chart that can express itself as a {@link Node}.
 * 
 * @author John Zeringue
 * @param <T> the type of data accepted by this chart
 */
public interface Chart<T> {

    /**
     * Returns this chart as a {@link Node}. This method should always return
     * the same object.
     * 
     * @return the {@link Node} representation of this chart
     */
    public Node asNode();

    /**
     * Returns an {@link Optional} that contains this {@link Chart}'s if it has
     * been set and is empty otherwise.
     * 
     * @return an {@link Optional} containing data of type <code>T</code>
     */
    public Optional<T> getData();

    /**
     * Sets this {@link Chart}'s data to the object specified.
     * 
     * @param data data of type <code>T</code>
     */
    public void setData(T data);
    
    public SettingScope getSettingScope();

}
