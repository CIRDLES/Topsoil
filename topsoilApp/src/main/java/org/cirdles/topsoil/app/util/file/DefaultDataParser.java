package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.ColumnTree;
import org.cirdles.topsoil.app.data.DataColumn;
import org.cirdles.topsoil.app.data.DataRow;
import org.cirdles.topsoil.app.data.DataSegment;

import java.nio.file.Path;
import java.util.*;

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
        String[][] headerRows = Arrays.copyOfRange(cells, 0, countHeaderRows());
        return new ColumnTree(parseHeaders(headerRows));
    }

    public DataSegment[] parseData() {
        ColumnTree columnTree = parseColumnTree();
        List<DataColumn> columns = columnTree.getLeafNodes();
        int startIndex = countHeaderRows();
        List<DataRow> rows = new ArrayList<>();
        Map<DataColumn, Object> valueMap;
        for (int rowIndex = startIndex; rowIndex < cells.length; rowIndex++) {
            valueMap = new HashMap<>();
            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                valueMap.put(columns.get(colIndex), cells[rowIndex][colIndex]);
            }
            rows.add(new DataRow("", valueMap));
        }
        return new DataSegment[]{ new DataSegment("", rows.toArray(new DataRow[]{})) };
    }

    private List<DataColumn> parseHeaders(String[][] headerRows) {
        List<DataColumn> columns = new ArrayList<>();
        StringJoiner joiner;
        for (int i = 0; i < headerRows[0].length; i++) {
            joiner = new StringJoiner("\n");
            for (int j = 0; j < headerRows.length; j++) {
                joiner.add(headerRows[j][i]);
            }
            columns.add(new DataColumn(joiner.toString()));
        }
        return columns;
    }

    private int countHeaderRows() {
        boolean isHeader = true;
        int count = 0;
        while (isHeader) {
            try {
                Double.parseDouble(cells[count][0]);
                isHeader = false;
            } catch (NumberFormatException e) {
                count++;
            }
        }
        return count;
    }


}
