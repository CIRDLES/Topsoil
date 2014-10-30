/*
 * Copyright 2014 pfif.
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
package org.cirdles.topsoil.chart.concordia.panels;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Font;
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.Tools;
import org.cirdles.topsoil.chart.concordia.ConcordiaLineType;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;

public class ChartCustomizationPanel extends CustomVBox<ChartCustomizationPanel> implements Initializable {

    @FXML private ToggleGroup concordiaLineToggleGroup;
    @FXML private ChoiceBox<String> fontChoiceBox;

    @FXML private AxisConfigurationPanel axisXConfigPanel;
    @FXML private AxisConfigurationPanel axisYConfigPanel;

    @FXML private Slider fontSizeAxisLabelSlider;
    @FXML private Label fontSizeAxisLabelValue;

    @FXML private Slider fontSizeTickLabelSlider;
    @FXML private Label fontSizeTickLabelValue;

    private ErrorEllipseChart chart;

    public ChartCustomizationPanel(ErrorEllipseChart chart) {
        super(self -> self.chart = chart);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        concordiaLineToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.getUserData().toString()) {
                case "Wetherill":
                    chart.setConcordiaLineType(ConcordiaLineType.WETHERILL);
                    break;
                case "Tera-Wasserburg":
                    chart.setConcordiaLineType(ConcordiaLineType.TERA_WASSERBURG);
                    break;
                case "None":
                    chart.setConcordiaLineType(ConcordiaLineType.NONE);
                    break;
            }
        });

        fontChoiceBox.getItems().addAll(Font.getFamilies());
        fontChoiceBox.getSelectionModel().select("System");
        chart.concordiaLineFontFamilyProperty().bind(fontChoiceBox.valueProperty());
        chart.getXAxis().fontFamilyProperty().bind(fontChoiceBox.valueProperty());
        chart.getYAxis().fontFamilyProperty().bind(fontChoiceBox.valueProperty());

        initSlider(fontSizeTickLabelSlider, fontSizeTickLabelValue, 10);
        chart.concordiaLineFontSizeProperty().bind(fontSizeTickLabelSlider.valueProperty());
        chart.getXAxis().fontSizeTickLabelProperty().bind(fontSizeTickLabelSlider.valueProperty());
        chart.getYAxis().fontSizeTickLabelProperty().bind(fontSizeTickLabelSlider.valueProperty());

        initSlider(fontSizeAxisLabelSlider, fontSizeAxisLabelValue, 12);
        chart.getXAxis().fontSizeAxisLabelProperty().bind(fontSizeAxisLabelSlider.valueProperty());
        chart.getYAxis().fontSizeAxisLabelProperty().bind(fontSizeAxisLabelSlider.valueProperty());

        axisXConfigPanel.axisProperty().set(chart.getXAxis());
        axisXConfigPanel.titleProperty().set(resources.getString("axisxLabel"));

        axisYConfigPanel.axisProperty().set(chart.getYAxis());
        axisYConfigPanel.titleProperty().set(resources.getString("axisyLabel"));
    }

    private void initSlider(Slider s, Label l, int initvalue) {
        s.setMin(8);
        s.setMax(20);
        s.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            l.setText(Tools.DYNAMIC_NUMBER_CONVERTER_TO_INTEGER.toString(newValue));
        });

        s.majorTickUnitProperty().set(1);
        s.minorTickCountProperty().set(0);
        s.setSnapToTicks(true);

        s.setValue(initvalue);
    }
}
