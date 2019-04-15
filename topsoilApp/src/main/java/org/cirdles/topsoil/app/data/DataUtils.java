package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.variable.DependentVariable;
import org.cirdles.topsoil.variable.IndependentVariable;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUtils {

    private DataUtils() {}

    public static List<String> getDataErrors(DataTable table) {
        List<String> errors = new ArrayList<>();
        DataColumn<?> rhoDataColumn = table.getVariableColumnMap().get(Variables.RHO);
        double value;
        if (rhoDataColumn != null && rhoDataColumn.getType().equals(Number.class)) {
            DataColumn<Number> rhoColumn = (DataColumn<Number>) rhoDataColumn;
            for (DataRow row : table.getDataRows()) {
                value = (double) row.getValueForColumn(rhoColumn).getValue();
                if (value < -1) {
                    errors.add(row.getLabel() + ": Rho value is less than -1. It will plot as 0.");
                } else if (value > 1) {
                    errors.add(row.getLabel() + ": Rho value is greater than 1. It will plot as 0.");
                }
            }
        }
        return errors;
    }

    /**
     * Extracts and returns the relevant plot data from a {@code DataTable} in a format that a {@link Plot} expects.
     *
     * @param table     DataTable
     * @return          plot data
     */
    public static List<Map<String, Object>> getPlotData(DataTable table) {
        List<Map<String, Object>> plotData = new ArrayList<>();
        List<DataSegment> tableAliquots = table.getDataRoot().getChildren();
        Map<Variable<?>, DataColumn<?>> varMap = table.getVariableColumnMap();

        List<DataRow> rows;
        DataColumn column;
        Map<String, Object> entry;

        for (DataSegment aliquot : tableAliquots) {
            rows = aliquot.getChildren();
            for (DataRow row : rows) {
                entry = new HashMap<>();

                entry.put(Variables.LABEL.getName(), row.getLabel());
                entry.put(Variables.ALIQUOT.getName(), aliquot.getLabel());
                entry.put(Variables.SELECTED.getName(), row.isSelected());

                for (Variable var : Variables.NUMBER_TYPE) {
                    Object value;
                    column = varMap.get(var);
                    if (column != null) {
                        column = varMap.get(var);
                        value = row.getValueForColumn(column).getValue();
                        if (var instanceof DependentVariable && Uncertainty.PERCENT_FORMATS.contains(table.getUncertainty())) {
                            double doubleVal = (double) value;
                            DependentVariable dependentVariable = (DependentVariable) var;
                            IndependentVariable dependency = (IndependentVariable) dependentVariable.getDependency();
                            DataColumn dependentColumn = varMap.get(dependency);
                            doubleVal /= 100;
                            doubleVal *= (Double) row.getValueForColumn(dependentColumn).getValue();
                            value = doubleVal;
                        }
                        if (var == IndependentVariable.RHO) {
                            double doubleVal = ((Number) value).doubleValue();
                            if (doubleVal < -1 || doubleVal > 1) {
                                value = var.defaultValue();
                            }
                        }
                    } else {
                        value = var.defaultValue();
                    }
                    entry.put(var.getName(), value);
                }
                plotData.add(entry);
            }
        }
        return plotData;
    }

}
