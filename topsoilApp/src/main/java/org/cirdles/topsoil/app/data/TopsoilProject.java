package org.cirdles.topsoil.app.data;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.javafx.PlotView;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents a working state of Topsoil data. Maintains a list of active {@link FXDataTable}s, and tracks which plots
 * belong to which tables.
 *
 * @author marottajb
 */
public class TopsoilProject {

    private ListProperty<FXDataTable> dataTables = new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<FXDataTable> dataTablesProperty() {
        return dataTables;
    }
    public final ObservableList<FXDataTable> getDataTables() {
        return FXCollections.unmodifiableObservableList(dataTables);
    }

    private final MapProperty<FXDataTable, ReadOnlyListProperty<PlotView>> plotMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
    public final ReadOnlyMapProperty<FXDataTable, ReadOnlyListProperty<PlotView>> plotMapProperty() {
        return plotMap;
    }
    public final ObservableMap<FXDataTable, ReadOnlyListProperty<PlotView>> getPlotMap() {
        return plotMap.get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(FXDataTable... tables) {
        addDataTables(tables);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void addDataTable(FXDataTable table) {
        plotMap.put(table, new SimpleListProperty<>(FXCollections.observableArrayList()));
        dataTables.add(table);
    }

    public void addDataTables(FXDataTable... tables) {
        for (FXDataTable table : tables) {
            addDataTable(table);
        }
    }

    public void removeDataTable(FXDataTable table) {
        dataTables.remove(table);
        ReadOnlyListProperty<PlotView> plotList = plotMap.get(table);
        for (PlotView plot : plotList) {
            ((Stage) plot.getScene().getWindow()).close();
        }
        plotMap.remove(table);
    }

    /**
     * Registers the {@code PlotView} for a table so that the plot can be tracked and updated.
     *
     * @param table     FXDataTable
     * @param plot      Plot
     */
    public void registerOpenPlot(FXDataTable table, PlotView plot) {
        if (! dataTables.contains(table)) {
            throw new IllegalArgumentException("Table is not contained in this project.");
        }

        ReadOnlyListProperty<PlotView> plotList = plotMap.get(table);
        if (plotList == null) {
            plotList = new SimpleListProperty<>(FXCollections.observableArrayList());
            plotMap.put(table, plotList);
        }
        plotList.add(plot);
    }

    /**
     * De-registers the specified plot for the table.
     *
     * @param table     FXDataTable
     * @param plot      PlotView
     */
    public void deregisterOpenPlot(FXDataTable table, PlotView plot) {
        if (! dataTables.contains(table)) {
            throw new IllegalArgumentException("Table is not contained in this project.");
        }

        ReadOnlyListProperty<PlotView> plotList = plotMap.get(table);
        if (plotList != null) {
            plotList.remove(plot);
        }
    }

}
