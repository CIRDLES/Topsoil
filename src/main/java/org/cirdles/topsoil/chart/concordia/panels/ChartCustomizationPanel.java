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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.cirdles.jfxutils.NumberField;
import org.cirdles.topsoil.chart.concordia.ConcordiaLineType;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;

public class ChartCustomizationPanel extends VBox implements Initializable {

    @FXML ToggleGroup concordiaLineToggleGroup;
    @FXML Label anchorTickLabel;
    @FXML Label tickUnitLabel;
    @FXML NumberField tickXnf;
    @FXML NumberField tickUnitXnf;
    @FXML CheckBox autoTickXCheckBox;
    @FXML NumberField tickYnf;
    @FXML NumberField tickUnitYnf;
    @FXML CheckBox autoTickYCheckBox;

    private final ErrorEllipseChart chart;

    public ChartCustomizationPanel(ErrorEllipseChart chart) {
        this.chart = chart;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chartcustomizationpanel.fxml"),
                                           ResourceBundle.getBundle("org.cirdles.topsoil.Resources"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            getChildren().add(new Label("There was an error loading this part of the panel."));
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        concordiaLineToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
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
            }
        });

        ObservableValue<Number> xRange
                = chart.getXAxis().upperBoundProperty().subtract(chart.getXAxis().lowerBoundProperty());
        ObservableValue<Number> yRange
                = chart.getYAxis().upperBoundProperty().subtract(chart.getYAxis().lowerBoundProperty());

        autoTickXCheckBox.selectedProperty().bindBidirectional((chart.getXAxis()).getTickGenerator().autoTickingProperty());
        autoTickYCheckBox.selectedProperty().bindBidirectional((chart.getYAxis()).getTickGenerator().autoTickingProperty());

        tickXnf.setTargetProperty((chart.getXAxis()).getTickGenerator().anchorTickProperty(), xRange);
        tickXnf.visibleProperty().bind(Bindings.not(autoTickXCheckBox.selectedProperty()));
        tickYnf.setTargetProperty((chart.getYAxis()).getTickGenerator().anchorTickProperty(), yRange);
        tickYnf.visibleProperty().bind(Bindings.not(autoTickYCheckBox.selectedProperty()));

        tickUnitXnf.setTargetProperty((chart.getXAxis()).getTickGenerator().tickUnitProperty(), xRange);
        tickUnitXnf.visibleProperty().bind(Bindings.not(autoTickXCheckBox.selectedProperty()));
        tickUnitYnf.setTargetProperty((chart.getYAxis()).getTickGenerator().tickUnitProperty(), yRange);
        tickUnitYnf.visibleProperty().bind(Bindings.not(autoTickYCheckBox.selectedProperty()));

        tickUnitLabel.visibleProperty().bind(Bindings.and(autoTickXCheckBox.selectedProperty(),
                                                          autoTickYCheckBox.selectedProperty()).not());

        anchorTickLabel.visibleProperty().bind(Bindings.and(autoTickXCheckBox.selectedProperty(),
                                                            autoTickYCheckBox.selectedProperty()).not());
    }
}
