package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.model.DataTable;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.control.treetable.TopsoilTreeTableView;

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
        this.setOnClosed(event -> {
            if (((ProjectView) Main.getController().getMainContent()).getTabPane().getTabs().isEmpty()) {
                FileMenuHelper.closeProject();
            }
        });
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
