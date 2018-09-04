package org.cirdles.topsoil.app.util.serialization;

import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.app.plot.PlotGenerationHandler;
import org.cirdles.topsoil.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.plot.TopsoilPlotView;
import org.cirdles.topsoil.variable.Variable;

import org.cirdles.topsoil.variable.Variables;
import org.cirdles.topsoil.app.tab.TopsoilDataView;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;

import org.cirdles.topsoil.app.table.ObservableTableData;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.plot.Plot;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.plot.PlotProperty;

import java.util.*;

/**
 * A serializable class which stores information about all open tables and plots for serialization to a .topsoil
 * file. Data is read from a {@link TopsoilTabPane} and placed into a serializable format.
 *
 * @author marottajb
 *
 * @see TopsoilSerializer
 * @see TopsoilTab
 * @see TopsoilTabPane
 */
class SerializableTopsoilSession implements Serializable {

    private static final long serialVersionUID = 5481508323179628701L;

    /**
     * A {@code List} of {@code Map}s that contain all of the data for the Topsoil session.
     */
    private List<Map<TableDataType, Serializable>> data;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs an instance of {@code SerializableTopsoilSession}, using
     * the provided {@code TopsoilTabPane} as a data source.
     *
     * @param tabs  a TopsoilTabPane which contains tables
     */
    public SerializableTopsoilSession(TopsoilTabPane tabs) {
        this.data = new ArrayList<>();
        List<TopsoilTab> topsoilTabs = tabs.getTopsoilTabs();

        for (TopsoilTab tab : topsoilTabs) {
            data.add( extractTableData(tab.getDataView().getData()) );
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Adds each stored table and its corresponding {@link Plot}s to the {@code TopsoilTabPane}.
     *
     * @param tabs  TopsoilTabPane
     */
    public void loadDataToTopsoilTabPane(TopsoilTabPane tabs) {
        ObservableTableData table;
        Double[][] data;
        String[] headers;
        IsotopeSystem isotopeType;
        UncertaintyFormat unctFormat;

        TopsoilDataView dataView;

        for (Map<TableDataType, Serializable> tableData : this.data) {
            data = (Double[][]) tableData.get(TableDataType.DATA);
            headers = (String[]) tableData.get(TableDataType.HEADERS);
            isotopeType = IsotopeSystem.valueOf(String.valueOf(tableData.get(TableDataType.ISOTOPE_TYPE)));
            unctFormat = UncertaintyFormat.valueOf(String.valueOf(tableData.get(TableDataType.UNCERTANTY_FORMAT)));

            table = new ObservableTableData(data, true, headers, isotopeType, unctFormat);

            table.setTitle(String.valueOf(tableData.get(TableDataType.TITLE)));
            tabs.add(table);

            dataView = tabs.getSelectedTab().getDataView();

            for (HashMap<PlotDataType, Serializable> plot
                    : (ArrayList<HashMap<PlotDataType, Serializable>>) tableData.get(TableDataType.PLOTS)) {
                this.loadPlot(dataView, plot);
            }

            HashMap<String, Integer> varData = (HashMap<String, Integer>) tableData.get(TableDataType.VARIABLE_ASSIGNMENTS);
            Variable<Number> variable;
            for (Map.Entry<String, Integer> entry : varData.entrySet()) {
                variable = null;
                for (Variable<Number> v : Variables.VARIABLE_LIST) {
                    if (v.getName().equals(entry.getKey())) {
                        variable = v;
                        break;
                    }
                }
                table.setVariableForColumn(entry.getValue(), variable);
            }
        }
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    /** {@inheritDoc}
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private HashMap<TableDataType, Serializable> extractTableData(ObservableTableData table) {

        HashMap<TableDataType, Serializable> tableData = new HashMap<>();

        tableData.put(TableDataType.TITLE, table.getTitle());
        tableData.put(TableDataType.HEADERS, table.getColumnHeaders());
        tableData.put(TableDataType.ISOTOPE_TYPE, table.getIsotopeType().name());
        tableData.put(TableDataType.UNCERTANTY_FORMAT, table.getUnctFormat().name());
        tableData.put(TableDataType.DATA, table.getData());

        List<TopsoilDataColumn> columns = table.getDataColumns();
        HashMap<String, Integer> varMap = new HashMap<>(); // String is the name of the variable, Integer is the index of the column
        for(int i = 0; i < columns.size(); i++) {
            if (columns.get(i).hasVariable()) {
                varMap.put(columns.get(i).getVariable().getName(), i);
            }
        }
        tableData.put(TableDataType.VARIABLE_ASSIGNMENTS, varMap);

        ArrayList<Map<PlotDataType, Serializable>> plots = new ArrayList<>();
        for (Map.Entry<TopsoilPlotType, TopsoilPlotView> entry : table.getOpenPlots().entrySet()) {
            plots.add(extractPlotData(
            		entry.getKey(),
		            new HashMap<>(entry.getValue().getPropertiesPanel().getPlotProperties())));
        }
        tableData.put(TableDataType.PLOTS, plots);

        return tableData;
    }

    private HashMap<PlotDataType, Serializable> extractPlotData(TopsoilPlotType type, HashMap<PlotProperty, Object> properties) {

        HashMap<PlotDataType, Serializable> plotData = new HashMap<>();

        plotData.put(PlotDataType.PLOT_TYPE, type);
        plotData.put(PlotDataType.PLOT_PROPERTIES, properties);

        return plotData;
    }

    /**
     * Re-generates a plot for the specified {@code TopsoilDataView}.
     *
     * @param dataView  a TopsoilDataView for table data
     * @param plot      a HashMap of information about the plot to re-create
     */
    private void loadPlot(TopsoilDataView dataView, HashMap<PlotDataType, Serializable> plot) {
        PlotGenerationHandler.generatePlotForDataView(
                dataView,
                TopsoilPlotType.valueOf(String.valueOf(plot.get(PlotDataType.PLOT_TYPE)))
        );
    }
}
