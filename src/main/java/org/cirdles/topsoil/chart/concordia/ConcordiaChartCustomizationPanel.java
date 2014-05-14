/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cirdles.topsoil.chart.concordia;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import org.cirdles.jfxutils.NumberField;

/**
 *
 * @author pfif
 */
public class ConcordiaChartCustomizationPanel extends VBox{
   

    public ConcordiaChartCustomizationPanel(ErrorEllipseStyleContainer eeStyleAccessor, ConcordiaChartStyleAccessor ccStyleAccessor) {
             
        //Color Picker for filling and stroking the errorellipses
        ColorPicker colorPickerStroke = new ColorPicker();
        colorPickerStroke.valueProperty().bindBidirectional(eeStyleAccessor.ellipseOutlineColorProperty());
        
        ColorPicker colorPickerFill = new ColorPicker();
        colorPickerFill.valueProperty().bindBidirectional(eeStyleAccessor.ellipseFillColorProperty());
        
        CheckBox showOutlineCheckBox = new CheckBox();
        showOutlineCheckBox.selectedProperty().bindBidirectional(eeStyleAccessor.ellipseOutlineShownProperty());
        
        Slider slider_opacity = new Slider(0,1,0.5);
        slider_opacity.valueProperty().bindBidirectional(eeStyleAccessor.ellipseFillOpacityProperty());
        
        NumberField tickXnf = new NumberField(ccStyleAccessor.axisXAnchorTickProperty());
        NumberField tickYnf = new NumberField(ccStyleAccessor.axisYAnchorTickProperty());
        
        NumberField tickUnitXnf = new NumberField(ccStyleAccessor.axisXTickUnitProperty());
        NumberField tickUnitYnf = new NumberField(ccStyleAccessor.axisYTickUnitProperty());
        
        CheckBox autoTickCheckBox = new CheckBox();
        autoTickCheckBox.selectedProperty().bindBidirectional(ccStyleAccessor.axisAutoTickProperty());
        

        getChildren().add(colorPickerStroke);
        getChildren().add(colorPickerFill);
        getChildren().add(showOutlineCheckBox);
        getChildren().add(tickXnf);
        getChildren().add(tickUnitXnf);
        getChildren().add(tickYnf);
        getChildren().add(tickUnitYnf);
        getChildren().add(autoTickCheckBox);
        getChildren().add(slider_opacity);
    }
    
}
