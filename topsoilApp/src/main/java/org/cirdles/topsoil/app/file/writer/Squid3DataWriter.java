package org.cirdles.topsoil.app.file.writer;

import org.cirdles.topsoil.app.data.DataTable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author marottajb
 */
public class Squid3DataWriter implements DataWriter {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public boolean writeTableToFile(Path path, DataTable table) throws IOException {
//        int columnDepth = table.getColumnRoot().getDepth();
//        List<DataColumn<?>> columns = table.getColumnRoot().getLeafNodes();
//        List<DataRow> dataRows = table.getDataRows();
//        String[][] rows = new String[dataRows.size() + columnDepth][columns.size()];
//
//        // Get columns
//        String[] split;
//        DataColumn<?> column;
//        for (int col = 0; col < columns.size(); col++) {
//            column = columns.get(col);
//            split = column.getLabel().split("\n");
//            for (int row = 0; row < split.length; row++) {
//                rows[row][col] = split[row];
//            }
//        }
//
//        // Get data
//        int r = columnDepth;
//        int c;
//        for (DataRow row : table.getDataRows()) {
//            c = 0;
//            for (DataColumn<?> col : columns) {
//                DataValue<?> value = row.getValueForColumn(col);
//                rows[r][c] = value.getLabel();
//                c++;
//            }
//            r++;
//        }
//
//        // Write lines
//        TableFileExtension ext = TableFileExtension.getExtensionFromPath(path);
//        String[] lines = new String[rows.length];
//        for (int i = 0; i < lines.length; i++) {
//            lines[i] = String.join(ext.getDelimiter().getValue(), rows[i]);
//        }
//
//        return DataWriter.writeLines(path, lines);
        return false;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

}
