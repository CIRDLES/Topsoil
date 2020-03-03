package org.cirdles.topsoil.file.parser;

import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTemplate;
import org.cirdles.topsoil.data.SimpleDataColumn;
import org.cirdles.topsoil.data.SimpleDataRow;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.data.SimpleDataTable;
import org.cirdles.topsoil.data.TableUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Parses value-separated data into a {@link DataTable}.
 * <p>
 * This {@link DataParser} assumes that data is exported in a specific format from Squid 3.
 *
 * @author marottajb
 */
public class Squid3DataParser<T extends DataTable, C extends DataColumn<?>, R extends DataRow> extends AbstractDataParser<T, C, R> {

    private Class<T> tableClass;
    private Class<C> columnClass;
    private Class<R> rowClass;

    public Squid3DataParser(Class<T> tableClass, Class<C> columnClass, Class<R> rowClass) {
        super(tableClass, columnClass, rowClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T parseDataTable(String[][] rows, String label) {
        List<C> columns = parseHeaders(rows);
        List<C> leafColumns = TableUtils.getLeafColumns(columns);

        int[] segIndices = readAliquots(rows);
        List<R> dataRows = new ArrayList<>();
        for (int i = 0; i < segIndices.length; i++) {
            dataRows.add(parseAliquot(
                    rows,
                    segIndices[i],
                    (i < (segIndices.length - 1) ? segIndices[i + 1] : -1),
                    leafColumns
            ));
        }

        T table = new SimpleDataTable(DataTemplate.SQUID_3, label, columns, dataRows);
//        prepareTable(table);
        return table;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private List<C> parseHeaders(String[][] rows) {
        List<C> headers = new ArrayList<>();
        int[] categoryIndices = readCategories(rows[0]);
        Map<String, Integer> usedColumnLabels = new HashMap<>();
        for (int i = 0; i < categoryIndices.length; i++) {
            headers.add(parseCategory(
                    rows,
                    categoryIndices[i],
                    (i == (categoryIndices.length - 1) ? -1 : categoryIndices[i + 1]),
                    usedColumnLabels
            ));
        }
        return headers;
    }

    @SuppressWarnings("unchecked")
    private DataColumn parseCategory(String[][] rows, int catIndex, int nextCatIndex, Map<String, Integer> usedColumnLabels) {
        int labelFreq;
        String[] catRow = rows[0];
        String catLabel = catRow[catIndex];
        if (usedColumnLabels.containsKey(catLabel)) {
            labelFreq = usedColumnLabels.get(catLabel);
            usedColumnLabels.put(catLabel, labelFreq + 1);
            catLabel += ("(" + labelFreq + ")");
        } else {
            usedColumnLabels.put(catLabel, 1);
        }

        List<C> columns = new ArrayList<>();
        String colLabel;
        StringJoiner joiner;
        if (nextCatIndex == -1 || nextCatIndex > catRow.length) {
            nextCatIndex = catRow.length;
        }
        Pattern unctColPattern = Pattern.compile(".*(±|\\+\\/-)\\d?(σ|sigma)\\W?%\\W?");
        DataColumn<Number> newNumberCol;
        DataColumn<String> newStringCol;
        for (int colIndex = catIndex; colIndex < nextCatIndex; colIndex++) {
            boolean isDependentColumn = false;

            joiner = new StringJoiner(" ");
            for (int rowIndex = 1; rowIndex < 5; rowIndex++) { //join 5 header rows
                colLabel = rows[rowIndex][colIndex];
                if (!colLabel.equals("")) {
                    joiner.add(colLabel);
                }
            }
            colLabel = joiner.toString().trim();
            if (colLabel.equals("")) {
                colLabel = "newColumn";
            }

            if (unctColPattern.matcher(colLabel).matches()) {
                isDependentColumn = true;
            }

            if (usedColumnLabels.containsKey(colLabel)) {
                labelFreq = usedColumnLabels.get(colLabel);
                usedColumnLabels.put(colLabel, labelFreq + 1);
                colLabel += ("(" + labelFreq + ")");
            } else {
                usedColumnLabels.put(colLabel, 1);
            }

            Class<?> clazz = getColumnDataType(rows, colIndex, 5);
            C newColumn;
            if (clazz == Number.class) {
                newColumn = newNumberCol = new SimpleDataColumn<>(colLabel, true, 0.0, Number.class);
                if (isDependentColumn) {
                    ((DataColumn<Number>) columns.get(columns.size() - 1)).setDependentColumn(newNumberCol);
                }
            } else {
                newColumn = newStringCol = new SimpleDataColumn<>(colLabel, true, "", String.class);
                if (isDependentColumn) {
                    ((DataColumn<String>) columns.get(columns.size() - 1)).setDependentColumn(newStringCol);
                }
            }
            columns.add(newColumn);
        }
        return new SimpleDataColumn(catLabel, true, columns.toArray(new SimpleDataColumn[]{}));
    }

    private R parseAliquot(String[][] rows, int segIndex, int nextSegIndex, List<C> columns) {
        String segmentLabel = rows[segIndex][0];
        List<DataRow> dataRows = new ArrayList<>();
        String rowLabel;
        if (nextSegIndex == -1 || nextSegIndex > rows.length) {
            nextSegIndex = rows.length;
        }
        for (int rowIndex = segIndex + 1; rowIndex < nextSegIndex; rowIndex++) {
            rowLabel = rows[rowIndex][0];
            dataRows.add(getTableRow(
                    rowLabel,
                    Arrays.copyOfRange(rows[rowIndex], 1, rows[rowIndex].length),
                    columns
            ));
        }
        return new SimpleDataRow(segmentLabel, true, dataRows.toArray(new SimpleDataRow[]{}));
    }

    /**
     * Returns an array of ints representing the column indices of each of the top-level data categories.
     *
     * @param catRow the top row of data
     * @return category indices
     */
    private static int[] readCategories(String[] catRow) {
        List<Integer> idxs = new ArrayList<>();
        for (int index = 0; index < catRow.length; index++) {
            if (!"".equals(catRow[index])) {
                idxs.add(index);
            }
        }
        return convertIntArray(idxs);
    }

    /**
     * Returns an array of ints representing the row indices of each data segment.
     *
     * @param cells String[][] of data values
     * @return segment indices
     */
    private static int[] readAliquots(String[][] cells) {
        List<Integer> idxs = new ArrayList<>();

        String last = cells[5][0];
        String current;
        if (!"".equals(last)) {
            idxs.add(5);
            for (int index = 6; index < cells.length; index++) {
                current = cells[index][0];
                if (!current.startsWith(last)) {
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
