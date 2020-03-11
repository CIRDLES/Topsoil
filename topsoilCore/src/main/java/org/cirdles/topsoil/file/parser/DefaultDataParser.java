package org.cirdles.topsoil.file.parser;

import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTemplate;
import org.cirdles.topsoil.data.DataTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static org.cirdles.topsoil.utils.TopsoilClassUtils.instantiate;

/**
 * Parses value-separated data into a {@link DataTable}.
 * <p>
 * This {@link DataParser} assumes that data is organized in a standard format, with one or more string header rows
 * followed by some data rows.
 *
 * @author marottajb
 */
public class DefaultDataParser<T extends DataTable<C, R>, C extends DataColumn<?>, R extends DataRow> extends AbstractDataParser<T, C, R> {

    private static final Class[] COLUMN_CONSTRUCTOR_ARG_TYPES = {String.class, Boolean.class, Object.class, Class.class};

    public DefaultDataParser(Class<T> tableClass) {
        super(DataTemplate.DEFAULT, tableClass);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    @Override
    protected List<C> parseColumns(String[][] cells, Object... args) {
        int headerRowCount = countHeaderRows(cells);
        List<C> columns = new ArrayList<>();
        Map<String, Integer> usedColumnLabels = new HashMap<>();
        String label;
        int labelFreq;
        StringJoiner joiner;
        Class<?> columnType;
        C newColumn;
        Object defaultValue;

        for (int colIndex = 0; colIndex < cells[0].length; colIndex++) {
            joiner = new StringJoiner("\n");
            for (int rowIndex = 0; rowIndex < headerRowCount; rowIndex++) {
                if (!cells[rowIndex][colIndex].isEmpty()) {
                    joiner.add(cells[rowIndex][colIndex]);
                }
            }

            columnType = getColumnDataType(cells, colIndex, headerRowCount);
            label = joiner.toString();

            if (label.equals("")) {
                label = "newColumn";
            }

            if (usedColumnLabels.containsKey(label)) {
                labelFreq = usedColumnLabels.get(label);
                usedColumnLabels.put(label, labelFreq + 1);
                label += ("(" + labelFreq + ")");
            } else {
                usedColumnLabels.put(label, 1);
            }

            if (columnType == Number.class) {
                defaultValue = 0.0;
            } else {
                defaultValue = "";
            }

            newColumn = instantiate(
                    columnClass,
                    COLUMN_CONSTRUCTOR_ARG_TYPES,
                    new Object[]{label, true, defaultValue, columnType}
            );
            columns.add(newColumn);
        }

        return columns;
    }

    @Override
    protected List<R> parseRows(String[][] cells, List<C> leafColumns, Object... args) {
        int headerRowCount = countHeaderRows(cells);
        List<R> dataRows = new ArrayList<>();
        R newRow;
        for (int rowIndex = headerRowCount; rowIndex < cells.length; rowIndex++) {
            newRow = getTableRow(
                    "row" + (rowIndex - headerRowCount + 1),
                    cells[rowIndex],
                    leafColumns
            );
            newRow.setSelected(true);
            dataRows.add(newRow);
        }
        return dataRows;
    }

}

