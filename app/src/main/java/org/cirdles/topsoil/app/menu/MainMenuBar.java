package org.cirdles.topsoil.app.menu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.TopsoilAboutScreen;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.menu.command.ClearTableCommand;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilDataTable;

import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

import java.io.IOException;
import javafx.application.Platform;

import static org.cirdles.topsoil.app.menu.MenuItemEventHandler.*;

/**
 * A custom {@code MenuBar} for the Topsoil {@link MainWindow}.
 *
 * @author sbunce
 * @see MenuBar
 * @see MainWindow
 */
public class MainMenuBar extends MenuBar {

    //***********************
    // Attributes
    //***********************

    /**
     * An instance of this menu bar as a {@code MenuBar}.
     */
    private MenuBar menuBar = new MenuBar();

    /**
     * A {@code ResourceExtractor} for extracting necessary resources. Used by CIRDLES projects.
     */
    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(MainMenuBar.class);

    /**
     * The {@code String} path to the {@code .fxml} file for the {@link TopsoilAboutScreen}
     */
    private final String TOPSOIL_ABOUT_SCREEN_FXML_NAME = "../topsoil-about-screen.fxml";

    // File Menu
    /**
     * When clicked, opens a new .topsoil project file.
     */
    private MenuItem openProjectItem;

    /**
     * When clicked, saves the current open project file.
     */
    private MenuItem saveProjectItem;

    /**
     * When clicked, saves the current open project file to a specific path.
     */
    private MenuItem saveProjectAsItem;

    /**
     * When clicked, closes the current open project file.
     */
    private MenuItem closeProjectItem;
    
    /**
     * When clicked, exits Topsoil.
     */
    private MenuItem exitItem;

    // Edit Menu
    /**
     * When clicked, undoes the last {@code Command} on the table.
     */
    private MenuItem undoItem;

    /**
     * When clicked, redoes the last {@code Command} on the table.
     */
    private MenuItem redoItem;

    // Table Menu
    /**
     * When clicked, creates a new table.
     */
    private MenuItem newTableItem;

    /**
     * When clicked, saves the current table.
     */
    private MenuItem saveTableItem;

    /**
     * When clicked, saves the current table to a specific path.
     */
    private MenuItem saveTableAsItem;

    /**
     * When clicked, clears the current {@code TableView}.
     */
    private MenuItem clearTableItem;

    // Table > Import Table >
    /**
     * When clicked, imports table data from a file.
     */
    private MenuItem tableFromFileItem;
    /**
     * When clicked, imports table data from the clipboard
     */
    private MenuItem tableFromClipboardItem;

    // Example Table >
    private MenuItem uPbExampleTableItem;
    private MenuItem uThExampleTableItem;

    
    // Table > Isotope System >
    /**
     * When clicked, changes the table's {@code IsotopeType} to Generic.
     */
    private MenuItem genericSystemItem;
    /**
     * When clicked, changes the table's {@code IsotopeType} to Uranium Lead.
     */
    private MenuItem uraniumLeadSystemItem;
    /**
     * When clicked, changes the table's {@code IsotopeType} to Uranium Thorium.
     */
    private MenuItem uraniumThoriumSystemItem;

    // Help Menu
    /**
     * When clicked, opens the browser so the user can submit a new issue.
     */
    private MenuItem reportIssueItem;
    /**
     * When clicked, re-opens the {@link TopsoilAboutScreen}.
     */
    private MenuItem aboutItem;

    //***********************
    // Constructors
    //***********************

    /**
     * Creates a new {@code MainMenuBar} for the specified {@code TopsoilTabPane}.
     *
     * @param tabs  TopsoilTabPane for the window
     */
    public MainMenuBar(TopsoilTabPane tabs) {
        super();
        this.initialize(tabs);
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    private void initialize(TopsoilTabPane tabs) {

        // File Menu
        Menu fileMenu = getFileMenu(tabs);

        // Edit Menu
        Menu editMenu = getEditMenu(tabs);

        // Table Menu
        Menu tableMenu = getTableMenu(tabs);

        // Plot Menu
        Menu plotMenu = getPlotMenu(tabs);

        // Help Menu
        Menu helpMenu = getHelpMenu(tabs);

        // Add menus to menuBar
        menuBar.getMenus()
                .addAll(fileMenu,
                        editMenu,
                        tableMenu,
                        plotMenu,
                        helpMenu);
    }

    /**
     * Creates and returns the 'File' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'File' Menu
     */
    private Menu getFileMenu(TopsoilTabPane tabs) {
        Menu fileMenu = new Menu("File");
//        newProjectItem = new MenuItem("New Project");
        saveProjectItem = new MenuItem("Save Project");
        saveProjectAsItem = new MenuItem("Save Project As");
        openProjectItem = new MenuItem("Open Project");
        closeProjectItem = new MenuItem("Close Project");
//        mostRecentItem = new MenuItem("Most Recently Used");
        exitItem = new MenuItem("Exit Topsoil");

        openProjectItem.setOnAction(event -> MenuItemEventHandler
                .handleOpenProjectFile(tabs));

        saveProjectItem.setOnAction(event -> MenuItemEventHandler.handleSaveProjectFile(tabs));

        saveProjectAsItem.setOnAction(event -> {
            if (!tabs.isEmpty()) {
                MenuItemEventHandler.handleSaveAsProjectFile(tabs);
            }
        });

        closeProjectItem.setOnAction(event -> MenuItemEventHandler.handleCloseProjectFile(tabs));

        exitItem.setOnAction(event -> {
            Platform.exit();
        });
        
        fileMenu.getItems()
                .addAll(
//                        newProjectItem,
                        openProjectItem,
                        closeProjectItem,
                        new SeparatorMenuItem(),
                        saveProjectItem,
                        saveProjectAsItem
//                        , mostRecentItem
                        ,new SeparatorMenuItem()
                        ,exitItem
                );

        fileMenu.setOnShown(event -> {
            saveProjectItem.setDisable(!TopsoilSerializer.isProjectOpen());
            saveProjectAsItem.setDisable(tabs.isEmpty());
            closeProjectItem.setDisable(!TopsoilSerializer.isProjectOpen());
        });

        return fileMenu;
    }

    /**
     * Creates and returns the 'Edit' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'Edit' Menu
     */
    private Menu getEditMenu(TopsoilTabPane tabs) {
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

        return editMenu;
    }

    /**
     * Creates and returns the 'Table' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'Table' Menu
     */
    private Menu getTableMenu(TopsoilTabPane tabs) {
        Menu tableMenu = new Menu("Table");
        newTableItem = new MenuItem("New Table");
        saveTableItem = new MenuItem("Save Table");
        saveTableAsItem = new MenuItem("Save Table As");
        clearTableItem = new MenuItem("Clear Table");

        // New, empty table
        newTableItem.setOnAction(event -> {

            // get new table
            TopsoilDataTable table = handleNewTable();

            // display new table
            if (table != null) {
                tabs.add(table);
            }
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

        //Creates Submenu for Example Table
        Menu exampleTable = new Menu("Open Example Table");
        uPbExampleTableItem = new MenuItem("Uranium-Lead");
        uThExampleTableItem = new MenuItem("Uranium-Thorium");
        exampleTable.getItems().addAll(
                uPbExampleTableItem,
                uThExampleTableItem);
        
        uPbExampleTableItem.setOnAction(event -> {
            TopsoilDataTable table = MenuItemEventHandler.handleOpenExampleTable(tabs, IsotopeType.UPb);
            tabs.add(table);
        });
        
        uThExampleTableItem.setOnAction(event -> {
            TopsoilDataTable table = MenuItemEventHandler.handleOpenExampleTable(tabs, IsotopeType.UTh);
            tabs.add(table);
        });

        
        // Import Table from File
        tableFromFileItem.setOnAction(event -> {

            TopsoilDataTable table = null;

            // get table from selections
            try {
                table = handleTableFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // display table
            if (table != null) {
                tabs.add(table);
            }
        });

        //Import Table from Clipboard
        tableFromClipboardItem.setOnAction(action -> {

            TopsoilDataTable table = null;

            try {
                table = handleTableFromClipboard();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (table != null) {
                tabs.add(table);
            }

        });

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
                 .addAll(importTable,
                         exampleTable,
                         new SeparatorMenuItem(),
                         saveTableItem,
                         saveTableAsItem,
                         new SeparatorMenuItem(),
                         newTableItem,
                         clearTableItem,
                         new SeparatorMenuItem(),
                         isoSystem);

        tableMenu.setOnShown(event -> {
            if (tabs.isEmpty()) {
                clearTableItem.setDisable(true);
                isoSystem.setDisable(true);
            } else {
                if (!tabs.getSelectedTab().getTableController().getTable().isCleared()) {
                    clearTableItem.setDisable(false);
                } else {
                    clearTableItem.setDisable(true);
                }
                isoSystem.setDisable(false);
            }
        });

        isoSystem.setOnShown(event -> {

        });

        uraniumLeadSystemItem.setOnAction(event -> {
            // if the table isn't already UPb
            if (!(tabs.getSelectedTab().getTableController().getTable().getIsotopeType() == IsotopeType.UPb)) {
                tabs.getSelectedTab().getTableController().getTable().setIsotopeType(IsotopeType.UPb);
            }
        });

        genericSystemItem.setOnAction(event -> {
            if (!tabs.getSelectedTab().getTableController().getTable().getIsotopeType().equals((IsotopeType.Generic))) {
                tabs.getSelectedTab().getTableController().getTable().setIsotopeType(IsotopeType.Generic);
            }
        });

        uraniumThoriumSystemItem.setOnAction(event -> {
            if (!tabs.getSelectedTab().getTableController().getTable().getIsotopeType().equals(IsotopeType.UTh)) {
                tabs.getSelectedTab().getTableController().getTable().setIsotopeType(IsotopeType.UTh);
            }
        });

        return tableMenu;
    }

    /**
     * Creates and returns the 'Plot' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'Plot' Menu
     */
    private Menu getPlotMenu(TopsoilTabPane tabs) {
        Menu plotMenu = new Menu("Plot");
        MenuItem generatePlotItem = new MenuItem("Generate Plot");
        plotMenu.getItems().add(generatePlotItem);

        // Generate Plot
        generatePlotItem.setOnAction(event -> {
            if (!tabs.isEmpty()) {
                MenuItemEventHandler.handlePlotGenerationForSelectedTab(tabs);
            }
        });

        plotMenu.setOnShown(event -> {
            if (tabs.isEmpty()) {
                generatePlotItem.setDisable(true);
            } else {
                generatePlotItem.setDisable(false);
            }
        });

        return plotMenu;
    }

    /**
     * Creates and returns the 'Help' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'Help' Menu
     */
    private Menu getHelpMenu(TopsoilTabPane tabs) {
        Menu helpMenu = new Menu("Help");
        reportIssueItem = new MenuItem("Report Issue");
        aboutItem = new MenuItem("About");

        // Report Issue
        reportIssueItem.setOnAction(event -> {
            handleReportIssue();
        });

        // About
        aboutItem.setOnAction(event -> {
            try {
                Parent about = FXMLLoader
                        .load(RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_ABOUT_SCREEN_FXML_NAME).toUri().toURL
                                ());
                Scene aboutScene = new Scene(about, 450, 600);
                Stage aboutWindow = new Stage(StageStyle.UNDECORATED);
                aboutWindow.setResizable(false);
                aboutWindow.setScene(aboutScene);

                aboutWindow.requestFocus();
                aboutWindow.initOwner(tabs.getScene().getWindow());
                aboutWindow.initModality(Modality.NONE);
                // Close window if main window gains focus.
                tabs.getScene().getWindow().focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        aboutWindow.close();
                    }
                });
                aboutWindow.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        helpMenu.getItems().addAll(reportIssueItem, aboutItem);

        return helpMenu;
    }

    /**
     * Returns an instance of this menu bar as a {@code MenuBar}.
     *
     * @return  MenuBar
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }
}
