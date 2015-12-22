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
package org.cirdles.topsoil.plot.standard;

import com.johnzeringue.extendsfx.layout.CustomVBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.plot.JavaFXDisplayable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    private UncertaintyEllipsePlot plot;
    private Executor executor;

    public UncertaintyEllipsePlotPropertiesPanel(UncertaintyEllipsePlot plot) {
        super(self -> {
            self.plot = plot;
            self.executor = Executors.newSingleThreadExecutor();
        });
    }

    @FXML
    private void initialize() {
        uncertaintyField.getItems().addAll(1.0, 2.0, 2.4477);
    }

    public void updateProperties() {
        String title = (String) plot.getProperty(TITLE);
        String xAxis = (String) plot.getProperty(X_AXIS);
        String yAxis = (String) plot.getProperty(Y_AXIS);

        Double uncertainty
                = ((Number) plot.getProperty(UNCERTAINTY)).doubleValue();

        String ellipseFillColorString
                = (String) plot.getProperty(ELLIPSE_FILL_COLOR);

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
            plot.setProperty(TITLE, titleField.getText());
        });
    }

    private void updateXAxis() {
        executor.execute(() -> {
            plot.setProperty(X_AXIS, xAxisField.getText());
        });
    }

    private void updateYAxis() {
        executor.execute(() -> {
            plot.setProperty(Y_AXIS, yAxisField.getText());
        });
    }

    private void updateUncertainty() {
        executor.execute(() -> {
            plot.setProperty(UNCERTAINTY, uncertaintyField.getValue());
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
            plot.setProperty(
                    ELLIPSE_FILL_COLOR,
                    toColorString(ellipseFillColorPicker.getValue()));
        });
    }

    @Override
    public Node displayAsNode() {
        return this;
    }

}
