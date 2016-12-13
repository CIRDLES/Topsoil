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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.BuilderFactory;
import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.dataset.DatasetMapper;
import org.cirdles.topsoil.app.dataset.RawData;
import org.cirdles.topsoil.app.dataset.SimpleDataset;
import org.cirdles.topsoil.app.dataset.reader.CsvRawDataReader;
import org.cirdles.topsoil.app.dataset.reader.RawDataReader;
import org.cirdles.topsoil.app.dataset.reader.TsvRawDataReader;
import org.cirdles.topsoil.app.flyway.FlywayMigrateTask;
import org.cirdles.topsoil.app.menu.IsotopeSystemMenu;
import org.cirdles.topsoil.app.menu.PlotMenu;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.cirdles.topsoil.app.plot.PlotType;
import org.cirdles.topsoil.app.plot.PlotWindow;
import org.cirdles.topsoil.app.plot.Variable;
import org.cirdles.topsoil.app.plot.VariableBindingDialog;
import org.cirdles.topsoil.app.plot.Variables;
import org.cirdles.topsoil.app.table.EmptyTablePlaceholder;
import org.cirdles.topsoil.app.table.TsvTable;
import org.cirdles.topsoil.app.util.AboutDialog;
import org.cirdles.topsoil.app.util.ErrorAlerter;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.app.util.IssueCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

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
    private IsotopeSystemMenu isotopeSystemMenu;
    @FXML
    private PlotMenu plotMenu;
    @FXML
    private Menu datasetsMenu;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button saveDatasetButton;
    @FXML
    private Button emptyTableButton;

    private Provider<AboutDialog> aboutDialog;
    private ApplicationMetadata metadata;
    private BuilderFactory builderFactory;
    private DatasetMapper datasetMapper;
    private FlywayMigrateTask flywayMigrate;
    private IssueCreator issueCreator;
    private WebBrowser webBrowser;

    @Inject
    public TopsoilMainWindow(
            Provider<AboutDialog> aboutDialog,
            ApplicationMetadata metadata,
            BuilderFactory builderFactory,
            DatasetMapper datasetMapper,
            FlywayMigrateTask flywayMigrate,
            IssueCreator issueCreator,
            WebBrowser webBrowser) {

        super(self -> {
            self.aboutDialog = aboutDialog;
            self.metadata = metadata;
            self.builderFactory = builderFactory;
            self.datasetMapper = datasetMapper;
            self.flywayMigrate = flywayMigrate;
            self.issueCreator = issueCreator;
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

        isotopeSystemMenu.setPlotMenu(plotMenu);
        plotMenu.setTopsoilMainWindow(this);
    }

    private void loadOpenDatasets() {
        for (Dataset dataset : datasetMapper.getOpenDatasets()) {
            loadDataset(dataset);
        }
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    Optional<TsvTable> getCurrentTable() {
        return getCurrentTab().map(tab -> (TsvTable) tab.getContent());
    }

    @FXML
    Tab createTab() {
        Tab tab = new Tab("Untitled Data");

        TsvTable table = new TsvTable();
        table.setPlaceholder(new EmptyTablePlaceholder(table));
        tab.setContent(table);

        tabPane.getTabs().add(tab);

        // focus on new tab
        SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);

        setTabPaneEmpty(false);

        tab.setOnClosed(event -> {
            datasetMapper.closeDataset(table.getDataset());

            if (!getCurrentTable().isPresent()) {
                setTabPaneEmpty(true);
            }
        });

        return tab;
    }

    void setTabPaneEmpty(boolean tabPaneEmpty) {
        saveDatasetButton.setDisable(tabPaneEmpty);
        emptyTableButton.setDisable(tabPaneEmpty);
    }

    private static Predicate<Dataset> hasName(String name) {
        return dataset -> dataset.getName().equals(name);
    }

    @FXML
    void saveDataset() {
        TextInputDialog textInputDialog = new TextInputDialog();

        textInputDialog.setContentText("Dataset name:");
        textInputDialog.showAndWait().ifPresent(datasetName -> {
            getCurrentTable()
                    .map(TsvTable::getDataset)
                    .ifPresent(dataset -> {
                        boolean nameInUse = datasetMapper.getDatasets()
                                .stream()
                                .anyMatch(hasName(datasetName));

                        if (nameInUse) {
                            new ErrorAlerter().alert("A dataset named \"" +
                                    datasetName + "\" already exists.");
                        } else {
                            RawData rawData = new RawData(
                                    dataset.getFields(),
                                    dataset.getEntries());

                            datasetMapper.addDataset(
                                    new SimpleDataset(datasetName, rawData));
                        }
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
                tabPane.getSelectionModel().getSelectedItem());
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
        TsvTable table = createTable();

        Tools.yesNoPrompt("Does the selected file contain headers?", response -> {
            try {
                RawDataReader rawDataReader
                        = datasetReaderConstructor.apply(response);

                RawData rawData = rawDataReader.read(filePath);
                Dataset dataset = new SimpleDataset("Unnamed dataset", rawData);

                table.setDataset(dataset);
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

    void initializeAndShow(PlotType plotType, Dataset dataset) {
        List<Variable> variables = asList(
                Variables.X,
                Variables.SIGMA_X,
                Variables.Y,
                Variables.SIGMA_Y,
                Variables.RHO);

        new VariableBindingDialog(variables, dataset).showAndWait()
                .ifPresent(data -> {
                    Plot plot = plotType.newInstance();
                    plot.setData(data);

                    Parent plotWindow = new PlotWindow(
                            plot, plotType.newPropertiesPanel(plot));

                    Scene scene = new Scene(plotWindow, 1200, 800);

                    Stage plotStage = new Stage();
                    plotStage.setScene(scene);
                    plotStage.show();
                });
    }

    public void initializeAndShow(PlotType plotType) {
        Dataset data = getCurrentTable().get().getDataset();
        initializeAndShow(plotType, data);
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

    @FXML
    void openAboutDialog() {
        aboutDialog.get().run();
    }

    @FXML
    void reportIssue() {
        issueCreator.create();
    }

    @Override
    public BuilderFactory getBuilderFactory() {
        return builderFactory;
    }

}
