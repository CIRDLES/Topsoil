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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.cirdles.javafx.CustomVBox;
import org.cirdles.topsoil.data.Dataset;
import org.cirdles.topsoil.app.utils.GetApplicationDirectoryOperation;
import org.cirdles.topsoil.app.utils.GetDocumentsDirectoryOperation;
import org.cirdles.topsoil.app.utils.TSVDatasetReader;
import org.cirdles.topsoil.app.utils.TSVDatasetWriter;
import org.cirdles.topsoil.app.utils.DatasetReader;
import org.cirdles.topsoil.app.utils.DatasetWriter;
import org.cirdles.topsoil.app.chart.ChartInitializationDialog;
import org.cirdles.topsoil.chart.JavaScriptChart;
import org.cirdles.topsoil.data.DatasetManager;
import org.cirdles.topsoil.app.utils.TSVDatasetManager;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author John Zeringue
 */
public class TopsoilMainWindow extends CustomVBox implements Initializable {

    private static final Logger LOGGER
            = Logger.getLogger(TopsoilMainWindow.class.getName());

    private final static Path APPLICATION_DIRECTORY
            = new GetApplicationDirectoryOperation().perform("Topsoil");

    private final static Path DATA_SETS_DIRECTORY
            = APPLICATION_DIRECTORY.resolve("Data Sets");

    @FXML
    private Menu chartsMenu;
    @FXML
    private Menu datasetsMenu;
    @FXML
    private TabPane dataTableTabPane;

    // JFB
    private final int ERROR_CHART_REQUIRED_COL_COUNT = 5;

    private Map<TSVTable, Dataset> dataTableToSet;

    private FileSystem jarFileSystem;

    private DatasetManager datasetManager;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param resources
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        dataTableToSet = new HashMap<>();

        datasetManager = new TSVDatasetManager(DATA_SETS_DIRECTORY);
        datasetManager.getDatasets().stream()
                .filter(datasetManager::isOpen)
                .forEach(this::loadDataSet);

        loadCustomScripts();
        reloadDatasetMenu();

        // set the window title to something like "Topsoil [0.3.4]"
        String applicationName = resources.getString("applicationName");
        String applicationVersion = resources.getString("applicationVersion");
        setWindowTitle(
                String.format("%s [%s]", applicationName, applicationVersion));
    }

    Optional<TSVTable> getCurrentTable() {
        Optional<TSVTable> result = Optional.empty();
        Tab currentTab = dataTableTabPane.getSelectionModel().getSelectedItem();

        if (currentTab.getContent() instanceof TSVTable) {
            result = Optional.of((TSVTable) currentTab.getContent());
        }

        return result;
    }

    @FXML
    void createDataTable() {
        createTab();
    }

    Tab createTab() {
        Tab dataTableTab = new Tab("Untitled Data");
        dataTableTab.setOnClosed(event -> {
            if (dataTableTab.getContent() instanceof TSVTable) {
                TSVTable table = (TSVTable) dataTableTab.getContent();

                if (dataTableToSet.containsKey(table)) {
                    datasetManager.close(dataTableToSet.get(table));
                }
            }
        });

        TSVTable dataTable = new TSVTable();
        dataTable.setPlaceholder(new EmptyTablePlaceholder(dataTable));
        dataTableTab.setContent(dataTable);

        dataTableTabPane.getTabs().add(dataTableTab);

        return dataTableTab;
    }

    @FXML
    void saveDataTable() {
        Dialogs.create()
                .message("Data set name:")
                .showTextInput().ifPresent(datasetName -> {
                    Path datasetPath = DATA_SETS_DIRECTORY.resolve(
                            datasetName + "__open____headers__.tsv");

                    getCurrentTable().ifPresent(table -> table.saveToPath(datasetPath));
                });

        // reload
        reloadDatasetMenu();
    }

    @FXML
    void loadCustomScripts() {
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
                            new ChartInitializationDialog(
                                    getCurrentTable().map(dataTableToSet::get).get(),
                                    new JavaScriptChart(filePath)).show();
                        });

                        chartsMenu.getItems().add(chartItem);
                    }
                });
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    void createDatasetMenuItem(Dataset dataset) {
        MenuItem datasetMenuItem = new MenuItem(dataset.getName());

        datasetMenuItem.setOnAction(event -> {
            loadDataSet(dataset);
        });

        datasetsMenu.getItems().add(datasetMenuItem);
    }

    void reloadDatasetMenu() {
        // allows this method to be called multiple times in the same session
        datasetsMenu.getItems().clear();

        datasetManager.getDatasets().forEach(this::createDatasetMenuItem);
    }

    void loadDataSet(Dataset dataset) {
        loadDataSet(dataset, createTab());
    }

    void loadDataSet(Dataset dataset, Tab tab) {
        Node content = tab.getContent();

        if (content instanceof TSVTable) {
            TSVTable table = (TSVTable) content;

            table.setDataset(dataset);
            dataTableToSet.put(table, dataset);
            datasetManager.open(dataset);
            tab.setText(dataset.getName());
        }
    }

    Tab getCurrentTab() {
        return dataTableTabPane.getSelectionModel().getSelectedItem();
    }

    void setWindowTitle(String title) {
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
                if (newWindow instanceof Stage) { // make the cast safe
                    Stage stage = (Stage) newWindow;

                    // actually set the title
                    stage.setTitle(title);
                }

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
    void importFromFile() {
        TSVTable dataTable = getCurrentTable().get();

        FileChooser tsvChooser = new FileChooser();
        tsvChooser.setInitialDirectory(Topsoil.USER_HOME.toFile());
        tsvChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Table Files", "TSV"));
        Path filePath = tsvChooser.showOpenDialog(getScene().getWindow()).toPath();

        // JFB for now, assume error chart is only chart style
        dataTable.setRequiredColumnCount(ERROR_CHART_REQUIRED_COL_COUNT);

        Tools.yesNoPrompt("Does the selected file contain headers?", response -> {
            DatasetReader tableReader = new TSVDatasetReader(response);

            try {
                Dataset dataset = tableReader.read(filePath);
                dataTable.setDataset(dataset);
            } catch (IOException ex) {
                Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
            }

            DatasetWriter tableWriter = new TSVDatasetWriter(dataTable.getRequiredColumnCount());
            try {
                tableWriter.write(dataTable.getDataset(), Topsoil.LAST_TABLE_PATH);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        });
    }

    @FXML
    void createScatterplot() {
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

            new ChartInitializationDialog(
                    getCurrentTable().map(dataTableToSet::get).get(), new JavaScriptChart(javascriptPath)).show();
        } catch (URISyntaxException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * For the new JS charts, {@link #createErrorChart} is the old method.
     */
    @FXML
    void createErrorEllipseChart() {
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

            new ChartInitializationDialog(
                    getCurrentTable().map(dataTableToSet::get).get(),
                    new JavaScriptChart(javascriptPath)).show();
        } catch (URISyntaxException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void pasteFromClipboard() {
        getCurrentTable().ifPresent(TSVTable::pasteFromClipboard);
    }

    @FXML
    void emptyTable() {
        getCurrentTable().ifPresent(TSVTable::clear);
    }
}
