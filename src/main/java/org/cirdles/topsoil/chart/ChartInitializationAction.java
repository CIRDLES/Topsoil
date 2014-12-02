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
package org.cirdles.topsoil.chart;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.chart.concordia.ErrorEllipse;
import org.cirdles.topsoil.app.chart.concordia.RecordToErrorEllipseConverter;
import org.cirdles.topsoil.app.component.SettingsPanel;
import org.cirdles.topsoil.app.table.Record;
import org.controlsfx.control.action.Action;

/**
 *
 * @author zeringue
 */
public class ChartInitializationAction extends Action {
    
    private static final String ACTION_NAME = "Create chart";

    public ChartInitializationAction(TableView<Record> table, Chart chart, final ChartInitializationDialog dialog) {
        super(ACTION_NAME, (javafx.event.ActionEvent event) -> {
            dialog.hide();
            ChartInitializationView columnSelector = (ChartInitializationView) dialog.getContent();
            
            // create a new converter for the chart
            RecordToErrorEllipseConverter converter = new RecordToErrorEllipseConverter(columnSelector.getXSelection(), columnSelector.getSigmaXSelection(), columnSelector.getYSelection(), columnSelector.getSigmaYSelection(), columnSelector.getRhoSelection());
            converter.setErrorSizeSigmaX(columnSelector.getSigmaXErrorSize());
            converter.setExpressionTypeSigmaX(columnSelector.getSigmaXExpressionType());
            converter.setErrorSizeSigmaY(columnSelector.getSigmaYErrorSize());
            converter.setExpressionTypeSigmaY(columnSelector.getSigmaYExpressionType());
            
            // build the data matrix
            double[][] data = new double[table.getItems().size()][5];
            for (int i = 0; i < table.getItems().size(); i++) {
                ErrorEllipse errorEllipse = converter.convert(
                        new XYChart.Data<>(0, 0, table.getItems().get(i)));
                
                data[i][0] = errorEllipse.getX();
                data[i][1] = errorEllipse.getSigmaX();
                data[i][2] = errorEllipse.getY();
                data[i][3] = errorEllipse.getSigmaY();
                data[i][4] = errorEllipse.getRho();
            }
            chart.setData(data);
            
            SettingsPanel settingsPanel = new SettingsPanel(chart.getSettingScope());
            
            Scene scene = new Scene(new VBox(chart.asNode(), settingsPanel), 1200, 800);
            
            Stage chartStage = new Stage();
            chartStage.setScene(scene);
            chartStage.show();
        });
    }
    
}
