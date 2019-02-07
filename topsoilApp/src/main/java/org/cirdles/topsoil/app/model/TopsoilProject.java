package org.cirdles.topsoil.app.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.plot.PlotType;

/**
 * @author marottajb
 */
public class TopsoilProject {

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ListProperty<DataTable> dataTableList = new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<DataTable> dataTableListProperty() {
        return dataTableList;
    }
    public final ObservableList<DataTable> getDataTableList() {
        return dataTableList.get();
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Table<PlotType, DataTable, TopsoilPlotView> openPlots = HashBasedTable.create();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(DataTable... tables) {
        dataTableList.addAll(tables);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void addDataTable(DataTable table) {
        dataTableList.add(table);
    }

    public boolean removeDataTable(DataTable table) {
        return dataTableList.remove(table);
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
