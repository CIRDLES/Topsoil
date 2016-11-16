package org.cirdles.topsoil.app.progress.menu;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import org.cirdles.topsoil.plot.Plot;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminmuldrow on 6/21/16.
 */
public class MenuItemEventHandler {

    /**
     * Handles importing tables from CSV / TSV files
     * @return Topsoil Table file
     * @throws IOException for invalid file selection
     */
    public static TopsoilTable handleTableFromFile() throws IOException {

        TopsoilTable table;
        boolean valid = true;

        // select file
        File file = FileParser.openTableDialogue(new Stage());
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
        }

        return table;
    }

    /**
     * Handle empty table creation
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

    /** Open default web browser and create a new GitHub issue with user specefications already supplied */
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
     * Creates a new .topsoil file for the current tabs and plots.
     *
     * @param tabs  the TopsoilTabPane from which to save tables
     */
    public static void handleNewProjectFile(TopsoilTabPane tabs) {
        File file = TopsoilFileChooser.getTopsoilFileChooser().showSaveDialog(new Stage());
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
     * Opens a .topsoil file.
     *
     * @param tabs  the TopsoilTabPane to which to add tables
     */
    public static void handleOpenProjectFile(TopsoilTabPane tabs) {
        File file = TopsoilFileChooser.getTopsoilFileChooser().showOpenDialog(new Stage());
        if (file != null) {
            String fileName = file.getName();
            String extension = fileName.substring(
                    fileName.lastIndexOf(".") + 1,
                    fileName.length());
            if (!extension.equals("topsoil")) {
                ErrorAlerter error = new ErrorAlerter();
                error.alert("Project must be a .topsoil file.");
            } else {
                tabs.getTabs().clear();
                List<Stage> stages = StageHelper.getStages();
                for (int index = stages.size() - 1; index > 0; index--) {
                    stages.get(index).close();
                }
                TopsoilSerializer.deserialize(file, tabs);
            }
        }
    }

    public static void handlePlotGenerationForSelectedTable(TopsoilTabPane tabs) {
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
                Optional<ButtonType> response = plotOverwrite.showAndWait();
                if (response.get() == ButtonType.YES) {
                    stage.close();
                    generateNewPlot(plotType, table);
                }
            } else {
                generateNewPlot(plotType, table);
            }
        }
    }

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
