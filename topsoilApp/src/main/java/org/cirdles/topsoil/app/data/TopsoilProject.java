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
import java.util.List;

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

    private ObjectProperty<Path> path = new SimpleObjectProperty<>();
    public ObjectProperty<Path> pathProperty() {
        return path;
    }
    public Path getPath() {
        return path.get();
    }
    public void setPath(Path path) {
        this.path.set(path);
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

}
