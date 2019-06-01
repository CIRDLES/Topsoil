package org.cirdles.topsoil.data;

import org.apache.commons.lang3.Validate;
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
        return convertToPlotData(table.getRows(), variableMap);
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
     * {@code DataRow} objects map {@code {@literal DataColumn<T, C>}} instances to values of type {@code T}, while
     * {@code DataEntry} objects map {@code {@literal Variable<T>}} instances to values of type {@code T}. Each new data
     * entry's values are set based on a data row, using the provided map. For each variable/column pair in the map, the
     * data entry's value for the variable is set to the data row's value for the column, if such a value exists. Data
     * entries are only created for data rows with no child rows.
     *
     * @param dataRows      List of DataRows in a table
     * @param variableMap   Map associating plotting variables to table columns
     * @return              List of DataEntry
     */
    private static <C extends DataRow> List<DataEntry> convertToPlotData(List<C> dataRows, Map<Variable<?>, DataColumn<?>> variableMap) {
        List<DataEntry> dataEntries = new ArrayList<>();
        DataEntry newEntry;
        Object value;
        for (C row : dataRows) {
            newEntry = new DataEntry();
            if (row.getChildren().size() == 0) {   // DataRow has no child rows, create new DataEntry
                newEntry.put(Variable.LABEL, row.getTitle());
                newEntry.put(Variable.SELECTED, row.isSelected());
                for (Map.Entry<Variable<?>, DataColumn<?>> e : variableMap.entrySet()) {
                    value = row.getValueForColumn(e.getValue());
                    if (value != null) {
                        newEntry.put(e.getKey(), value);
                    }
                }
                dataEntries.add(newEntry);
            } else {        // DataRow has child rows, recursively create entries for list of child rows
                dataEntries.addAll(convertToPlotData(row.getChildren(), variableMap));
            }
        }
        return dataEntries;
    }

}
