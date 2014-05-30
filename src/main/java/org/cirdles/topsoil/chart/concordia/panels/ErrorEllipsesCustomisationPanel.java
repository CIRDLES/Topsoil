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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;

/**
 *
 * @author pfif
 */
public class ErrorEllipsesCustomisationPanel extends VBox {

    @FXML
    private ColorPicker colorPickerStroke;
    @FXML
    private CheckBox showOutlineCheckBox;
    @FXML
    private ColorPicker colorPickerFill;
    @FXML
    private Slider sliderOpacity;

    public ErrorEllipsesCustomisationPanel(ErrorEllipseChart chart) {
        super(5);

        FXMLLoader loader = new FXMLLoader(ErrorEllipsesCustomisationPanel.class.getResource("errorellipsecustomizationpanel.fxml"),
                                           ResourceBundle.getBundle("org.cirdles.topsoil.Resources"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            //Binding everything to their property
            showOutlineCheckBox.selectedProperty().bindBidirectional(chart.ellipseOutlineShownProperty());

            colorPickerStroke.valueProperty().bindBidirectional(chart.ellipseOutlineColorProperty());
            colorPickerStroke.disableProperty().bind(Bindings.not(showOutlineCheckBox.selectedProperty()));

            colorPickerFill.valueProperty().bindBidirectional(chart.ellipseFillColorProperty());

            sliderOpacity.valueProperty().bindBidirectional(chart.ellipseFillOpacityProperty());
        } catch (IOException e) {
            getChildren().add(new Label("There was an error loading this part of the panel."));
            e.printStackTrace();
        }

    }

}
