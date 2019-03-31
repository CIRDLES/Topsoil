package org.cirdles.topsoil.app.menu;

import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import org.cirdles.topsoil.app.MainController;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.DataImportDialog;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.RecentFiles;
import org.cirdles.topsoil.app.util.ExampleData;
import org.cirdles.topsoil.app.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.Delimiter;
import org.cirdles.topsoil.app.util.ResourceBundles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author marottajb
 */
public class FileMenu extends Menu {

    private MenuItem openProjectItem;
    private Menu openRecentProjectMenu;
    private MenuItem clearRecentProjectsItem;
    private Menu openExampleMenu;
    private MenuItem openUPbExampleItem;
    private MenuItem openUThExampleItem;
    private MenuItem openSquid3ExampleItem;
    private MenuItem saveProjectItem;
    private MenuItem saveProjectAsItem;
    private MenuItem closeProjectItem;
    private Menu importTableMenu;
    private MenuItem fromFileItem;
    private MenuItem fromMultipleItem;
    private MenuItem fromClipboardItem;
    private MenuItem exportTableMenuItem;
    private MenuItem exitTopsoilItem;

    FileMenu() {
        super(ResourceBundles.MENU_BAR.getString("fileMenu"));

        ResourceBundle resources = ResourceBundles.MENU_BAR.getBundle();

        openProjectItem = new MenuItem(resources.getString("openProject"));
        openProjectItem.setOnAction(event -> FileMenuHelper.openProject());
        MenuItem placeholder = new MenuItem(resources.getString("recentPlaceholder"));
        placeholder.setDisable(true);
        clearRecentProjectsItem = new MenuItem(resources.getString("clearRecent"));
        clearRecentProjectsItem.setOnAction(event -> {
            RecentFiles.clear();
            MainController.getInstance().getHomeView().refreshRecentFiles();
        });
        openRecentProjectMenu = new Menu(resources.getString("openRecent"), null, placeholder);
        openRecentProjectMenu.setOnShowing(event -> {
            Path[] paths = RecentFiles.getPaths();
            if (paths.length != 0) {
                openRecentProjectMenu.getItems().remove(placeholder);
                for (Path path : RecentFiles.getPaths()) {
                    MenuItem item = new MenuItem(path.toString());
                    item.setOnAction(event1 -> FileMenuHelper.openProject(path));
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

        openUPbExampleItem = new MenuItem(resources.getString("UPb"));
        openUPbExampleItem.setOnAction(event -> openExampleTable(ExampleData.UPB));
        openUThExampleItem = new MenuItem(resources.getString("UTh"));
        openUThExampleItem.setOnAction(event -> openExampleTable(ExampleData.UTH));
        openSquid3ExampleItem = new MenuItem(resources.getString("squid3"));
        openSquid3ExampleItem.setOnAction(event -> openExampleTable(ExampleData.SQUID_3));
        openExampleMenu = new Menu(resources.getString("openExample"), null,
                openUPbExampleItem,
                openUThExampleItem,
                openSquid3ExampleItem
        );

        saveProjectItem = new MenuItem(resources.getString("saveProject"));
        saveProjectItem.setOnAction(event -> {
            TopsoilProject project = ProjectManager.getProject();
            if (project != null) {
                FileMenuHelper.saveProject(project);
            }
        });
        saveProjectAsItem = new MenuItem(resources.getString("saveProjectAs"));
        saveProjectAsItem.setOnAction(event -> {
            TopsoilProject project = ProjectManager.getProject();
            if (project != null) {
                FileMenuHelper.saveProjectAs(project);
            }
        });
        closeProjectItem = new MenuItem(resources.getString("closeProject"));
        closeProjectItem.setOnAction(event -> FileMenuHelper.closeProject());

        fromFileItem = new MenuItem(resources.getString("importFile"));
        fromFileItem.setOnAction(event -> {
            File file = TopsoilFileChooser.openTableFile().showOpenDialog(Topsoil.getPrimaryStage());
            if (file != null && file.exists()) {
                Path path = Paths.get(file.toURI());
                try {
                    Delimiter delimiter = DataParser.guessDelimiter(path);
                    Map<DataImportDialog.Key, Object> settings =
                            DataImportDialog.showDialog(path.getFileName().toString(), delimiter);
                    if (settings != null) {
                        delimiter = (Delimiter) settings.get(DataImportDialog.Key.DELIMITER);
                        DataTemplate template = (DataTemplate) settings.get(DataImportDialog.Key.TEMPLATE);

                        if (delimiter != null && template != null) {
                            DataTable table = FileMenuHelper.importTableFromFile(path, delimiter, template);
                            if (DataTableOptionsDialog.showDialog(table, Topsoil.getPrimaryStage())) {
                                TopsoilProject project = ProjectManager.getProject();
                                if (project != null) {
                                    project.addDataTable(table);
                                } else {
                                    project = new TopsoilProject(table);
                                    ProjectManager.setProject(project);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    TopsoilNotification.error(
                            "Error",
                            resources.getString("importFileError") + path.getFileName().toString());
                }
            }
        });
        fromMultipleItem = new MenuItem(resources.getString("importMultiFile"));
        fromMultipleItem.setOnAction(event -> FileMenuHelper.importMultipleFiles());
        fromClipboardItem = new MenuItem(resources.getString("importClipboard"));
        fromClipboardItem.setOnAction(event -> FileMenuHelper.importTableFromClipboard());

        importTableMenu = new Menu(resources.getString("importMenu"), null,
                                   fromFileItem,
                                   fromMultipleItem,
                                   fromClipboardItem
        );
        importTableMenu.setOnShown(event -> fromClipboardItem.setDisable(! Clipboard.getSystemClipboard().hasString()));

        exportTableMenuItem = new MenuItem(resources.getString("exportTable"));
        exportTableMenuItem.setOnAction(event -> {
            DataTable table = MenuUtils.getCurrentDataTable();
            if (table != null) {
                FileMenuHelper.exportTableAs(table);
            }
        });

        exitTopsoilItem = new MenuItem(resources.getString("exit"));
        exitTopsoilItem.setOnAction(event -> {
            if (FileMenuHelper.handleDataBeforeClose()) {
                Topsoil.shutdown();
            }
        });

        this.setOnShown(event -> {
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

        this.getItems().addAll(
                openProjectItem,
                openRecentProjectMenu,
                openExampleMenu,
                saveProjectItem,
                saveProjectAsItem,
                closeProjectItem,
                new SeparatorMenuItem(),
                importTableMenu,
                exportTableMenuItem,
                new SeparatorMenuItem(),
                exitTopsoilItem
        );
    }

    private void openExampleTable(ExampleData example) {
        FileMenuHelper.openExampleData(example);
    }
    
}


