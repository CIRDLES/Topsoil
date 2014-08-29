/*
 * Copyright 2014 CIRDLES.
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
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.cirdles.topsoil.table.Record;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TSVTableWriter;
import org.cirdles.topsoil.utils.TableReader;
import org.cirdles.topsoil.utils.TableWriter;

/**
 * The main window of the Topsoil standalone program.
 * @author pfif
 */
public class TopsoilMainWindow extends VBox {
    /**
     * The table that contain data
     */
    TSVTable dataTable;

    public TopsoilMainWindow(Stage primaryStage) {
        dataTable = new TSVTable();
        VBox.setVgrow(dataTable, Priority.ALWAYS);
        
        TopsoilTableToolbar toolbar = new TopsoilTableToolbar(dataTable);
        
        this.getChildren().add(new TopsoilMainMenubar(primaryStage));
        this.getChildren().add(toolbar);
        this.getChildren().add(dataTable);
    }
    
    
    /**
     * The menu bar of Topsoil's main window.
     */
    public class TopsoilMainMenubar extends MenuBar{

        public TopsoilMainMenubar(Stage primaryStage) {
            Menu fileMenu = new Menu("File");
            MenuItem importFromFile = new MenuItem("Import from File");
            fileMenu.getItems().add(importFromFile);
            importFromFile.setOnAction(event -> {
                FileChooser tsvChooser = new FileChooser();
                tsvChooser.setInitialDirectory(Topsoil.USER_HOME.toFile());
                tsvChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Table Files", "TSV"));
                Path filePath = tsvChooser.showOpenDialog(primaryStage).toPath();

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
            });

            MenuBar menuBar = new MenuBar();
            menuBar.getMenus().add(fileMenu);
        }
    }
    
}
