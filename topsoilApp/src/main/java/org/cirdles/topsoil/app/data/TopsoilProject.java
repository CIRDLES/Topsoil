package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
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

    private transient ListProperty<DataTable> dataTableList;
    public ListProperty<DataTable> dataTableListProperty() {
        if (dataTableList == null) {
            dataTableList = new SimpleListProperty<>(FXCollections.observableArrayList());
        }
        return dataTableList;
    }
    public final ObservableList<DataTable> getDataTableList() {
        return dataTableListProperty().get();
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private transient Table<PlotType, DataTable, TopsoilPlotView> openPlots = HashBasedTable.create();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(DataTable... tables) {
        getDataTableList().addAll(tables);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void addDataTable(DataTable table) {
        getDataTableList().add(table);
    }

    public boolean removeDataTable(DataTable table) {
        return getDataTableList().remove(table);
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
            if (dataTableList.size() != other.getDataTableList().size()) {
                return false;
            }
            for (int i = 0; i < dataTableList.size(); i++) {
                if (! dataTableList.get(i).equals(other.getDataTableList().get(i))) {
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
        out.writeObject(new ArrayList<>(getDataTableList()));
        for (Lambda lambda : Lambda.values()) {
            out.writeDouble(lambda.getValue());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        getDataTableList().addAll((List<DataTable>) in.readObject());
        for (Lambda lambda : Lambda.values()) {
            lambda.setValue(in.readDouble());
        }
    }

}
