package org.cirdles.topsoil.app.util.file.writer;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author marottajb
 */
public class Squid3DataWriter implements DataWriter {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public boolean writeTableToFile(Path path, DataTable table) throws IOException {
        int columnDepth = 0;
        List<DataColumn<?>> columns = table.getColumnTree().getLeafNodes();
        for (DataColumn<?> column : columns) {
            columnDepth = Math.max(columnDepth, StringUtils.countOccurrencesOf(column.getLabel(), "\n"));
        }
        String[][] rows = new String[table.countLeafNodes() + columnDepth][table.countLeafNodes()];

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
        int c = 0;
        for (DataRow row : table.getDataRows()) {
            for (DataColumn<?> col : columns) {
                rows[r][c] = row.getValueForColumn(col).getLabel();
            }
        }

        // Write lines
        TableFileExtension ext = TableFileExtension.getExtensionFromPath(path);
        String[] lines = new String[rows.length];
        for (int i = 0; i < rows.length; i++) {
            lines[i] = String.join(ext.getDelimiter().getValue(), lines[i]);
        }

        return DataWriter.writeLines(path, lines);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

}
