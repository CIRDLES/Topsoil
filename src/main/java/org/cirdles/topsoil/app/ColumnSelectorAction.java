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
package org.cirdles.topsoil.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.chart.concordia.ErrorEllipse;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChartExtendedPanel;
import org.cirdles.topsoil.chart.concordia.RecordToErrorEllipseConverter;
import org.cirdles.topsoil.lib.chart.Chart;
import org.cirdles.topsoil.table.Record;
import org.controlsfx.control.action.Action;

/**
 *
 * @author zeringue
 */
public class ColumnSelectorAction extends Action {
    
    private static final String ACTION_NAME = "Create chart";

    public ColumnSelectorAction(TableView<Record> table, Chart<double[][]> chart, ColumnSelectorDialog dialog) {
        super(ACTION_NAME, (javafx.event.ActionEvent event) -> {
            dialog.hide();
            ColumnSelectorView columnSelector = (ColumnSelectorView) dialog.getContent();
            
            // create a new converter for the chart
            RecordToErrorEllipseConverter converter = new RecordToErrorEllipseConverter(columnSelector.getXSelection(), columnSelector.getSigmaXSelection(), columnSelector.getYSelection(), columnSelector.getSigmaYSelection(), columnSelector.getRhoSelection());
            converter.setErrorSizeSigmaX(columnSelector.getSigmaXErrorSize());
            converter.setExpressionTypeSigmaX(columnSelector.getSigmaXExpressionType());
            converter.setErrorSizeSigmaY(columnSelector.getSigmaYErrorSize());
            converter.setExpressionTypeSigmaY(columnSelector.getSigmaYExpressionType());
            
            // build the data matrix
            double[][] data = new double[table.getItems().size()][5];
            for (int i = 0; i < data.length; i++) {
                Record record = table.getItems().get(i);
                ErrorEllipse ellipse = converter.convert(new Data<>(0, 0, record));
                
                data[i][0] = ellipse.getX();
                data[i][1] = ellipse.getSigmaX();
                data[i][2] = ellipse.getY();
                data[i][3] = ellipse.getSigmaY();
                data[i][4] = ellipse.getRho();
            }
            
            chart.setData(data);
            Scene scene = new Scene((Parent) chart.asNode(), 1200, 800);
            
            Stage chartStage = new Stage();
            chartStage.setScene(scene);
            chartStage.show();
        });
    }
    
}
