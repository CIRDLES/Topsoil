package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @author marottajb
 */
public class DefaultDataWriter implements DataWriter {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public boolean writeTableToFile(Path path, DataTable table) throws IOException {
        int columnDepth = table.getColumnRoot().getDepth();
        Set<DataRow> dataRows = table.getDataRows();
        List<DataColumn<?>> columns = table.getDataColumns();
        String[][] rows = new String[dataRows.size() + columnDepth][columns.size()];

        // Get columns
        String[] split;
        DataColumn<?> column;
        for (int col = 0; col < columns.size(); col++) {
            column = columns.get(col);
            split = column.getLabel().split("\n");
            for (int row = 0; row < split.length; row++) {
                rows[row][col] = split[row];
            }
        }

        // Get data
        int r = columnDepth;
        int c;
        for (DataRow row : dataRows) {
            c = 0;
            for (DataColumn<?> col : columns) {
                rows[r][c] = row.getValueForColumn(col).getValue().toString();
                c++;
            }
            r++;
        }

        // Write lines
        TableFileExtension ext = TableFileExtension.getExtensionFromPath(path);
        String[] lines = new String[rows.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = String.join(ext.getDelimiter().getValue(), rows[i]);
        }

        return DataWriter.writeLines(path, lines);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//



}
