package org.cirdles.topsoil.app.menu;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import static org.cirdles.topsoil.app.MainWindow.verifyFinalSave;
import org.cirdles.topsoil.app.TopsoilAboutScreen;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.menu.command.ClearTableCommand;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilDataTable;

import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

import java.io.IOException;
import javafx.application.Platform;

import static org.cirdles.topsoil.app.menu.MenuItemEventHandler.*;
import org.cirdles.topsoil.app.menu.command.DeleteTableCommand;

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
    private final String TOPSOIL_ABOUT_SCREEN_FXML_NAME = "topsoil-about-screen.fxml";

    // Project Menu
    /**
     * When clicked, starts the process of creating a new project.
     */
    private MenuItem newProjectItem;

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

    // Data Table Menu
    /**
     * When clicked, creates a new table.
     */
    private MenuItem newTableItem;

    private MenuItem exportTableItem;
    
    /**
     * When clicked, clears the current {@code TableView}.
     */
    private MenuItem clearTableItem;
    
    /**
     * When clicked, deletes the current {@code TableView}.
     */
    private MenuItem deleteTableItem;

    // Data Table > Import Table >
    /**
     * When clicked, imports table data from a file.
     */
    private MenuItem tableFromFileItem;
    /**
     * When clicked, imports table data from the clipboard
     */
    private MenuItem tableFromClipboardItem;

    // Data Table > Import Table >
    /**
     * When clicked, imports sample UPb data table 
     */
    private MenuItem uPbExampleTableItem;
    /**
     * When clicked, imports sample UTh data table 
     */
    private MenuItem uThExampleTableItem;


    // Help Menu
    /**
     * When clicked, opens the browser at the Topsoil project page.
     */
    private MenuItem onlineHelpItem;
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

        // Project Menu
        Menu projectMenu = getProjectMenu(tabs);

        // Edit Menu
        Menu editMenu = getEditMenu(tabs);

        // Table Menu
        Menu tableMenu = getTableMenu(tabs);

        // Help Menu
        Menu helpMenu = getHelpMenu(tabs);

        // Add menus to menuBar
        menuBar.getMenus()
                .addAll(projectMenu,
                        editMenu,
                        tableMenu,
                        helpMenu);
    }

    /**
     * Creates and returns the 'Project' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'Project' Menu
     */
    private Menu getProjectMenu(TopsoilTabPane tabs) {
        Menu projectMenu = new Menu("Project");
        newProjectItem = new MenuItem("New Project");
        saveProjectItem = new MenuItem("Save Project");
        saveProjectAsItem = new MenuItem("Save Project As");
        openProjectItem = new MenuItem("Open Project");
        closeProjectItem = new MenuItem("Close Project");
        exitItem = new MenuItem("Exit Topsoil");

        newProjectItem.setOnAction(event -> MenuItemEventHandler.handleNewProject(tabs));

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
            Boolean save = verifyFinalSave();
            // If save verification was not cancelled
            if (save != null) {
                if (save) {
                    // If file was successfully saved
                    if (MenuItemEventHandler.handleSaveAsProjectFile(tabs)) {
                        Platform.exit();
                    }
                    // If user doesn't want to save
                } else {
                    Platform.exit();
                }
            }
        });
        
        projectMenu.getItems()
                .addAll(
                        newProjectItem,
                        openProjectItem,
                        closeProjectItem,
                        new SeparatorMenuItem(),
                        saveProjectItem,
                        saveProjectAsItem
                        ,new SeparatorMenuItem()
                        ,exitItem
                );

        projectMenu.setOnShown(event -> {
            saveProjectItem.setDisable(!TopsoilSerializer.isProjectOpen());
            saveProjectAsItem.setDisable(tabs.isEmpty());
            closeProjectItem.setDisable(!TopsoilSerializer.isProjectOpen());
        });

        return projectMenu;
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
     * Creates and returns the 'Data Table' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'Data Table' Menu
     */
    private Menu getTableMenu(TopsoilTabPane tabs) {
        Menu tableMenu = new Menu("Data Table");
        newTableItem = new MenuItem("New Data Table");
        exportTableItem = new MenuItem("Export Table");
        clearTableItem = new MenuItem("Clear Data Table");
        deleteTableItem = new MenuItem("Delete Data Table");

        // New, empty table
        newTableItem.setOnAction(event -> {

            // get new table
            TopsoilDataTable table = handleNewTable();

            // display new table
            if (table != null) {
                tabs.add(table);
            }
        });
        
        exportTableItem = new MenuItem("Export Table");
         
         exportTableItem.setOnAction(event -> {
             TopsoilDataTable table = tabs.getSelectedTab().getTableController().getTable();
             if (table != null) {
                 handleExportTable(table);
             }
             else
                 System.out.println("PANIC");
         });


        clearTableItem.setOnAction(action -> {
            // clear table and add an empty row
            ClearTableCommand clearTableCommand =
                    new ClearTableCommand(tabs.getSelectedTab().getTabContent().getTableView());
            clearTableCommand.execute();
            tabs.getSelectedTab().addUndo(clearTableCommand);
        });
        
        deleteTableItem.setOnAction(action -> {
            DeleteTableCommand deleteTableCommand =
                    new DeleteTableCommand(tabs.getSelectedTab());
            deleteTableCommand.execute();
            //tabs.addUndo(deleteTableCommand);
        });

        //Creates Submenu for Imports
        Menu importTable = new Menu("Import Data Table");
        tableFromFileItem = new MenuItem("From File");
        tableFromClipboardItem = new MenuItem("From Clipboard");
        importTable.getItems().addAll(
                tableFromFileItem,
                tableFromClipboardItem);

        //Creates Submenu for Example Table
        Menu exampleTable = new Menu("Open Example Data Table");
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

        tableMenu.getItems()
                 .addAll(importTable,
                         exampleTable,
                         new SeparatorMenuItem(),
                         exportTableItem,
                         new SeparatorMenuItem(),
                         newTableItem,
                         //clearTableItem,
                         new SeparatorMenuItem(),
                         deleteTableItem);

        tableMenu.setOnShown(event -> {
            if (tabs.isEmpty()) {
                clearTableItem.setDisable(true);
                deleteTableItem.setDisable(true);
                exportTableItem.setDisable(true);
            } else {
                if (!tabs.getSelectedTab().getTableController().getTable().isCleared()) {
                    clearTableItem.setDisable(false);
                    exportTableItem.setDisable(false);
                } else {
                    clearTableItem.setDisable(true);
                    exportTableItem.setDisable(true);
                }
                deleteTableItem.setDisable(false);
            }
        });

        return tableMenu;
    }

    /**
     * Creates and returns the 'Help' menu.
     *
     * @param tabs  TopsoilTabPane for the window
     * @return  'Help' Menu
     */
    private Menu getHelpMenu(TopsoilTabPane tabs) {
        Menu helpMenu = new Menu("Help");
        onlineHelpItem = new MenuItem("Online Help");
        reportIssueItem = new MenuItem("Report Issue");
        aboutItem = new MenuItem("About");

        onlineHelpItem.setOnAction(event -> {
            handleOpenOnlineHelp();
        });
        
        // Report Issue
        reportIssueItem.setOnAction(event -> {
            handleReportIssue();
        });

        // About
        aboutItem.setOnAction(event -> {
            try {
                Parent about = FXMLLoader.load(RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_ABOUT_SCREEN_FXML_NAME).toUri().toURL());

                final double ABOUT_WIDTH = 550;
                final double ABOUT_HEIGHT = 650;

                Scene aboutScene = new Scene(about, ABOUT_WIDTH, ABOUT_HEIGHT);
                Stage aboutWindow = new Stage(StageStyle.UNDECORATED);
                aboutWindow.setResizable(false);
                aboutWindow.setScene(aboutScene);

                double newX = MainWindow.getPrimaryStage().getX() + (MainWindow.getPrimaryStage().getWidth() / 2) -
                             (ABOUT_WIDTH / 2);
                double newY = MainWindow.getPrimaryStage().getY() + (MainWindow.getPrimaryStage().getHeight() / 2) -
                              (ABOUT_HEIGHT / 2);

                aboutWindow.setX(newX);
                aboutWindow.setY(newY);

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

        helpMenu.getItems().addAll(
                onlineHelpItem,
                reportIssueItem,
                new SeparatorMenuItem(),
                aboutItem
        );

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
