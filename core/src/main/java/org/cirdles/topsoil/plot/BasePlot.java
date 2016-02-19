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
public abstract class BasePlot implements Plot {

    private List<Map<String, Object>> data;
    private Map<String, Object> properties;

    public BasePlot() {
        this(new HashMap<>());
    }

    public BasePlot(Map<String, Object> defaultProperties) {
        data = emptyList();
        properties = defaultProperties;
    }

    @Override
    public List<Map<String, Object>> getData() {
        return data;
    }

    @Override
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
