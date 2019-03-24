package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.plot.PlotType;

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

    private List<TopsoilPlotView> openPlots = new ArrayList<>();

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

    public List<TopsoilPlotView> getOpenPlots() {
        return new ArrayList<>(openPlots);
    }

    public void addOpenPlot(TopsoilPlotView plotView) {
        openPlots.add(plotView);
    }

    public void removeOpenPlot(TopsoilPlotView plotView) {
        openPlots.remove(plotView);
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
