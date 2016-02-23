/*
 * Copyright 2016 CIRDLES.
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
package org.cirdles.topsoil.app.plot.standard;

import com.johnzeringue.extendsfx.layout.CustomVBox;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.plot.JavaFXDisplayable;
import org.cirdles.topsoil.plot.Plot;

import java.util.Map;

import static org.cirdles.topsoil.plot.standard.UncertaintyEllipsePlotProperties.ELLIPSE_FILL_COLOR;
import static org.cirdles.topsoil.plot.standard.UncertaintyEllipsePlotProperties.TITLE;
import static org.cirdles.topsoil.plot.standard.UncertaintyEllipsePlotProperties.UNCERTAINTY;
import static org.cirdles.topsoil.plot.standard.UncertaintyEllipsePlotProperties.X_AXIS;
import static org.cirdles.topsoil.plot.standard.UncertaintyEllipsePlotProperties.Y_AXIS;

/**
 * Created by johnzeringue on 11/8/15.
 */
public class UncertaintyEllipsePlotPropertiesPanel
        extends CustomVBox<UncertaintyEllipsePlotPropertiesPanel>
        implements JavaFXDisplayable {

    @FXML
    private TextField titleField;

    @FXML
    private TextField xAxisField;

    @FXML
    private TextField yAxisField;

    @FXML
    private ChoiceBox<Double> uncertaintyField;

    @FXML
    private ColorPicker ellipseFillColorPicker;

    private Plot plot;

    public UncertaintyEllipsePlotPropertiesPanel(Plot plot) {
        super(self -> {
            self.plot = plot;
        });
    }

    private ChangeListener<Object> updateProperty(String property) {
        return (observable, oldValue, newValue) -> {
            Map<String, Object> properties = plot.getProperties();
            properties.put(property, newValue);
            plot.setProperties(properties);
        };
    }

    @FXML
    private void initialize() {
        ellipseFillColorPicker.setValue(
                Color.valueOf(
                        (String) plot.getProperties().get(ELLIPSE_FILL_COLOR)));

        ellipseFillColorPicker.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    Map<String, Object> properties = plot.getProperties();

                    String fillColor = String.format(
                            "#%02X%02X%02X",
                            (int) (newValue.getRed() * 255),
                            (int) (newValue.getGreen() * 255),
                            (int) (newValue.getBlue() * 255));

                    properties.put(ELLIPSE_FILL_COLOR, fillColor);
                    plot.setProperties(properties);
                });

        titleField.setText((String) plot.getProperties().get(TITLE));
        titleField.textProperty().addListener(updateProperty(TITLE));

        uncertaintyField.getItems().addAll(1.0, 2.0, 2.4477);
        uncertaintyField.setValue(((Number) plot.getProperties().get(UNCERTAINTY)).doubleValue());
        uncertaintyField.valueProperty().addListener(updateProperty(UNCERTAINTY));

        xAxisField.setText((String) plot.getProperties().get(X_AXIS));
        xAxisField.textProperty().addListener(updateProperty(X_AXIS));

        yAxisField.setText((String) plot.getProperties().get(Y_AXIS));
        yAxisField.textProperty().addListener(updateProperty(Y_AXIS));
    }

    @Override
    public Node displayAsNode() {
        return this;
    }

}
