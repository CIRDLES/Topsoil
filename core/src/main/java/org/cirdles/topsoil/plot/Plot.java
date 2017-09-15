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
package org.cirdles.topsoil.plot;

import java.util.List;
import java.util.Map;
import javafx.scene.web.WebEngine;

/**
 * A generalized plot that can express itself as a {@link javafx.scene.Node}.
 *
 * @author John Zeringue
 */
public interface Plot extends Displayable {

    /**
     * Gets the data for the {@code Plot}.
     *
     * @return  a List of Maps of String field names to Object data values
     */
    List<Map<String, Object>> getData();

    /**
     * Sets the data for the {@code Plot}.
     *
     * @param data  a List of Maps of String field names to Object data values
     */
    void setData(List<Map<String, Object>> data);

    /**
     * Gets the properties for the {@code Plot}.
     *
     * @return  a Map of String keys to Object values
     */
    Map<String, Object> getProperties();

    /**
     * Sets the properties for the {@code Plot}.
     *
     * @param properties    a Map of String keys to Object values
     */
    void setProperties(Map<String, Object> properties);

    /**
     * Sets a single property for the {@code Plot}.
     *
     * @param key   String property key
     * @param value Object property value
     */
    void setProperty(String key, Object value);

    /**
     * Re-centers the plot to its default view.
     */
    void recenter();

    /**
     * Attempts to stop the {@code Plot}'s {@link WebEngine} running JavaScript content.
     */
    void stop();

}
