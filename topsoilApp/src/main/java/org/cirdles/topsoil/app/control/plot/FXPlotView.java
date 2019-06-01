package org.cirdles.topsoil.app.control.plot;

import javafx.collections.ListChangeListener;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.app.data.FXDataRow;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;

import java.util.Map;

public class FXPlotView extends PlotView {

    public FXPlotView(PlotType plotType, FXDataTable table, Map<Variable<?>, DataColumn<?>> variableMap, PlotOptions options) {
        super(plotType, table, variableMap, options);
    }

    @Override
    public void setDataTable(DataTable table, Map<Variable<?>, DataColumn<?>> variableMap) {
        super.setDataTable(table, variableMap);

        if (table instanceof FXDataTable) {
            ((FXDataTable) table).rowsProperty().addListener((ListChangeListener<FXDataRow>) c -> {
                while (c.next()) {
                    updateDataEntries();
                }
            });
        }
    }
}
