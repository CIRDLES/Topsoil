package org.cirdles.topsoil.app.progress.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;

import org.cirdles.topsoil.app.progress.util.serialization.TopsoilSerializer;
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

    // File Menu
//    private MenuItem newProjectItem;
    private MenuItem saveProjectItem;
    private MenuItem saveProjectAsItem;
    private MenuItem openProjectItem;
    //    private MenuItem mostRecentItem;
    private MenuItem closeProjectItem;

    // Edit Menu
    private MenuItem undoItem;
    private MenuItem redoItem;

    // Table Menu
    private MenuItem newTableItem;
    private MenuItem saveTableItem;
    private MenuItem saveTableAsItem;
    private MenuItem clearTableItem;

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

        // File Menu
        Menu fileMenu = setFileMenuItems(tabs);

        // Edit Menu
        Menu editMenu = new Menu("Edit");
        undoItem = new MenuItem("Undo \"\"");
        redoItem = new MenuItem("Redo \"\"");

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

        editMenu.setOnShown(event -> {
            if (tabs.isEmpty()) {
                undoItem.setText("Undo \"\"");
                undoItem.setDisable(true);
                redoItem.setText("Redo \"\"");
                redoItem.setDisable(true);
            } else {
                if (tabs.getSelectedTab().getLastUndoMessage().equals("")) {
                    undoItem.setText("Undo \"\"");
                    undoItem.setDisable(true);
                } else {
                    undoItem.setText(String.format("Undo \"%s\"",
                            tabs.getSelectedTab().getLastUndoMessage()));
                    undoItem.setDisable(false);
                }

                if (tabs.getSelectedTab().getLastRedoMessage().equals("")) {
                    redoItem.setText("Redo \"\"");
                    redoItem.setDisable(true);
                } else {
                    redoItem.setText(String.format("Redo \"%s\"",
                            tabs.getSelectedTab().getLastRedoMessage()));
                    redoItem.setDisable(false);
                }
            }
        });

        // Table Menu
        Menu tableMenu = new Menu("Table");
        newTableItem = new MenuItem("New Table");
        saveTableItem = new MenuItem("Save Table");
        saveTableAsItem = new MenuItem("Save Table As");
        clearTableItem = new MenuItem("Clear Table");

        newTableItem.setOnAction(event -> {
            TopsoilTable table = MenuItemEventHandler.handleNewTable();
            tabs.add(table);
        });

        //Saves the currently opened table
        saveTableItem = new MenuItem("Save Table");
        //Saves the currently opened table as a specified file
        saveTableAsItem = new MenuItem("Save Table As");

        clearTableItem.setOnAction(action -> {
            // clear table and add an empty row
            ClearTableCommand clearTableCommand =
                    new ClearTableCommand(tabs.getSelectedTab()
                                              .getTopsoilTable().getTable());
            clearTableCommand.execute();
            tabs.getSelectedTab().addUndo(clearTableCommand);
        });

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
                        new SeparatorMenuItem(),
                        saveTableItem,
                        saveTableAsItem,
                        new SeparatorMenuItem(),
                        clearTableItem,
                        new SeparatorMenuItem(),
                        importTable,
                        isoSystem);

        tableMenu.setOnShown(event -> {
            if (tabs.isEmpty()) {
                clearTableItem.setDisable(true);
            } else {
                if (!tabs.getSelectedTab().getTopsoilTable().isCleared()) {
                    clearTableItem.setDisable(false);
                } else {
                    clearTableItem.setDisable(true);
                }
            }
        });

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

        plotMenu.setOnShown(event -> {
            if (tabs.isEmpty()) {
                generatePlotItem.setDisable(true);
            } else {
                generatePlotItem.setDisable(false);
            }
        });

        // Help Menu
        Menu helpMenu = new Menu("Help");
        reportIssueItem = new MenuItem("Report Issue");
        aboutItem = new MenuItem("About");
        helpMenu.getItems()
                .addAll(reportIssueItem,
                        aboutItem);

        // Add menus to menuBar
        menuBar.getMenus()
                .addAll(fileMenu,
                        editMenu,
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
                MenuItemEventHandler.handlePlotGenerationForSelectedTab(tabs);
            }
        });

        // Report Issue
        reportIssueItem.setOnAction(event -> {
            handleReportIssue();
        });
    }

    private Menu setFileMenuItems(TopsoilTabPane tabs) {
        Menu fileMenu = new Menu("File");
//        newProjectItem = new MenuItem("New Project");
        saveProjectItem = new MenuItem("Save Project");
        saveProjectAsItem = new MenuItem("Save Project As");
        openProjectItem = new MenuItem("Open Project");
        closeProjectItem = new MenuItem("Close Project");
//        mostRecentItem = new MenuItem("Most Recently Used");

        openProjectItem.setOnAction(event -> MenuItemEventHandler
                .handleOpenProjectFile(tabs));

        saveProjectItem.setOnAction(event -> MenuItemEventHandler.handleSaveProjectFile(tabs));

        saveProjectAsItem.setOnAction(event -> {
            if (!tabs.isEmpty()) {
                MenuItemEventHandler.handleSaveAsProjectFile(tabs);
            }
        });

        closeProjectItem.setOnAction(event -> MenuItemEventHandler.handleCloseProjectFile(tabs));

        fileMenu.getItems()
                .addAll(
//                        newProjectItem,
                        openProjectItem,
                        closeProjectItem,
                        new SeparatorMenuItem(),
                        saveProjectItem,
                        saveProjectAsItem
//                        , mostRecentItem
                );

        fileMenu.setOnShown(event -> {
            saveProjectItem.setDisable(!TopsoilSerializer.isProjectOpen());
            saveProjectAsItem.setDisable(tabs.isEmpty());
            closeProjectItem.setDisable(!TopsoilSerializer.isProjectOpen());
        });

        return fileMenu;
    }

    //Returns compatible type to be added to main window
    public MenuBar getMenuBar() {
        return menuBar;
    }
}
