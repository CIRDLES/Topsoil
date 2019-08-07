package org.cirdles.topsoil.file.parser;

import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTemplate;
import org.cirdles.topsoil.data.SimpleDataColumn;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.data.SimpleDataTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Parses value-separated data into a {@link DataTable}.
 *
 * This {@link DataParser} assumes that data is organized in a standard format, with one or more string header rows
 * followed by some data rows.
 *
 * @author marottajb
 */
public class DefaultDataParser extends AbstractDataParser {

    /** {@inheritDoc} */
    @Override
    protected DataTable parseDataTable(String[][] rows, String label) {
        int startIndex = countHeaderRows(rows);
        List<DataColumn<?>> columns = parseColumns(rows, startIndex);
        List<DataRow> dataRows = new ArrayList<>();
        DataRow row;
        for (int rowIndex = startIndex; rowIndex < rows.length; rowIndex++) {
            row = getTableRow(
                    "row" + (rowIndex - startIndex + 1),
                    rows[rowIndex],
                    columns
            );
            row.setSelected(true);
            dataRows.add(row);
        }

        return new SimpleDataTable(DataTemplate.DEFAULT, label, columns, dataRows);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private List<DataColumn<?>> parseColumns(String[][] rows, int numHeaderRows) {
        List<DataColumn<?>> columns = new ArrayList<>();
        Map<String, Integer> usedColumnLabels = new HashMap<>();
        String label;
        int labelFreq;
        StringJoiner joiner;
        Class clazz;

        for (int colIndex = 0; colIndex < rows[0].length; colIndex++) {
            joiner = new StringJoiner("\n");
            for (int hRowIndex = 0; hRowIndex < numHeaderRows; hRowIndex++) {
                if (! rows[hRowIndex][colIndex].isEmpty()) {
                    joiner.add(rows[hRowIndex][colIndex]);
                }
            }

            clazz = getColumnDataType(rows, colIndex, numHeaderRows);
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

            if (clazz == Number.class) {
                columns.add(new SimpleDataColumn<>(label, true, 0.0, Number.class));
            } else {
                columns.add(new SimpleDataColumn<>(label, true, "", String.class));
            }
        }

        return columns;
    }

    private int countHeaderRows(String[][] rows) {
        int count = 0;
        for (String[] row : rows) {
            if (isDouble(row[0])) {
                break;
            }
            count++;
        }
        return count;
    }

}

