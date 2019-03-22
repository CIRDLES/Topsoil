package org.cirdles.topsoil.app.control;

import javafx.scene.control.Tab;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.control.tree.TopsoilTreeTableView;

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

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    ProjectTableTab(DataTable table) {
        super();
        this.table = table;
        this.setContent(new TopsoilTreeTableView(table));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public DataTable getDataTable() {
        return table;
    }

}
