package org.cirdles.topsoil.app.menu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.menu.command.ClearTableCommand;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilTable;

import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.io.IOException;

import static org.cirdles.topsoil.app.menu.MenuItemEventHandler.*;

/**
 * Created by sbunce on 5/30/2016.
 */
public class MainMenuBar extends MenuBar {

    // TODO Hella reorganize

    private MenuBar menuBar = new MenuBar();
    private ResourceExtractor resourceExtractor = new ResourceExtractor(MainMenuBar.class);
    private final String TOPSOIL_ABOUT_SCREEN_FXML_NAME = "topsoil-about-screen.fxml";

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
    private MenuItem genericSystemItem;

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
                    new ClearTableCommand(tabs.getSelectedTab().getTabContent().getTableView());
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

        importTable.setOnShown(event -> {
            if (Clipboard.getSystemClipboard().hasString()) {
                tableFromClipboardItem.setDisable(false);
            } else {
                tableFromClipboardItem.setDisable(true);
            }
        });

        //Creates Submenu for Isotype system selection
        Menu isoSystem = new Menu("Set Isotope System");
        uraniumLeadSystemItem = new MenuItem("UPb");
        uraniumThoriumSystemItem = new MenuItem("UTh");
        genericSystemItem = new MenuItem("Gen");
        isoSystem.getItems().addAll(
                uraniumLeadSystemItem,
                uraniumThoriumSystemItem,
                genericSystemItem);

        isoSystem.setOnShown(event -> {
            if (tabs.isEmpty()) {
                uraniumLeadSystemItem.setDisable(true);
                uraniumThoriumSystemItem.setDisable(true);
                genericSystemItem.setDisable(true);
            } else {
                uraniumLeadSystemItem.setDisable(false);
                uraniumThoriumSystemItem.setDisable(false);
                genericSystemItem.setDisable(false);
            }
        });

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
            if (!(tabs.getSelectedTab().getTopsoilTable().getIsotopeType() == IsotopeType.UPb)) {
                tabs.getSelectedTab().getTopsoilTable().setIsotopeType(IsotopeType.UPb);
            }
        });

        genericSystemItem.setOnAction(event -> {
            if (!tabs.getSelectedTab().getTopsoilTable().getIsotopeType().equals((IsotopeType.Generic))) {
                tabs.getSelectedTab().getTopsoilTable().setIsotopeType(IsotopeType.Generic);
            }
        });

        uraniumThoriumSystemItem.setOnAction(event -> {
            if (!tabs.getSelectedTab().getTopsoilTable().getIsotopeType().equals(IsotopeType.UTh)) {
                tabs.getSelectedTab().getTopsoilTable().setIsotopeType(IsotopeType.UTh);
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

        //Import Table from Clipboard
        tableFromClipboardItem.setOnAction(action -> {

            TopsoilTable table = null;

            try {
                table = handleTableFromClipboard();
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            if (table != null) {
                tabs.add(table);
            }
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

        // About
        aboutItem.setOnAction(event -> {
            try {
                Parent about = FXMLLoader
                        .load(resourceExtractor.extractResourceAsPath(TOPSOIL_ABOUT_SCREEN_FXML_NAME).toUri().toURL
                                ());
                Scene aboutScene = new Scene(about, 450, 600);
                Stage aboutWindow = new Stage(StageStyle.UNDECORATED);
                aboutWindow.setResizable(false);
                aboutWindow.setScene(aboutScene);

                aboutWindow.requestFocus();
                aboutWindow.initOwner(tabs.getScene().getWindow());
                aboutWindow.initModality(Modality.NONE);
                // Close window if main window gains focus.
//                tabs.getScene().getWindow().focusedProperty().addListener((observable, oldValue, newValue) -> {
//                    if (newValue) {
//                        aboutWindow.close();
//                    }
//                });
                aboutWindow.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
