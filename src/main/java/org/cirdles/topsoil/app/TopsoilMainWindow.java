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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.inject.Inject;
import org.cirdles.topsoil.app.chart.ChartWindow;
import org.cirdles.topsoil.app.chart.VariableBindingDialog;
import org.cirdles.topsoil.app.dataset.reader.DatasetReader;
import org.cirdles.topsoil.app.utils.GetApplicationDirectoryOperation;
import org.cirdles.topsoil.app.dataset.reader.CSVDatasetReader;
import org.cirdles.topsoil.app.dataset.reader.TSVDatasetReader;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.cirdles.topsoil.chart.Chart;
import org.cirdles.topsoil.chart.JavaScriptChart;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.DatasetResource;
import org.cirdles.topsoil.dataset.DatasetResourceFactory;
import org.cirdles.topsoil.dataset.DatasetResourceLoader;

/**
 * FXML Controller class
 *
 * @author John Zeringue
 */
@ResourceBundle("Resources")
public class TopsoilMainWindow extends CustomVBox {

    private static final Logger LOGGER
            = Logger.getLogger(TopsoilMainWindow.class.getName());

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

    private DatasetResourceLoader datasetResourceLoader;
    private DatasetResourceFactory datasetResourceFactory;

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
    public void initialize() {
        datasetResourceLoader = new DatasetResourceLoader(DATASETS_DIRECTORY);
        datasetResourceFactory = new DatasetResourceFactory(DATASETS_DIRECTORY);

        datasetResourceLoader.getDatasetResources(DATASETS_DIRECTORY).stream()
                .filter(DatasetResource::isOpen)
                .forEach(datasetResource -> {
                    loadDataset(datasetResource);
                });

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
        dataTableTab.setOnCloseRequest(new CloseTabEventHandler(response -> saveDataTable()));

        TSVTable dataTable = new TSVTable();
        dataTable.setPlaceholder(new EmptyTablePlaceholder(dataTable));
        dataTableTab.setContent(dataTable);

        dataTableTabPane.getTabs().add(dataTableTab);

        dataTableTabPane.getSelectionModel().select(dataTableTab);

        return dataTableTab;
    }

    @FXML
    void saveDataTable() {
        getCurrentTable().ifPresent(table -> {
            TextInputDialog textInputDialog = new TextInputDialog();

            textInputDialog.setContentText("Data set name:");
            textInputDialog.showAndWait().ifPresent((String datasetName) -> {

                Path datasetPath = DATASETS_DIRECTORY
                        .resolve("open")
                        .resolve(datasetName + ".tsv");

                if (!Files.exists(datasetPath)
                        && !Files.exists(DATASETS_DIRECTORY.resolve("closed").resolve(datasetName + ".tsv"))) {

                    //TSVTable either have a dataset or a DatasetResource
                    Dataset data = table.getDataset()
                            .orElse(table.getDatasetResource().get().getDataset());

                    //Close the current dataset
                    table.getDatasetResource().ifPresent(DatasetResource::close);

                    table.getDataset().ifPresent(dataset -> {
                        dataset.setName(datasetName);
                        table.setDatasetResource(datasetResourceFactory.makeDatasetResource(dataset));
                    });

                    getCurrentTab().ifPresent(
                            tab -> tab.setText(datasetName + ".tsv")
                    );
                    reloadDatasetMenu();

                } else {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setHeaderText("This name already exists.");
                    alert.showAndWait();
                }
            });
        });
    }

    void saveDataTableOnImport(Dataset dataset, String fileName) {

        String datasetName = setDatasetName(fileName.split("\\.")[0]) + ".tsv";

        dataset.setName(datasetName);

        TSVTable dataTable = createTable();
        dataTable.setDatasetResource(datasetResourceFactory.makeDatasetResource(dataset));

        getCurrentTab().ifPresent(
                tab -> tab.setText(datasetName)
        );

        reloadDatasetMenu();
    }

    private String setDatasetName(String name) {

        StringBuilder fileName = new StringBuilder(name);

        Path openPath = DATASETS_DIRECTORY.resolve("open").resolve(fileName + ".tsv");
        Path closedPath = DATASETS_DIRECTORY.resolve("closed").resolve(fileName + ".tsv");

        while (Files.exists(openPath) || Files.exists(closedPath)) {
            fileName.append("-copy");
        }

        return fileName.toString();
    }

    void createDatasetMenuItem(DatasetResource datasetResource) {
        Menu datasetPopMenu = datasetResource.getDataset().getName()
                .map(Menu::new)
                .orElseGet(Menu::new);

        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.setOnAction(event -> {
            loadDataset(datasetResource);
        });

        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            deleteDataset(datasetResource);
        });

        datasetPopMenu.getItems().addAll(openMenuItem, deleteMenuItem);
        datasetsMenu.getItems().add(datasetPopMenu);
    }

    void reloadDatasetMenu() {
        // allows this method to be called multiple times in the same session
        datasetsMenu.getItems().clear();

        datasetResourceLoader.getDatasetResources(DATASETS_DIRECTORY).forEach(datasetResource -> {
            createDatasetMenuItem(datasetResource);
        });
    }

    void loadDataset(DatasetResource datasetResource) {
        Tab tab = createTab();
        Node content = tab.getContent();

        if (content instanceof TSVTable) {
            TSVTable table = (TSVTable) content;

            datasetResource.open();
            table.setDatasetResource(datasetResource);

            tab.setText(datasetResource.getDataset().getName().orElse("Untitled Data"));
        }

    }

    void deleteDataset(DatasetResource datasetResource) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.getButtonTypes().clear(); //Remove "Ok" and "Cancel" buttons
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to delete "
                + datasetResource.getDataset().getName().orElse("this dataset")
                + " ?");

        if (datasetResource.isOpen()) {
            alert.setContentText(datasetResource.getDataset().getName().orElse("This dataset")
                    + " is opened and will not be closed.");
        }

        alert.showAndWait().ifPresent(confirmation -> {
            if (confirmation.equals(ButtonType.YES)) {
                datasetResource.delete();
                reloadDatasetMenu();
            }
        });
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

        Tools.yesNoPrompt("Does the selected file contain headers?", response -> {
            try {
                DatasetReader datasetReader
                        = datasetReaderConstructor.apply(response);

                Dataset dataset = datasetReader.read(filePath);
                saveDataTableOnImport(dataset, filePath.getFileName().toString());

            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
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
        getCurrentTable()
                .flatMap(TSVTable::getDatasetResource)
                .ifPresent(datasetResource -> {
                    initializeAndShow(javaScriptChart, datasetResource.getDataset());
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
    void loadFromClipboard() {
        getCurrentTable().ifPresent(tsvTable -> tsvTable.pasteFromClipboard());
    }

    @FXML
    void emptyTable() {
        getCurrentTable().ifPresent(TSVTable::clear);
    }

}
