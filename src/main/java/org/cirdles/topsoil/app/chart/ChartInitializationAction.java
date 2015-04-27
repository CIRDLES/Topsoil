/*
 * Copyright 2014 zeringue.
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

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.component.SettingsPanel;
import org.cirdles.topsoil.app.utils.SVGSaver;
import org.cirdles.topsoil.chart.JavaScriptChart;
import org.cirdles.topsoil.chart.SimpleVariableContext;
import org.cirdles.topsoil.app.UncertaintyVariableFormat;
import org.cirdles.topsoil.chart.VariableContext;
import org.cirdles.topsoil.data.Dataset;
import org.controlsfx.control.action.Action;

/**
 *
 * @author zeringue
 */
public class ChartInitializationAction extends Action {

    private static final String ACTION_NAME = "Create chart";

    public ChartInitializationAction(Dataset dataset, JavaScriptChart chart, final ChartInitializationDialog dialog) {
        super(ACTION_NAME, event -> {
            dialog.hide();
            ChartInitializationView columnSelector = (ChartInitializationView) dialog.getContent();

            VariableContext variableContext = new SimpleVariableContext();

            chart.getVariables().ifPresent(variables -> {
                // x
                variableContext.addBinding(
                        variables.get(0), columnSelector.getXSelection());

                // sigma x
                variableContext.addBinding(
                        variables.get(1), columnSelector.getSigmaXSelection(),
                        new UncertaintyVariableFormat(
                                columnSelector.getSigmaXErrorSize(),
                                columnSelector.getSigmaXExpressionType()));

                // y
                variableContext.addBinding(
                        variables.get(2), columnSelector.getYSelection());

                // sigma y
                variableContext.addBinding(
                        variables.get(3), columnSelector.getSigmaYSelection(),
                        new UncertaintyVariableFormat(
                                columnSelector.getSigmaYErrorSize(),
                                columnSelector.getSigmaYExpressionType()));

                // rho
                variableContext.addBinding(
                        variables.get(4), columnSelector.getRhoSelection());
            });

            chart.setData(dataset, variableContext);

            SettingsPanel settingsPanel = new SettingsPanel(chart.getSettingScope());

            ToolBar toolBar = new ToolBar();

            Button saveToSVG = new Button("Save as SVG");
            saveToSVG.setOnAction(mouseEvent -> {
                new SVGSaver().save(chart.toSVG());
            });

            Button fitData = new Button("Fit data");
            fitData.setOnAction(mouseEvent -> {
                chart.fitData();
            });

            toolBar.getItems().addAll(saveToSVG, fitData);

            HBox chartAndConfig = new HBox(chart.displayAsNode(), settingsPanel);

            VBox chartWindow = new VBox(toolBar, chartAndConfig);
            Scene scene = new Scene(chartWindow, 1200, 800);

            Stage chartStage = new Stage();
            chartStage.setScene(scene);
            chartStage.show();
        });
    }

}
