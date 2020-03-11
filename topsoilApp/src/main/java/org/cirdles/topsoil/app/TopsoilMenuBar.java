package org.cirdles.topsoil.app;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.app.control.dialog.PlotConfigDialog;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.control.plot.PlotGenerator;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.RecentFiles;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.ExampleData;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringJoiner;

/**
 * The main {@code MenuBar} for the application.
 *
 * @author marottajb
 */
public class TopsoilMenuBar extends MenuBar {

    private ResourceBundle resources = ResourceBundles.MENU_BAR.getBundle();

    public TopsoilMenuBar() {
        super();
        this.getMenus().addAll(
                getFileMenu(),
                getEditMenu(),
                getVisualizationsMenu(),
                getHelpMenu()
        );
    }

    private Menu getFileMenu() {

        MenuItem openProjectItem = new MenuItem(resources.getString("openProject"));
        openProjectItem.setOnAction(event -> MenuItemHelper.openProject());
        MenuItem placeholder = new MenuItem(resources.getString("recentPlaceholder"));
        placeholder.setDisable(true);
        MenuItem clearRecentProjectsItem = new MenuItem(resources.getString("clearRecent"));
        clearRecentProjectsItem.setOnAction(event -> {
            RecentFiles.clear();
            MainController.getInstance().getHomeView().refreshRecentFiles();
        });
        Menu openRecentProjectMenu = new Menu(resources.getString("openRecent"), null, placeholder);
        openRecentProjectMenu.setOnShowing(event -> {
            Path[] paths = RecentFiles.getPaths();
            if (paths.length != 0) {
                openRecentProjectMenu.getItems().remove(placeholder);
                for (Path path : RecentFiles.getPaths()) {
                    MenuItem item = new MenuItem(path.toString());
                    item.setOnAction(event1 -> MenuItemHelper.openProject(path));
                    openRecentProjectMenu.getItems().add(item);
                }
                openRecentProjectMenu.getItems().add(new SeparatorMenuItem());
                openRecentProjectMenu.getItems().add(clearRecentProjectsItem);
            }
        });
        openRecentProjectMenu.setOnHidden(event -> {
            openRecentProjectMenu.getItems().clear();
            openRecentProjectMenu.getItems().add(placeholder);
        });

        MenuItem openUPbExampleItem = new MenuItem(resources.getString("UPb"));
        openUPbExampleItem.setOnAction(event -> MenuItemHelper.openExampleData(ExampleData.UPB));
        MenuItem openUThExampleItem = new MenuItem(resources.getString("UTh"));
        openUThExampleItem.setOnAction(event -> MenuItemHelper.openExampleData(ExampleData.UTH));
        MenuItem openSquid3ExampleItem = new MenuItem(resources.getString("squid3"));
        openSquid3ExampleItem.setOnAction(event -> MenuItemHelper.openExampleData(ExampleData.SQUID_3));
        Menu openExampleMenu = new Menu(resources.getString("openExample"), null,
                openUPbExampleItem,
                openUThExampleItem,
                openSquid3ExampleItem
        );

        MenuItem saveProjectItem = new MenuItem(resources.getString("saveProject"));
        saveProjectItem.setOnAction(event -> {
            TopsoilProject project = ProjectManager.getProject();
            if (project != null) {
                MenuItemHelper.saveProject(project);
            }
        });
        MenuItem saveProjectAsItem = new MenuItem(resources.getString("saveProjectAs"));
        saveProjectAsItem.setOnAction(event -> {
            TopsoilProject project = ProjectManager.getProject();
            if (project != null) {
                MenuItemHelper.saveProjectAs(project);
            }
        });
        MenuItem closeProjectItem = new MenuItem(resources.getString("closeProject"));
        closeProjectItem.setOnAction(event -> MenuItemHelper.closeProject());

        MenuItem fromFileItem = new MenuItem(resources.getString("importFile"));
        fromFileItem.setOnAction(event -> {
            try {
                MenuItemHelper.importTableFromFile();
            } catch (IOException e) {
                TopsoilNotification.error("Invalid File", "Topsoil could not read the selected file.");
            }
        });
        MenuItem fromMultipleItem = new MenuItem(resources.getString("importMultiFile"));
        fromMultipleItem.setOnAction(event -> MenuItemHelper.importMultipleFiles());
        MenuItem fromClipboardItem = new MenuItem(resources.getString("importClipboard"));
        fromClipboardItem.setOnAction(event -> MenuItemHelper.importTableFromClipboard());

        Menu importTableMenu = new Menu(resources.getString("importMenu"), null,
                fromFileItem,
                fromMultipleItem,
                fromClipboardItem
        );
        importTableMenu.setOnShown(event -> fromClipboardItem.setDisable(! Clipboard.getSystemClipboard().hasString()));

        MenuItem exportTableMenuItem = new MenuItem(resources.getString("exportTable"));
        exportTableMenuItem.setOnAction(event -> {
            FXDataTable table = MenuUtils.getCurrentDataTable();
            if (table != null) {
                MenuItemHelper.exportTableAs(table);
            }
        });

        MenuItem exitTopsoilItem = new MenuItem(resources.getString("exit"));
        exitTopsoilItem.setOnAction(event -> Topsoil.safeShutdown());

        Menu fileMenu = new Menu(resources.getString("fileMenu"));
        fileMenu.setOnShown(event -> {
            TopsoilProject project = ProjectManager.getProject();
            if (project != null) {
                exportTableMenuItem.setDisable(false);
                saveProjectAsItem.setDisable(false);
                closeProjectItem.setDisable(false);
                if (ProjectManager.getProjectPath() != null) {
                    saveProjectItem.setDisable(false);
                }
            } else {
                exportTableMenuItem.setDisable(true);
                saveProjectAsItem.setDisable(true);
                saveProjectItem.setDisable(true);
                closeProjectItem.setDisable(true);
            }
        });
        fileMenu.getItems().addAll(
                importTableMenu,
                openProjectItem,
                openRecentProjectMenu,
                openExampleMenu,
                saveProjectItem,
                saveProjectAsItem,
                closeProjectItem,
                new SeparatorMenuItem(),
                exportTableMenuItem,
                new SeparatorMenuItem(),
                exitTopsoilItem
        );
        return fileMenu;
    }

    private Menu getEditMenu() {
        MenuItem preferencesItem = new MenuItem(resources.getString("preferences"));
        preferencesItem.setDisable(true);

        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setOnAction(event -> MenuUtils.undoLastAction());

        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setOnAction(event -> MenuUtils.redoLastAction());

        MenuItem tableOptionsItem = new MenuItem(resources.getString("tableOptions"));
        tableOptionsItem.setOnAction(event -> MenuItemHelper.editTableOptions());

        Menu editMenu = new Menu(resources.getString("editMenu"), null,
                undoItem,
                redoItem,
                new SeparatorMenuItem(),
                tableOptionsItem
        );
        editMenu.setOnShown(event -> {
            if (MenuUtils.lastUndoName() != null) {
                undoItem.setDisable(false);
                undoItem.setText(resources.getString("undo") + " \"" + MenuUtils.lastUndoName() + "\"");
            } else {
                undoItem.setDisable(true);
                undoItem.setText(resources.getString("undo"));
            }
            if (MenuUtils.lastRedoName() != null) {
                redoItem.setDisable(false);
                redoItem.setText(resources.getString("redo") + " \"" + MenuUtils.lastRedoName() + "\"");
            } else {
                redoItem.setDisable(true);
                redoItem.setText(resources.getString("redo"));
            }
            tableOptionsItem.setDisable(ProjectManager.getProject() == null);
        });

        return editMenu;
    }

    private Menu getVisualizationsMenu() {
        MenuItem generatePlotItem = new MenuItem(resources.getString("generatePlot"));
        generatePlotItem.setOnAction(event -> {
            FXDataTable table = MenuUtils.getCurrentDataTable();
            if (table == null) {
                return;
            }

            PlotConfigDialog dialog = new PlotConfigDialog(table);
            Map<PlotConfigDialog.Key, Object> settings = dialog.showAndWait().orElse(null);
            if (settings == null) {
                return;
            }

            PlotOptions options = PlotOptions.defaultOptions();
            Map<Variable<?>, DataColumn<?>> variableMap =
                    (Map<Variable<?>, DataColumn<?>>) settings.get(PlotConfigDialog.Key.VARIABLE_MAP);

            // Check for required plotting variables
            List<Variable> missing = PlotGenerator.findMissingVariables(variableMap, PlotType.SCATTER);
            if (! missing.isEmpty()) {
                StringJoiner joiner = new StringJoiner(", ");
                for (Variable v : missing) {
                    joiner.add(v.getAbbreviation());
                }
                TopsoilNotification.error(
                        "Missing Variables",
                        "The following variables must be assigned for this plot type:\n\n[" + joiner.toString() + "]"
                );
                return;
            }

            IsotopeSystem isotopeSystem = (IsotopeSystem) settings.get(PlotConfigDialog.Key.ISOTOPE_SYSTEM);
            options.put(PlotOption.ISOTOPE_SYSTEM, isotopeSystem);

            options.put(PlotOption.TITLE, table.getTitle());
            options.put(PlotOption.X_AXIS, variableMap.get(Variable.X).getTitle());
            options.put(PlotOption.Y_AXIS, variableMap.get(Variable.Y).getTitle());

            PlotGenerator.generatePlot(
                    ProjectManager.getProject(),
                    table,
                    variableMap,
                    PlotType.SCATTER,
                    options);
        });

        Menu visualizationsMenu = new Menu(resources.getString("visualizationsMenu"), null,
                                           generatePlotItem
        );
        visualizationsMenu.setOnShown(event -> generatePlotItem.setDisable(ProjectManager.getProject() == null));
        return visualizationsMenu;
    }

    private Menu getHelpMenu() {
        MenuItem onlineHelpItem = new MenuItem(resources.getString("onlineHelp"));
        onlineHelpItem.setOnAction(event -> MenuItemHelper.openOnlineHelp());

        MenuItem reportIssueItem = new MenuItem(resources.getString("reportIssue"));
        reportIssueItem.setOnAction(event -> MenuItemHelper.openIssueReporter());

        MenuItem aboutItem = new MenuItem(resources.getString("about"));
        aboutItem.setOnAction(event -> MenuItemHelper.openAboutScreen(Topsoil.getPrimaryStage()));

        return new Menu(resources.getString("helpMenu"), null,
                        onlineHelpItem,
                        reportIssueItem,
                        aboutItem
        );
    }

}
