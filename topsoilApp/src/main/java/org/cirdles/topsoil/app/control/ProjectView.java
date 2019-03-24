package org.cirdles.topsoil.app.control;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.control.tree.ProjectTreeView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.util.ResourceBundles;

import java.io.IOException;
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

    @FXML private Label projectTreeViewLabel;
    @FXML private AnchorPane projectTreeViewPane;
    private ProjectTreeView projectTreeView;

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
        projectTreeViewLabel.setText(resources.getString("projectStructure"));
        constantsEditorLabel.setText(resources.getString("constantsEditor"));

        this.projectTreeView = new ProjectTreeView(project);
        FXMLUtils.setAnchorPaneConstraints(projectTreeView, 0.0, 0.0, 0.0, 0.0);
        projectTreeViewPane.getChildren().add(projectTreeView);

        this.constantsEditor = new ConstantsEditor();
        FXMLUtils.setAnchorPaneConstraints(constantsEditor, 0.0, 0.0, 0.0, 0.0);
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

    public DataTable getVisibleDataTable() {
        return ((ProjectTableTab) tabPane.getSelectionModel().getSelectedItem()).getDataTable();
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
//            if (tabPane.getTabs().isEmpty()) {
//                Topsoil.getController().setHomeView();
//            }
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
