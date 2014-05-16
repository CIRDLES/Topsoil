/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cirdles.topsoil.chart.concordia;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import org.cirdles.jfxutils.NumberField;
import org.cirdles.topsoil.chart.NumberAxis;

/**
 *
 * @author pfif
 */
public class ConcordiaChartCustomizationPanel extends GridPane{
    public static final String NODE_TITLE = "Customization";
    public static final String ELLIPSES_NODESECTION_TITLE = "Ellipses";
    
    public static final String STROKE_LABEL = "Stroke";
    public static final String FILL_LABEL = "Fill";
    
    public static final String CHART_NODESECTION_TITLE = "Chart";
    public static final String CONCORDIALINE_OPACITY_LABEL = "Concordia Line";
    
    public static final String TICKER_NODESUBSECTION_TITLE = "Axis";
    public static final String AXISX_LABEL = "Axis X";
    public static final String AXISY_LABEL = "Axis Y";
    public static final String AUTOTICK_LABEL = "Auto Tick";
    
    public ConcordiaChartCustomizationPanel(ConcordiaChart chart) {
        
        //Creaton of the label
        Label title = new Label(NODE_TITLE);
        Label ellipse_title = new Label(ELLIPSES_NODESECTION_TITLE);
        Label stroke_label = new Label(STROKE_LABEL);
        Label fill_label = new Label(FILL_LABEL);
        Label chart_title = new Label(CHART_NODESECTION_TITLE);
        Label concordialine_label = new Label(CONCORDIALINE_OPACITY_LABEL);
        Label ticker_title = new Label(TICKER_NODESUBSECTION_TITLE);
        Label axisx_label = new Label(AXISX_LABEL);
        Label axisy_label = new Label(AXISY_LABEL);
        Label autotick_label = new Label(AUTOTICK_LABEL);
        
        ErrorEllipseStyleContainer eeStyleAccessor = chart.getErrorEllipseStyleAccessor();
        ConcordiaChartStyleAccessor ccStyleAccessor = chart.getConcordiaChartStyleAccessor();
        
        //Color Picker for filling and stroking the errorellipses
        ColorPicker colorPickerStroke = new ColorPicker();
        colorPickerStroke.valueProperty().bindBidirectional(eeStyleAccessor.ellipseOutlineColorProperty());
        
        ColorPicker colorPickerFill = new ColorPicker();
        colorPickerFill.valueProperty().bindBidirectional(eeStyleAccessor.ellipseFillColorProperty());
        
        CheckBox showOutlineCheckBox = new CheckBox();
        showOutlineCheckBox.selectedProperty().bindBidirectional(eeStyleAccessor.ellipseOutlineShownProperty());
        
        Slider slider_opacity = new Slider(0,1,0.5);
        slider_opacity.valueProperty().bindBidirectional(eeStyleAccessor.ellipseFillOpacityProperty());
        
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        
        ObservableValue<Number> xRange = xAxis.upperBoundProperty().subtract(xAxis.lowerBoundProperty());
        ObservableValue<Number> yRange = yAxis.upperBoundProperty().subtract(yAxis.lowerBoundProperty());
        
        NumberField tickXnf = new NumberField(ccStyleAccessor.axisXAnchorTickProperty(), xRange);
        NumberField tickYnf = new NumberField(ccStyleAccessor.axisYAnchorTickProperty(), yRange);
        
        NumberField tickUnitXnf = new NumberField(ccStyleAccessor.axisXTickUnitProperty(), xRange);
        NumberField tickUnitYnf = new NumberField(ccStyleAccessor.axisYTickUnitProperty(), yRange);
        
        CheckBox autoTickCheckBox = new CheckBox();
        autoTickCheckBox.selectedProperty().bindBidirectional(ccStyleAccessor.axisAutoTickProperty());
        

        //Title window (0)
        add(title,0,0);
        
        //Ellipse section title (1)
        add(ellipse_title, 0, 1);
        
        //Stroke (2)
        add(stroke_label,0,2);
        add(colorPickerStroke,1,2);
        add(showOutlineCheckBox, 2,2);
        
        //Fill (3)
        add(fill_label, 0,3);
        add(colorPickerFill, 1,3);
        add(slider_opacity,2,3);
        
        //Chart title (4)
        add(chart_title, 0,4);
        
        //Concordia Line (5)
        add(concordialine_label,0,5);
        add(new Label("Not Implemented yet"),1,5);
        
        //Axis title (6)
        add(ticker_title,0,6);
        
        //Axis X (7)
        add(axisx_label, 0, 7);
        add(tickXnf,1,7);
        add(tickUnitXnf,2,7);
        
        //Axis X (8)
        add(axisy_label, 0, 8);
        add(tickYnf,1,8);
        add(tickUnitYnf,2,8);
        
        //Auto Axis Tick (9)
        add(autotick_label,0,9);
        add(autoTickCheckBox,1,9);
    }
    
}
