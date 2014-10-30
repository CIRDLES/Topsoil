/*
 * Copyright 2014 zeringuej.
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
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.table.Record;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TSVTableWriter;
import org.cirdles.topsoil.utils.TableReader;
import org.cirdles.topsoil.utils.TableWriter;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author zeringuej
 */
public class TopsoilMainWindow extends CustomVBox implements Initializable {

    @FXML private TSVTable dataTable;

    // JFB
    private final int ERROR_CHART_REQUIRED_COL_COUNT = 5;

    private ResourceBundle resources;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param resources
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        dataTable.setSavePath(Topsoil.LAST_TABLE_PATH);
        dataTable.load();

        // when the stage/window loads...
        sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            getScene().windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                // set the window title to something like "Topsoil [0.3.4]"
                Stage stage = (Stage) newWindow;

                String applicationName = resources.getString("applicationName");
                String applicationVersion = resources.getString("applicationVersion");

                stage.setTitle(String.format("%s [%s]", applicationName, applicationVersion));
            });
        });
    }

    @FXML
    private void importFromFile(ActionEvent event) {
        FileChooser tsvChooser = new FileChooser();
        tsvChooser.setInitialDirectory(Topsoil.USER_HOME.toFile());
        tsvChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Table Files", "TSV"));
        Path filePath = tsvChooser.showOpenDialog(getScene().getWindow()).toPath();

        // JFB for now, assume error chart is only chart style
        dataTable.setRequiredColumnCount(ERROR_CHART_REQUIRED_COL_COUNT);

        Tools.yesNoPrompt("Does the selected file contain headers?", response -> {
            TableReader tableReader = new TSVTableReader(response);

            try {
                tableReader.read(filePath, dataTable);
            } catch (IOException ex) {
                Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
            }

            TableWriter<Record> tableWriter = new TSVTableWriter(true, dataTable.getRequiredColumnCount());
            tableWriter.write(dataTable, Topsoil.LAST_TABLE_PATH);
        });
    }

    @FXML
    private void createErrorChart(ActionEvent event) {
//        // table needs 5 columns to generate chart
//        if (dataTable.getColumns().size() < 5) {
//            Dialogs.create().message(Topsoil.NOT_ENOUGH_COLUMNS_MESSAGE).showWarning();
//        } else {
//            new ColumnSelectorDialog(dataTable).show();
//        }

        // JFB for now, assume error chart is only chart style
        dataTable.setRequiredColumnCount(ERROR_CHART_REQUIRED_COL_COUNT);

        if (!dataTable.hasRequiredColumnCount()) {
            Dialogs.create().message(Topsoil.NOT_ENOUGH_COLUMNS_MESSAGE_2).showWarning();
            dataTable.save();
            dataTable.load();
        }

        new ColumnSelectorDialog(dataTable).show();

    }

    @FXML
    private void pasteFromClipboard(ActionEvent event) {
        dataTable.pasteFromClipboard();
    }

    @FXML
    private void emptyTable(ActionEvent event) {
        dataTable.clear();
        dataTable.save();
    }
}
