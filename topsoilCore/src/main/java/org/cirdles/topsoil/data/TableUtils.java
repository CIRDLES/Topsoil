package org.cirdles.topsoil.data;

import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.DependentVariable;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.plot.DataEntry;
import org.cirdles.topsoil.plot.Plot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TableUtils {

    private TableUtils() {}

    public static <T extends DataColumn<?>> List<T> getLeafColumns(List<T> columns) {
        List<T> leafColumns = new ArrayList<>();
        for (T column : columns) {
            leafColumns.addAll((List<T>) column.getLeafChildren());
        }
        return leafColumns;
    }

    public static List<DataEntry> getPlotData(DataTable table, Map<Variable<?>, DataColumn<?>> variableMap) {
        return convertToPlotData(table.getRows(), variableMap, table.getUncertainty());
    }

    public static <T extends DataComponent<T>> T findIn(String title, List<? extends T> list) {
        T target;
        for (T component : list) {
            target = component.find(title);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    public static <T> List<T> valuesForDataColumn(List<? extends DataRow> rows, DataColumn<T> column) {
        Validate.notNull(column, "Column cannot be null.");

        List<T> values = new ArrayList<>();
        for (DataRow row : rows) {
            if (row.countChildren() > 0) {
                values.addAll(valuesForDataColumn(row.getChildren(), column));
            } else {
                values.add(row.getValueForColumn(column));
            }
        }
        return values;
    }

    public static int maxFractionDigitsForColumn(List<? extends DataRow> rows, DataColumn<Number> column) {
        Validate.notNull(column, "Column cannot be null.");

        int maxFractionDigits = -1;
        for (DataRow row : rows) {
            if (row.countChildren() > 0) {
                maxFractionDigits = Math.max(maxFractionDigits, maxFractionDigitsForColumn(row.getChildren(), column));
            } else {
                maxFractionDigits = Math.max(maxFractionDigits, countFractionDigits(row.getValueForColumn(column)));
            }
        }
        return maxFractionDigits;
    }

    public static int countFractionDigits(Number number) {
        if (number != null) {
            String str = number.toString().toLowerCase();
            int dotIndex = str.indexOf(".");
            return str.substring(dotIndex + 1).length();
        }
        return -1;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Creates a list of data entries for a {@link Plot} from a list of {@code DataRow}s and a map associating plotting
     * {@code Variable}s with instances of {@code DataColumn}.
     * <p>
     * {@code DataRow} objects map {@code {@literal DataColumn<T>}} instances to values of type {@code T}, while
     * {@code DataEntry} objects map {@code {@literal Variable<T>}} instances to values of type {@code T}. Each new data
     * entry's values are set based on a data row, using the provided map. For each variable/column pair in the map, the
     * data entry's value for the variable is set to the data row's value for the column, if such a value exists. Data
     * entries are only created for data rows with no child rows.
     *
     * @param dataRows      List of DataRows in a table
     * @param variableMap   Map associating plotting variables to table columns
     * @return              List of DataEntry
     */
    private static <T extends DataRow> List<DataEntry> convertToPlotData(List<T> dataRows, Map<Variable<?>, DataColumn<?>> variableMap, Uncertainty uncertainty) {
        List<DataEntry> dataEntries = new ArrayList<>();
        DataEntry newEntry;
        Object value;
        for (T row : dataRows) {
            newEntry = new DataEntry();

            if (row.getChildren().size() > 0) {
                // DataRow has child rows, recursively create entries for list of child rows
                dataEntries.addAll(convertToPlotData(row.getChildren(), variableMap, uncertainty));
                continue;
            }

            // Extract values for each variable from the row
            newEntry.put(Variable.LABEL, row.getTitle());
            newEntry.put(Variable.SELECTED, row.isSelected());
            for (Map.Entry<Variable<?>, DataColumn<?>> e : variableMap.entrySet()) {
                Variable<?> variable = e.getKey();
                value = row.getValueForColumn(e.getValue());

                if (value == null) {
                    continue;
                }

                if (variable instanceof DependentVariable && Uncertainty.PERCENT_FORMATS.contains(uncertainty)) {
                    double doubleValue = (double) value;
                    Variable<Number> dependency = ((DependentVariable) variable).getDependency();
                    DataColumn<Number> dependentColumn = (DataColumn<Number>) variableMap.get(dependency);
                    doubleValue /= 100;
                    doubleValue *= (Double) row.getValueForColumn(dependentColumn);
                    value = doubleValue;
                }
                newEntry.put(variable, value);
            }
            dataEntries.add(newEntry);
        }
        return dataEntries;
    }

}
