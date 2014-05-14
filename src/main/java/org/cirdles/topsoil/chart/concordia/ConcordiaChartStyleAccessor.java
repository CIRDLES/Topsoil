/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cirdles.topsoil.chart.concordia;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import org.cirdles.topsoil.chart.StyleAccessor;

/**
 *
 * @author pfif
 */
public interface ConcordiaChartStyleAccessor extends StyleAccessor{
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
     * Return the <code>BooleanProperty</code> that say, according to this <code>StyleAccessor</code> if the axis should generate their own tick. 
     */
    public BooleanProperty axisAutoTickProperty();
}
