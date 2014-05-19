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

package org.cirdles.topsoil.chart.concordia;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import org.cirdles.topsoil.chart.StyleAccessor;

/**
 *
 * @author pfif
 */
public interface ErrorEllipseChartStyleAccessor extends StyleAccessor{
    public static Boolean concordiaLineShownDefault = true;
    
    public static Double axisXAnchorTickDefault = 0.;
    public static Double axisYAnchorTickDefault = 0.;
    
    public static Double axisXTickUnitDefault  = 0.5;
    public static Double axisYTickUnitDefault = 1.;
    
    public static Boolean axisAutoTickProperty = true;
    
    
    /**
     * Return the <code>BooleanProperty</code> that says if the concordia line styled according to this <code>StyleAccessor</code> should be shown.
     */
    public BooleanProperty concordiaLineShownProperty();
    
    /**
     * Return the <code>DoubleProperty</code> that give the Anchor Tick for the X axis according to this <code>StyleAccessor</code>.
     * Hey John, regarde, un peu de Français!
     * Hey Florent, don't think you're sneaky!
     */
    public DoubleProperty axisXAnchorTickProperty();
    
    /**
     * Return the <code>DoubleProperty</code> that the give Tick Unit for the X axis according to this <code>StyleAccessor</code>.
     */   
    public DoubleProperty axisXTickUnitProperty();
    
    /**
     * Return the <code>DoubleProperty</code> that give the Anchor Tick for the Y axis according to this <code>StyleAccessor</code>.
     */    
    public DoubleProperty axisYAnchorTickProperty();
    
    /**
     * Return the <code>DoubleProperty</code> that the give Tick Unit for the Y axis according to this <code>StyleAccessor</code>.
     */  
    public DoubleProperty axisYTickUnitProperty();
    
    /**
     * Return the <code>BooleanProperty</code> that say, according to this <code>StyleAccessor</code> if the axis X should generate their own tick. 
     */
    public BooleanProperty axisXAutoTickProperty();
    
        /**
     * Return the <code>BooleanProperty</code> that say, according to this <code>StyleAccessor</code> if the axis Y should generate their own tick. 
     */
    public BooleanProperty axisYAutoTickProperty();
}
