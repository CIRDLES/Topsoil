/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.topsoil.chart.standard;

import com.johnzeringue.extendsfx.layout.CustomVBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.chart.JavaFXDisplayable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.cirdles.topsoil.chart.standard.UncertaintyEllipseChartProperties.*;

/**
 * Created by johnzeringue on 11/8/15.
 */
public class UncertaintyEllipseChartPropertiesPanel
        extends CustomVBox<UncertaintyEllipseChartPropertiesPanel>
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

    private UncertaintyEllipseChart chart;
    private Executor executor;

    public UncertaintyEllipseChartPropertiesPanel(UncertaintyEllipseChart chart) {
        super(self -> {
            self.chart = chart;
            self.executor = Executors.newSingleThreadExecutor();
        });
    }

    @FXML
    private void initialize() {
        uncertaintyField.getItems().addAll(1.0, 2.0, 2.4477);
    }

    public void updateProperties() {
        String title = (String) chart.getProperty(TITLE);
        String xAxis = (String) chart.getProperty(X_AXIS);
        String yAxis = (String) chart.getProperty(Y_AXIS);

        Double uncertainty
                = ((Number) chart.getProperty(UNCERTAINTY)).doubleValue();

        String ellipseFillColorString
                = (String) chart.getProperty(ELLIPSE_FILL_COLOR);

        Color ellipseFillColor = Color.valueOf(ellipseFillColorString);

        Platform.runLater(() -> {
            titleField.setText(title);
            xAxisField.setText(xAxis);
            yAxisField.setText(yAxis);
            uncertaintyField.setValue(uncertainty);
            ellipseFillColorPicker.setValue(ellipseFillColor);

            titleField
                    .textProperty()
                    .addListener(((observable, oldValue, newValue) -> {
                        updateTitle();
                    }));

            xAxisField
                    .textProperty()
                    .addListener(((observable, oldValue, newValue) -> {
                        updateXAxis();
                    }));

            yAxisField
                    .textProperty()
                    .addListener(((observable, oldValue, newValue) -> {
                        updateYAxis();
                    }));

            uncertaintyField
                    .getSelectionModel()
                    .selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        updateUncertainty();
                    });

            ellipseFillColorPicker
                    .valueProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        updateEllipseFillColor();
                    });
        });
    }

    private void updateTitle() {
        executor.execute(() -> {
            chart.setProperty(TITLE, titleField.getText());
        });
    }

    private void updateXAxis() {
        executor.execute(() -> {
            chart.setProperty(X_AXIS, xAxisField.getText());
        });
    }

    private void updateYAxis() {
        executor.execute(() -> {
            chart.setProperty(Y_AXIS, yAxisField.getText());
        });
    }

    private void updateUncertainty() {
        executor.execute(() -> {
            chart.setProperty(UNCERTAINTY, uncertaintyField.getValue());
        });
    }

    private String toColorString(Color color) {
        return String.format(
                "#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void updateEllipseFillColor() {
        executor.execute(() -> {
            chart.setProperty(
                    ELLIPSE_FILL_COLOR,
                    toColorString(ellipseFillColorPicker.getValue()));
        });
    }

    @Override
    public Node displayAsNode() {
        return this;
    }

}
