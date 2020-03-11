package org.cirdles.topsoil.file.parser;

import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTemplate;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.utils.TopsoilTableUtils;
import org.cirdles.topsoil.data.Uncertainty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import static org.cirdles.topsoil.utils.TopsoilClassUtils.instantiate;

/**
 * Parses value-separated data into a {@link DataTable}.
 * <p>
 * This {@link DataParser} assumes that data is exported in a specific format from Squid 3.
 *
 * @author marottajb
 */
public class Squid3DataParser<T extends DataTable<C, R>, C extends DataColumn<?>, R extends DataRow> extends AbstractDataParser<T, C, R> {

    private static final Class[] TABLE_CONSTRUCTOR_ARG_TYPES = {
            DataTemplate.class,
            String.class,
            Uncertainty.class,
            List.class,
            List.class
    };
    private static final Class[] ALIQUOT_CONSTRUCTOR_ARG_TYPES = {String.class};
    private static final Class[] CATEGORY_CONSTRUCTOR_ARG_TYPES = {String.class};
    private static final Class[] COLUMN_CONSTRUCTOR_ARG_TYPES = {String.class, Boolean.class, Object.class, Class.class};

    public Squid3DataParser(Class<T> tableClass) {
        super(DataTemplate.SQUID_3, tableClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T parseDataTable(String[][] cells, String label) {
        List<C> columns = parseColumns(cells);
        List<C> leafColumns = TopsoilTableUtils.getLeafColumns(columns);
        List<R> rows = parseRows(cells, leafColumns);

        return instantiate(
                tableClass,
                TABLE_CONSTRUCTOR_ARG_TYPES,
                new Object[]{template, label, null, columns, rows}
        );
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    @Override
    protected List<C> parseColumns(String[][] cells, Object... args) {
        List<C> headers = new ArrayList<>();
        int[] categoryIndices = readCategories(cells[0]);
        Map<String, Integer> usedColumnLabels = new HashMap<>();
        for (int i = 0; i < categoryIndices.length; i++) {
            headers.add(parseCategory(
                    cells,
                    categoryIndices[i],
                    (i == (categoryIndices.length - 1) ? -1 : categoryIndices[i + 1]),
                    usedColumnLabels
            ));
        }
        return headers;
    }

    @Override
    protected List<R> parseRows(String[][] cells, List<C> leafColumns, Object... args) {
        int[] segIndices = readAliquots(cells);
        List<R> rows = new ArrayList<>();
        for (int i = 0; i < segIndices.length; i++) {
            rows.add(parseAliquot(
                    cells,
                    segIndices[i],
                    (i < (segIndices.length - 1) ? segIndices[i + 1] : -1),
                    leafColumns
            ));
        }
        return rows;
    }

    @SuppressWarnings("unchecked")
    private C parseCategory(String[][] rows, int catIndex, int nextCatIndex, Map<String, Integer> usedColumnLabels) {
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
        C newColumn;
        Class<?> columnType;
        StringJoiner joiner;
        if (nextCatIndex == -1 || nextCatIndex > catRow.length) {
            nextCatIndex = catRow.length;
        }
        Pattern unctColPattern = Pattern.compile(".*(±|\\+\\/-)\\d?(σ|sigma)\\W?%\\W?");
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

            columnType = getColumnDataType(rows, colIndex, 5);

            if (columnType == Number.class) {
                newColumn = instantiate(
                        columnClass,
                        COLUMN_CONSTRUCTOR_ARG_TYPES,
                        new Object[]{colLabel, true, 0.0, Number.class}
                );
                if (isDependentColumn) {
                    ((DataColumn<Number>) columns.get(columns.size() - 1)).setDependentColumn((DataColumn<Number>) newColumn);
                }
            } else {
                newColumn = instantiate(
                        columnClass,
                        COLUMN_CONSTRUCTOR_ARG_TYPES,
                        new Object[]{colLabel, true, "", String.class}
                );
                if (isDependentColumn) {
                    ((DataColumn<String>) columns.get(columns.size() - 1)).setDependentColumn((DataColumn<String>) newColumn);
                }
            }
            columns.add(newColumn);
        }

        C newCategory = instantiate(columnClass, CATEGORY_CONSTRUCTOR_ARG_TYPES, new Object[]{catLabel, true, null});
        ((List<C>) newCategory.getChildren()).addAll(columns);
        return newCategory;
    }

    @SuppressWarnings("unchecked")
    private R parseAliquot(String[][] cells, int segIndex, int nextSegIndex, List<C> columns) {
        String aliquotLabel = cells[segIndex][0];
        List<R> rows = new ArrayList<>();
        String rowLabel;
        if (nextSegIndex == -1 || nextSegIndex > cells.length) {
            nextSegIndex = cells.length;
        }
        for (int rowIndex = segIndex + 1; rowIndex < nextSegIndex; rowIndex++) {
            rowLabel = cells[rowIndex][0];
            rows.add(getTableRow(
                    rowLabel,
                    Arrays.copyOfRange(cells[rowIndex], 1, cells[rowIndex].length),
                    columns
            ));
        }

        R newAliquot = instantiate(rowClass, ALIQUOT_CONSTRUCTOR_ARG_TYPES, new Object[]{aliquotLabel});
        ((List<R>) newAliquot.getChildren()).addAll(rows);
        return newAliquot;
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
