package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.node.DataNode;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;

/**
 * @author marottajb
 */
public class Squid3DataParser extends DataParser {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private String[][] cells;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public Squid3DataParser(Path path) {
        super(path);
        this.cells = parseCells();
    }

    public Squid3DataParser(String content) {
        super(content);
        this.cells = parseCells();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    ColumnTree parseColumnTree() {
        List<DataNode> topLevel = new ArrayList<>();
        int[] categoryIndices = readCategories(cells[0]);
        for (int i = 0; i < categoryIndices.length; i++) {
            topLevel.add(parseCategory(
                    cells,
                    categoryIndices[i],
                    (i == (categoryIndices.length - 1) ? -1 : categoryIndices[i + 1])
            ));
        }
        return new ColumnTree(topLevel);
    }

    List<DataSegment> parseData() {
        ColumnTree columnTree = parseColumnTree();
        List<DataColumn> columns = columnTree.getLeafNodes();

        Integer[] segIndices = readDataSegments(cells);
        List<DataSegment> dataSegments = new ArrayList<>();
        for (int i = 0; i < segIndices.length; i++) {
            dataSegments.add(parseDataSegment(
                    cells,
                    segIndices[i],
                    (i < (segIndices.length - 1) ? segIndices[i + 1] : -1),
                    columns
            ));
        }
        return dataSegments;
    }

    static DataCategory parseCategory(String[][] cells, int catIndex, int nextCatIndex) {
        String[] catRow = cells[0];
        String catLabel = catRow[catIndex];

        List<DataColumn> columns = new ArrayList<>();
        StringJoiner joiner;
        String str;
        if (nextCatIndex == -1 || nextCatIndex > catRow.length) {
            nextCatIndex = catRow.length;
        }
        for (int colIndex = catIndex; colIndex < nextCatIndex; colIndex++) {
            joiner = new StringJoiner(" ");
            for (int rowIndex = 1; rowIndex < 5; rowIndex++) {
                str = cells[rowIndex][colIndex];
                if (! str.equals("")) {
                    joiner.add(str);
                }
            }
            str = joiner.toString().trim();
            if (! str.equals("")) {
                columns.add(new DataColumn(joiner.toString()));
            }
        }

        return new DataCategory(catLabel, columns.toArray(new DataColumn[]{}));
    }

    static DataSegment parseDataSegment(String[][] rows, int startIndex, int nextSegIndex, List<DataColumn> columns) {
        String segmentLabel = rows[startIndex][0];
        List<DataRow> dataRows = new ArrayList<>();
        String rowLabel;
        Map<DataColumn, Object> valueMap;
        if (nextSegIndex == -1 || nextSegIndex > rows.length) {
            nextSegIndex = rows.length;
        }
        for (int rowIndex = startIndex + 1; rowIndex < nextSegIndex; rowIndex++) {
            rowLabel = rows[rowIndex][0];
            valueMap = new HashMap<>();
            for (int colIndex = 1; colIndex < rows[rowIndex].length - 1; colIndex++) {
                valueMap.put(columns.get(colIndex - 1), rows[rowIndex][colIndex]);
            }
            dataRows.add(new DataRow(rowLabel, valueMap));
        }
        return new DataSegment(segmentLabel, dataRows.toArray(new DataRow[]{}));
    }

    private static int[] readCategories(String[] catRow) {
        List<Integer> idxs = new ArrayList<>();
        for (int index = 0; index < catRow.length; index++) {
            if (! "".equals(catRow[index])) {
                idxs.add(index);
            }
        }
        int[] rtnval = new int[idxs.size()];
        for (int index = 0; index < rtnval.length; index++) {
            rtnval[index] = idxs.get(index);
        }
        return rtnval;
    }

    private static Integer[] readDataSegments(String[][] cells) {
        List<Integer> idxs = new ArrayList<>();

        String last = cells[5][0];
        String current;
        if (! "".equals(last)) {
            idxs.add(5);
            for (int index = 6; index < cells.length; index++) {
                current = cells[index][0];
                if (! current.startsWith(last)) {
                    idxs.add(index);
                    last = current;
                }
            }
            int[] rtnval = new int[idxs.size()];
            for (int index = 0; index < rtnval.length; index++) {
                rtnval[index] = idxs.get(index);
            }
        }

        return idxs.toArray(new Integer[]{});
    }

}
