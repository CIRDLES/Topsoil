package org.cirdles.topsoil.app.menu;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import static org.cirdles.topsoil.app.MainWindow.verifyFinalSave;

import org.cirdles.topsoil.app.TopsoilAboutScreen;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;

import org.cirdles.topsoil.app.spreadsheet.CellFormatDialog;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.util.TopsoilException;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

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

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    // Project Menu
    private MenuItem newProjectItem, openProjectItem, saveProjectItem, saveProjectAsItem, closeProjectItem, exitItem;

    // Edit Menu
    private MenuItem undoItem, redoItem;

    // Data Table Menu
    private MenuItem newTableItem, exportTableItem, deleteTableItem, setCellFormatItem;

    // Data Table > Import Table >
    private MenuItem tableFromFileItem, tableFromClipboardItem;

    // Data Table > Example Table >
    private MenuItem uPbExampleTableItem, uThExampleTableItem;

    // Help Menu
    private MenuItem onlineHelpItem, reportIssueItem, aboutItem;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Creates a new {@code MainMenuBar} for the specified {@code TopsoilTabPane}.
     *
     * @param   tabs
     *          TopsoilTabPane for the window
     */
    public MainMenuBar(TopsoilTabPane tabs) {
        super();
        this.initialize(tabs);
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void initialize(TopsoilTabPane tabs) {

        // Project Menu
        Menu projectMenu = projectMenu(tabs);

        // Edit Menu
        Menu editMenu = editMenu(tabs);

        // Table Menu
        Menu tableMenu = tableMenu(tabs);

        // Help Menu
        Menu helpMenu = helpMenu(tabs);

        // Add menus to menuBar
        this.getMenus().addAll(
                projectMenu,
                editMenu,
                tableMenu,
                helpMenu
        );
    }

    /**
     * Creates and returns the 'Project' menu.
     *
     * @param   tabs
     *          TopsoilTabPane for the window
     *
     * @return  'Project' Menu
     */
    private Menu projectMenu(TopsoilTabPane tabs) {
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
     * @param   tabs
     *          TopsoilTabPane for the window
     *
     * @return  'Edit' Menu
     */
    private Menu editMenu(TopsoilTabPane tabs) {
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
     * @param   tabs
     *          TopsoilTabPane for the window
     *
     * @return  'Data Table' Menu
     */
    private Menu tableMenu(TopsoilTabPane tabs) {
        Menu tableMenu = new Menu("Data Table");
        newTableItem = new MenuItem("New Data Table");
        exportTableItem = new MenuItem("Export Table");
        deleteTableItem = new MenuItem("Delete Data Table");
	    setCellFormatItem = new MenuItem("Set Cell Format");

        // New, empty table
        newTableItem.setOnAction(event -> {

            // get new table
            ObservableDataTable table = handleNewTable();

            // display new table
            if (table != null) {
                tabs.add(table);
            }
        });
        
        exportTableItem = new MenuItem("Export Table");
         
         exportTableItem.setOnAction(event -> {
             ObservableDataTable table = tabs.getSelectedTab().getDataView().getData();
             if (table != null) {
                 handleExportTable(table);
             }
             else
                 System.out.println("PANIC");
         });
        
        deleteTableItem.setOnAction(event -> {
            DeleteTableCommand deleteTableCommand =
                    new DeleteTableCommand(tabs.getSelectedTab());
            deleteTableCommand.execute();
            //tabs.addUndo(deleteTableCommand);
        });

        setCellFormatItem.setOnAction(event -> {
        	try {
		        String pattern = CellFormatDialog.open(null);
		        tabs.getSelectedTab().getDataView().getSpreadsheet().setFormat(pattern);
	        } catch (TopsoilException e) {
		        e.printStackTrace();
	        }
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
        
        uPbExampleTableItem.setOnAction(event -> MenuItemEventHandler.handleOpenExampleTable(tabs, IsotopeSystem.UPB));
        
        uThExampleTableItem.setOnAction(event -> MenuItemEventHandler.handleOpenExampleTable(tabs, IsotopeSystem.UTH));
        
        // Import Table from File
        tableFromFileItem.setOnAction(event -> {

            ObservableDataTable table = importDataFromFile();

            if (table != null) {
                tabs.add(table);
            }
        });

        //Import Table from Clipboard
        tableFromClipboardItem.setOnAction(event -> {

            ObservableDataTable table = importDataFromClipboard();

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
//                         new SeparatorMenuItem(),
//                         newTableItem,
                         new SeparatorMenuItem(),
                         deleteTableItem,
                         new SeparatorMenuItem(),
                         setCellFormatItem);

        tableMenu.setOnShown(event -> {
            if (tabs.isEmpty()) {
                deleteTableItem.setDisable(true);
                exportTableItem.setDisable(true);
            } else {
                deleteTableItem.setDisable(false);
                exportTableItem.setDisable(false);
            }
        });

        return tableMenu;
    }

    /**
     * Creates and returns the 'Help' menu.
     *
     * @param   tabs
     *          TopsoilTabPane for the window
     *
     * @return  'Help' Menu
     */
    private Menu helpMenu(TopsoilTabPane tabs) {
        Menu helpMenu = new Menu("Help");
        onlineHelpItem = new MenuItem("Online Help");
        reportIssueItem = new MenuItem("Report Issue");
        aboutItem = new MenuItem("About");

        onlineHelpItem.setOnAction(event -> handleOpenOnlineHelp() );
        
        // Report Issue
        reportIssueItem.setOnAction(event -> handleReportIssue() );

        // About
        aboutItem.setOnAction(event -> {
            Stage aboutWindow = TopsoilAboutScreen.getFloatingStage();

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
        });

        helpMenu.getItems().addAll(
                onlineHelpItem,
                reportIssueItem,
                new SeparatorMenuItem(),
                aboutItem
        );

        return helpMenu;
    }
}
