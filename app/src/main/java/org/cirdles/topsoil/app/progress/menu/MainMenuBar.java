package org.cirdles.topsoil.app.progress.menu;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.dataset.SimpleDataset;
import org.cirdles.topsoil.app.plot.PlotWindow;
import org.cirdles.topsoil.app.plot.Variable;
import org.cirdles.topsoil.app.plot.VariableBindingDialog;
import org.cirdles.topsoil.app.progress.TopsoilRawData;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.plot.PlotDialog;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.util.ErrorAlerter;
import org.cirdles.topsoil.plot.Plot;

import java.io.IOException;
import java.util.List;

import static org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler.handleNewTable;
import static org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler.handleReportIssue;
import static org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler.handleTableFromFile;

/**
 * Created by sbunce on 5/30/2016.
 */
public class MainMenuBar extends MenuBar {

    private MenuBar menuBar = new MenuBar();

    // Project Menu
    private MenuItem newProjectItem;
    private MenuItem saveProjectItem;
    private MenuItem saveProjectAsItem;
    private MenuItem openProjectItem;
    private MenuItem mostRecentItem;
    private MenuItem closeProjectItem;

    // Table Menu
    private MenuItem newTableItem;
    private MenuItem saveTableItem;
    private MenuItem saveTableAsItem;
    // Import >
    private MenuItem tableFromFileItem;
    private MenuItem tableFromClipboardItem;
    // Isotope System >
    private MenuItem uraniumLeadSystemItem;
    private MenuItem uraniumThoriumSystemItem;

    // Help Menu
    private MenuItem reportIssueItem;
    private MenuItem aboutItem;

    //Passed the main scene and tabbed pane
    public MainMenuBar(TopsoilTabPane tabs) {
        super();
        this.initialize(tabs);
    }

    private void initialize(TopsoilTabPane tabs) {
        // Project Menu
        Menu projectMenu = new Menu("Project");
        newProjectItem = new MenuItem("New Project");
        saveProjectItem = new MenuItem("Save Project");
        saveProjectAsItem = new MenuItem("Save Project As");
        openProjectItem = new MenuItem("Open Project");
        closeProjectItem = new MenuItem("Close Project");
        mostRecentItem = new MenuItem("Most Recently Used");
        projectMenu.getItems()
                .addAll(newProjectItem,
                        saveProjectItem,
                        saveProjectAsItem,
                        openProjectItem,
                        closeProjectItem,
                        mostRecentItem);

        // Table Menu
        Menu tableMenu = new Menu("Table");
        newTableItem = new MenuItem("New Table");
        saveTableItem = new MenuItem("Save Table");
        saveTableAsItem = new MenuItem("Save Table As");

        newTableItem.setOnAction(event -> {
            TopsoilTable table = MenuItemEventHandler.handleNewTable();
            tabs.add(table);
        });

        //Saves the currently opened table
        saveTableItem = new MenuItem("Save Table");
        //Saves the currently opened table as a specified file
        saveTableAsItem = new MenuItem("Save Table As");

        //Creates Submenu for Imports
        Menu importTable = new Menu("Import Table");
        tableFromFileItem = new MenuItem("From File");
        tableFromClipboardItem = new MenuItem("From Clipboard");
        importTable.getItems().addAll(
                tableFromFileItem,
                tableFromClipboardItem);

        //Creates Submenu for Isotype system selection
        Menu isoSystem = new Menu("Set Isotope System");
        uraniumLeadSystemItem = new MenuItem("UPb");
        uraniumThoriumSystemItem = new MenuItem("UTh");
        isoSystem.getItems().addAll(
                uraniumLeadSystemItem,
                uraniumThoriumSystemItem);
        tableMenu.getItems()
                .addAll(newTableItem,
                        saveTableItem,
                        saveTableAsItem,
                        importTable,
                        isoSystem);

        uraniumLeadSystemItem.setOnAction(event -> {
            // if the table isn't already UPb
            if (!tabs.getSelectedTab().getTopsoilTable().getIsotopeType().equals(IsotopeType.UPb)) {
                tabs.getSelectedTab().getTopsoilTable().setIsotopeType(IsotopeType.UPb);
            }
        });

        // Plot Menu
        Menu plotMenu = new Menu("Plot");
        MenuItem generatePlotItem = new MenuItem("Generate Plot");
        plotMenu.getItems().add(generatePlotItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        reportIssueItem = new MenuItem("Report Issue");
        aboutItem = new MenuItem("About");
        helpMenu.getItems()
                .addAll(reportIssueItem,
                        aboutItem);

        // Add menus to menuBar
        menuBar.getMenus()
                .addAll(projectMenu,
                        tableMenu,
                        plotMenu,
                        helpMenu);

        // Import Table from File
        tableFromFileItem.setOnAction(event -> {

            TopsoilTable table = null;

            // get table from selections
            try {
                table = handleTableFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // display table
            if (table != null) {
                tabs.add(table);
            } else {
                ErrorAlerter alerter = new ErrorAlerter();
            }

        });

        // New, empty table
        newTableItem.setOnAction(event -> {

            // get new table
            TopsoilTable table = handleNewTable();

            // display new table
            tabs.add(table);
        });

        // Generate Plot
        generatePlotItem.setOnAction(event -> {
            if (tabs.getTabs().size() > 0) {

                TopsoilTable table = tabs.getSelectedTab().getTopsoilTable();

                // ask the user what kind of plot
                TopsoilPlotType plotType = new PlotDialog(table.getIsotopeType()).select();

                // variable binding dialog
                if (plotType != null) {
                    List<Variable> variables = plotType.getVariables();
                    SimpleDataset dataset = new SimpleDataset(table.getTitle(), new TopsoilRawData(table).getRawData());
                    new VariableBindingDialog(variables, dataset).showAndWait()
                            .ifPresent(data -> {
                                Plot plot = plotType.getPlot();
                                plot.setData(data);

                                Parent plotWindow = new PlotWindow(
                                        plot, plotType.getPropertiesPanel());

                                Scene scene = new Scene(plotWindow, 1200, 800);

                                Stage plotStage = new Stage();
                                plotStage.setScene(scene);
                                plotStage.show();
                            });
                }
            }
        });

        // Report Issue
        reportIssueItem.setOnAction(event -> {
            handleReportIssue();
        });
    }

    //Returns compatible type to be added to main window
    public MenuBar getMenuBar() {
        return menuBar;
    }
}
