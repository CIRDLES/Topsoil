package org.cirdles.topsoil.app.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.node.DataNode;

import java.io.IOException;
import java.util.*;

/**
 * @author marottajb
 */
public class ProjectViewTab extends VBox {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "project-table-tab-view.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private TopsoilTreeTableView treeTableView;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private MapProperty<DataNode, BooleanProperty> nodeSelectionMap =
            new SimpleMapProperty<>(FXCollections.observableHashMap());
    public MapProperty<DataNode, BooleanProperty> nodeSelectionMapProperty() {
        return nodeSelectionMap;
    }
    public Map<DataNode, BooleanProperty> getNodeSelections() {
        return nodeSelectionMap.get();
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private DataTable dataTable;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    ProjectViewTab(DataTable table) {
        try {
            final ResourceExtractor re = new ResourceExtractor(ProjectViewTab.class);
            final FXMLLoader loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
    }

    @FXML
    protected void initialize() {
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable table) {
        this.dataTable = table;
        treeTableView.setDataTable(dataTable);
    }

}
