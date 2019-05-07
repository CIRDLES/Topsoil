package org.cirdles.topsoil.app.data;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperties;
import org.cirdles.topsoil.plot.PlotType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents a working state of Topsoil data. Maintains a list of active {@link DataTable}s, and tracks which plots
 * belong to which tables.
 *
 * @author marottajb
 */
public class TopsoilProject {

    private MapProperty<Lambda, Number> lambdas = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
    public MapProperty<Lambda, Number> lambdasProperty() {
        return lambdas;
    }
    public final ObservableMap<Lambda, Number> getLambdas() {
        return FXCollections.unmodifiableObservableMap(lambdas);
    }

    private ListProperty<DataTable> dataTables = new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<DataTable> dataTablesProperty() {
        return dataTables;
    }
    public final ObservableList<DataTable> getDataTables() {
        return FXCollections.unmodifiableObservableList(dataTables);
    }

    private final ListProperty<OpenPlot> openPlots = new SimpleListProperty<>(FXCollections.observableArrayList());
    public final ObservableList<OpenPlot> getOpenPlots() {
        return FXCollections.unmodifiableObservableList(openPlots);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilProject(DataTable... tables) {
        addDataTables(tables);
        resetAllLambdas();
        lambdas.addListener((MapChangeListener<? super Lambda, ? super Number>) c -> {
            updatePlots();
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Number getLambdaValue(Lambda lambda) {
        return lambdas.get(lambda);
    }

    public void setLambdaValue(Lambda lambda, Number value) {
        lambdas.put(lambda, value);
    }

    public void resetLambdaValue(Lambda lambda) {
        lambdas.put(lambda, lambda.getDefaultValue());
    }

    public void resetAllLambdas() {
        for (Lambda lambda : Lambda.values()) {
            resetLambdaValue(lambda);
        }
    }

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

    /**
     * Registers the {@code PlotView} for a table so that the open plot can be tracked and updated.
     *
     * @param table     DataTable
     * @param plot      Plot
     */
    public void registerOpenPlot(DataTable table, Plot plot) {
        openPlots.add(new OpenPlot(table, plot));
    }

    /**
     * De-registers the plot of the specified plot type for the provided table.
     *
     * @param table     DataTable
     * @param plotType  PlotType
     */
    public void deregisterOpenPlot(DataTable table, PlotType plotType) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table) && openPlot.getPlot().getPlotType().equals(plotType)) {
                openPlots.remove(openPlot);
                break;
            }
        }
    }

    /**
     * Returns a list of the plot types that are currently open for the specified table.
     *
     * @param table     DataTable
     *
     * @return          List of PlotType
     */
    public List<PlotType> getOpenPlotTypesForTable(DataTable table) {
        List<PlotType> plotTypes = new ArrayList<>();
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table)) {
                plotTypes.add(openPlot.getPlot().getPlotType());
            }
        }
        return plotTypes;
    }

    /**
     * Updates the data for all open plots for the specified table.
     *
     * @param table     DataTable
     */
    public void updatePlotsForTable(DataTable table) {
        for (OpenPlot openPlot : openPlots) {
            if (openPlot.getTable().equals(table)) {
                openPlot.getPlot().setData(DataUtils.getPlotData(table));
            }
        }
    }

    public void updatePlots() {
        DataTable table;
        Plot plot;
        PlotProperties properties;
        for (OpenPlot openPlot : openPlots) {
            table = openPlot.getTable();
            plot = openPlot.getPlot();
            properties = plot.getProperties();

            properties.set(PlotProperties.LAMBDA_U234, getLambdaValue(Lambda.U234));
            properties.set(PlotProperties.LAMBDA_U235, getLambdaValue(Lambda.U235));
            properties.set(PlotProperties.LAMBDA_U238, getLambdaValue(Lambda.U238));
            properties.set(PlotProperties.LAMBDA_TH230, getLambdaValue(Lambda.Th230));

            plot.setData(DataUtils.getPlotData(table));
            plot.setProperties(properties);
        }
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    public static class OpenPlot {
        private DataTable table;
        private Plot plot;

        OpenPlot(DataTable table, Plot plot) {
            this.table = table;
            this.plot = plot;
        }

        /**
         * Returns the table associated with this plot.
         *
         * @return  DataTable
         */
        public DataTable getTable() {
            return table;
        }

        public Plot getPlot() {
            return plot;
        }

    }

}
