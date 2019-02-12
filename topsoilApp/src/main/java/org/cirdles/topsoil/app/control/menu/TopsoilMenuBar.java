package org.cirdles.topsoil.app.control.menu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.model.DataColumn;
import org.cirdles.topsoil.app.model.DataTable;
import org.cirdles.topsoil.app.model.TopsoilProject;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.control.dialog.VariableChooserDialog;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.serialization.ProjectSerializer;
import org.cirdles.topsoil.app.control.ProjectTableTab;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.control.menu.helpers.HelpMenuHelper;
import org.cirdles.topsoil.app.control.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.variable.IndependentVariable;
import org.cirdles.topsoil.variable.Variable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            ProjectView projectView = new ProjectView(FileMenuHelper.newProject());
            Main.getController().setProjectView(projectView);
        });
        Menu newMenu = new Menu("New", null,
                                newProjectItem
        );

        MenuItem openProjectItem = new MenuItem ("Open...");
        openProjectItem.setOnAction(event -> {
            // TODO
        });
        Menu openRecentProjectMenu = new Menu("Open Recent");
        openRecentProjectMenu.setDisable(true);
        openRecentProjectMenu.setOnShown(event -> {
            // @TODO
        });

        MenuItem openUPbSampleItem = new MenuItem("Uranium-Lead");
        openUPbSampleItem.setOnAction(event -> {
            if (isProjectOpen()) {
                getCurrentProjectView().getProject().addDataTable(FileMenuHelper.openSampleData(SampleData.UPB));
            } else {
                Main.getController().setProjectView(new ProjectView(new TopsoilProject(
                                        FileMenuHelper.openSampleData(SampleData.UPB)))
                );
            }
        });
        MenuItem openUThSampleItem = new MenuItem("Uranium-Thorium");
        openUThSampleItem.setOnAction(event -> {
            if (isProjectOpen()) {
                getCurrentProjectView().getProject().addDataTable(FileMenuHelper.openSampleData(SampleData.UTH));
            } else {
                Main.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openSampleData(SampleData.UTH)))
                );
            }
        });
        MenuItem openSquid3SampleItem = new MenuItem("Squid 3 Data");
        openSquid3SampleItem.setOnAction(event -> {
            if (isProjectOpen()) {
                getCurrentProjectView().getProject().addDataTable(FileMenuHelper.openSampleData(SampleData.SQUID_3));
            } else {
                Main.getController().setProjectView(new ProjectView(new TopsoilProject(
                        FileMenuHelper.openSampleData(SampleData.SQUID_3)))
                );
            }
        });
        Menu openSampleMenu = new Menu("Open Sample", null,
                                       openUPbSampleItem,
                                       openUThSampleItem,
                                       openSquid3SampleItem
        );

        MenuItem saveProjectItem = new MenuItem("Save");
        saveProjectItem.disableProperty().bind(Bindings.isNotNull(ProjectSerializer.currentProjectPathProperty()));
        saveProjectItem.setOnAction(event -> {
            FileMenuHelper.saveProject(getCurrentProjectView().getProject());
        });
        MenuItem saveProjectAsItem = new MenuItem("Save As...");
        saveProjectAsItem.disableProperty().bind(Bindings.isNotNull(ProjectSerializer.currentProjectPathProperty()));
        saveProjectAsItem.setOnAction(event -> {
            if (getCurrentProjectView() != null) {
                if (! FileMenuHelper.saveProjectAs(getCurrentProjectView().getProject())) {
                    // TODO Error message
                }
            } else {
                // TODO Error message
            }
        });
        MenuItem closeProjectItem = new MenuItem("Close Project");
        closeProjectItem.disableProperty().bind(Bindings.isNotNull(ProjectSerializer.currentProjectPathProperty()));
        closeProjectItem.setOnAction(event -> {
            if (getCurrentProjectView() != null) {
                if (! FileMenuHelper.closeProject()) {
                    // TODO Error message
                }
            } else {
                // TODO Error message
            }
        });

        Menu importTableMenu = getImportTableMenu();
        MenuItem exportTableMenuItem = new MenuItem("Export Table...");
        exportTableMenuItem.setOnAction(event -> {
            if (getCurrentProjectView() != null) {
                DataTable table = ((ProjectTableTab) getCurrentProjectView().getTabPane().getSelectionModel()
                                                                            .getSelectedItem()).getDataTable();
                if (! FileMenuHelper.exportTableAs(table)) {
                    // TODO Error message
                }
            } else {
                // TODO Error message
            }
        });

        MenuItem exitTopsoilItem = new MenuItem("Exit Topsoil");
        exitTopsoilItem.setOnAction(event -> FileMenuHelper.exitTopsoilSafely());

        Menu fileMenu = new Menu("File", null,
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
        return fileMenu;
    }

    private Menu getImportTableMenu() {
        MenuItem fromFileItem = new MenuItem("From File");
        fromFileItem.setOnAction(event -> {
            File file = TopsoilFileChooser.openTableFile().showOpenDialog(Main.getController().getPrimaryStage());
            if (file.exists()) {
                Path path = Paths.get(file.toURI());
                // TODO Show dialog for DataTemplate/IsotopeSystem/Uncertainty
            }
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
        formatTextItem.setDisable(true);
        formatTextItem.setOnAction(event -> {
            // @TODO
        });
        MenuItem formatNumberItem = new MenuItem("Numbers...");
        formatNumberItem.setDisable(true);
        formatNumberItem.setOnAction(event -> {
            // @TODO
        });

        Menu viewMenu = new Menu("View", null,
                        dataFormatMenu
        );
        return viewMenu;
    }

    private Menu getVisualizationsMenu() {
        MenuItem assignVarsItem = new MenuItem("Assign Variables...");
        assignVarsItem.setOnAction(event -> {
            Map<Variable<?>, DataColumn<?>> selections = VariableChooserDialog.showDialog(getCurrentDataTable(),
                                                                                    Arrays.asList(IndependentVariable.X,
                                                                                                IndependentVariable.Y));
            getCurrentDataTable().setColumnsForAllVariables(selections);
        });

        MenuItem generatePlotItem = new MenuItem("Generate Plot...");
        generatePlotItem.setOnAction(event -> {
            // @TODO Check to make sure proper variables are assigned
            ProjectTableTab projectTab = (ProjectTableTab) getCurrentProjectView().getTabPane().getSelectionModel().getSelectedItem();
            VisualizationsMenuHelper.generatePlot(PlotType.SCATTER, projectTab.getDataTable(),
                                                  getCurrentProjectView().getProject(), null);
        });

        Menu visualizationsMenu = new Menu("Visualizations", null,
                                           assignVarsItem,
                                           new SeparatorMenuItem(),
                                           generatePlotItem
        );
        visualizationsMenu.setOnShown(event -> {
            assignVarsItem.setDisable(! isProjectOpen());
            generatePlotItem.setDisable(! isProjectOpen());
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

    private ProjectView getCurrentProjectView() {
        return isProjectOpen() ? (ProjectView) Main.getController().getMainContent() : null;
    }

    private DataTable getCurrentDataTable() {
        return ((ProjectTableTab) getCurrentProjectView().getTabPane().getSelectionModel().getSelectedItem()).getDataTable();
    }

    private boolean isProjectOpen() {
        return Main.getController().getMainContent() instanceof ProjectView;
    }

}
