package org.cirdles.topsoil.app.control.menu;

import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.DataImportDialog;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.RecentFiles;
import org.cirdles.topsoil.app.util.ExampleData;
import org.cirdles.topsoil.app.file.ProjectSerializer;
import org.cirdles.topsoil.app.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.Delimiter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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
        super("File");

        openProjectItem = new MenuItem("Open...");
        openProjectItem.setOnAction(event -> {
            TopsoilProject project = FileMenuHelper.openProject();
            if (project != null) {
                Topsoil.getController().setProject(project);
            }
        });
        MenuItem placeholder = new MenuItem("No recent projects.");
        placeholder.setDisable(true);
        clearRecentProjectsItem = new MenuItem("Clear recent files");
        clearRecentProjectsItem.setOnAction(event -> {
            RecentFiles.clear();
            Topsoil.getController().getHomeView().clearRecentFiles();
        });
        openRecentProjectMenu = new Menu("Open Recent", null, placeholder);
        openRecentProjectMenu.setOnShowing(event -> {
            Path[] paths = RecentFiles.getPaths();
            if (paths.length != 0) {
                openRecentProjectMenu.getItems().remove(placeholder);
                for (Path path : RecentFiles.getPaths()) {
                    MenuItem item = new MenuItem(path.getFileName().toString());
                    item.setOnAction(event1 -> {
                        TopsoilProject project = FileMenuHelper.openProject(path);
                        if (project != null) {
                            Topsoil.getController().setProject(project);
                        }
                    });
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

        openUPbExampleItem = new MenuItem("Uranium-Lead");
        openUPbExampleItem.setOnAction(event -> openExampleTable(ExampleData.UPB));
        openUThExampleItem = new MenuItem("Uranium-Thorium");
        openUThExampleItem.setOnAction(event -> openExampleTable(ExampleData.UTH));
        openSquid3ExampleItem = new MenuItem("Squid 3 Data");
        openSquid3ExampleItem.setOnAction(event -> openExampleTable(ExampleData.SQUID_3));
        openExampleMenu = new Menu("Open Example", null,
                openUPbExampleItem,
                openUThExampleItem,
                openSquid3ExampleItem
        );

        saveProjectItem = new MenuItem("Save");
        saveProjectItem.setOnAction(event -> {
            TopsoilProject project = Topsoil.getController().getProject();
            if (project != null) {
                if (ProjectSerializer.getCurrentPath() != null) {
                    FileMenuHelper.saveProject(project);
                }
            }
        });
        saveProjectAsItem = new MenuItem("Save As...");
        saveProjectAsItem.setOnAction(event -> {
            TopsoilProject project = Topsoil.getController().getProject();
            if (project != null) {
                FileMenuHelper.saveProjectAs(project);
            }
        });
        closeProjectItem = new MenuItem("Close Project");
        closeProjectItem.setOnAction(event -> FileMenuHelper.closeProject());

        fromFileItem = new MenuItem("From File (csv, tsv, txt)");
        fromFileItem.setOnAction(event -> {
            File file = TopsoilFileChooser.openTableFile().showOpenDialog(Topsoil.getPrimaryStage());
            if (file != null && file.exists()) {
                Path path = Paths.get(file.toURI());
                try {
                    Delimiter delimiter = DataParser.guessDelimiter(path);
                    Map<DataImportDialog.Key, Object> settings =
                            DataImportDialog.showDialog(path.getFileName().toString(), delimiter);
                    delimiter = (Delimiter) settings.get(DataImportDialog.Key.DELIMITER);
                    DataTemplate template = (DataTemplate) settings.get(DataImportDialog.Key.TEMPLATE);

                    if (delimiter != null && template != null) {
                        DataTable table = FileMenuHelper.importTableFromFile(path, delimiter, template);
                        if (DataTableOptionsDialog.showDialog(table, Topsoil.getPrimaryStage())) {
                            TopsoilProject project = Topsoil.getController().getProject();
                            if (project != null) {
                                project.addDataTable(table);
                            } else {
                                project = new TopsoilProject(table);
                                Topsoil.getController().setProject(project);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    TopsoilNotification.showNotification(TopsoilNotification.NotificationType.ERROR,
                                                         "Error",
                                                         "Unable to read file: " + path.getFileName().toString());
                }
            }
        });
        fromMultipleItem = new MenuItem("From Files... (csv, tsv, txt)");
        fromMultipleItem.setOnAction(event -> FileMenuHelper.importMultipleFiles());
        fromClipboardItem = new MenuItem("From Clipboard");
        fromClipboardItem.setOnAction(event -> FileMenuHelper.importTableFromClipboard());

        importTableMenu = new Menu("Import", null,
                                   fromFileItem,
                                   fromMultipleItem,
                                   fromClipboardItem
        );
        importTableMenu.setOnShown(event -> fromClipboardItem.setDisable(! Clipboard.getSystemClipboard().hasString()));

        exportTableMenuItem = new MenuItem("Export Table...");
        exportTableMenuItem.setOnAction(event -> {
            DataTable table = MenuUtils.getCurrentTable();
            if (table != null && !FileMenuHelper.exportTableAs(table)) {
                TopsoilNotification.showNotification(TopsoilNotification.NotificationType.ERROR,
                                                     "Error",
                                                     "Could not export table.");
            }
        });

        exitTopsoilItem = new MenuItem("Exit Topsoil");
        exitTopsoilItem.setOnAction(event -> {
            if (FileMenuHelper.handleDataBeforeClose()) {
                Topsoil.shutdown();
            }
        });

        this.setOnShown(event -> {
            TopsoilProject project = Topsoil.getController().getProject();
            if (project != null) {
                exportTableMenuItem.setDisable(false);
                saveProjectAsItem.setDisable(false);
                if (ProjectSerializer.getCurrentPath() != null) {
                    closeProjectItem.setDisable(false);
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
        TopsoilProject project = Topsoil.getController().getProject();
        if (project != null) {
            project.addDataTable(FileMenuHelper.openExampleData(example));
        } else {
            project = new TopsoilProject(FileMenuHelper.openExampleData(example));
            Topsoil.getController().setProject(project);
        }
    }
    
}


