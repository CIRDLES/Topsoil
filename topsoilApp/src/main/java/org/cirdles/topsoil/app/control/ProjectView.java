package org.cirdles.topsoil.app.control;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.control.tree.ProjectTreeView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.io.IOException;

/**
 * The main view of Topsoil when there is data showing.
 *
 * @author marottajb
 */
public class ProjectView extends SplitPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "topsoil-project-view.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private TabPane tabPane;
    @FXML private Label projectViewLabel;
    @FXML private AnchorPane projectTreeViewPane;
    private ProjectTreeView projectTreeView;
    @FXML private AnchorPane constantsEditorPane;
    private ConstantsEditor constantsEditor;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private TopsoilProject project;
    private ObservableList<DataTable> dataTableList = FXCollections.observableArrayList();

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
        this.projectTreeView = new ProjectTreeView(project);
        FXMLUtils.setAnchorPaneBounds(projectTreeView, 0.0, 0.0, 0.0, 0.0);
        projectTreeViewPane.getChildren().add(projectTreeView);

        this.constantsEditor = new ConstantsEditor();
        FXMLUtils.setAnchorPaneBounds(constantsEditor, 0.0, 0.0, 0.0, 0.0);
        constantsEditorPane.getChildren().add(constantsEditor);

        for (DataTable table : project.getDataTables()) {
            addTabForTable(table);
        }
        project.getDataTables().addListener((ListChangeListener.Change<? extends DataTable> c) -> {
            while (c.next()) {
                for (DataTable table : c.getAddedSubList()) {
                    addTabForTable(table);
                }
            }
        });
//        Bindings.bindContent(dataTableList, project.getDataTables());
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public TabPane getTabPane() {
        return tabPane;
    }

    public TopsoilProject getProject() {
        return project;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Adds a new {@code Tab} to the {@code TabPane} for the {@code DataTable}.
     *
     * @param table DataTable
     */
    private void addTabForTable(DataTable table) {
        ProjectTableTab tableTab = new ProjectTableTab(table);
        tableTab.setOnClosed(event -> {
            project.removeDataTable(table);
            if (tabPane.getTabs().isEmpty()) {
                Main.getController().closeProjectView();
            }
        });
        tableTab.textProperty().bindBidirectional(table.labelProperty());
        tabPane.getTabs().add(tableTab);
        tabPane.getSelectionModel().select(tableTab);
    }

    /**
     * Removes the {@code Tab} for the specified {@code DataTable} from the {@code TabPane}.
     *
     * @param table DataTable
     */
    private void removeTabForTable(DataTable table) {
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
