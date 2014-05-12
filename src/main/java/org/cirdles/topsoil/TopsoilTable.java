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

package org.cirdles.topsoil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static org.cirdles.topsoil.Topsoil.LAST_TABLE_PATH;
import org.cirdles.topsoil.chart.DataConverter;
import org.cirdles.topsoil.chart.concordia.ConcordiaChart;
import org.cirdles.topsoil.chart.concordia.ErrorChartToolBar;
import org.cirdles.topsoil.chart.concordia.ErrorEllipse;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TSVTableWriter;
import org.cirdles.topsoil.utils.TableReader;
import org.cirdles.topsoil.utils.TableWriter;

/**
 * A table containing data used to generate charts.
 * Implements some shortcut.
 * Since it implements <code>ColumnSelectorDialog.ColumnSelectorDialogListener</code>, it is also responsible of generating charts.
 */
public class TopsoilTable extends TableView<Map> implements ColumnSelectorDialog.ColumnSelectorDialogListener {

    public TopsoilTable() {
        if (Files.exists(Topsoil.LAST_TABLE_PATH)) {
            TableReader tableReader = new TSVTableReader(true);
            try {
                tableReader.read(Topsoil.LAST_TABLE_PATH, this);
            } catch (IOException ex) {
                Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        this.setOnKeyPressed((KeyEvent event) -> {
            if (event.isShortcutDown() && event.getCode().equals(KeyCode.V)) {
                TinkeringTools.yesNoPrompt("Does the pasted data contain headers?", response -> {
                    TableReader tableReader = new TSVTableReader(response);
                    tableReader.read(Clipboard.getSystemClipboard().getString(), this);

                    TableWriter<Map> tableWriter = new TSVTableWriter(true);
                    tableWriter.write(this, LAST_TABLE_PATH);
                });
            }
        });

    }
    
        /**
     * Receive a converter from a <code>ColumnSelectorDialog</code> and create a
     * chart from it.
     *
     * @param converter
     */
    @Override
    public void receiveConverter(DataConverter<ErrorEllipse> converter) {
        //Creating a serie with all the data

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (Map<Object, Integer> row_ellipse : this.getItems()) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(1138, 1138, row_ellipse);
            series.getData().add(data);
        }

        ConcordiaChart chart = new ConcordiaChart(converter);
        chart.getData().add(series);
        VBox.setVgrow(chart, Priority.ALWAYS);
        
        ToolBar toolBar = new ErrorChartToolBar(chart);

        Scene scene = new Scene(new VBox(toolBar, chart), 1200, 800, true, SceneAntialiasing.DISABLED);
        Stage chartStage = new Stage();
        chartStage.setScene(scene);
        chartStage.show();
    }
}
