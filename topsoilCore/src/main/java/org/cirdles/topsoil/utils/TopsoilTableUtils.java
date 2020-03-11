package org.cirdles.topsoil.utils;

import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataComponent;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.plot.DataEntry;
import org.cirdles.topsoil.plot.Plot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TopsoilTableUtils {

    private static final char DECIMAL_SEPARATOR;
    static {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
        DECIMAL_SEPARATOR = df.getDecimalFormatSymbols().getDecimalSeparator();
    }

    private TopsoilTableUtils() {}

    public static <T extends DataColumn<?>> List<T> getLeafColumns(List<T> columns) {
        Validate.notNull(columns, "Columns cannot be null.");

        List<T> leafColumns = new ArrayList<>();
        for (T column : columns) {
            leafColumns.addAll((List<T>) column.getLeafChildren());
        }
        return leafColumns;
    }

    public static int countLeafColumns(List<? extends DataColumn<?>> columns) {
        Validate.notNull(columns, "Columns cannot be null.");

        return countLeafComponents(columns);
    }

    public static <T extends DataRow> List<T> getLeafRows(List<T> rows) {
        Validate.notNull(rows, "Rows cannot be null.");

        List<T> leafRows = new ArrayList<>();
        for (T row : rows) {
            leafRows.addAll((List<T>) row.getLeafChildren());
        }
        return leafRows;
    }

    public static int countLeafRows(List<? extends DataRow> rows) {
        Validate.notNull(rows, "Rows cannot be null.");
        return countLeafComponents(rows);
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

    public static int maxFractionDigitsForColumn(List<? extends DataRow> rows, DataColumn<Number> column, boolean scientificNotation) {
        Validate.notNull(column, "Column cannot be null.");

        int maxFractionDigits = -1;
        for (DataRow row : rows) {
            if (row.countChildren() > 0) {
                maxFractionDigits = Math.max(maxFractionDigits, maxFractionDigitsForColumn(row.getChildren(), column, scientificNotation));
            } else {
                if (scientificNotation) {
                    maxFractionDigits = Math.max(maxFractionDigits, countSignificantDigits(row.getValueForColumn(column)) - 1);
                } else {
                    maxFractionDigits = Math.max(maxFractionDigits, countFractionDigits(row.getValueForColumn(column)));
                }
            }
        }
        return maxFractionDigits;
    }

    public static int countFractionDigits(Number number) {
        if (number instanceof Double) {
            String str = number.toString().toLowerCase();
            int dotIndex = str.indexOf(DECIMAL_SEPARATOR);
            return str.substring(dotIndex + 1).length();
        } else if (number instanceof Integer) {
            return 0;
        }
        return -1;
    }

    /**
     * Counts the significant digits in a number by splitting its String representation on a regular expression that
     * identifies ranges of insignificant digits. The lengths of the resulting substrings are aggregated into the total
     * number of significant digits.
     *
     * @param number    Number
     * @return          int # of significant digits
     */
    public static int countSignificantDigits(Number number) {
        String str = number.toString();
        // if the number is a decimal value, use the first regex; if the number is an integer value, use the second
        String[] chunks = str.split((str.indexOf(DECIMAL_SEPARATOR) > -1) ? "(^0+(\\.?)0*|(~\\.)0+$|\\.)" : "(^0+|0+$)");
        int count = 0;
        for (String chunk : chunks) {
            count += chunk.length();
        }
        return count;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private static <T extends DataComponent<T>> int countLeafComponents(List<? extends T> components) {
        int count = 0;
        for (T component : components) {
            if (component.countChildren() > 0) {
                count += countLeafComponents(component.getChildren());
            } else {
                count += 1;
            }
        }
        return count;
    }

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
            newEntry.put(Variable.VISIBLE, row.isVisible());
            for (Map.Entry<Variable<?>, DataColumn<?>> e : variableMap.entrySet()) {
                Variable<?> variable = e.getKey();
                value = row.getValueForColumn(e.getValue());

                if (value == null) {
                    continue;
                }

                if (variable instanceof Variable.DependentVariable && Uncertainty.PERCENT_FORMATS.contains(uncertainty)) {
                    double doubleValue = (double) value;
                    Variable<Number> dependency = ((Variable.DependentVariable) variable).getDependency();
                    DataColumn<Number> dependentColumn = (DataColumn<Number>) variableMap.get(dependency);
                    doubleValue /= 100;
                    doubleValue *= row.getValueForColumn(dependentColumn).doubleValue();
                    value = doubleValue;
                }
                newEntry.put(variable, value);
            }

            // Make sure there are values for each variable
            for (Variable<?> variable : Variable.CLASSIC) {
                if (! newEntry.containsKey(variable)) {
                    newEntry.put(variable, variable.getDefaultValue());
                }
            }

            dataEntries.add(newEntry);
        }
        return dataEntries;
    }

}
