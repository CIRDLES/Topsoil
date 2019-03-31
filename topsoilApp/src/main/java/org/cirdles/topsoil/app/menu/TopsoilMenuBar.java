package org.cirdles.topsoil.app.menu;

import javafx.scene.control.*;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.menu.helpers.HelpMenuHelper;
import org.cirdles.topsoil.app.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.app.util.ResourceBundles;
import org.cirdles.topsoil.plot.PlotType;

import java.util.ResourceBundle;

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
                new FileMenu(),
                getEditMenu(),
//                getViewMenu(),
                getVisualizationsMenu(),
                getHelpMenu()
        );
    }

    private Menu getEditMenu() {
        MenuItem preferencesItem = new MenuItem(resources.getString("preferences"));
        preferencesItem.setDisable(true);

        MenuItem tableOptionsItem = new MenuItem(resources.getString("tableOptions"));
        tableOptionsItem.setOnAction(event -> {
            DataTableOptionsDialog.showDialog(MenuUtils.getCurrentDataTable(), Topsoil.getPrimaryStage());
        });

        Menu editMenu = new Menu(resources.getString("editMenu"), null,
                tableOptionsItem
//                new SeparatorMenuItem(),
//                preferencesItem
        );
        editMenu.setOnShown(event -> {
            tableOptionsItem.setDisable(ProjectManager.getProject() == null);
        });

        return editMenu;
    }

    private Menu getViewMenu() {
        Menu viewMenu = new Menu(resources.getString("viewMenu"), null);
        return viewMenu;
    }

    private Menu getVisualizationsMenu() {
        MenuItem generatePlotItem = new MenuItem(resources.getString("generatePlot"));
        generatePlotItem.setOnAction(event -> {
            // @TODO Check to make sure proper variables are assigned
            VisualizationsMenuHelper.generatePlot(
                    PlotType.SCATTER,
                    MenuUtils.getCurrentDataTable(),
                    null);
        });

        Menu visualizationsMenu = new Menu(resources.getString("visualizationsMenu"), null,
                                           generatePlotItem
        );
        visualizationsMenu.setOnShown(event -> {
            generatePlotItem.setDisable(ProjectManager.getProject() == null);
        });
        return visualizationsMenu;
    }

    private Menu getHelpMenu() {
        MenuItem onlineHelpItem = new MenuItem(resources.getString("onlineHelp"));
        onlineHelpItem.setOnAction(event -> HelpMenuHelper.openOnlineHelp());

        MenuItem reportIssueItem = new MenuItem(resources.getString("reportIssue"));
        reportIssueItem.setOnAction(event -> HelpMenuHelper.openIssueReporter());

        MenuItem aboutItem = new MenuItem(resources.getString("about"));
        aboutItem.setOnAction(event -> HelpMenuHelper.openAboutScreen(Topsoil.getPrimaryStage()));

        return new Menu(resources.getString("helpMenu"), null,
                        onlineHelpItem,
                        reportIssueItem,
                        aboutItem
        );
    }

}
