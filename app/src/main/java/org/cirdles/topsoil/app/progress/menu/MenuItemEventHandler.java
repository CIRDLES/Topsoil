package org.cirdles.topsoil.app.progress.menu;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.plot.*;
import org.cirdles.topsoil.app.progress.dataset.NumberDataset;
import org.cirdles.topsoil.app.progress.isotope.IsotopeSelectionDialog;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.plot.PlotChoiceDialog;
import org.cirdles.topsoil.app.progress.plot.PlotPropertiesPanelController;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTab;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.table.TopsoilTableController;
import org.cirdles.topsoil.app.progress.util.FileParser;
import org.cirdles.topsoil.app.progress.util.TopsoilFileChooser;
import org.cirdles.topsoil.app.progress.util.serialization.PlotInformation;
import org.cirdles.topsoil.app.progress.util.serialization.TopsoilSerializer;
import org.cirdles.topsoil.app.util.ErrorAlerter;
import org.cirdles.topsoil.app.util.IssueCreator;
import org.cirdles.topsoil.app.util.StandardGitHubIssueCreator;
import org.cirdles.topsoil.app.util.YesNoAlert;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.base.BasePlot;
import org.cirdles.topsoil.plot.scatter.ScatterPlot;
import org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlot;
import org.cirdles.topsoil.plot.uth.evolution.EvolutionPlot;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A class containing a set of methods for handling actions for
 * <tt>MenuItems</tt> in the <tt>MainMenuBar</tt>.
 *
 * @author benjaminmuldrow
 * @see MainMenuBar
 */
public class MenuItemEventHandler {

    /**
     * Handles importing tables from CSV / TSV files
     *
     * @return Topsoil Table file
     * @throws IOException for invalid file selection
     */
    public static TopsoilTable handleTableFromFile() throws IOException {

        TopsoilTable table = null;

        // select file
        File file = FileParser.openTableDialogue(StageHelper.getStages().get(0));

        if (file != null && FileParser.isSupportedTableFile(file)) {

            // select headers
            String[] headers = null;
            Boolean hasHeaders;

            // TODO For now, the user must have headers in the file. In the future, they can specify.
//            hasHeaders = FileParser.containsHeaderDialogue();
            hasHeaders = true;

            // hasHeaders would only be null if the user clicked "Cancel".
            if (hasHeaders != null) {
                if (hasHeaders) {
                    headers = FileParser.parseHeaders(file);
                }

                // select isotope flavor
                IsotopeType isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());

                // isotopeType would only be null if the user clicked "Cancel".
                if (isotopeType != null) {
                    List<TopsoilDataEntry> entries = FileParser.parseFile(file, hasHeaders);

                    // create table
                    if (entries == null) {
                        table = null;
                    } else {
                        ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);
                        table = new TopsoilTable(headers, isotopeType, data.toArray(new TopsoilDataEntry[data.size()]));
                        table.setTitle(file.getName().substring(0, file.getName().indexOf(".")));
                    }
                }
            }
        }

        return table;
    }

    public static TopsoilTable handleTableFromClipboard() {

        TopsoilTable table = null;
        String content = Clipboard.getSystemClipboard().getString();

        String delim = FileParser.getDelimiter(content);

        if (delim != null) {

            String[] headers = null;
            Boolean hasHeaders;

            // TODO For now, the user must have headers in the file. In the future, they can specify.
//            hasHeaders = FileParser.containsHeaderDialogue();
            hasHeaders = true;

            if (hasHeaders != null) {
                if (hasHeaders) {
                    headers = FileParser.parseHeaders(content, delim);
                }

                // select isotope flavor
                IsotopeType isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());

                if (isotopeType != null) {
                    List<TopsoilDataEntry> entries = FileParser.parseClipboard(hasHeaders, delim);

                    // create table
                    if (entries == null) {
                        table = null;
                    } else {
                        ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);
                        table = new TopsoilTable(headers, isotopeType, data.toArray(new TopsoilDataEntry[data.size()]));
                    }
                }
            }
        }
        return table;
    }

    /**
     * Handle empty table creation
     *
     * @return new, empty table
     */
    public static TopsoilTable handleNewTable() {

        TopsoilTable table;

        // select isotope flavor
        IsotopeType isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());

        // create empty table
        if (isotopeType == null) {
            table = null;
        } else {
            table = new TopsoilTable(null, isotopeType, new TopsoilDataEntry[]{});
        }

        return table;
    }

    /**
     * Open default web browser and create a new GitHub issue with user
     * specifications already supplied
     * */
    public static void handleReportIssue() {
        IssueCreator issueCreator = new StandardGitHubIssueCreator(
                new TopsoilMetadata(),
                System.getProperties(),
                new DesktopWebBrowser(Desktop.getDesktop(), new ErrorAlerter()),
                new StringBuilder()
        );
        issueCreator.create();
    }

    /**
     * Clear a table of all data
     *
     * @param table to clear
     * @return updated table
     */
    public static TopsoilTable handleClearTable(TopsoilTable table) {

        // alert user for confirmation
        Alert confirmAlert = new YesNoAlert("Are you sure you want to clear the table?");
        Optional<ButtonType> response = confirmAlert.showAndWait();
        TopsoilTable resultingTable = table;

        // get user confirmation
        if (response.isPresent()
                && response.get() == ButtonType.YES) {
            resultingTable = new TopsoilTable(table.getColumnNames(), table.getIsotopeType(), new TopsoilDataEntry[]{});
        }

        return resultingTable;
    }

    /**
     * Closes all open tabs in the <tt>TopsoilTabPane</tt>, as well as any
     * open plots. Used when a project is loaded, or when one is closed.
     *
     * @param tabs  the active TopsoilTabPane
     */
    private static void closeAllTabsAndPlots(TopsoilTabPane tabs) {
        tabs.getTabs().clear();
        List<Stage> stages = StageHelper.getStages();
        for (int index = stages.size() - 1; index > 0; index--) {
            stages.get(index).close();
        }
    }

    /**
     * Opens a .topsoil project <tt>File</tt>. If any tabs or plots are open,
     * they are closed and replaced with the project's information.
     *
     * @param tabs  the TopsoilTabPane to which to add tables
     */
    public static void handleOpenProjectFile(TopsoilTabPane tabs) {
        if (!tabs.isEmpty()) {
            Alert verification = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Opening a Topsoil project will replace your current tables. Continue?",
                    ButtonType.CANCEL,
                    ButtonType.YES
            );
            verification.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    File file = TopsoilFileChooser.getTopsoilOpenFileChooser().showOpenDialog(StageHelper.getStages().get(0));
                    openProjectFile(tabs, file);
                }
            });
        } else {
            File file = TopsoilFileChooser.getTopsoilOpenFileChooser().showOpenDialog(StageHelper.getStages().get(0));
            openProjectFile(tabs, file);
        }
    }

    /**
     * Opens a .topsoil <tt>File</tt>.
     *
     * @param tabs  the active TopsoilTabPane.
     */
    public static void openProjectFile(TopsoilTabPane tabs, File file) {
        if (file != null) {
            String fileName = file.getName();
            String extension = fileName.substring(
                    fileName.lastIndexOf(".") + 1,
                    fileName.length());
            if (!extension.equals("topsoil")) {
                ErrorAlerter error = new ErrorAlerter();
                error.alert("Project must be a .topsoil file.");
            } else {
                closeAllTabsAndPlots(tabs);
                TopsoilSerializer.deserialize(file, tabs);
            }
        }
    }

    /**
     * Saves changes to an open .topsoil project.
     * <p>
     *     If the project is open, but the file can't be found (e.g. if the
     *     file was deleted externally while the project was open), then the
     *     user is forced to "Save As".
     * </p>
     *
     * @param tabs  the active TopsoilTabPane
     */
    public static void handleSaveProjectFile(TopsoilTabPane tabs) {
        if (TopsoilSerializer.isProjectOpen()) {
            if (TopsoilSerializer.projectFileExists()) {
                File file = TopsoilSerializer.getCurrentProjectFile();
                saveProjectFile(file, tabs);
            } else {
                handleSaveAsProjectFile(tabs);
            }
        }
    }

    /**
     * Saves an open .topsoil project.
     *
     * @param file  the open .topsoil project File
     * @param tabs  the active TopsoilTabPane
     */
    private static void saveProjectFile(File file, TopsoilTabPane tabs) {
        if (file != null) {
            String fileName = file.getName();
            String extension = fileName.substring(
                    fileName.lastIndexOf(".") + 1,
                    fileName.length());
            if (!extension.equals("topsoil")) {
                ErrorAlerter error = new ErrorAlerter();
                error.alert("Project must be a .topsoil file.");
            } else {
                TopsoilSerializer.serialize(file, tabs);
            }
        }
    }

    /**
     * Creates a new .topsoil file for the current tabs and plots.
     *
     * @param tabs  the TopsoilTabPane from which to save tables
     */
    public static void handleSaveAsProjectFile(TopsoilTabPane tabs) {
        FileChooser fileChooser =  TopsoilFileChooser.getTopsoilSaveFileChooser();
        File file = fileChooser.showSaveDialog(StageHelper.getStages().get(0));

        if (file != null) {
            saveProjectFile(file, tabs);
            TopsoilSerializer.setCurrentProjectFile(file);
        }
    }

    /**
     * Closes the project file, and closes all open tabs and plots.
     *
     * @param tabs  the active TopsoilTabPane
     */
    public static void handleCloseProjectFile(TopsoilTabPane tabs) {
        Alert saveAndCloseAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you want to save your changes?",
                ButtonType.CANCEL, ButtonType.NO, ButtonType.YES);
        saveAndCloseAlert.showAndWait().ifPresent(response -> {
            if (response != ButtonType.CANCEL) {
                if (response == ButtonType.YES) {
                    MenuItemEventHandler.handleSaveProjectFile(tabs);
                }
                closeAllTabsAndPlots(tabs);
                TopsoilSerializer.closeProjectFile();
            }
        });
    }

    /**
     * Generates a plot for the selected <tt>TopsoilTab</tt>.
     *
     * @param tabs  the active TopsoilTabPane
     */
    public static void handlePlotGenerationForSelectedTab(TopsoilTabPane tabs) {
        TopsoilTableController tableController = tabs.getSelectedTab().getTableController();

        // ask the user what kind of plot
        TopsoilPlotType plotType = new PlotChoiceDialog(tableController.getTable().getIsotopeType()).select();

        // variable binding dialog
        if (plotType != null) {

            // Check for open plots.
            List<Stage> stages = StageHelper.getStages();
            if (stages.size() > 1) {
                Alert plotOverwrite = new Alert(Alert.AlertType.CONFIRMATION,
                        "Creating a new plot will overwrite the existing plot. " +
                                "Are you sure you want to continue?",
                        ButtonType.CANCEL,
                        ButtonType.YES);
                plotOverwrite.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.YES) {
                                for (TopsoilTab tab : tabs.getTopsoilTabs()) {
                                    for (PlotInformation plotInfo : tab.getTopsoilTable().getOpenPlots()) {
                                        tab.getTopsoilTable().removeOpenPlot(plotInfo.getTopsoilPlotType());
                                        plotInfo.getStage().close();
                                    }
                                }
                                PlotContext plotContext = generatePlotContext(tableController);
                                generatePlot(tableController, plotType, plotContext);
                            }
                        });
            } else {
                PlotContext plotContext = generatePlotContext(tableController);
                generatePlot(tableController, plotType, plotContext);
            }
        }
    }

    public static void handlePlotGenerationFromFile(TopsoilTableController tableController, TopsoilPlotType plotType,
                                                    PlotContext plotContext) {
        generatePlot(tableController, plotType, plotContext);
    }

    private static void generatePlot(TopsoilTableController tableController, TopsoilPlotType plotType,
                                     PlotContext plotContext) {

        List<Map<String, Object>> data = plotContext.getData();

        PlotPropertiesPanelController propertiesPanel = tableController.getTabContent().getPlotPropertiesPanelController();
        Map<String, Object> plotProperties = propertiesPanel.getProperties();

        Plot plot;
        switch (plotType) {
            case BASE_PLOT:
                plot = new BasePlot();
                break;
            case SCATTER_PLOT:
                plot = new ScatterPlot();
                break;
            case UNCERTAINTY_ELLIPSE_PLOT:
                plot = new UncertaintyEllipsePlot();
                break;
            case EVOLUTION_PLOT:
                plot = new EvolutionPlot();
                break;
            default:
                plot = plotType.getPlot();
                break;
        }

        plot.setData(data);
        plot.setProperties(plotProperties);
        propertiesPanel.setPlot(plot);

        // Create Plot Scene
        Parent plotWindow = new PlotWindow(plot);
        Scene scene = new Scene(plotWindow, 800, 600);

        // Create Plot Stage
        Stage plotStage = new Stage();
        plotStage.setScene(scene);
        plotStage.setResizable(false);

        // Connect Plot with PropertiesPanel
        plotStage.setTitle(plotType.getName() + ": " + propertiesPanel.getTitle());
        propertiesPanel.titleProperty().addListener(c -> {
            if (propertiesPanel.getTitle().length() > 0) {
                plotStage.setTitle(plotType.getName() + ": " + propertiesPanel.getTitle());
            } else {
                plotStage.setTitle(plotType.getName());
            }
        });

        plotStage.setOnCloseRequest(closeEvent -> {
            tableController.getTable().removeOpenPlot(plotType);
            propertiesPanel.removePlot();
        });

        // Show Plot
        plotStage.show();

        // Store plot information in TopsoilTable
        PlotInformation plotInfo = new PlotInformation(plot, plotType, propertiesPanel.getProperties(), plotContext, plotStage);
        plotInfo.setVariableBindings(plotContext.getBindings());
        tableController.getTable().addOpenPlot(plotInfo);
    }

    private static PlotContext generatePlotContext(TopsoilTableController tableController) {
        NumberDataset dataset = tableController.getDataset();
        SimplePlotContext plotContext = new SimplePlotContext(dataset);

        // Bind variables
        Variable<Number> variable;
        VariableFormat<Number> format;
        for (int i = 0; i < dataset.getFields().size(); i++) {
            variable = Variables.VARIABLE_LIST.get(i);

            if (variable == Variables.SIGMA_X) {
                format = tableController.getTabContent().getXUncertainty();
            } else if (variable == Variables.SIGMA_Y) {
                format = tableController.getTabContent().getYUncertainty();
            } else {
                format = variable.getFormats().size() > 0 ? variable.getFormats().get(0) : null;
            }

            plotContext.addBinding(variable, dataset.getFields().get(i), format);
        }

        return plotContext;
    }
}
