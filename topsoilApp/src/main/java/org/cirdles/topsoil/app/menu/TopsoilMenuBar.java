package org.cirdles.topsoil.app.menu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.util.serialization.ProjectSerializer;
import org.cirdles.topsoil.app.view.ProjectTableTab;
import org.cirdles.topsoil.app.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.menu.helpers.HelpMenuHelper;
import org.cirdles.topsoil.app.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.app.view.TopsoilProjectView;
import org.cirdles.topsoil.plot.PlotType;

/**
 * @author marottajb
 */
public class TopsoilMenuBar extends MenuBar {

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

    private Menu getFileMenu() {
        MenuItem newProjectItem = new MenuItem("Project from Files");
        newProjectItem.setOnAction(event -> {
            // @TODO
        });
        Menu newMenu = new Menu("New", null,
                                newProjectItem
        );

        MenuItem openProjectItem = new MenuItem ("Open...");
        openProjectItem.setOnAction(event -> {
            // @TODO
        });
        Menu openRecentProjectMenu = new Menu("Open Recent");
        openRecentProjectMenu.setDisable(true);
        openRecentProjectMenu.setOnShown(event -> {
            // @TODO
        });

        MenuItem openUPbSampleItem = new MenuItem("Uranium-Lead");
        openUPbSampleItem.setOnAction(event -> {
            // @TODO
        });
        MenuItem openUThSampleItem = new MenuItem("Uranium-Thorium");
        openUThSampleItem.setOnAction(event -> {
            // @TODO
        });
        MenuItem openSquid3SampleItem = new MenuItem("Squid 3 Data");
        openSquid3SampleItem.setOnAction(event -> {
            // @TODO
        });
        Menu openSampleMenu = new Menu("Open Sample", null,
                                       openUPbSampleItem,
                                       openUThSampleItem,
                                       openSquid3SampleItem
        );

        MenuItem saveProjectItem = new MenuItem("Save");
        saveProjectItem.disableProperty().bind(Bindings.isNotNull(ProjectSerializer.currentProjectFileProperty()));
        saveProjectItem.setOnAction(event -> {
            // @TODO
        });
        MenuItem saveAsProjectItem = new MenuItem("Save As...");
        saveAsProjectItem.disableProperty().bind(Bindings.isNotNull(ProjectSerializer.currentProjectFileProperty()));
        saveAsProjectItem.setOnAction(event -> {
            // @TODO
        });
        MenuItem closeProjectItem = new MenuItem("Close Project");
        closeProjectItem.disableProperty().bind(Bindings.isNotNull(ProjectSerializer.currentProjectFileProperty()));
        closeProjectItem.setOnAction(event -> {
            // @TODO
        });

        Menu importTableMenu = getImportTableMenu();
        MenuItem exportTableMenuItem = new MenuItem("Export Table...");
        exportTableMenuItem.setOnAction(event -> {
            // @TODO
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
            // @TODO
        });
        MenuItem fromClipboardItem = new MenuItem("From Clipboard");
        fromClipboardItem.disableProperty().bind(new ReadOnlyBooleanWrapper(Clipboard.getSystemClipboard().hasString()));
        fromClipboardItem.setOnAction(event -> {
            // @TODO
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
            // @TODO
        });
        MenuItem formatNumberItem = new MenuItem("Numbers...");
        formatNumberItem.setOnAction(event -> {
            // @TODO
        });
        MenuItem formatDateItem = new MenuItem("Timestamps...");
        formatDateItem.setOnAction(event -> {
            // @TODO
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
            ProjectTableTab projectTab = (ProjectTableTab) getCurrentProjectView().getTabPane().getSelectionModel().getSelectedItem();
            VisualizationsMenuHelper.generatePlot(PlotType.SCATTER, projectTab.getDataTable(),
                                                  getCurrentProjectView().getProject(), null);
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
        aboutItem.setOnAction(event -> HelpMenuHelper.openAboutScreen(Main.getPrimaryStage()));

        return new Menu("Help", null,
                        onlineHelpItem,
                        reportIssueItem,
                        aboutItem
        );
    }

    private TopsoilProjectView getCurrentProjectView() {
        return isProjectOpen() ? (TopsoilProjectView) Main.getController().getMainContent() : null;
    }

    private boolean isProjectOpen() {
        return Main.getController().getMainContent() instanceof TopsoilProjectView;
    }

}
