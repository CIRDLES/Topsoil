package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.column.*;
import org.cirdles.topsoil.app.data.composite.DataComponent;
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
public class Squid3DataParser implements DataParser {

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
        String[][] rows = DataParser.readCells(DataParser.readLines(path), delimiter);
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
        List<DataComponent> topLevel = new ArrayList<>();
        int[] categoryIndices = readCategories(rows[0]);
        for (int i = 0; i < categoryIndices.length; i++) {
            topLevel.add(parseCategory(
                    rows,
                    categoryIndices[i],
                    (i == (categoryIndices.length - 1) ? -1 : categoryIndices[i + 1])
            ));
        }
        return new ColumnRoot(topLevel.toArray(new DataComponent[]{}));
    }

    private DataTable parseDataTable(String[][] rows, String label) {
        ColumnRoot columnRoot = parseColumnTree(rows);
        List<DataColumn<?>> columns = columnRoot.getLeafNodes();

        int[] segIndices = readDataSegments(rows);
        List<DataSegment> segments = new ArrayList<>();
        for (int i = 0; i < segIndices.length; i++) {
            segments.add(parseDataSegment(
                    rows,
                    segIndices[i],
                    (i < (segIndices.length - 1) ? segIndices[i + 1] : -1),
                    columns
            ));
        }
        DataRoot dataRoot = new DataRoot(segments.toArray(new DataSegment[]{}));
        return new DataTable(DataTemplate.SQUID_3, label, columnRoot, dataRoot);
    }

    /**
     * Parses a {@code DataCategory} from the provided start and end indices.
     *
     * @param rows         String[][] of data values
     * @param catIndex      start index of the category
     * @param nextCatIndex  index of the next category
     * @return              DataCategory
     */
    private DataCategory parseCategory(String[][] rows, int catIndex, int nextCatIndex) {
        String[] catRow = rows[0];
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
                str = rows[rowIndex][colIndex];
                if (! str.equals("")) {
                    joiner.add(str);
                }
            }
            str = joiner.toString().trim();
            if (! str.equals("")) {
                Class<?> clazz = DataParser.getColumnDataType(rows, colIndex, 5);
                if (clazz == Number.class) {
                    columns.add(new NumberColumn(joiner.toString()));
                } else {
                    columns.add(new StringColumn(joiner.toString()));
                }
            }
        }

        return new DataCategory(catLabel, columns.toArray(new DataColumn[]{}));
    }

    /**
     * Parses a {@code DataSegment} from the provided start and end indices.
     *
     * @param rows         String[][] of data values
     * @param segIndex      start index of the segment
     * @param nextSegIndex  index of the next segment
     * @param columns       List of DataColumns for the table
     * @return              DataSegment
     */
    private DataSegment parseDataSegment(String[][] rows, int segIndex, int nextSegIndex,
                                        List<DataColumn<?>> columns) {
        String segmentLabel = rows[segIndex][0];
        List<DataRow> dataRows = new ArrayList<>();
        String rowLabel;
        if (nextSegIndex == -1 || nextSegIndex > rows.length) {
            nextSegIndex = rows.length;
        }
        for (int rowIndex = segIndex + 1; rowIndex < nextSegIndex; rowIndex++) {
            rowLabel = rows[rowIndex][0];
            dataRows.add(DataParser.getDataRow(
                    rowLabel,
                    Arrays.copyOfRange(rows[rowIndex], 1, rows[rowIndex].length),
                    columns
            ));
        }
        return new DataSegment(segmentLabel, dataRows.toArray(new DataRow[]{}));
    }

    /**
     * Returns an array of ints representing the column indices of each of the top-level data categories.
     *
     * @param catRow    the top row of data
     * @return          category indices
     */
    private static int[] readCategories(String[] catRow) {
        List<Integer> idxs = new ArrayList<>();
        for (int index = 0; index < catRow.length; index++) {
            if (! "".equals(catRow[index])) {
                idxs.add(index);
            }
        }
        return convertIntArray(idxs);
    }

    /**
     * Returns an array of ints representing the row indices of each data segment.
     *
     * @param cells     String[][] of data values
     * @return          segment indices
     */
    private static int[] readDataSegments(String[][] cells) {
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
        }
        return convertIntArray(idxs);
    }

    private static int[] convertIntArray(List<Integer> integers) {
        int[] rtnval = new int[integers.size()];
        for (int index = 0; index < rtnval.length; index++) {
            rtnval[index] = integers.get(index);
        }
        return rtnval;
    }

}
