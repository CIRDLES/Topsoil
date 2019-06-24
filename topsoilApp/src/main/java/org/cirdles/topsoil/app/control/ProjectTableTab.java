package org.cirdles.topsoil.app.control;

import javafx.scene.control.Tab;
import org.cirdles.topsoil.app.control.data.FXDataTableViewer;
import org.cirdles.topsoil.app.data.FXDataTable;

/**
 * A custom {@code Tab} associated with a particular {@code DataTable}.
 *
 * @author marottajb
 */
public class ProjectTableTab extends Tab {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private FXDataTable table;
    private FXDataTableViewer viewer;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    ProjectTableTab(FXDataTable table) {
        super();
        this.table = table;
        this.viewer = new FXDataTableViewer(table);
        this.setContent(viewer);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public FXDataTable getDataTable() {
        return table;
    }

    public FXDataTableViewer getDataTableViewer() {
        return viewer;
    }

}
