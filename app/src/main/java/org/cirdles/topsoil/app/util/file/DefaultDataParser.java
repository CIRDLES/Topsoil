package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.ColumnTree;
import org.cirdles.topsoil.app.data.DataColumn;
import org.cirdles.topsoil.app.data.DataSegment;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author marottajb
 */
public class DefaultDataParser extends DataParser {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private String[][] cells;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DefaultDataParser(Path path) {
        super(path);
        this.cells = parseCells();
    }

    public DefaultDataParser(String content) {
        super(content);
        this.cells = parseCells();
    }

    public ColumnTree parseColumnTree() {
        // TODO see template project
        // 1. Count header rows
        boolean isHeader = true;
        int index = 0;
        do {
            try {
                Double.parseDouble(cells[index][0]);
                isHeader = false;
            } catch (NumberFormatException e) {
                index++;
            }
        } while (isHeader);

        String[][] headerRows = Arrays.copyOfRange(cells, 0, index + 1);
        return new ColumnTree(parseHeaders(headerRows, index + 1));
    }

    public DataSegment[] parseData() {
        ColumnTree columnTree = parseColumnTree();
        // TODO see template project
        return null;
    }

    private List<DataColumn> parseHeaders(String[][] headerRows, int numHeaders) {
        List<DataColumn> columns = new ArrayList<>();
        StringJoiner joiner;
        for (int i = 0; i < numHeaders; i++) {
            joiner = new StringJoiner("\n");
            for (int j = 0; j < headerRows.length; j++) {
                joiner.add(headerRows[j][i]);
            }
            columns.add(new DataColumn(joiner.toString()));
        }
        return columns;
    }


}
