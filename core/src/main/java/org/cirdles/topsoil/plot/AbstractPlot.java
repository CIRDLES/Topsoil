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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * A partial implementation of {@link Plot} that stores and retrieves set data.
 *
 * @author John Zeringue
 */
public abstract class AbstractPlot implements Plot {

    private List<Map<String, Object>> data;
    private Map<String, Object> properties;

    /**
     * Constructs a new {@code AbstractPlot}. No properties are set by default.
     */
    public AbstractPlot() {
        this(new HashMap<>());
    }

    /**
     * Constructs a new {@code AbstractPlot} with the specified properties.
     *
     * @param defaultProperties a Map of String keys to Object values
     */
    public AbstractPlot(Map<String, Object> defaultProperties) {
        data = emptyList();
        properties = defaultProperties;
    }

    /**{@inheritDoc}*/
    @Override
    public List<Map<String, Object>> getData() {
        return data;
    }

    /**{@inheritDoc}*/
    @Override
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    /**{@inheritDoc}*/
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**{@inheritDoc}*/
    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Attempts to destroy the {@code Plot} object to avoid concurrency problems in the {@code WebEngine}.
     *
     * @throws Throwable    literally any problem
     */
    public void killPlot() throws Throwable {
        super.finalize();
    }
}
