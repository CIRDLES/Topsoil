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
        
        public static final String OPACITY_LABEL = "Opacity";
        public static final String SHOWN_LABEL = "Shown";

        public ErrorEllipsesCustomisationPanel(ErrorEllipseStyleContainer eeStyleAccessor) {
            Label node_title = new Label(ELLIPSES_NODESECTION_TITLE);
            Label stroke_label = new Label(STROKE_LABEL);
            Label fill_label = new Label(FILL_LABEL);
            Label opacity_label = new Label(OPACITY_LABEL);
            Label shown_label = new Label(SHOWN_LABEL);
            
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
            ellipsesCustomization.add(shown_label, 2, 0);
            ellipsesCustomization.add(showOutlineCheckBox, 3, 0);

            //Fill (1)
            ellipsesCustomization.add(fill_label, 0, 1);
            ellipsesCustomization.add(colorPickerFill, 1, 1);
            ellipsesCustomization.add(opacity_label, 2, 1);
            ellipsesCustomization.add(slider_opacity, 3, 1);
            
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
        public static final String ANCHORTICK_LABEL = "Anchor tick";
        public static final String TICKUNIT_LABEL = "Tick Unit";
        

        public ChartCustomizationPanel(ConcordiaChart chart) {
            ConcordiaChartStyleAccessor ccStyleAccessor = chart.getConcordiaChartStyleAccessor();
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();
            
            Label node_title = new Label(CHART_NODESECTION_TITLE);
            Label concordialine_label = new Label(CONCORDIALINE_OPACITY_LABEL);
            Label ticker_title = new Label(TICKER_NODESUBSECTION_TITLE);
            Label axisx_label = new Label(AXISX_LABEL);
            Label axisy_label = new Label(AXISY_LABEL);
            Label anchortick_label = new Label(ANCHORTICK_LABEL);
            Label tickunit_label = new Label(TICKUNIT_LABEL);
            Label autotick_label = new Label(AUTOTICK_LABEL);
            


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
            axisPane.add(anchortick_label, 1, 0);
            axisPane.add(tickunit_label,2,0);
            axisPane.add(autotick_label,3,0);

            //Axis X (1)
            axisPane.add(axisx_label, 0, 1);
            axisPane.add(tickXnf, 1, 1);
            axisPane.add(tickUnitXnf, 2, 1);
            axisPane.add(autoTickXCheckBox, 3, 1);

            //Axis Y (2)
            axisPane.add(axisy_label, 0, 2);
            axisPane.add(tickYnf, 1, 2);
            axisPane.add(tickUnitYnf, 2, 2);
            axisPane.add(autoTickYCheckBox, 3, 2);
            
            getChildren().add(node_title);
            getChildren().add(concordialine_box);
            getChildren().add(ticker_title);
            getChildren().add(axisPane);
            
                       
        }
    } 
    
}
