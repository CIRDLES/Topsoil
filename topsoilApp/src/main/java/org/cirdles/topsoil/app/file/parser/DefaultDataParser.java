package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author marottajb
 */
public class DefaultDataParser implements DataParser {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc} */
    @Override
    public ColumnRoot parseColumnTree(Path path, String delimiter) throws IOException {
        String[][] rows = DataParser.readCells(DataParser.readLines(path), delimiter);
        return parseColumnTree(rows);
    }

    /** {@inheritDoc} */
    @Override
    public ColumnRoot parseColumnTree(String content, String delimiter) {
        String[][] rows = DataParser.readCells(DataParser.readLines(content), delimiter);
        return parseColumnTree(rows);
    }

    /** {@inheritDoc} */
    @Override
    public DataTable parseDataTable(Path path, String delimiter, String label) throws IOException {
        String[] lines = DataParser.readLines(path);
        String[][] rows = DataParser.readCells(lines, delimiter);
        return parseDataTable(rows, (label != null) ? label : path.getFileName().toString());
    }

    /** {@inheritDoc} */
    @Override
    public DataTable parseDataTable(String content, String delimiter, String label) {
        String[][] rows = DataParser.readCells(DataParser.readLines(content), delimiter);
        return parseDataTable(rows, label);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private ColumnRoot parseColumnTree(String[][] rows) {
        List<DataColumn<?>> columns = new ArrayList<>();
        int numHeaderRows = countHeaderRows(rows);
        StringJoiner joiner;
        Class clazz;
        for (int i = 0; i < rows[0].length; i++) {
            joiner = new StringJoiner("\n");
            for (int j = 0; j < numHeaderRows; j++) {
                if (! rows[j][i].isEmpty()) {
                    joiner.add(rows[j][i]);
                }
            }
            clazz = DataParser.getColumnDataType(rows, i, numHeaderRows);
            if (clazz == Number.class) {
                columns.add(DataColumn.numberColumn(joiner.toString()));
            } else {
                columns.add(DataColumn.stringColumn(joiner.toString()));
            }
        }
        return new ColumnRoot(columns.toArray(new DataColumn[]{}));
    }

    private DataTable parseDataTable(String[][] rows, String label) {
        ColumnRoot columnRoot = parseColumnTree(rows);
        List<DataColumn<?>> columns = columnRoot.getLeafNodes();
        int startIndex = countHeaderRows(rows);
        List<DataRow> dataRows = new ArrayList<>();
        for (int rowIndex = startIndex; rowIndex < rows.length; rowIndex++) {
            dataRows.add(DataParser.getDataRow(
                    "row" + (rowIndex - startIndex + 1),
                    rows[rowIndex],
                    columns
            ));
        }
        DataRoot dataRoot = new DataRoot(new DataSegment("model", dataRows.toArray(new DataRow[]{})));

        return new DataTable(DataTemplate.DEFAULT, label, columnRoot, dataRoot);
    }

    private int countHeaderRows(String[][] rows) {
        int count = 0;
        for (String[] row : rows) {
            if (DataParser.isDouble(row[0])) {
                break;
            }
            count++;
        }
        return count;
    }
}

