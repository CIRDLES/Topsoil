package org.cirdles.topsoil.app.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.DataTable;

import java.io.IOException;

/**
 * @author marottajb
 */
public class ProjectTableTab extends Tab {

    private DataTable table;
    private TabView tabView;

    public ProjectTableTab(DataTable table) {
        super();
        this.table = table;
        this.tabView = new TabView(table);
        this.setContent(tabView);
    }

    public DataTable getDataTable() {
        return table;
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    class TabView extends VBox {

        private static final String CONTROLLER_FXML = "project-table-tab-view.fxml";

        @FXML private TopsoilTreeTableView treeTableView;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        TabView(DataTable table) {
            try {
                final ResourceExtractor re = new ResourceExtractor(TabView.class);
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
            treeTableView.setDataTable(table);
        }

    }
}
