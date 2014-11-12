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

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChart;

/**
 *
 * @author pfif
 */
public class ErrorEllipseCustomizationPanel extends CustomVBox<ErrorEllipseCustomizationPanel> {

    @FXML private ColorPicker colorPickerStroke;
    @FXML private CheckBox showOutlineCheckBox;
    @FXML private ColorPicker colorPickerFill;
    @FXML private Slider sliderOpacity;

    private ErrorEllipseChart chart;

    public ErrorEllipseCustomizationPanel(ErrorEllipseChart chart) {
        super(self -> self.chart = chart);
    }

    @FXML
    private void initialize() {
        setSpacing(5);

        //Binding everything to their property
        showOutlineCheckBox.selectedProperty().bindBidirectional(chart.ellipseOutlineShownProperty());

        colorPickerStroke.valueProperty().bindBidirectional(chart.ellipseOutlineColorProperty());
        colorPickerStroke.disableProperty().bind(Bindings.not(showOutlineCheckBox.selectedProperty()));

        colorPickerFill.valueProperty().bindBidirectional(chart.ellipseFillColorProperty());

        sliderOpacity.valueProperty().bindBidirectional(chart.ellipseFillOpacityProperty());
    }

}
