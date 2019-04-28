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
import org.cirdles.topsoil.plot.PlotProperties.Property;

/**
 * A generalized plot that can express itself as a {@link javafx.scene.Node}.
 *
 * @author John Zeringue
 */
public interface Plot extends Displayable {

    /**
     * Returns the type of the plot.
     *
     * @return  PlotType
     */
    PlotType getPlotType();

    /**
     * Returns the data of the plot as a list of {@code PlotDataEntry} objects.
     *
     * @return  list of PlotDataEntries
     */
    List<PlotDataEntry> getData();

    void setData(List<PlotDataEntry> data);

    /**
     * Gets the properties for the {@code Plot}.
     *
     * @return  PlotProperties object
     */
    PlotProperties getProperties();

    /**
     * Sets the properties for the {@code Plot}.
     *
     * @param properties    PlotProperties object
     */
    void setProperties(PlotProperties properties);

    /**
     * Sets a single property for the {@code Plot}.
     *
     * @param property   Property
     * @param value      Object property value
     */
    void setProperty(Property<?> property, Object value);

    /**
     *
     * Syncs Java and Javascript's properties
     */
    void updateProperties();

    /**
     *
     * @return  a boolean when properties have been updated in Javascript but not Java
     */
    boolean getIfUpdated();

    /**
     *
     * @param update    a flag that marks if properties have been updated in Javascript but not Java
     */
    void setIfUpdated(boolean update);

    /**
     * Re-centers the plot to its default control.
     */
    void recenter();

    /**
     * Allows the user to set the X and Y axis extents.
     *
     * @param xMin  as Double
     * @param xMax  as Double
     * @param yMin  as Double
     * @param yMax  as Double
     */
    void setAxes(Double xMin, Double xMax, Double yMin, Double yMax);
    
    /**
     * Zooms plot so Concordia displays from corner to corner of plot.
     */
    void snapToCorners();


}
