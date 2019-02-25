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
 * A partial implementation of {@link Plot} that stores and retrieves set model.
 *
 * @author John Zeringue
 */
public abstract class AbstractPlot implements Plot {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private List<Map<String, Object>> data;
    private PlotType plotType;
    private Map<PlotProperty, Object> properties;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new {@code AbstractPlot}. No properties are set by default.
     *
     * @param plotType  PloType
     */
    public AbstractPlot(PlotType plotType) {
        this(plotType, new HashMap<>());
    }

    /**
     * Constructs a new {@code AbstractPlot} with the specified properties.
     *
     * @param plotType   PlotType
     * @param properties a Map of PlotProperty keys to Object values
     */
    public AbstractPlot(PlotType plotType, Map<PlotProperty, Object> properties) {
        this.plotType = plotType;
        data = emptyList();
        this.properties = properties;
    }

    /**{@inheritDoc}*/
    @Override
    public PlotType getPlotType() {
        return plotType;
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
    public Map<PlotProperty, Object> getProperties() {
        return properties;
    }

    /**{@inheritDoc}*/
    @Override
    public void setProperties(Map<PlotProperty, Object> properties) {
        this.properties = properties;
    }

    /**{@inheritDoc}*/
    @Override
    public void setProperty(PlotProperty key, Object value) {
        this.properties.put(key, value);
    }

//    /**
//     * Attempts to destroy the {@code Plot} object to avoid concurrency problems in the {@code WebEngine}.
//     *
//     * @throws Throwable    literally any problem
//     */
//    public void killPlot() throws Throwable {
//        super.finalize();
//    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

}
