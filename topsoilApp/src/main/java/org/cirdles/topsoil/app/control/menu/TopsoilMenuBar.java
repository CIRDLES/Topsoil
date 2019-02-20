package org.cirdles.topsoil.app.control.menu;

import javafx.scene.control.*;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.control.dialog.VariableChooserDialog;
import org.cirdles.topsoil.app.control.ProjectTableTab;
import org.cirdles.topsoil.app.control.menu.helpers.HelpMenuHelper;
import org.cirdles.topsoil.app.control.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.variable.IndependentVariable;
import org.cirdles.topsoil.variable.Variable;

import java.util.Arrays;
import java.util.Map;

/**
 * The main {@code MenuBar} for the application.
 *
 * @author marottajb
 */
public class TopsoilMenuBar extends MenuBar {

    public TopsoilMenuBar() {
        super();
        this.getMenus().addAll(
                new FileMenu(),
                getEditMenu(),
                getViewMenu(),
                getVisualizationsMenu(),
                getHelpMenu()
        );
    }


    private Menu getEditMenu() {
        MenuItem preferencesItem = new MenuItem("Preferences...");
        preferencesItem.setDisable(true);

        MenuItem tableOptionsItem = new MenuItem("Table Options...");
        tableOptionsItem.setOnAction(event -> {
            DataTableOptionsDialog.showDialog(MenuUtils.getCurrentTable(), Main.getController().getPrimaryStage());
        });

        Menu editMenu = new Menu("Edit", null,
                                 preferencesItem,
                                 new SeparatorMenuItem(),
                                 tableOptionsItem
        );
        editMenu.setOnShown(event -> {
            tableOptionsItem.setDisable(! MenuUtils.isDataOpen());
        });

        return editMenu;
    }

    private Menu getViewMenu() {
        Menu dataFormatMenu = new Menu("Set Data Format");
        MenuItem formatTextItem = new MenuItem("Text...");
        formatTextItem.setDisable(true);
        formatTextItem.setOnAction(event -> {
            // @TODO
        });
        MenuItem formatNumberItem = new MenuItem("Numbers...");
        formatNumberItem.setDisable(true);
        formatNumberItem.setOnAction(event -> {
            // @TODO
        });
        dataFormatMenu.getItems().addAll(formatNumberItem);

        Menu viewMenu = new Menu("View", null,
                        dataFormatMenu
        );
        return viewMenu;
    }

    private Menu getVisualizationsMenu() {
        MenuItem assignVarsItem = new MenuItem("Assign Variables...");
        assignVarsItem.setOnAction(event -> {
            Map<Variable<?>, DataColumn<?>> selections = VariableChooserDialog.showDialog(MenuUtils.getCurrentTable(),
                                                                                    Arrays.asList(IndependentVariable.X,
                                                                                                IndependentVariable.Y));
            if (selections != null) {
                MenuUtils.getCurrentTable().setColumnsForAllVariables(selections);
            }
        });

        MenuItem generatePlotItem = new MenuItem("Generate Plot...");
        generatePlotItem.setOnAction(event -> {
            // @TODO Check to make sure proper variables are assigned
            ProjectTableTab projectTab = MenuUtils.getSelectedTableTab();
            VisualizationsMenuHelper.generatePlot(PlotType.SCATTER, projectTab.getDataTable(),
                                                  MenuUtils.getProjectView().getProject(), null);
        });

        Menu visualizationsMenu = new Menu("Visualizations", null,
                                           assignVarsItem,
                                           new SeparatorMenuItem(),
                                           generatePlotItem
        );
        visualizationsMenu.setOnShown(event -> {
            assignVarsItem.setDisable(! MenuUtils.isDataOpen());
            generatePlotItem.setDisable(! MenuUtils.isDataOpen());
        });
        return visualizationsMenu;
    }

    private Menu getHelpMenu() {
        MenuItem onlineHelpItem = new MenuItem("Online Help");
        onlineHelpItem.setOnAction(event -> HelpMenuHelper.openOnlineHelp());

        MenuItem reportIssueItem = new MenuItem("Report Issue");
        reportIssueItem.setOnAction(event -> HelpMenuHelper.openIssueReporter());

        MenuItem aboutItem = new MenuItem("About...");
        aboutItem.setOnAction(event -> HelpMenuHelper.openAboutScreen(Main.getController().getPrimaryStage()));

        return new Menu("Help", null,
                        onlineHelpItem,
                        reportIssueItem,
                        aboutItem
        );
    }

}
