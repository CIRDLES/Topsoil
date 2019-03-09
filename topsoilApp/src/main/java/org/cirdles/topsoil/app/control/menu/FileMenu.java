package org.cirdles.topsoil.app.control.menu;

import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.control.dialog.DataImportDialog;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
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

    private Menu newMenu;
    private MenuItem newProjectItem;
    private MenuItem openProjectItem;
    private Menu openRecentProjectMenu;
    private Menu openExampleMenu;
    private MenuItem openUPbExampleItem;
    private MenuItem openUThExampleItem;
    private MenuItem openSquid3ExampleItem;
    private MenuItem saveProjectItem;
    private MenuItem saveProjectAsItem;
    private MenuItem closeProjectItem;
    private Menu importTableMenu;
    private MenuItem fromFileItem;
    private MenuItem fromClipboardItem;
    private MenuItem exportTableMenuItem;
    private MenuItem exitTopsoilItem;

    FileMenu() {
        super("File");

        newProjectItem = new MenuItem("Project from Files");
        newProjectItem.setOnAction(event -> {
            TopsoilProject project = FileMenuHelper.newProject();
            if (project != null) {
                ProjectView projectView = new ProjectView(project);
                Topsoil.getController().setProjectView(projectView);
            }
        });
        newMenu = new Menu("New", null,
                           newProjectItem
        );

        openProjectItem = new MenuItem("Open...");
        openProjectItem.setOnAction(event -> {
            TopsoilProject project = FileMenuHelper.openProject();
            if (project != null) {
                ProjectView projectView = new ProjectView(project);
                Topsoil.getController().setProjectView(projectView);
            }
        });
        MenuItem placeholder = new MenuItem("No recent projects.");
        placeholder.setDisable(true);
        openRecentProjectMenu = new Menu("Open Recent", null, placeholder);
        openRecentProjectMenu.setOnShowing(event -> {
            Path[] paths = Topsoil.getController().getRecentFiles();
            if (paths.length != 0) {
                openRecentProjectMenu.getItems().remove(placeholder);
                for (Path path : Topsoil.getController().getRecentFiles()) {
                    MenuItem item = new MenuItem(path.getFileName().toString());
                    item.setOnAction(event1 -> {
                        TopsoilProject project = FileMenuHelper.openProject(path);
                        if (project != null) {
                            Topsoil.getController().setProjectView(new ProjectView(project));
                        }
                    });
                    openRecentProjectMenu.getItems().add(item);
                }
            }
        });
        openRecentProjectMenu.setOnHidden(event -> {
            openRecentProjectMenu.getItems().clear();
            openRecentProjectMenu.getItems().add(placeholder);
        });

        openUPbExampleItem = new MenuItem("Uranium-Lead");
        openUPbExampleItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                MenuUtils.getProjectView().getProject().addDataTable(FileMenuHelper.openExampleData(ExampleData.UPB));
            } else {
                Topsoil.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openExampleData(ExampleData.UPB)))
                );
            }
        });
        openUThExampleItem = new MenuItem("Uranium-Thorium");
        openUThExampleItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                MenuUtils.getProjectView().getProject().addDataTable(FileMenuHelper.openExampleData(ExampleData.UTH));
            } else {
                Topsoil.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openExampleData(ExampleData.UTH)))
                );
            }
        });
        openSquid3ExampleItem = new MenuItem("Squid 3 Data");
        openSquid3ExampleItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                MenuUtils.getProjectView().getProject().addDataTable(FileMenuHelper.openExampleData(ExampleData.SQUID_3));
            } else {
                Topsoil.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openExampleData(ExampleData.SQUID_3)))
                );
            }
        });
        openExampleMenu = new Menu("Open Example", null,
                openUPbExampleItem,
                openUThExampleItem,
                openSquid3ExampleItem
        );

        saveProjectItem = new MenuItem("Save");
        saveProjectItem.setOnAction(event -> {
            FileMenuHelper.saveProject(MenuUtils.getProjectView().getProject());
        });
        saveProjectAsItem = new MenuItem("Save As...");
        saveProjectAsItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                if (! FileMenuHelper.saveProjectAs(MenuUtils.getProjectView().getProject())) {
                    TopsoilNotification.showNotification(TopsoilNotification.NotificationType.ERROR,
                                                         "Error",
                                                         "Could not save project.");
                }
            }
        });
        closeProjectItem = new MenuItem("Close Project");
        closeProjectItem.setOnAction(event -> {
            if (ProjectSerializer.getCurrentProjectPath() != null) {
                if (! FileMenuHelper.closeProject()) {
                    TopsoilNotification.showNotification(TopsoilNotification.NotificationType.ERROR,
                                                         "Error",
                                                         "Could not close project.");
                }
            }
        });

        fromFileItem = new MenuItem("From File");
        fromFileItem.setOnAction(event -> {
            File file = TopsoilFileChooser.openTableFile().showOpenDialog(Topsoil.getController().getPrimaryStage());
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
                        if (DataTableOptionsDialog.showDialog(table, Topsoil.getController().getPrimaryStage())) {
                            if (MenuUtils.isDataOpen()) {
                                MenuUtils.getProjectView().getProject().addDataTable(table);
                            } else {
                                Topsoil.getController().setProjectView(new ProjectView(new TopsoilProject(table)));
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
        fromClipboardItem = new MenuItem("From Clipboard");
        fromClipboardItem.setOnAction(event -> {
            String content = Clipboard.getSystemClipboard().getString();
            Delimiter delimiter = DataParser.guessDelimiter(content);
            Map<DataImportDialog.Key, Object> settings = DataImportDialog.showDialog("Clipboard", delimiter);
            if (settings != null) {
                delimiter = (Delimiter) settings.get(DataImportDialog.Key.DELIMITER);
                DataTemplate template = (DataTemplate) settings.get(DataImportDialog.Key.TEMPLATE);

                if (delimiter != null && template != null) {
                    DataTable table = FileMenuHelper.importTableFromString(content, delimiter, template);
                    if (DataTableOptionsDialog.showDialog(table, Topsoil.getController().getPrimaryStage())) {
                        if (MenuUtils.isDataOpen()) {
                            MenuUtils.getProjectView().getProject().addDataTable(table);
                        } else {
                            Topsoil.getController().setProjectView(new ProjectView(new TopsoilProject(table)));
                        }
                    }
                }
            }
        });
        importTableMenu = new Menu("Import Table", null,
                                   fromFileItem,
                                   fromClipboardItem
        );
        importTableMenu.setOnShown(event -> {
            fromClipboardItem.setDisable(! Clipboard.getSystemClipboard().hasString());
        });

        exportTableMenuItem = new MenuItem("Export Table...");
        exportTableMenuItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                DataTable table = MenuUtils.getCurrentTable();
                if (! FileMenuHelper.exportTableAs(table)) {
                    TopsoilNotification.showNotification(TopsoilNotification.NotificationType.ERROR,
                                                         "Error",
                                                         "Could not export table.");
                }
            }
        });

        exitTopsoilItem = new MenuItem("Exit Topsoil");
        exitTopsoilItem.setOnAction(event -> FileMenuHelper.exitTopsoilSafely());

        this.setOnShown(event -> {
            saveProjectItem.setDisable(ProjectSerializer.getCurrentProjectPath() == null);
            saveProjectAsItem.setDisable(! MenuUtils.isDataOpen());
            closeProjectItem.setDisable(ProjectSerializer.getCurrentProjectPath() == null);
            exportTableMenuItem.setDisable(! MenuUtils.isDataOpen());
        });

        this.getItems().addAll(
                newMenu,
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
    
}
