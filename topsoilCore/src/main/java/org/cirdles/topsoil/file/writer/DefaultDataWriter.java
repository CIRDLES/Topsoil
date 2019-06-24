package org.cirdles.topsoil.file.writer;

import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.file.Delimiter;

import java.util.List;

/**
 * @author marottajb
 */
public class DefaultDataWriter extends AbstractDataWriter {

    //**********************************************//
    //                PUBLIC METHODS                //
    //********************
    // **************************//

    /** {@inheritDoc} */
    @Override
    protected String[] linesFromData(DataTable table, Delimiter delimiter) {
        List<? extends DataRow> dataRows = table.getRows();
        List<? extends DataColumn<?>> columns = table.getLeafColumns();
        String[][] rows = new String[dataRows.size() + 1][columns.size()];

        // Write header rows
        String[] split;
        DataColumn<?> column;
        for (int col = 0; col < columns.size(); col++) {
            column = columns.get(col);
            split = column.getTitle().split("\n");
            for (int row = 0; row < split.length; row++) {
                rows[row][col] = split[row];
            }
        }

        // Write rows
        int r = 1;
        int c;
        for (DataRow row : dataRows) {
            c = 0;
            for (DataColumn<?> col : columns) {
                rows[r][c] = row.getValueForColumn(col).toString();
                c++;
            }
            r++;
        }

        String[] lines = new String[rows.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = String.join(delimiter.asString(), rows[i]);
        }

        return lines;
    }
}
