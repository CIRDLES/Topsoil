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

/**
 * A generalized plot that can express itself as a {@link javafx.scene.Node}.
 *
 * @author John Zeringue
 */
public interface Plot extends Displayable {

    PlotType getPlotType();

    List<Map<String, Object>> getData();

    void setData(List<Map<String, Object>> data);

    /**
     * Gets the properties for the {@code Plot}.
     *
     * @return  a Map of PlotProperty keys to Object values
     */
    Map<PlotProperty, Object> getProperties();

    /**
     * Sets the properties for the {@code Plot}.
     *
     * @param properties    a Map of PlotProperty keys to Object values
     */
    void setProperties(Map<PlotProperty, Object> properties);

    /**
     * Sets a single property for the {@code Plot}.
     *
     * @param key   PlotProperty key
     * @param value Object property value
     */
    void setProperty(PlotProperty key, Object value);

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
     * @param xMin as String
     * @param xMax as String
     * @param yMin as String
     * @param yMax as String
     */
    void setAxes(String xMin, String xMax, String yMin, String yMax);
    
    /**
     * Zooms plot so Concordia displays from corner to corner of plot.
     */
    void snapToCorners();


}
