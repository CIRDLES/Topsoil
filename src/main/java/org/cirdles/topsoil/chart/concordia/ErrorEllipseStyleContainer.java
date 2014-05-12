/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cirdles.topsoil.chart.concordia;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.scene.paint.Color;

/**
 *  
 *  @author pfif
 */
public interface ErrorEllipseStyleContainer {
    /**
     * Return the <code>Property</code> that hold the <code>Color</code> of the stroke of the Ellipses styled according to this <code>ErrorEllipseStyleContainer</code>
     * @see Color 
     */
    public ObjectProperty<Color> ellipseOutlineColorProperty();
    
    /**
     * Return the <code>Property</code> that hold the <code>Color</code> that fill of the Ellipses styled according to this <code>ErrorEllipseStyleContainer</code>
     * @see Color 
     */
    public ObjectProperty<Color> ellipseFillColorProperty();
    
    /**
     * Return the <code>DoubleProperty</code> that hold the opacity of the filling color Ellipses styled according to this <code>ErrorEllipseStyleContainer</code>
     * @see Color 
     */
    public DoubleProperty ellipseFillOpacityProperty();
    
    /**
     * Return the <code>BooleanProperty</code> that says if the stroke of the Ellipses styled according to this <code>ErrorEllipseStyleContainer</code> should be shown.
     */
    public BooleanProperty ellipseOutlineShownProperty();
}
