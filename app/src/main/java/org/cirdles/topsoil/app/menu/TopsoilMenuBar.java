package org.cirdles.topsoil.app.menu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.serialization.Serializer;
import org.cirdles.topsoil.app.view.ProjectViewTabPane;
import org.cirdles.topsoil.app.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.menu.helpers.HelpMenuHelper;
import org.cirdles.topsoil.app.menu.helpers.ViewMenuHelper;
import org.cirdles.topsoil.app.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.view.TopsoilProjectView;
import org.cirdles.topsoil.plot.AbstractPlot;

import java.nio.file.Path;

/**
 * @author marottajb
 */
public class TopsoilMenuBar extends MenuBar {

    private TopsoilProjectView projectView;

    public TopsoilMenuBar() {
        super();
        this.getMenus().addAll(
                getFileMenu(),
                getEditMenu(),
                getViewMenu(),
                getVisualizationsMenu(),
                getHelpMenu()
        );
    }

    public void setProjectView(TopsoilProjectView projectView) {
        this.projectView = projectView;
    }

    private Menu getFileMenu() {
        MenuItem newProjectItem = new MenuItem("Project from Files");
        newProjectItem.setOnAction(event -> {
            TopsoilProjectView pv = FileMenuHelper.newProject();
            if (projectView != null) {
                // TODO Close old project
            }
            projectView = pv;
        });
        Menu newMenu = new Menu("New", null,
                                newProjectItem
        );

        MenuItem openProjectItem = new MenuItem ("Open...");
        openProjectItem.setOnAction(event -> {
            FileMenuHelper.openProject(projectView);
        });
        Menu openRecentProjectMenu = new Menu("Open Recent");
        openRecentProjectMenu.setDisable(true);
        openRecentProjectMenu.setOnShown(event -> {
            // @TODO
        });

        MenuItem openUPbSampleItem = new MenuItem("Uranium-Lead");
        openUPbSampleItem.setOnAction(event -> {
            if (projectView == null) {
                projectView = FileMenuHelper.openSampleData(SampleData.UPB);
            } else {
                FileMenuHelper.openSampleData(SampleData.UPB, projectView);
            }
        });
        MenuItem openUThSampleItem = new MenuItem("Uranium-Thorium");
        openUThSampleItem.setOnAction(event -> {
            if (projectView == null) {
                projectView = FileMenuHelper.openSampleData(SampleData.UTH);
            } else {
                FileMenuHelper.openSampleData(SampleData.UTH, projectView);
            }
        });
        MenuItem openSquid3SampleItem = new MenuItem("Squid 3 Data");
        openSquid3SampleItem.setOnAction(event -> {
            if (projectView == null) {
                projectView = FileMenuHelper.openSampleData(SampleData.SQUID3);
            } else {
                FileMenuHelper.openSampleData(SampleData.SQUID3, projectView);
            }
        });
        Menu openSampleMenu = new Menu("Open Sample", null,
                                       openUPbSampleItem,
                                       openUThSampleItem,
                                       openSquid3SampleItem
        );

        MenuItem saveProjectItem = new MenuItem("Save");
        saveProjectItem.disableProperty().bind(Bindings.isNotNull(Serializer.currentProjectFileProperty()));
        saveProjectItem.setOnAction(event -> {
            FileMenuHelper.saveProject(projectView);
        });
        MenuItem saveAsProjectItem = new MenuItem("Save As...");
        saveAsProjectItem.disableProperty().bind(Bindings.isNotNull(Serializer.currentProjectFileProperty()));
        saveAsProjectItem.setOnAction(event -> {
            Path path = null;
            // @TODO
            FileMenuHelper.saveProjectAs(projectView, path);
        });
        MenuItem closeProjectItem = new MenuItem("Close Project");
        closeProjectItem.disableProperty().bind(Bindings.isNotNull(Serializer.currentProjectFileProperty()));
        closeProjectItem.setOnAction(event -> {
            FileMenuHelper.closeProject(projectView);
        });

        Menu importTableMenu = getImportTableMenu();
        MenuItem exportTableMenuItem = new MenuItem("Export Table...");
        exportTableMenuItem.setOnAction(event -> {
            FileMenuHelper.exportTable(projectView);
        });

        MenuItem exitTopsoilItem = new MenuItem("Exit Topsoil");
        exitTopsoilItem.setOnAction(event -> FileMenuHelper.exitTopsoilSafely());

        Menu fileMenu = new Menu("File", null,
                newMenu,
                openProjectItem,
                openRecentProjectMenu,
                openSampleMenu,
                saveProjectItem,
                saveAsProjectItem,
                closeProjectItem,
                new SeparatorMenuItem(),
                importTableMenu,
                exportTableMenuItem,
                new SeparatorMenuItem(),
                exitTopsoilItem
        );

        return fileMenu;
    }

    private Menu getImportTableMenu() {
        MenuItem fromFileItem = new MenuItem("From File");
        fromFileItem.setOnAction(event -> {
            if (projectView == null) {
                projectView = FileMenuHelper.importTableFromFile();
            } else {
                if (! FileMenuHelper.importTableFromFile(projectView)) {
                    // TODO Show some error notification
                }
            }
        });
        MenuItem fromClipboardItem = new MenuItem("From Clipboard");
        fromClipboardItem.disableProperty().bind(new ReadOnlyBooleanWrapper(Clipboard.getSystemClipboard().hasString()));
        fromClipboardItem.setOnAction(event -> {
            if (projectView == null) {
                projectView = FileMenuHelper.importTableFromClipboard();
            } else {
                if (! FileMenuHelper.importTableFromClipboard(projectView)) {
                    // TODO Show some error notification
                }
            }
        });

        Menu importTableMenu =  new Menu("Import Table", null,
                                         fromFileItem,
                                         fromClipboardItem
        );

        return importTableMenu;
    }

    private Menu getEditMenu() {
        MenuItem preferencesItem = new MenuItem("Preferences...");
        preferencesItem.setDisable(true);

        Menu editMenu = new Menu("Edit", null,
                        preferencesItem
        );

        return editMenu;
    }

    private Menu getViewMenu() {
        Menu dataFormatMenu = new Menu("Set Data Format");
        MenuItem formatTextItem = new MenuItem("Text...");
        formatTextItem.setOnAction(event -> {
            ViewMenuHelper.editTextFormatting(projectView);
        });
        MenuItem formatNumberItem = new MenuItem("Numbers...");
        formatNumberItem.setOnAction(event -> {
            ViewMenuHelper.editNumberFormatting(projectView);
        });
        MenuItem formatDateItem = new MenuItem("Timestamps...");
        formatDateItem.setOnAction(event -> {
            ViewMenuHelper.editDateFormatting(projectView);
        });

        Menu viewMenu = new Menu("View", null,
                        dataFormatMenu
        );
        return viewMenu;
    }

    private Menu getVisualizationsMenu() {
        MenuItem generatePlotItem = new MenuItem("Generate Plot...");
        generatePlotItem.setOnAction(event -> {
            // @TODO Check to make sure proper variables are assigned
            ProjectViewTabPane projectTab =
                    (ProjectViewTabPane) projectView.getTabPane().getSelectionModel().getSelectedItem().getContent();
            VisualizationsMenuHelper.generatePlot(AbstractPlot.PlotType.SCATTER, projectTab.getDataTable(), projectView, null);
        });
        return new Menu("Visualizations", null,
                        generatePlotItem
        );
    }

    private Menu getHelpMenu() {
        MenuItem onlineHelpItem = new MenuItem("Online Help");
        onlineHelpItem.setOnAction(event -> HelpMenuHelper.openOnlineHelp());

        MenuItem reportIssueItem = new MenuItem("Report Issue");
        reportIssueItem.setOnAction(event -> HelpMenuHelper.openIssueReporter());

        MenuItem aboutItem = new MenuItem("About...");
        aboutItem.setOnAction(event -> HelpMenuHelper.openAboutScreen(MainWindow.getPrimaryStage()));

        return new Menu("Help", null,
                        onlineHelpItem,
                        reportIssueItem,
                        aboutItem
        );
    }

}
