/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cirdles.topsoil.chart.concordia;

import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.jfxutils.NumberField;
import org.cirdles.topsoil.chart.NumberAxis;

/**
 *
 * @author pfif
 */
public class ConcordiaChartCustomizationPanel extends VBox {

    public static final String NODE_TITLE = "Customization";
    
    

    
    public ConcordiaChartCustomizationPanel(ConcordiaChart chart) {
        ColumnConstraints labelConstraints = new ColumnConstraints();
        labelConstraints.setMinWidth(100);
        //getColumnConstraints().add(labelConstraints);
        
        ErrorEllipseStyleContainer eeStyleAccessor = chart.getErrorEllipseStyleAccessor();

        //Creaton of the label
        Label title = new Label(NODE_TITLE);
        ErrorEllipsesCustomisationPanel eeCustomizationPane = new ErrorEllipsesCustomisationPanel(eeStyleAccessor);
        ChartCustomizationPanel ccCustomizationPane = new ChartCustomizationPanel(chart);
        
        getChildren().add(title);
        getChildren().add(eeCustomizationPane);
        getChildren().add(ccCustomizationPane);
    }
    
    private static class ErrorEllipsesCustomisationPanel extends VBox{
        public static final String ELLIPSES_NODESECTION_TITLE = "Ellipses";
        public static final String STROKE_LABEL = "Stroke";
        public static final String FILL_LABEL = "Fill";
        

        public ErrorEllipsesCustomisationPanel(ErrorEllipseStyleContainer eeStyleAccessor) {
            Label node_title = new Label(ELLIPSES_NODESECTION_TITLE);
            Label stroke_label = new Label(STROKE_LABEL);
            Label fill_label = new Label(FILL_LABEL);
            
            //Color Picker for filling and stroking the errorellipses
            ColorPicker colorPickerStroke = new ColorPicker();
            colorPickerStroke.valueProperty().bindBidirectional(eeStyleAccessor.ellipseOutlineColorProperty());
        
            ColorPicker colorPickerFill = new ColorPicker();
            colorPickerFill.valueProperty().bindBidirectional(eeStyleAccessor.ellipseFillColorProperty());
        
            CheckBox showOutlineCheckBox = new CheckBox();
            showOutlineCheckBox.selectedProperty().bindBidirectional(eeStyleAccessor.ellipseOutlineShownProperty());
        
            Slider slider_opacity = new Slider(0, 1, 0.5);
            slider_opacity.valueProperty().bindBidirectional(eeStyleAccessor.ellipseFillOpacityProperty());
            
            GridPane ellipsesCustomization = new GridPane();

            //Stroke (0)
            ellipsesCustomization.add(stroke_label, 0, 0);
            ellipsesCustomization.add(colorPickerStroke, 1, 0);
            ellipsesCustomization.add(showOutlineCheckBox, 2, 0);

            //Fill (1)
            ellipsesCustomization.add(fill_label, 0, 1);
            ellipsesCustomization.add(colorPickerFill, 1, 1);
            ellipsesCustomization.add(slider_opacity, 2, 1);
            
            getChildren().add(node_title);
            getChildren().add(ellipsesCustomization);
        }
        
    }
    
    private static class ChartCustomizationPanel extends VBox{
        public static final String CHART_NODESECTION_TITLE = "Chart";
        public static final String CONCORDIALINE_OPACITY_LABEL = "Concordia Line";
    
        public static final String TICKER_NODESUBSECTION_TITLE = "Axis";
        public static final String AXISX_LABEL = "Axis X";
        public static final String AXISY_LABEL = "Axis Y";
        public static final String AUTOTICK_LABEL = "Auto Tick";

        public ChartCustomizationPanel(ConcordiaChart chart) {
            ConcordiaChartStyleAccessor ccStyleAccessor = chart.getConcordiaChartStyleAccessor();
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();
            
            Label node_title = new Label(CHART_NODESECTION_TITLE);
            Label concordialine_label = new Label(CONCORDIALINE_OPACITY_LABEL);
            Label ticker_title = new Label(TICKER_NODESUBSECTION_TITLE);
            Label axisx_label = new Label(AXISX_LABEL);
            Label axisy_label = new Label(AXISY_LABEL);
            


            CheckBox checkbox_concordia = new CheckBox();
            ccStyleAccessor.concordiaLineShownProperty().bind(checkbox_concordia.selectedProperty());

            ObservableValue<Number> xRange = xAxis.upperBoundProperty().subtract(xAxis.lowerBoundProperty());
            ObservableValue<Number> yRange = yAxis.upperBoundProperty().subtract(yAxis.lowerBoundProperty());

            NumberField tickXnf = new NumberField(ccStyleAccessor.axisXAnchorTickProperty(), xRange);
            NumberField tickYnf = new NumberField(ccStyleAccessor.axisYAnchorTickProperty(), yRange);

            NumberField tickUnitXnf = new NumberField(ccStyleAccessor.axisXTickUnitProperty(), xRange);
            NumberField tickUnitYnf = new NumberField(ccStyleAccessor.axisYTickUnitProperty(), yRange);

            CheckBox autoTickXCheckBox = new CheckBox();
            autoTickXCheckBox.selectedProperty().bindBidirectional(ccStyleAccessor.axisXAutoTickProperty());

            CheckBox autoTickYCheckBox = new CheckBox();
            autoTickYCheckBox.selectedProperty().bindBidirectional(ccStyleAccessor.axisYAutoTickProperty());
            
            HBox concordialine_box = new HBox();
            concordialine_box.getChildren().add(concordialine_label);
            concordialine_box.getChildren().add(checkbox_concordia);
            
            GridPane axisPane = new GridPane();
            //Axis X (0)
            axisPane.add(axisx_label, 0, 0);
            axisPane.add(tickXnf, 1, 0);
            axisPane.add(tickUnitXnf, 2, 0);
            axisPane.add(autoTickXCheckBox, 3, 0);

            //Axis Y (1)
            axisPane.add(axisy_label, 0, 1);
            axisPane.add(tickYnf, 1, 1);
            axisPane.add(tickUnitYnf, 2, 1);
            axisPane.add(autoTickYCheckBox, 3, 1);
            
            getChildren().add(node_title);
            getChildren().add(concordialine_box);
            getChildren().add(ticker_title);
            getChildren().add(axisPane);
            
                       
        }
    } 
    
}
