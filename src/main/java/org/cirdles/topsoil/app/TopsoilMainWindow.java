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
package org.cirdles.topsoil.app;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.app.table.Record;
import org.cirdles.topsoil.app.utils.GetDocumentsDirectoryOperation;
import org.cirdles.topsoil.app.utils.TSVTableReader;
import org.cirdles.topsoil.app.utils.TSVTableWriter;
import org.cirdles.topsoil.app.utils.TableReader;
import org.cirdles.topsoil.app.utils.TableWriter;
import org.cirdles.topsoil.chart.ChartInitializationDialog;
import org.cirdles.topsoil.chart.JavaScriptChart;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author John Zeringue
 */
public class TopsoilMainWindow extends CustomVBox implements Initializable {

    @FXML private TSVTable dataTable;
    @FXML private Menu chartsMenu;

    // JFB
    private final int ERROR_CHART_REQUIRED_COL_COUNT = 5;

    private FileSystem jarFileSystem;

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

        loadCustomScripts();

        // set the window title to something like "Topsoil [0.3.4]"
        String applicationName = resources.getString("applicationName");
        String applicationVersion = resources.getString("applicationVersion");
        setWindowTitle(String.format("%s [%s]", applicationName, applicationVersion));
    }

    @FXML
    private void loadCustomScripts() {
        // only keep the first two charts
        chartsMenu.getItems().retainAll(chartsMenu.getItems().subList(0, 2));

        Path topsoilScripts = new GetDocumentsDirectoryOperation().perform("Topsoil Scripts");

        if (Files.exists(topsoilScripts)) {
            try {
                Files.walk(topsoilScripts).forEach(filePath -> {
                    String fileName = filePath.getFileName().toString();

                    if (fileName.matches(".*\\.js")) {
                        MenuItem chartItem = new MenuItem(fileName.replace(".js", ""));

                        chartItem.setOnAction(event -> {
                            new ChartInitializationDialog(dataTable, new JavaScriptChart(filePath)).show();
                        });

                        chartsMenu.getItems().add(chartItem);
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(TopsoilMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setWindowTitle(String title) {
        // while the code below is long and ugly, anonymous inner classes are
        // necessary (in Java 8) in order to allow the listeners to reference
        // and remove themselves
        // initially lambdas were used (see git history)
        // this keeps TestFX from causing errors

        // create self-removing window listener
        // runs second
        ChangeListener<Window> windowListener = new ChangeListener<Window>() {
            @Override
            public void changed(ObservableValue<? extends Window> observableWindow, Window oldWindow, Window newWindow) {
                Stage stage = (Stage) newWindow;

                // actually set the title
                stage.setTitle(title);

                getScene().windowProperty().removeListener(this);
            }
        };

        // create self-removing scene listener
        // runs first
        ChangeListener<Scene> sceneListener = new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableScene, Scene oldScene, Scene newScene) {
                getScene().windowProperty().addListener(windowListener);
                sceneProperty().removeListener(this);
            }
        };

        sceneProperty().addListener(sceneListener);
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
    private void createScatterplot(ActionEvent event) {
        try {
            // get the path to the JavaScript file
            URI javascriptURI = getClass().getResource("scatterplot.js").toURI();
            Path javascriptPath;

            // JARs and Netbeans builds must be handled differently
            if (javascriptURI.toString().startsWith("jar:")) {
                System.out.println(javascriptURI);
                String[] uriParts = javascriptURI.toString().split("!");

                Map<String, ?> env = new HashMap<>();
                if (jarFileSystem == null) {
                    jarFileSystem = FileSystems.newFileSystem(URI.create(uriParts[0]), env);
                }

                javascriptPath = jarFileSystem.getPath(uriParts[1]);
            } else {
                javascriptPath = Paths.get(javascriptURI);
            }

            new ChartInitializationDialog(dataTable, new JavaScriptChart(javascriptPath)).show();
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(TopsoilMainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    /**
     * For the new JS charts, {@link #createErrorChart} is the old method.
     */
    private void createErrorEllipseChart() {
        try {
            // get the path to the JavaScript file
            URI javascriptURI = getClass().getResource("errorellipsechart.js").toURI();
            Path javascriptPath;

            // JARs and Netbeans builds must be handled differently
            if (javascriptURI.toString().startsWith("jar:")) {
                System.out.println(javascriptURI);
                String[] uriParts = javascriptURI.toString().split("!");

                Map<String, ?> env = new HashMap<>();
                if (jarFileSystem == null) {
                    jarFileSystem = FileSystems.newFileSystem(URI.create(uriParts[0]), env);
                }

                javascriptPath = jarFileSystem.getPath(uriParts[1]);
            } else {
                javascriptPath = Paths.get(javascriptURI);
            }

            new ChartInitializationDialog(dataTable, new JavaScriptChart(javascriptPath)).show();
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(TopsoilMainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void createErrorChart() {
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
