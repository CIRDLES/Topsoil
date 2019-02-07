package org.cirdles.topsoil.app.control;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.model.DataTable;
import org.cirdles.topsoil.app.model.TopsoilProject;

import java.io.IOException;

/**
 * @author marottajb
 */
public class TopsoilProjectView extends SplitPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "topsoil-project-control.fxml";

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

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProjectView(TopsoilProject project) {
        this.project = project;

        try {
            final ResourceExtractor re = new ResourceExtractor(TopsoilProjectView.class);
            final FXMLLoader loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
    }

    @FXML
    public void initialize() {
        this.projectTreeView = new ProjectTreeView(project);
        AnchorPane.setTopAnchor(projectTreeView, 0.0);
        AnchorPane.setRightAnchor(projectTreeView, 0.0);
        AnchorPane.setBottomAnchor(projectTreeView, 0.0);
        AnchorPane.setLeftAnchor(projectTreeView, 0.0);
        projectTreeViewPane.getChildren().add(projectTreeView);

        this.constantsEditor = new ConstantsEditor();
        AnchorPane.setTopAnchor(this.constantsEditor, 0.0);
        AnchorPane.setRightAnchor(this.constantsEditor, 0.0);
        AnchorPane.setBottomAnchor(this.constantsEditor, 0.0);
        AnchorPane.setLeftAnchor(this.constantsEditor, 0.0);
        constantsEditorPane.getChildren().add(constantsEditor);

        for (DataTable table : project.getDataTableList()) {
            addTabForTable(table);
        }
        project.dataTableListProperty().addListener((ListChangeListener.Change<? extends DataTable> c) -> {
            c.next();
            if (c.wasAdded()) {
                for (DataTable table : c.getAddedSubList()) {
                    addTabForTable(table);
                }
            }
            if (c.wasRemoved()) {
                for (DataTable table : c.getRemoved()) {
                    removeTabForTable(table);
                }
            }
        });
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

    private void addTabForTable(DataTable table) {
        ProjectTableTab tableTab = new ProjectTableTab(table);
        tableTab.textProperty().bindBidirectional(table.labelProperty());
        tabPane.getTabs().add(tableTab);
    }

    private void removeTabForTable(DataTable table) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab instanceof  ProjectTableTab) {
                if (((ProjectTableTab) tab).getDataTable().equals(table)) {
                    tabPane.getTabs().remove(tab);
                    break;
                }
            }
        }
    }

}
