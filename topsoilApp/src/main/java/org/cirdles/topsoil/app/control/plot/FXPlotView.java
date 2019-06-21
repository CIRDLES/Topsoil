package org.cirdles.topsoil.app.control.plot;

import javafx.collections.ListChangeListener;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.app.data.FXDataRow;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.DataEntry;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;

import java.util.List;
import java.util.Map;

public class FXPlotView extends PlotView {

    private ListChangeListener<FXDataRow> tableListener;

    FXPlotView(PlotType plotType, PlotOptions options, FXDataTable table, Map<Variable<?>, DataColumn<?>> variableMap) {
        super(plotType, options, table, variableMap);
    }

    @Override
    public void setData(List<DataEntry> data) {
        DataTable oldTable = getDataTable();

        setTableListenerIfNull();
        if (oldTable instanceof FXDataTable) {
            ((FXDataTable) oldTable).rowsProperty().removeListener(tableListener);
        }

        super.setData(data);
    }

    @Override
    public void setData(DataTable table, Map<Variable<?>, DataColumn<?>> variableMap) {
        DataTable oldTable = getDataTable();

        setTableListenerIfNull();
        if (table instanceof FXDataTable) {
            ((FXDataTable) table).rowsProperty().addListener(tableListener);
        }
        if (oldTable instanceof FXDataTable) {
            ((FXDataTable) oldTable).rowsProperty().removeListener(tableListener);
        }

        super.setData(table, variableMap);
    }

    private void setTableListenerIfNull() {
        if (tableListener == null) {
            tableListener = c -> {
                while (c.next()) {
                    updateDataEntries();
                }
            };
        }
    }
}
