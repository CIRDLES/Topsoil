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
package org.cirdles.topsoil;

import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.chart.concordia.ErrorEllipseChartExtendedPanel;
import org.cirdles.topsoil.chart.concordia.RecordToErrorEllipseConverter;
import org.cirdles.topsoil.table.Record;
import org.controlsfx.control.action.Action;

/**
 *
 * @author zeringue
 */
class ColumnSelectorAction extends Action {
    
    private static final String ACTION_NAME = "Create chart";
    
    private final ColumnSelectorDialog dialog;

    public ColumnSelectorAction(TableView<Record> table, final ColumnSelectorDialog dialog) {
        super(ACTION_NAME, (javafx.event.ActionEvent event) -> {
            dialog.hide();
            ColumnSelectorView columnSelector = (ColumnSelectorView) dialog.getContent();
            RecordToErrorEllipseConverter converter = new RecordToErrorEllipseConverter(columnSelector.getXSelection(), columnSelector.getSigmaXSelection(), columnSelector.getYSelection(), columnSelector.getSigmaYSelection(), columnSelector.getRhoSelection());
            converter.setErrorSizeSigmaX(columnSelector.getSigmaXErrorSize());
            converter.setExpressionTypeSigmaX(columnSelector.getSigmaXExpressionType());
            converter.setErrorSizeSigmaY(columnSelector.getSigmaYErrorSize());
            converter.setExpressionTypeSigmaY(columnSelector.getSigmaYExpressionType());
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (Record record : table.getItems()) {
                series.getData().add(new XYChart.Data<>(0, 0, record));
            }
            ErrorEllipseChartExtendedPanel ccExtendedPanel = new ErrorEllipseChartExtendedPanel();
            ccExtendedPanel.getChart().setConverter(converter);
            ccExtendedPanel.getChart().getData().add(series);
            VBox.setVgrow(ccExtendedPanel.getMasterDetailPane(), Priority.ALWAYS);
            Scene scene = new Scene(ccExtendedPanel, 1200, 800);
            Stage chartStage = new Stage();
            chartStage.setScene(scene);
            chartStage.show();
        });
        this.dialog = dialog;
    }
    
}
