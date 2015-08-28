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

import com.johnzeringue.extendsfx.annotation.ResourceBundle;
import com.johnzeringue.extendsfx.layout.CustomVBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.cirdles.topsoil.app.chart.ChartWindow;
import org.cirdles.topsoil.app.chart.VariableBindingDialog;
import org.cirdles.topsoil.app.dataset.TSVDatasetManager;
import org.cirdles.topsoil.app.dataset.reader.CSVDatasetReader;
import org.cirdles.topsoil.app.dataset.reader.DatasetReader;
import org.cirdles.topsoil.app.dataset.reader.TSVDatasetReader;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.cirdles.topsoil.app.util.GetApplicationDirectoryOperation;
import org.cirdles.topsoil.chart.Chart;
import org.cirdles.topsoil.chart.JavaScriptChart;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.DatasetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * FXML Controller class
 *
 * @author John Zeringue
 */
@ResourceBundle("Resources")
public class TopsoilMainWindow extends CustomVBox {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(TopsoilMainWindow.class);

    private static final Path APPLICATION_DIRECTORY
            = new GetApplicationDirectoryOperation().perform("Topsoil");

    private static final Path DATASETS_DIRECTORY
            = APPLICATION_DIRECTORY.resolve("Data Sets");

    @FXML
    Menu chartsMenu;
    @FXML
    Menu datasetsMenu;
    @FXML
    TabPane dataTableTabPane;

    private DatasetManager datasetManager;

    private final ApplicationMetadata metadata;

    @Inject
    public TopsoilMainWindow(ApplicationMetadata metadata) {
        this.metadata = metadata;

        // something like "Topsoil [0.3.4]"
        String nameAndVersion = String.format("%s [%s]",
                metadata.getName(),
                metadata.getVersion());

        setWindowTitle(nameAndVersion);
    }

    /**
     * Initializes the controller class.
     */
    private void initialize() {
        datasetManager = new TSVDatasetManager(DATASETS_DIRECTORY);
        datasetManager.getDatasets().stream()
                .filter(datasetManager::isOpen)
                .forEach(this::loadDataset);

        reloadDatasetMenu();
    }

    Optional<TSVTable> getCurrentTable() {
        return getCurrentTab().map(tab -> (TSVTable) tab.getContent());
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
                datasetManager.close(table.getDataset());
            }
        });

        TSVTable dataTable = new TSVTable();
        dataTable.setPlaceholder(new EmptyTablePlaceholder(dataTable));
        dataTableTab.setContent(dataTable);

        dataTableTabPane.getTabs().add(dataTableTab);

        // focus on new tab
        SelectionModel<Tab> selectionModel = dataTableTabPane.getSelectionModel();
        selectionModel.select(dataTableTab);

        return dataTableTab;
    }

    @FXML
    void saveDataTable() {
        TextInputDialog textInputDialog = new TextInputDialog();

        textInputDialog.setContentText("Data set name:");
        textInputDialog.showAndWait().ifPresent(datasetName -> {
            Path datasetPath = DATASETS_DIRECTORY
                    .resolve("open")
                    .resolve(datasetName + ".tsv");

            getCurrentTable().ifPresent(table -> table.saveToPath(datasetPath));
            getCurrentTab().ifPresent(tab -> tab.setText(datasetName));
        });

        // reload
        reloadDatasetMenu();
    }

    void createDatasetMenuItem(Dataset dataset) {
        MenuItem datasetMenuItem = dataset.getName()
                .map(MenuItem::new)
                .orElseGet(MenuItem::new);

        datasetMenuItem.setOnAction(event -> {
            TopsoilMainWindow.this.loadDataset(dataset);
        });

        datasetsMenu.getItems().add(datasetMenuItem);
    }

    void reloadDatasetMenu() {
        // allows this method to be called multiple times in the same session
        datasetsMenu.getItems().clear();

        datasetManager.getDatasets().forEach(this::createDatasetMenuItem);
    }

    void loadDataset(Dataset dataset) {
        loadDataset(dataset, createTab());
    }

    void loadDataset(Dataset dataset, Tab tab) {
        Node content = tab.getContent();

        if (content instanceof TSVTable) {
            TSVTable table = (TSVTable) content;

            table.setDataset(dataset);
            datasetManager.open(dataset);

            dataset.getName().ifPresent(name -> {
                tab.setText(name);
            });
        }
    }

    Optional<Tab> getCurrentTab() {
        return Optional.ofNullable(
                dataTableTabPane.getSelectionModel().getSelectedItem());
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

    public TSVTable createTable() {
        return (TSVTable) createTab().getContent();
    }

    void importFromFile(Path filePath,
            Function<Boolean, DatasetReader> datasetReaderConstructor) {
        TSVTable dataTable = createTable();

        Tools.yesNoPrompt("Does the selected file contain headers?", response -> {
            try {
                DatasetReader datasetReader
                        = datasetReaderConstructor.apply(response);

                Dataset dataset = datasetReader.read(filePath);
                dataTable.setDataset(dataset);
            } catch (IOException ex) {
                LOGGER.error(null, ex);
            }

            //set Tab title
            getCurrentTab().ifPresent(tab -> {
                tab.setText(filePath.getFileName().toString().split("\\.")[0]);
            });
        });
    }

    void importFromCSV(Path filePath) {
        importFromFile(filePath, CSVDatasetReader::new);
    }

    void importFromTSV(Path filePath) {
        importFromFile(filePath, TSVDatasetReader::new);
    }

    private File userHome() {
        return new File(System.getProperty("user.home"));
    }

    void importFromFile(FileChooser.ExtensionFilter extensionFilter,
            Consumer<Path> importFromFile) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(userHome());
        fileChooser.setSelectedExtensionFilter(extensionFilter);

        Optional.ofNullable(fileChooser.showOpenDialog(getScene().getWindow()))
                .map(File::toPath)
                .ifPresent(importFromFile);
    }

    @FXML
    void importFromCSV() {
        importFromFile(new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                this::importFromCSV);
    }

    @FXML
    void importFromTSV() {
        importFromFile(new FileChooser.ExtensionFilter("TSV Files", "*.tsv"),
                this::importFromTSV);
    }

    void initializeAndShow(Chart chart, Dataset dataset) {
        new VariableBindingDialog(chart.getVariables(), dataset).showAndWait()
                .ifPresent(variableContext -> {
                    chart.setData(variableContext);

                    Parent chartWindow = new ChartWindow(chart);
                    Scene scene = new Scene(chartWindow, 1200, 800);

                    Stage chartStage = new Stage();
                    chartStage.setScene(scene);
                    chartStage.show();
                });
    }

    private void initializeAndShow(JavaScriptChart javaScriptChart) {
        getCurrentTable().map(TSVTable::getDataset).ifPresent(dataset -> {
            initializeAndShow(javaScriptChart, dataset);
        });
    }

    @FXML
    void createScatterplot() {
        initializeAndShow(new ScatterplotChart());
    }

    @FXML
    void createErrorEllipseChart() {
        initializeAndShow(new ErrorEllipseChart());
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
