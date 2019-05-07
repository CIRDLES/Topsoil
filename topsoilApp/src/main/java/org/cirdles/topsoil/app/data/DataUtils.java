package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.plot.PlotDataEntry;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.variable.DependentVariable;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataUtils {

    private DataUtils() {}

    public static List<String> getDataErrors(DataTable table) {
        List<String> errors = new ArrayList<>();
        DataColumn<?> rhoDataColumn = table.getColumnForVariable(Variables.RHO);
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
    public static List<PlotDataEntry> getPlotData(DataTable table) {
        List<PlotDataEntry> plotData = new ArrayList<>();
        List<DataSegment> tableAliquots = table.getDataRoot().getChildren();
        Map<Variable<?>, DataColumn<?>> varMap = table.getVariableColumnMap();

        List<DataRow> rows;
        DataColumn column;
        PlotDataEntry entry;

        for (DataSegment aliquot : tableAliquots) {
            rows = aliquot.getChildren();
            for (DataRow row : rows) {
                entry = new PlotDataEntry();

                entry.set(Variables.LABEL, row.getLabel());
                entry.set(Variables.ALIQUOT, aliquot.getLabel());
                entry.set(Variables.SELECTED, row.isSelected());

                for (Variable var : Variables.NUMBER_TYPE) {
                    Object value;
                    column = varMap.get(var);
                    if (column != null) {
                        column = varMap.get(var);
                        value = row.getValueForColumn(column).getValue();
                        if (var instanceof DependentVariable && Uncertainty.PERCENT_FORMATS.contains(table.getUncertainty())) {
                            double doubleVal = (double) value;
                            DependentVariable dependentVariable = (DependentVariable) var;
                            Variable<Number> dependency = dependentVariable.getDependency();
                            DataColumn dependentColumn = varMap.get(dependency);
                            doubleVal /= 100;
                            doubleVal *= (Double) row.getValueForColumn(dependentColumn).getValue();
                            value = doubleVal;
                        }
                        if (var == Variables.RHO) {
                            double doubleVal = ((Number) value).doubleValue();
                            if (doubleVal < -1 || doubleVal > 1) {
                                value = var.getDefaultValue();
                            }
                        }
                    } else {
                        value = var.getDefaultValue();
                    }
                    entry.set(var, value);
                }
                plotData.add(entry);
            }
        }
        return plotData;
    }

    public static int countFractionDigits(Number number) {
        if (number != null) {
            String str = number.toString().toLowerCase();
            int dotIndex = str.indexOf(".");
            return str.substring(dotIndex + 1).length();
        }
        return -1;
    }

}
