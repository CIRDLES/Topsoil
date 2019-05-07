package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;

import java.util.*;

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
        ColumnRoot columnRoot = parseColumnTree(rows);
        List<DataColumn<?>> columns = columnRoot.getLeafNodes();
        int startIndex = countHeaderRows(rows);
        List<DataRow> dataRows = new ArrayList<>();
        for (int rowIndex = startIndex; rowIndex < rows.length; rowIndex++) {
            dataRows.add(
                    getDataRow(
                            "row" + (rowIndex - startIndex + 1),
                            rows[rowIndex],
                            columns
                    )
            );
        }
        DataRoot dataRoot = new DataRoot(new DataSegment("model", dataRows.toArray(new DataRow[]{})));

        return new DataTable(DataTemplate.DEFAULT, label, columnRoot, dataRoot);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private ColumnRoot parseColumnTree(String[][] rows) {
        List<DataColumn<?>> columns = new ArrayList<>();
        int numHeaderRows = countHeaderRows(rows);
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
                columns.add(DataColumn.numberColumn(label));
            } else {
                columns.add(DataColumn.stringColumn(label));
            }
        }
        return new ColumnRoot(columns.toArray(new DataColumn[]{}));
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

