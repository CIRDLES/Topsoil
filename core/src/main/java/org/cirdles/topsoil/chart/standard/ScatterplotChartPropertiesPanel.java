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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.chart.JavaFXDisplayable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.cirdles.topsoil.chart.standard.ScatterplotChartProperties.POINT_FILL_COLOR;
import static org.cirdles.topsoil.chart.standard.ScatterplotChartProperties.TITLE;

/**
 * Created by johnzeringue on 11/8/15.
 */
public class ScatterplotChartPropertiesPanel
        extends CustomVBox<ScatterplotChartPropertiesPanel>
        implements JavaFXDisplayable {

    @FXML
    private TextField titleField;

    @FXML
    private ColorPicker pointFillColorPicker;

    private ScatterplotChart chart;
    private Executor executor;

    public ScatterplotChartPropertiesPanel(ScatterplotChart chart) {
        super(self -> {
            self.chart = chart;
            self.executor = Executors.newSingleThreadExecutor();
        });
    }

    public void updateProperties() {
        String title = (String) chart.getProperty(TITLE);

        String pointFillColorString
                = (String) chart.getProperty(POINT_FILL_COLOR);

        Color pointFillColor = Color.valueOf(pointFillColorString);

        Platform.runLater(() -> {
            titleField.setText(title);
            pointFillColorPicker.setValue(pointFillColor);

            titleField
                    .textProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        updateTitle();
                    });

            pointFillColorPicker
                    .valueProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        updatePointFillColor();
                    });
        });
    }

    private void updateTitle() {
        executor.execute(() -> {
            chart.setProperty("Title", titleField.getText());
        });
    }

    private String toColorString(Color color) {
        return String.format(
                "#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void updatePointFillColor() {
        executor.execute(() -> {
            chart.setProperty(
                    POINT_FILL_COLOR,
                    toColorString(pointFillColorPicker.getValue()));
        });
    }

    @Override
    public Node displayAsNode() {
        return this;
    }

}
