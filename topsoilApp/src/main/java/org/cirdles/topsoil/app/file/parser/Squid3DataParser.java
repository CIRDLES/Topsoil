package org.cirdles.topsoil.app.file.parser;

import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.variable.Variables;

import java.util.*;

/**
 * Parses value-separated data into a {@link DataTable}.
 *
 * This {@link DataParser} assumes that data is exported in a specific format from Squid 3.
 *
 * @author marottajb
 */
public class Squid3DataParser extends AbstractDataParser {

    /** {@inheritDoc} */
    @Override
    protected DataTable parseDataTable(String[][] rows, String label) {
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

        DataTable table = new DataTable(DataTemplate.SQUID_3, label, columnRoot, dataRoot);
        prepareTable(table);
        return table;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private ColumnRoot parseColumnTree(String[][] rows) {
        List<DataComponent> topLevel = new ArrayList<>();
        int[] categoryIndices = readCategories(rows[0]);
        Map<String, Integer> usedColumnLabels = new HashMap<>();
        for (int i = 0; i < categoryIndices.length; i++) {
            topLevel.add(parseCategory(
                    rows,
                    categoryIndices[i],
                    (i == (categoryIndices.length - 1) ? -1 : categoryIndices[i + 1]),
                    usedColumnLabels
            ));
        }
        return new ColumnRoot(topLevel.toArray(new DataComponent[]{}));
    }

    /**
     * Parses a {@code DataCategory} from the provided start and end indices.
     *
     * @param rows         String[][] of data values
     * @param catIndex      start index of the category
     * @param nextCatIndex  index of the next category
     * @return              DataCategory
     */
    private DataCategory parseCategory(String[][] rows, int catIndex, int nextCatIndex, Map<String, Integer> usedColumnLabels) {
        String[] catRow = rows[0];
        String catLabel = catRow[catIndex];
        String colLabel;
        int labelFreq;

        List<DataColumn> columns = new ArrayList<>();
        StringJoiner joiner;
        if (nextCatIndex == -1 || nextCatIndex > catRow.length) {
            nextCatIndex = catRow.length;
        }
        for (int colIndex = catIndex; colIndex < nextCatIndex; colIndex++) {
            joiner = new StringJoiner(" ");
            for (int rowIndex = 1; rowIndex < 5; rowIndex++) {
                colLabel = rows[rowIndex][colIndex];
                if (! colLabel.equals("")) {
                    joiner.add(colLabel);
                }
            }
            colLabel = joiner.toString().trim();
            if (colLabel.equals("")) {
                colLabel = "newColumn";
            }

            if (usedColumnLabels.containsKey(colLabel)) {
                labelFreq = usedColumnLabels.get(colLabel);
                usedColumnLabels.put(colLabel, labelFreq + 1);
                colLabel += ("(" + labelFreq + ")");
            } else {
                usedColumnLabels.put(colLabel, 1);
            }

            Class<?> clazz = getColumnDataType(rows, colIndex, 5);
            if (clazz == Number.class) {
                columns.add(DataColumn.numberColumn(colLabel));
            } else {
                columns.add(DataColumn.stringColumn(colLabel));
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
            dataRows.add(getDataRow(
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

    private void prepareTable(DataTable table) {
        DataComponent c = table.getColumnRoot().find("204Pb-Corrected");
        if (c instanceof DataCategory) {
            DataCategory category = (DataCategory) c;
            int colCount = category.getChildren().size();
            DataColumn<Number> xColumn = (DataColumn<Number>) category.getChildren().get(colCount - 5);
            DataColumn<Number> sXColumn = (DataColumn<Number>) category.getChildren().get(colCount - 4);
            DataColumn<Number> yColumn = (DataColumn<Number>) category.getChildren().get(colCount - 3);
            DataColumn<Number> sYColumn = (DataColumn<Number>) category.getChildren().get(colCount - 2);
            DataColumn<Number> rhoColumn = (DataColumn<Number>) category.getChildren().get(colCount - 1);

            table.setColumnForVariable(Variables.X, xColumn);
            table.setColumnForVariable(Variables.SIGMA_X, sXColumn);
            table.setColumnForVariable(Variables.Y, yColumn);
            table.setColumnForVariable(Variables.SIGMA_Y, sYColumn);
            table.setColumnForVariable(Variables.RHO, rhoColumn);

            List<DataColumn<?>> importantColumns = Arrays.asList(xColumn, sXColumn, yColumn, sYColumn, rhoColumn);
            deselectComponent(table.getColumnRoot(), importantColumns);

            table.setIsotopeSystem(IsotopeSystem.UPB);
            table.setUncertainty(Uncertainty.ONE_SIGMA_PERCENT);
        }
    }

    private void deselectComponent(DataComponent component, List<DataColumn<?>> importantColumns) {
        if (component instanceof DataComposite) {
            boolean shouldDeselectComposite = true;
            for (DataComponent child : ((DataComposite<DataComponent>) component).getChildren()) {
                deselectComponent(child, importantColumns);
                if (child.isSelected()) {
                    shouldDeselectComposite = false;
                }
            }
            if (shouldDeselectComposite) {
                component.setSelected(false);
            }
        } else if (component instanceof DataColumn) {
            if (! importantColumns.contains(component)) {
                component.setSelected(false);
            }
        }
    }

}
