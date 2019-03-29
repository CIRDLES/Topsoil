package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.plot.PlotType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a working state of Topsoil data. Maintains a list of active {@link DataTable}s, and tracks which plots
 * belong to which tables.
 *
 * @author marottajb
 */
public class TopsoilProject {

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ObservableList<DataTable> dataTables = FXCollections.observableArrayList();
    public ObservableList<DataTable> getDataTables() {
        return dataTables;
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(DataTable... tables) {
        addDataTables(tables);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void addDataTable(DataTable table) {
        dataTables.add(table);
    }

    public void addDataTables(DataTable... tables) {
        for (DataTable table : tables) {
            addDataTable(table);
        }
    }

    public void removeDataTable(DataTable table) {
        dataTables.remove(table);
    }

}
