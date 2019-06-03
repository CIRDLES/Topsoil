package org.cirdles.topsoil.app.control;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.control.data.FXDataTableViewer;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.ResourceBundles;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The main view of Topsoil when there is data showing.
 *
 * @author marottajb
 */
public class ProjectView extends SplitPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "project-view.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private AnchorPane tabPaneContainer;
    @FXML private TabPane tabPane;

    @FXML private Label projectSidebarLabel;
    @FXML private AnchorPane projectSidebarPane;
    private ProjectSidebar projectSidebar;

    @FXML private Label constantsEditorLabel;
    @FXML private AnchorPane constantsEditorPane;
    private ConstantsEditor constantsEditor;

    private VBox noTables = new VBox(new Label("No tables loaded. Import from the \"File\" menu."));

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private TopsoilProject project;

    ResourceBundle resources = ResourceBundles.MAIN.getBundle();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ProjectView(TopsoilProject project) {
        this.project = project;
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, ProjectView.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        projectSidebarLabel.setText(resources.getString("projectStructure"));
        constantsEditorLabel.setText(resources.getString("constantsEditor"));

        this.projectSidebar = new ProjectSidebar(project);
        FXMLUtils.setAnchorPaneConstraints(projectSidebar, 0.0, 0.0, 0.0, 0.0);
        projectSidebarPane.getChildren().add(projectSidebar);

        this.constantsEditor = new ConstantsEditor(project);
        FXMLUtils.setAnchorPaneConstraints(constantsEditor, 0.0, 0.0, 0.0, 0.0);
        constantsEditorPane.getChildren().add(constantsEditor);

        for (FXDataTable table : project.getDataTables()) {
            addTabForTable(table);
        }
        project.dataTablesProperty().addListener((ListChangeListener.Change<? extends FXDataTable> c) -> {
            while (c.next()) {
                for (FXDataTable table : c.getAddedSubList()) {
                    addTabForTable(table);
                }
            }
        });

        noTables.setAlignment(Pos.CENTER);
        FXMLUtils.setAnchorPaneConstraints(noTables, 0.0, 0.0, 0.0, 0.0);
        if (tabPane.getTabs().size() == 0) {
            tabPaneContainer.getChildren().setAll(noTables);
        }
        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> c) -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    if (tabPane.getTabs().size() == 0) {
                        tabPaneContainer.getChildren().setAll(noTables);
                    }
                }
                if (c.wasAdded()) {
                    if (c.getAddedSize() == tabPane.getTabs().size()) {
                        tabPaneContainer.getChildren().setAll(tabPane);
                    }
                }
            }
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public TopsoilProject getProject() {
        return project;
    }

    public FXDataTable getVisibleDataTable() {
        return ((ProjectTableTab) tabPane.getSelectionModel().getSelectedItem()).getDataTable();
    }

    public FXDataTableViewer getViewerForTable(FXDataTable table) {
        List<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            ProjectTableTab tableTab = (ProjectTableTab) tab;
            if (tableTab.getDataTable().equals(table)) {
                return tableTab.getDataTableViewer();
            }
        }
        return null;
    }

    public void selectDataTable(FXDataTable table) {
        List<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if (((ProjectTableTab) tab).getDataTable().equals(table)) {
                tabPane.getSelectionModel().select(tab);
            }
        }
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Adds a new {@code Tab} to the {@code TabPane} for the {@code DataTable}.
     *
     * @param table DataTable
     */
    private void addTabForTable(FXDataTable table) {
        ProjectTableTab tableTab = new ProjectTableTab(table);
        tableTab.setOnClosed(event -> project.removeDataTable(table));
        tableTab.textProperty().bindBidirectional(table.titleProperty());
        tabPane.getTabs().add(tableTab);
        tabPane.getSelectionModel().select(tableTab);
    }

    /**
     * Removes the {@code Tab} for the specified {@code DataTable} from the {@code TabPane}.
     *
     * @param table DataTable
     */
    private void removeTabForTable(FXDataTable table) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab instanceof ProjectTableTab) {
                if (((ProjectTableTab) tab).getDataTable().equals(table)) {
                    tabPane.getTabs().remove(tab);
                    break;
                }
            }
        }
    }

}
