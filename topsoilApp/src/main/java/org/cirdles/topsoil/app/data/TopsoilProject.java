package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.constant.Lambda;
import org.cirdles.topsoil.plot.PlotType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Table<PlotType, DataTable, TopsoilPlotView> openPlots = HashBasedTable.create();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(DataTable... tables) {
        dataTables.addAll(tables);
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

    public Table<PlotType, DataTable, TopsoilPlotView> getOpenPlots() {
        return openPlots;
    }

    public void addOpenPlot(PlotType plotType, DataTable dataTable, TopsoilPlotView plotView) {
        openPlots.put(plotType, dataTable, plotView);
    }

    public void removeOpenPlot(PlotType plotType, DataTable dataTable) {
        openPlots.remove(plotType, dataTable);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TopsoilProject) {
            TopsoilProject other = (TopsoilProject) object;
            if (this.getDataTables().size() != other.getDataTables().size()) {
                return false;
            }
            for (int i = 0; i < this.getDataTables().size(); i++) {
                if (! this.getDataTables().get(i).equals(other.getDataTables().get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        List<Object> objects = new ArrayList<>();
        Collections.addAll(objects, dataTables);
        return Objects.hash(objects.toArray());
    }

}
