package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.control.tree.TopsoilTreeTableView;
import org.cirdles.topsoil.app.util.FXMLUtils;

import java.io.IOException;

/**
 * A custom {@code Tab} associated with a particular {@code DataTable}.
 *
 * @author marottajb
 */
public class ProjectTableTab extends Tab {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private DataTable table;
    private TabView tabView;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ProjectTableTab(DataTable table) {
        super();
        this.table = table;
        this.tabView = new TabView(table);
        this.setContent(tabView);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public DataTable getDataTable() {
        return table;
    }

    //**********************************************//
    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    /**
     * The content of each {@link ProjectTableTab}.
     */
    class TabView extends VBox {

        //**********************************************//
        //                  CONSTANTS                   //
        //**********************************************//

        private static final String CONTROLLER_FXML = "project-table-tab-view.fxml";

        //**********************************************//
        //                   CONTROLS                   //
        //**********************************************//

        @FXML private TopsoilTreeTableView treeTableView;

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private DataTable table;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        TabView(DataTable table) {
            this.table = table;
            try {
                FXMLUtils.loadController(CONTROLLER_FXML, TabView.class, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void initialize() {
            treeTableView.setDataTable(table);
        }

    }
}
