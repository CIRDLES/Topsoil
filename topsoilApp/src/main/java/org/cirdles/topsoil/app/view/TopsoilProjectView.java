package org.cirdles.topsoil.app.view;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.io.IOException;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilProjectView extends SplitPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "topsoil-project-view.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private TabPane tabPane;
    @FXML private ProjectTreeView projectTreeView;
    @FXML private Label projectViewLabel;
    @FXML private ConstantsEditor constantsEditor;

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
