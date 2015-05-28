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
package org.cirdles.topsoil.app.chart;

import com.johnzeringue.extendsfx.layout.CustomVBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import org.cirdles.topsoil.app.component.SettingsPanel;
import org.cirdles.topsoil.app.utils.SVGSaver;
import org.cirdles.topsoil.chart.Chart;
import org.cirdles.topsoil.chart.JavaScriptChart;

/**
 *
 * @author John Zeringue
 */
public class ChartWindow extends CustomVBox<ChartWindow> {
    
    @FXML
    private ToolBar chartToolBar;
    @FXML
    private HBox chartAndConfig;
    
    private Chart chart;
    
    public ChartWindow(Chart chart) {
        super(self -> self.chart = chart);
    }
    
    private void initializeToolbar() {
        if (chart instanceof JavaScriptChart) {
            JavaScriptChart javaScriptChart = (JavaScriptChart) chart;
            
            Button saveToSVG = new Button("Save as SVG");
            saveToSVG.setOnAction(mouseEvent -> {
                new SVGSaver().save(javaScriptChart.displayAsSVGDocument());
            });
            
            Button fitData = new Button("Fit data");
            fitData.setOnAction(mouseEvent -> {
                javaScriptChart.fitData();
            });
            
            chartToolBar.getItems().addAll(saveToSVG, fitData);
        }
    }
    
    private void initializeChartAndConfig() {
        chartAndConfig.getChildren().setAll(
                chart.displayAsNode(),
                new SettingsPanel(chart.getSettingScope())
        );
    }
    
    @FXML
    private void initialize() {
        initializeToolbar();
        initializeChartAndConfig();
    }
    
}
