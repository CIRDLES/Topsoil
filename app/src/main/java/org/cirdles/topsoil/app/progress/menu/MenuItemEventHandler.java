package org.cirdles.topsoil.app.progress.menu;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.dataset.SimpleDataset;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.plot.PlotWindow;
import org.cirdles.topsoil.app.plot.SimplePlotContext;
import org.cirdles.topsoil.app.plot.Variable;
import org.cirdles.topsoil.app.plot.VariableBindingDialog;
import org.cirdles.topsoil.app.plot.VariableBindingDialogPane;
import org.cirdles.topsoil.app.progress.TopsoilRawData;
import org.cirdles.topsoil.app.progress.isotope.IsotopeSelectionDialog;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.plot.PlotChoiceDialog;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.FileParser;
import org.cirdles.topsoil.app.progress.util.TopsoilFileChooser;
import org.cirdles.topsoil.app.progress.util.serialization.PlotInformation;
import org.cirdles.topsoil.app.progress.util.serialization.TopsoilSerializer;
import org.cirdles.topsoil.app.util.ErrorAlerter;
import org.cirdles.topsoil.app.util.IssueCreator;
import org.cirdles.topsoil.app.util.StandardGitHubIssueCreator;
import org.cirdles.topsoil.app.util.YesNoAlert;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

        TopsoilTable table;
        boolean valid = true;

        // select file
        File file = FileParser.openTableDialogue(StageHelper.getStages().get(0));
        if (file == null) {
            valid = false;
        }

        // select headers
        String [] headers = null;
        Boolean hasHeaders = null;
        if (valid) {
            hasHeaders = FileParser.containsHeaderDialogue();
            if (hasHeaders == null) {
                valid = false;
            } else if (hasHeaders) {
                headers = FileParser.parseHeaders(file);
            } else {
                headers = null;
            }
        }

        // select isotope flavor
        IsotopeType isotopeType = null;
        ObservableList<TopsoilDataEntry> data = null;
        if (valid) {
            isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());
            List<TopsoilDataEntry> entries = FileParser.parseFile(file, hasHeaders);
            data = FXCollections.observableList(entries);
        }

        // create table
        if (data == null ||  isotopeType == null) {
            table = null;
        } else {
            table = new TopsoilTable(headers, isotopeType, data.toArray(new TopsoilDataEntry[data.size()]));
            table.setTitle(file.getName().substring(0, file.getName().indexOf(".")));
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
        System.out.println("here");
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
            resultingTable = new TopsoilTable(table.getHeaders(), table.getIsotopeType(), new TopsoilDataEntry[]{});
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
            System.out.println(file);
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

        saveProjectFile(file, tabs);
        TopsoilSerializer.setCurrentProjectFile(file);
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
        TopsoilTable table = tabs.getSelectedTab().getTopsoilTable();

        // ask the user what kind of plot
        TopsoilPlotType plotType = new PlotChoiceDialog(table.getIsotopeType()).select();

        // variable binding dialog
        if (plotType != null) {

            /* TODO
             Once more than one plot is able to exist at a time, the code
             below (or some variation) should handle overwrites to similar
             plots belonging to the same table.
              */

//            boolean shouldGenerate = true;
//            for (PlotInformation plotInfo : table.getOpenPlots()) {
//                if (plotInfo.getTopsoilPlotType().getName().equals(plotType.getName())) {
//                    Alert plotOverwrite = new Alert(Alert.AlertType.CONFIRMATION,
//                                "Creating a new " +
//                                        plotType.getName() +
//                                        " for this table will overwrite the existing " +
//                                        plotType.getName() +
//                                        ". Are you sure you want to continue?",
//                                ButtonType.CANCEL,
//                                ButtonType.YES);
//                    Optional<ButtonType> response = plotOverwrite.showAndWait();
//                    if(response.get() == ButtonType.YES) {
//                        plotInfo.getStage().close();
//                    } else {
//                        shouldGenerate = false;
//                    }
//                    break;
//                }
//            }
//
//            this.generateNewPlot(plotType, table);

            // Check for open plots of the same type.
            List<Stage> stages = StageHelper.getStages();
            if (stages.size() > 1) {
                Stage stage = stages.get(1);
                Alert plotOverwrite = new Alert(Alert.AlertType.CONFIRMATION,
                        "Creating a new plot will overwrite the existing plot. " +
                                "Are you sure you want to continue?",
                        ButtonType.CANCEL,
                        ButtonType.YES);
                plotOverwrite.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.YES) {
                                for (PlotInformation plotInfo : tabs.getSelectedTab()
                                        .getTopsoilTable().getOpenPlots()) {
                                    try {
                                        ((JavaScriptPlot) plotInfo.getPlot()).finalize();
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                                stage.close();
                                generateNewPlot(plotType, table);
                            }
                        });
            } else {
                generateNewPlot(plotType, table);
            }
        }
    }

    /**
     * Generates a plot of plotType for the specified <tt>TopsoilTable</tt>.
     *
     * @param plotType  the TopsoilPlotType of the plot
     * @param table the TopsoilTable data to reference
     */
    private static void generateNewPlot(TopsoilPlotType plotType, TopsoilTable table) {
        List<Variable> variables = plotType.getVariables();
        SimpleDataset dataset = new SimpleDataset(table.getTitle(), new TopsoilRawData(table).getRawData());
        VariableBindingDialog variableBindingDialog = new VariableBindingDialog(variables, dataset);
        variableBindingDialog.showAndWait()
                .ifPresent(data -> {

                    Plot plot = plotType.getPlot();

                    plot.setData(data);
                    Parent plotWindow = new PlotWindow(
                            plot, plotType.getPropertiesPanel());

                    SimplePlotContext plotContext =
                            (SimplePlotContext)
                                    ((VariableBindingDialogPane)
                                            variableBindingDialog
                                                    .getDialogPane()).getPlotContext();

                    Scene scene = new Scene(plotWindow, 1200, 800);

                    Stage plotStage = new Stage();
                    plotStage.setTitle(plotType.getName() + ": " + table.getTitle());
                    plotStage.setOnCloseRequest(closeEvent -> table.removeOpenPlot(plotType));
                    plotStage.setScene(scene);
                    plotStage.show();

                    // Store plot information in TopsoilTable
                    PlotInformation plotInfo = new PlotInformation(plot, plotType);
                    plotInfo.setVariableBindings(plotContext.getBindings());
                    plotInfo.setStage(plotStage);
                    table.addOpenPlot(plotInfo);
                });
    }
}
