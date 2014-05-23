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
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.cirdles.jfxutils.NumberField;
import org.cirdles.topsoil.chart.NumberAxis;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;

public class ChartCustomizationPanel extends VBox {

        @FXML
        CheckBox checkboxConcordia;
        @FXML
        Label anchorTickLabel;
        @FXML
        Label tickUnitLabel;
        @FXML
        NumberField tickXnf;
        @FXML
        NumberField tickUnitXnf;
        @FXML
        CheckBox autoTickXCheckBox;
        @FXML
        NumberField tickYnf;
        @FXML
        NumberField tickUnitYnf;
        @FXML
        CheckBox autoTickYCheckBox;

        public ChartCustomizationPanel(ErrorEllipseChart chart) {
            super(5);

            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();

            FXMLLoader loader = new FXMLLoader(ErrorEllipsesCustomisationPanel.class.getResource("chartcustomizationpanel.fxml"),
                                               ResourceBundle.getBundle("org.cirdles.topsoil.Resources"));
            loader.setRoot(this);
            loader.setController(this);

            try {
                loader.load();
                chart.concordiaLineShownProperty().bind(checkboxConcordia.selectedProperty());

                ObservableValue<Number> xRange = xAxis.upperBoundProperty().subtract(xAxis.lowerBoundProperty());
                ObservableValue<Number> yRange = yAxis.upperBoundProperty().subtract(yAxis.lowerBoundProperty());

                autoTickXCheckBox.selectedProperty().bindBidirectional(((NumberAxis) chart.getXAxis()).getTickGenerator().autoTickingProperty());

                autoTickYCheckBox.selectedProperty().bindBidirectional(((NumberAxis) chart.getYAxis()).getTickGenerator().autoTickingProperty());

                tickXnf.setTargetProperty(((NumberAxis) chart.getXAxis()).getTickGenerator().anchorTickProperty(), xRange);
                tickXnf.visibleProperty().bind(Bindings.not(autoTickXCheckBox.selectedProperty()));
                tickYnf.setTargetProperty(((NumberAxis) chart.getYAxis()).getTickGenerator().anchorTickProperty(), yRange);
                tickYnf.visibleProperty().bind(Bindings.not(autoTickYCheckBox.selectedProperty()));

                tickUnitXnf.setTargetProperty(((NumberAxis) chart.getXAxis()).getTickGenerator().tickUnitProperty(), xRange);
                tickUnitXnf.visibleProperty().bind(Bindings.not(autoTickXCheckBox.selectedProperty()));
                tickUnitYnf.setTargetProperty(((NumberAxis) chart.getYAxis()).getTickGenerator().tickUnitProperty(), yRange);
                tickUnitYnf.visibleProperty().bind(Bindings.not(autoTickYCheckBox.selectedProperty()));

                tickUnitLabel.visibleProperty().bind(Bindings.and(autoTickXCheckBox.selectedProperty(), autoTickYCheckBox.selectedProperty()).not());

                anchorTickLabel.visibleProperty().bind(Bindings.and(autoTickXCheckBox.selectedProperty(), autoTickYCheckBox.selectedProperty()).not());
            } catch (IOException e) {
                getChildren().add(new Label("There was an error loading this part of the panel."));
                e.printStackTrace();
            }

        }
    }
