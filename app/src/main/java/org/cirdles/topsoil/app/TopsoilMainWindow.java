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
import org.cirdles.topsoil.app.dataset.DatasetMapper;
import org.cirdles.topsoil.app.dataset.reader.CsvRawDataReader;
import org.cirdles.topsoil.app.dataset.reader.RawDataReader;
import org.cirdles.topsoil.app.dataset.reader.TsvRawDataReader;
import org.cirdles.topsoil.app.flyway.FlywayMigrateTask;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.cirdles.topsoil.chart.Chart;
import org.cirdles.topsoil.chart.JavaScriptChart;
import org.cirdles.topsoil.chart.standard.UncertaintyEllipseChart;
import org.cirdles.topsoil.chart.standard.ScatterplotChart;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.RawData;
import org.cirdles.topsoil.dataset.SimpleDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cirdles.topsoil.app.browse.WebBrowser;

/**
 * FXML Controller class
 *
 * @author John Zeringue
 */
@ResourceBundle("Resources")
public class TopsoilMainWindow extends CustomVBox<TopsoilMainWindow> {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(TopsoilMainWindow.class);

    private static final String TOPSOIL_ISSUES_URI_STRING = "https://github.com/CIRDLES/Topsoil/issues";
    private static final String TOPSOIL_WIKI_URI_STRING = "https://github.com/CIRDLES/Topsoil/wiki#topsoil";

    @FXML
    Menu chartsMenu;
    @FXML
    Menu datasetsMenu;
    @FXML
    TabPane dataTableTabPane;
    @FXML
    Button saveDataTableButton;
    @FXML
    Button emptyTableButton;

    private ApplicationMetadata metadata;
    private DatasetMapper datasetMapper;
    private FlywayMigrateTask flywayMigrate;
    private WebBrowser webBrowser;

    @Inject
    public TopsoilMainWindow(
            ApplicationMetadata metadata,
            DatasetMapper datasetMapper,
            FlywayMigrateTask flywayMigrate,
            WebBrowser webBrowser) {
        super(self -> {
            self.metadata = metadata;
            self.datasetMapper = datasetMapper;
            self.flywayMigrate = flywayMigrate;
            self.webBrowser = webBrowser;
        });
    }

    @FXML
    private void initialize() {
        flywayMigrate.setOnSucceeded(event -> {
            reloadDatasetMenu();
            loadOpenDatasets();
        });
        new Thread(flywayMigrate).start();

        // something like "Topsoil [0.3.4]"
        String nameAndVersion = String.format("%s [%s]",
                metadata.getName(),
                metadata.getVersion());

        setWindowTitle(nameAndVersion);
    }

    private void loadOpenDatasets() {
        for (Dataset dataset : datasetMapper.getOpenDatasets()) {
            loadDataset(dataset);
        }
    }

    Optional<TsvTable> getCurrentTable() {
        return getCurrentTab().map(tab -> (TsvTable) tab.getContent());
    }

    @FXML
    void createDataTable() {
        createTab();
    }

    Tab createTab() {
        Tab dataTableTab = new Tab("Untitled Data");

        TsvTable dataTable = new TsvTable();
        dataTable.setPlaceholder(new EmptyTablePlaceholder(dataTable));
        dataTableTab.setContent(dataTable);

        dataTableTabPane.getTabs().add(dataTableTab);

        // focus on new tab
        SelectionModel<Tab> selectionModel = dataTableTabPane.getSelectionModel();
        selectionModel.select(dataTableTab);

        setDataTableTabPaneEmpty(false);

        dataTableTab.setOnClosed(event -> {
            datasetMapper.closeDataset(dataTable.getDataset());

            if (!getCurrentTable().isPresent()) {
                setDataTableTabPaneEmpty(true);
            }
        });

        return dataTableTab;
    }

    void setDataTableTabPaneEmpty(boolean dataTableTabPaneEmpty) {
        saveDataTableButton.setDisable(dataTableTabPaneEmpty);
        emptyTableButton.setDisable(dataTableTabPaneEmpty);
    }

    @FXML
    void saveDataTable() {
        TextInputDialog textInputDialog = new TextInputDialog();

        textInputDialog.setContentText("Dataset name:");
        textInputDialog.showAndWait().ifPresent(datasetName -> {
            getCurrentTable()
                    .map(TsvTable::getDataset)
                    .ifPresent(dataset -> {
                        RawData rawData = new RawData(
                                dataset.getFields(),
                                dataset.getEntries());

                        datasetMapper.addDataset(
                                new SimpleDataset(datasetName, rawData));
                    });

            getCurrentTab().ifPresent(tab -> tab.setText(datasetName));
        });

        // reload
        reloadDatasetMenu();
    }

    void createDatasetSubmenu(Dataset dataset) {
        Menu datasetSubmenu = new Menu(dataset.getName());

        MenuItem loadDatasetMenuItem = new MenuItem("Load dataset");
        loadDatasetMenuItem.setOnAction(event -> {
            loadDataset(dataset);
        });

        MenuItem deleteDatasetMenuItem = new MenuItem("Delete dataset");
        deleteDatasetMenuItem.setOnAction(event -> {
            String confirmationMessage = String.format(
                    "Are you sure that you want to delete \"%s\"?",
                    dataset.getName());

            new Alert(Alert.AlertType.CONFIRMATION, confirmationMessage)
                    .showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        datasetsMenu.getItems().remove(datasetSubmenu);
                        datasetMapper.removeDataset(dataset);
                    });
        });

        datasetSubmenu.getItems().addAll(
                loadDatasetMenuItem,
                deleteDatasetMenuItem);

        datasetsMenu.getItems().add(datasetSubmenu);
    }

    void reloadDatasetMenu() {
        // allows this method to be called multiple times in the same session
        datasetsMenu.getItems().clear();

        datasetMapper.getDatasets().forEach(this::createDatasetSubmenu);
    }

    void loadDataset(Dataset dataset) {
        loadDataset(dataset, createTab());
    }

    void loadDataset(Dataset dataset, Tab tab) {
        Node content = tab.getContent();

        if (content instanceof TsvTable) {
            TsvTable table = (TsvTable) content;
            table.setDataset(dataset);
            tab.setText(dataset.getName());
            datasetMapper.openDataset(dataset);
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

    public TsvTable createTable() {
        return (TsvTable) createTab().getContent();
    }

    void importFromFile(
            Path filePath,
            Function<Boolean, RawDataReader> datasetReaderConstructor) {
        TsvTable dataTable = createTable();

        Tools.yesNoPrompt("Does the selected file contain headers?", response -> {
            try {
                RawDataReader rawDataReader
                        = datasetReaderConstructor.apply(response);

                RawData rawData = rawDataReader.read(filePath);
                Dataset dataset = new SimpleDataset("Unnamed dataset", rawData);

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
        importFromFile(filePath, CsvRawDataReader::new);
    }

    void importFromTSV(Path filePath) {
        importFromFile(filePath, TsvRawDataReader::new);
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
        getCurrentTable().map(TsvTable::getDataset).ifPresent(dataset -> {
            initializeAndShow(javaScriptChart, dataset);
        });
    }

    @FXML
    void createScatterplot() {
        initializeAndShow(new ScatterplotChart());
    }

    @FXML
    void createUncertaintyEllipseChart() {
        initializeAndShow(new UncertaintyEllipseChart());
    }

    @FXML
    void openDocumentationLink() {
        webBrowser.browse(TOPSOIL_WIKI_URI_STRING);
    }

    @FXML
    void openIssuesLink() {
        webBrowser.browse(TOPSOIL_ISSUES_URI_STRING);
    }

    @FXML
    void emptyTable() {
        getCurrentTable().ifPresent(TsvTable::clear);
    }

}
