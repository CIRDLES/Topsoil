package org.cirdles.topsoil.app.progress.menu;

import javafx.scene.control.*;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;

import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.io.IOException;

import static org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler.handleNewTable;
import static org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler.handleReportIssue;
import static org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler.handleTableFromFile;

/**
 * Created by sbunce on 5/30/2016.
 */
public class MainMenuBar extends MenuBar {

    private MenuBar menuBar = new MenuBar();

    // Edit Menu
    private MenuItem undoItem;
    private MenuItem redoItem;

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
        // Edit Menu
        Menu editMenu = new Menu("Edit");
        undoItem = new MenuItem("Undo");
        redoItem = new MenuItem("Redo");

        undoItem.setOnAction(event -> {
            if (!tabs.isEmpty()) {
                tabs.getSelectedTab().undo();
            }
        });
        redoItem.setOnAction(event -> {
            if (!tabs.isEmpty()) {
                tabs.getSelectedTab().redo();
            }
        });

        editMenu.getItems()
                .addAll(undoItem,
                        redoItem);

        // Project Menu
        Menu projectMenu = initializeProjectMenuItems(tabs);

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
                .addAll(editMenu,
                        projectMenu,
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
            if (!tabs.isEmpty()) {
                MenuItemEventHandler.handlePlotGenerationForSelectedTable(tabs);
            }
        });

        // Report Issue
        reportIssueItem.setOnAction(event -> {
            handleReportIssue();
        });
    }

    private Menu initializeProjectMenuItems(TopsoilTabPane tabs) {
        Menu projectMenu = new Menu("Project");
        newProjectItem = new MenuItem("New Project");
        saveProjectItem = new MenuItem("Save Project");
        saveProjectAsItem = new MenuItem("Save Project As");
        openProjectItem = new MenuItem("Open Project");
        closeProjectItem = new MenuItem("Close Project");
        mostRecentItem = new MenuItem("Most Recently Used");

        newProjectItem.setOnAction(event -> {
            if (!tabs.isEmpty()) {
                MenuItemEventHandler.handleNewProjectFile(tabs);
            }
        });

        openProjectItem.setOnAction(event -> {
            if (!tabs.isEmpty()) {
                Alert verification = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Opening a Topsoil project will replace your current tables. Continue?",
                        ButtonType.CANCEL,
                        ButtonType.YES
                );
                verification.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        MenuItemEventHandler.handleOpenProjectFile(tabs);
                    }
                });
            } else {
                MenuItemEventHandler.handleOpenProjectFile(tabs);
            }

        });

        projectMenu.getItems()
                .addAll(newProjectItem,
                        saveProjectItem,
                        saveProjectAsItem,
                        openProjectItem,
                        closeProjectItem,
                        mostRecentItem);

        return projectMenu;
    }

    //Returns compatible type to be added to main window
    public MenuBar getMenuBar() {
        return menuBar;
    }
}
