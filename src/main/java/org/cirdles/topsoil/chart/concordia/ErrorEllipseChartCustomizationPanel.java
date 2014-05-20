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

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.jfxutils.NumberField;
import org.cirdles.topsoil.Tools;
import org.cirdles.topsoil.chart.NumberAxis;

/**
 *
 * @author pfif
 */
public class ErrorEllipseChartCustomizationPanel extends VBox {

    public static final String NODE_TITLE = "Customization";
    
    

    
    public ErrorEllipseChartCustomizationPanel(ErrorEllipseChart chart) {
        super(10);
        setPadding(new Insets(10));
        
        ColumnConstraints labelConstraints = new ColumnConstraints();
        labelConstraints.setMinWidth(100);
        //getColumnConstraints().add(labelConstraints);
        getStylesheets().add(ErrorEllipseChartCustomizationPanel.class.getResource("ConcordiaChart.css").toExternalForm());

        //Creaton of the label
        Label title = Tools.label_minsize(NODE_TITLE);
        title.getStyleClass().add("title-panel");
                
        ErrorEllipsesCustomisationPanel eeCustomizationPane = new ErrorEllipsesCustomisationPanel(chart);
        ChartCustomizationPanel ccCustomizationPane = new ChartCustomizationPanel(chart);
        
        getChildren().add(title);
        getChildren().add(eeCustomizationPane);
        getChildren().add(ccCustomizationPane);
    }
    
    private static class ErrorEllipsesCustomisationPanel extends VBox{
        public static final String ELLIPSES_NODESECTION_TITLE = "Ellipses";
        public static final String STROKE_LABEL = "Ellipses border";
        public static final String FILL_LABEL = "Fill";
        
        public static final String OPACITY_LABEL = "Opacity";
        public static final String SHOWN_LABEL = "Shown";

        public ErrorEllipsesCustomisationPanel(ErrorEllipseChart chart) {
            super(5);

            Label node_title = Tools.label_minsize(ELLIPSES_NODESECTION_TITLE);
            
            node_title.getStyleClass().add("title-subpanel");
            
            Label stroke_label = Tools.label_minsize(STROKE_LABEL);
            stroke_label.setMaxWidth(USE_PREF_SIZE);
            Label fill_label = Tools.label_minsize(FILL_LABEL);
            Label opacity_label = Tools.label_minsize(OPACITY_LABEL);
            Label shown_label = Tools.label_minsize(SHOWN_LABEL);
            
            //Color Picker for filling and stroking the errorellipses
            CheckBox showOutlineCheckBox = new CheckBox();
            showOutlineCheckBox.selectedProperty().bindBidirectional(chart.ellipseOutlineShownProperty());
            
            ColorPicker colorPickerStroke = new ColorPicker();
            colorPickerStroke.valueProperty().bindBidirectional(chart.ellipseOutlineColorProperty());
            colorPickerStroke.disableProperty().bind(Bindings.not(showOutlineCheckBox.selectedProperty()));
        
            ColorPicker colorPickerFill = new ColorPicker();
            colorPickerFill.valueProperty().bindBidirectional(chart.ellipseFillColorProperty());
        

        
            Slider slider_opacity = new Slider(0, 1, 0.5);
            slider_opacity.valueProperty().bindBidirectional(chart.ellipseFillOpacityProperty());
            
            GridPane ellipsesCustomization = new GridPane();
            ellipsesCustomization.setHgap(10);
            ellipsesCustomization.setVgap(10);
            
            ellipsesCustomization.setPadding(new Insets(0, 1, 0, 1));

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
        

        public ChartCustomizationPanel(ErrorEllipseChart chart) {
            super(5);

            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();
            
            Label node_title = Tools.label_minsize(CHART_NODESECTION_TITLE);
            node_title.getStyleClass().add("title-subpanel");
            
            Label concordialine_label = Tools.label_minsize(CONCORDIALINE_OPACITY_LABEL);
            Label ticker_title = Tools.label_minsize(TICKER_NODESUBSECTION_TITLE);
            ticker_title.getStyleClass().add("title-subsubpanel");
            Label axisx_label = Tools.label_minsize(AXISX_LABEL);
            Label axisy_label = Tools.label_minsize(AXISY_LABEL);
            Label autotick_label = Tools.label_minsize(AUTOTICK_LABEL);

            CheckBox checkbox_concordia = new CheckBox();
            chart.concordiaLineShownProperty().bind(checkbox_concordia.selectedProperty());

            ObservableValue<Number> xRange = xAxis.upperBoundProperty().subtract(xAxis.lowerBoundProperty());
            ObservableValue<Number> yRange = yAxis.upperBoundProperty().subtract(yAxis.lowerBoundProperty());
            
            CheckBox autoTickXCheckBox = new CheckBox();
            autoTickXCheckBox.selectedProperty().bindBidirectional(((NumberAxis) chart.getXAxis()).getTickGenerator().autoTickingProperty());
            
            CheckBox autoTickYCheckBox = new CheckBox();
            autoTickYCheckBox.selectedProperty().bindBidirectional(((NumberAxis) chart.getYAxis()).getTickGenerator().autoTickingProperty());

            NumberField tickXnf = new NumberField(((NumberAxis) chart.getXAxis()).getTickGenerator().anchorTickProperty(), xRange);
            tickXnf.visibleProperty().bind(Bindings.not(autoTickXCheckBox.selectedProperty()));
            NumberField tickYnf = new NumberField(((NumberAxis) chart.getYAxis()).getTickGenerator().anchorTickProperty(), yRange);
            tickYnf.visibleProperty().bind(Bindings.not(autoTickYCheckBox.selectedProperty()));

            NumberField tickUnitXnf = new NumberField(((NumberAxis) chart.getXAxis()).getTickGenerator().tickUnitProperty(), xRange);
            tickUnitXnf.visibleProperty().bind(Bindings.not(autoTickXCheckBox.selectedProperty()));
            NumberField tickUnitYnf = new NumberField(((NumberAxis) chart.getYAxis()).getTickGenerator().tickUnitProperty(), yRange);
            tickUnitYnf.visibleProperty().bind(Bindings.not(autoTickYCheckBox.selectedProperty()));
            
            Label tickunit_label = new Label(TICKUNIT_LABEL);
            tickunit_label.visibleProperty().bind(Bindings.and(autoTickXCheckBox.selectedProperty(), autoTickYCheckBox.selectedProperty()).not());
            Label anchortick_label = new Label(ANCHORTICK_LABEL);
            anchortick_label.visibleProperty().bind(Bindings.and(autoTickXCheckBox.selectedProperty(), autoTickYCheckBox.selectedProperty()).not());


            
            HBox concordialine_box = new HBox();
            concordialine_box.getChildren().add(concordialine_label);
            concordialine_box.getChildren().add(checkbox_concordia);
            
            GridPane axisPane = new GridPane();
            axisPane.setHgap(10);
            axisPane.setVgap(10);
            
            axisPane.add(anchortick_label, 3, 0);
            axisPane.add(tickunit_label,2,0);
            axisPane.add(autotick_label,1,0);

            //Axis X (1)
            axisPane.add(axisx_label, 0, 1);
            axisPane.add(tickXnf, 3, 1);
            axisPane.add(tickUnitXnf, 2, 1);
            axisPane.add(autoTickXCheckBox, 1, 1);

            //Axis Y (2)
            axisPane.add(axisy_label, 0, 2);
            axisPane.add(tickYnf, 3, 2);
            axisPane.add(tickUnitYnf, 2, 2);
            axisPane.add(autoTickYCheckBox, 1, 2);
            
            getChildren().add(node_title);
            getChildren().add(concordialine_box);
            getChildren().add(ticker_title);
            getChildren().add(axisPane);
            
                       
        }
    } 
    
}
