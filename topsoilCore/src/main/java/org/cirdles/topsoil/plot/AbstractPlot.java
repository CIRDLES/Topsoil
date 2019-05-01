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

    protected List<PlotDataEntry> data;
    protected PlotType plotType;
    protected PlotProperties properties;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new {@code AbstractPlot} with the specified properties.
     *
     * @param plotType   PlotType
     * @param properties PlotProperties
     */
    AbstractPlot(PlotType plotType, PlotProperties properties) {
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
    public List<PlotDataEntry> getData() {
        return data;
    }

    /**{@inheritDoc}*/
    @Override
    public void setData(List<PlotDataEntry> data) {
        this.data = data;
    }

    /**{@inheritDoc}*/
    @Override
    public PlotProperties getProperties() {
        return properties;
    }

    /**{@inheritDoc}*/
    @Override
    public void setProperties(PlotProperties properties) {
        this.properties.setAll(properties);
    }

    /**{@inheritDoc}*/
    @Override
    public void setProperty(PlotProperties.Property<?> property, Object value) {
        this.properties.set(property, value);
    }

}
