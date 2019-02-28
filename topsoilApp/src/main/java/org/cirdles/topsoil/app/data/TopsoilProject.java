package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.constant.Lambda;
import org.cirdles.topsoil.plot.PlotType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a working state of Topsoil data. Maintains a list of active {@link DataTable}s, and tracks which plots
 * belong to which tables.
 *
 * @author marottajb
 */
public class TopsoilProject implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -3769935813685997131L;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private transient ObservableList<DataTable> dataTables = FXCollections.observableArrayList();

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private transient Table<PlotType, DataTable, TopsoilPlotView> openPlots = HashBasedTable.create();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(DataTable... tables) {
        dataTables.addAll(tables);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ObservableList<DataTable> getDataTables() {
        return dataTables;
    }

    public void addDataTable(DataTable table) {
        dataTables.add(table);
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
        if (object instanceof TopsoilProject) {
            TopsoilProject other = (TopsoilProject) object;
            if (dataTables.size() != other.getDataTables().size()) {
                return false;
            }
            for (int i = 0; i < dataTables.size(); i++) {
                if (! dataTables.get(i).equals(other.getDataTables().get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(new ArrayList<>(dataTables));
        for (Lambda lambda : Lambda.values()) {
            out.writeDouble(lambda.getValue());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        dataTables = FXCollections.observableArrayList();
        dataTables.addAll((List<DataTable>) in.readObject());
        for (Lambda lambda : Lambda.values()) {
            lambda.setValue(in.readDouble());
        }
    }

}
