package org.cirdles.topsoil.app.util.file;

import org.cirdles.topsoil.app.model.*;
import org.cirdles.topsoil.app.model.composite.DataComponent;

import java.nio.file.Path;
import java.util.*;

/**
 * @author marottajb
 */
public class Squid3DataParser extends DataParser {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public Squid3DataParser(Path path) {
        super(path);
    }

    public Squid3DataParser(String content) {
        super(content);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /** {@inheritDoc} */
    @Override
    ColumnTree parseColumnTree() {
        List<DataComponent> topLevel = new ArrayList<>();
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

    /** {@inheritDoc} */
    @Override
    List<DataSegment> parseData() {
        ColumnTree columnTree = parseColumnTree();
        List<DataColumn<?>> columns = columnTree.getLeafNodes();

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

    /**
     * Parses a {@code DataCategory} from the provided start and end indices.
     *
     * @param cells         String[][] of data values
     * @param catIndex      start index of the category
     * @param nextCatIndex  index of the next category
     * @return              DataCategory
     */
    private DataCategory parseCategory(String[][] cells, int catIndex, int nextCatIndex) {
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
                Class clazz = getColumnDataType(colIndex, 5);
                if (clazz == Double.class) {
                    columns.add(new DataColumn<>(joiner.toString(), (Class<Double>) clazz));
                } else {
                    columns.add(new DataColumn<>(joiner.toString(), (Class<String>) clazz));
                }
            }
        }

        return new DataCategory(catLabel, columns.toArray(new DataColumn[]{}));
    }

    /**
     * Parses a {@code DataSegment} from the provided start and end indices.
     *
     * @param cells         String[][] of data values
     * @param segIndex      start index of the segment
     * @param nextSegIndex  index of the next segment
     * @param columns       List of DataColumns for the table
     * @return              DataSegment
     */
    private DataSegment parseDataSegment(String[][] cells, int segIndex, int nextSegIndex,
                                        List<DataColumn<?>> columns) {
        String segmentLabel = cells[segIndex][0];
        List<DataRow> dataRows = new ArrayList<>();
        String rowLabel;
        if (nextSegIndex == -1 || nextSegIndex > cells.length) {
            nextSegIndex = cells.length;
        }
        for (int rowIndex = segIndex + 1; rowIndex < nextSegIndex; rowIndex++) {
            rowLabel = cells[rowIndex][0];
            dataRows.add(new DataRow(rowLabel, getValuesForRow(cells[rowIndex], columns)));
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
        int[] rtnval = new int[idxs.size()];
        for (int index = 0; index < rtnval.length; index++) {
            rtnval[index] = idxs.get(index);
        }
        return rtnval;
    }

    /**
     * Returns an array of ints representing the row indices of each data segment.
     *
     * @param cells     String[][] of data values
     * @return          segment indices
     */
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
