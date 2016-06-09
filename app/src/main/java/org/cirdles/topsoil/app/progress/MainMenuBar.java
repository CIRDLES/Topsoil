package org.cirdles.topsoil.app.progress;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Created by sbunce on 5/30/2016.
 */

public class MainMenuBar extends MenuBar {
    private MenuBar menuBar = new MenuBar();

    public MainMenuBar() {
        super();
        this.initialize();
    }

    public void initialize() {
        // File Menu
        Menu projectMenu = new Menu("Project");
        MenuItem newProject = new MenuItem("New Project");
        MenuItem saveProject = new MenuItem("Save Project");
        MenuItem saveProjectAs = new MenuItem("Save Project As");
        MenuItem openProject = new MenuItem("Open Project");
        MenuItem mostRecent = new MenuItem("Most Recently Used");
        MenuItem closeProject = new MenuItem("Close Project");
        projectMenu.getItems()
                .addAll(newProject,
                        saveProject,
                        saveProjectAs,
                        openProject,
                        mostRecent,
                        closeProject);

        // Table Menu
        Menu tableMenu = new Menu("Table");
        MenuItem newTable = new MenuItem("New Table");
        MenuItem saveTable = new MenuItem("Save Table");
        MenuItem saveTableAs = new MenuItem("Save Table As");

        //Creates Submenu for Imports
        Menu importTable = new Menu("Import Table");
        importTable.getItems().add(new MenuItem("From File"));
        importTable.getItems().add(new MenuItem("From Clipboard"));

        //Creates Submenu for Isotype system selection
        Menu isoSystem = new Menu("Set Isotope System");
        isoSystem.getItems().add(new MenuItem("UPb"));
        isoSystem.getItems().add(new MenuItem("UTh"));
        tableMenu.getItems()
                .addAll(newTable,
                        saveTable,
                        saveTableAs,
                        importTable,
                        isoSystem);

        // Plot Menu
        Menu plotMenu = new Menu("Plot");

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem report = new MenuItem("Report Issue");
        MenuItem about = new MenuItem("About");
        helpMenu.getItems()
                .addAll(report,
                        about);

        // Add menus to menuBar
        menuBar.getMenus()
                .addAll(projectMenu,
                        tableMenu,
                        plotMenu,
                        helpMenu);
    }

    //Returns compatible type to be added to main window
    public MenuBar getMenuBar() {
        return menuBar;
    }
}
