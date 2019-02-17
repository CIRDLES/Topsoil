package org.cirdles.topsoil.app.control.menu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.ProjectTableTab;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.control.dialog.DataImportDialog;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.util.file.ProjectSerializer;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.file.parser.DataParser;
import org.cirdles.topsoil.app.util.file.parser.Delimiter;

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
    private Menu openSampleMenu;
    private MenuItem openUPbSampleItem;
    private MenuItem openUThSampleItem;
    private MenuItem openSquid3SampleItem;
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
                Main.getController().setProjectView(projectView);
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
                Main.getController().setProjectView(projectView);
            }
        });
        openRecentProjectMenu = new Menu("Open Recent");
        openRecentProjectMenu.setDisable(true);
        openRecentProjectMenu.setOnShown(event -> {
            // @TODO
        });

        openUPbSampleItem = new MenuItem("Uranium-Lead");
        openUPbSampleItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                MenuUtils.getProjectView().getProject().addDataTable(FileMenuHelper.openSampleData(SampleData.UPB));
            } else {
                Main.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openSampleData(SampleData.UPB)))
                );
            }
        });
        openUThSampleItem = new MenuItem("Uranium-Thorium");
        openUThSampleItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                MenuUtils.getProjectView().getProject().addDataTable(FileMenuHelper.openSampleData(SampleData.UTH));
            } else {
                Main.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openSampleData(SampleData.UTH)))
                );
            }
        });
        openSquid3SampleItem = new MenuItem("Squid 3 Data");
        openSquid3SampleItem.setOnAction(event -> {
            if (MenuUtils.isDataOpen()) {
                MenuUtils.getProjectView().getProject().addDataTable(FileMenuHelper.openSampleData(SampleData.SQUID_3));
            } else {
                Main.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openSampleData(SampleData.SQUID_3)))
                );
            }
        });
        openSampleMenu = new Menu("Open Sample", null,
                                                                                 openUPbSampleItem,
                                                                                 openUThSampleItem,
                                                                                 openSquid3SampleItem
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
            File file = TopsoilFileChooser.openTableFile().showOpenDialog(Main.getController().getPrimaryStage());
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
                        if (DataTableOptionsDialog.showDialog(table, (Stage) fromFileItem.getGraphic().getScene().getWindow())) {
                            if (MenuUtils.isDataOpen()) {
                                MenuUtils.getProjectView().getProject().addDataTable(table);
                            } else {
                                Main.getController().setProjectView(new ProjectView(new TopsoilProject(table)));
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
                    if (DataTableOptionsDialog.showDialog(table, Main.getController().getPrimaryStage())) {
                        if (MenuUtils.isDataOpen()) {
                            MenuUtils.getProjectView().getProject().addDataTable(table);
                        } else {
                            Main.getController().setProjectView(new ProjectView(new TopsoilProject(table)));
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
                openSampleMenu,
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
