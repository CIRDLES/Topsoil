package org.cirdles.topsoil.app.spreadsheet;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.data.ObservableDataRow;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilNumberCell;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilHeaderCell;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilSpreadsheetCell;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilVariableChooserCell;
import org.cirdles.topsoil.app.spreadsheet.picker.ColumnVariablePicker;
import org.cirdles.topsoil.app.spreadsheet.picker.DataRowPicker;
import org.cirdles.topsoil.app.util.ListenerHandlerBase;
import org.controlsfx.control.spreadsheet.*;

import java.util.*;

/**
 * A custom {@code SpreadsheetView} for displaying Topsoil data.
 *
 * @author marottajb
 */
public class TopsoilSpreadsheetView extends SpreadsheetView {

    /*
        @TODO Expand JavaDoc tag for class
     */

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    public static final int DATA_ROW_OFFSET = 2;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private ObservableDataTable data;
    private SpreadsheetHandler spreadsheetHandler;      // reference to avoid garbage collection; no others ATOW
	private DataCellFormatter dataCellFormatter;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /**
     * The @{code String} format for all cells in the spreadsheet.
     *
     * @see java.text.DecimalFormat
     */
    private StringProperty format = new SimpleStringProperty();
    public StringProperty formatProperty() {
        return format;
    }
    public String getFormat() {
        return format.get();
    }
    public void setFormat(String pattern) {
        format.set(pattern);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilSpreadsheetView(ObservableDataTable data) {
        this(data, SpreadsheetConstants.DEFAULT_REGULAR_DOUBLE_PATTERN);
    }

    public TopsoilSpreadsheetView(ObservableDataTable data, String format) {
        super();

        this.data = data;
        setFormat(format);

        setGrid(makeGrid(this.data));
        getFixedRows().addAll(0, 1);    // fixes header and variable rows
        setFixingRowsAllowed(false);
        setFixingColumnsAllowed(false);

	    spreadsheetHandler = new SpreadsheetHandler(this);
        spreadsheetHandler.refreshAllPickers();
	    this.data.addObserver(spreadsheetHandler);  // listening for data table changes

	    dataCellFormatter = new DataCellFormatter(this);
        dataCellFormatter.format();
	    this.format.addListener(c -> dataCellFormatter.format() );  // listening for cell value changes

        for (SpreadsheetColumn column : getColumns()) {
            column.setPrefWidth(SpreadsheetConstants.INIT_COLUMN_WIDTH);
        }

        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);    // disable multi-cell selection
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ObservableDataTable getData() {
        return data;
    }

    @Override
    public ContextMenu getSpreadsheetViewContextMenu() {

        // @TODO Do this better
//        TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
//        int rowIndex = getModelRow(pos.getRowIndex());
//        int colIndex = getModelColumn(pos.getColumn());
//
//        SpreadsheetCell targetCell = ((rowIndex >= 0 && colIndex >= 0) ?
//                this.getGrid().getRows().get(rowIndex).get(colIndex) : null);
//
//        if (targetCell instanceof TopsoilNumberCell) {
//            System.out.println("CLICKED DATA CELL");
//
//            final ContextMenu contextMenu = new ContextMenu();
//
//            final Menu addRowMenu = new Menu("Insert Row");
//            final MenuItem addRowAboveItem = new MenuItem("Above");
//            addRowAboveItem.setOnAction(event -> {
//                List<Double> row = new ArrayList<>(data.colCount());
//                for (int i = 0; i < data.colCount(); i++) {
//                    row.add(0.0);
//                }
//
//                InsertRowCommand command = new InsertRowCommand(data, rowIndex, row);
//                command.execute();
//                TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
//            });
//            final MenuItem addRowBelowItem = new MenuItem("Below");
//            addRowBelowItem.setOnAction(event -> {
//                int index = rowIndex + 1;
//
//                List<Double> row = new ArrayList<>(data.colCount());
//                for (int i = 0; i < data.colCount(); i++) {
//                    row.add(0.0);
//                }
//
//                InsertRowCommand command = new InsertRowCommand(data, index, row);
//                command.execute();
//                TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
//            });
//            addRowMenu.getItems().addAll(addRowAboveItem, addRowBelowItem);
//
//            final MenuItem deleteRowItem = new MenuItem("Delete Row");
//            deleteRowItem.setOnAction(event -> {
//                DeleteRowCommand command = new DeleteRowCommand(data, rowIndex);
//                command.execute();
//                TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
//            });
//
//            final Menu addColumnMenu = new Menu("Add Column");
//            final MenuItem addColumnLeftItem = new MenuItem("Left");
//            addColumnLeftItem.setOnAction(event -> {
//                List<Double> column = new ArrayList<>(data.rowCount());
//                for (int i = 0; i < data.rowCount(); i++) {
//                    column.add(0.0);
//                }
//
//                InsertColumnCommand command = new InsertColumnCommand(data, colIndex, column);
//                command.execute();
//                TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
//            });
//            final MenuItem addColumnRightItem = new MenuItem("Right");
//            addColumnRightItem.setOnAction(event -> {
//                int index = colIndex + 1;
//
//                List<Double> column = new ArrayList<>(data.rowCount());
//                for (int i = 0; i < data.rowCount(); i++) {
//                    column.add(0.0);
//                }
//
//                InsertColumnCommand command = new InsertColumnCommand(data, index, column);
//                command.execute();
//                TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
//            });
//            addColumnMenu.getItems().addAll(addColumnLeftItem, addColumnRightItem);
//
//            final MenuItem deleteColumnItem = new MenuItem("Delete Column");
//            deleteColumnItem.setOnAction(event -> {
//                DeleteColumnCommand command = new DeleteColumnCommand(data, colIndex);
//                command.execute();
//                TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
//            });
//
//            contextMenu.getItems().addAll(
//                    addRowMenu,
//                    deleteRowItem,
//                    new SeparatorMenuItem(),
//                    addColumnMenu,
//                    deleteColumnItem);
//
//            return contextMenu;
//        }
        return null;
    }

    /**
     * Overridden to copy content as tab-separated doubles, instead of as a list of {@code GridChange}s.
     * <p>
     * This assumes no cell spanning, as it is not needed at time of writing.
     */
    @Override
    public void copyClipboard() {
        ObservableList<TablePosition> posList = FXCollections.observableArrayList(getSelectionModel().getSelectedCells());
        posList.sort( (TablePosition o1, TablePosition o2) -> {
            if (o1.getRow() < o2.getRow()) {
                return -1;
            } else if (o1.getRow() > o2.getRow()) {
                return 1;
            } else {
                if (o1.getColumn() < o2.getColumn()) {
                    return -1;
                } else if (o1.getColumn() > o2.getColumn()) {
                    return 1;
                }
            }
            return 0;
        });

        StringJoiner newlineJoiner = new StringJoiner(System.getProperty("line.separator"));
        StringJoiner tabJoiner = new StringJoiner("\t");
        SpreadsheetCell cell;
        Integer row = null;
        for (final TablePosition<?, ?> pos : posList) {
            cell = getGrid().getRows().get(getModelRow(pos.getRow())).get(getModelColumn(pos.getColumn()));
            if (row == null) {
                row = cell.getRow();
            } else if (cell.getRow() > row) {
                newlineJoiner.add(tabJoiner.toString());
                tabJoiner = new StringJoiner("\t");
            }
            tabJoiner.add(cell.getItem() == null ? null : cell.getItem().toString());
        }
        if (tabJoiner.length() > 0) {
            newlineJoiner.add(tabJoiner.toString());
        }
        final ClipboardContent content = new ClipboardContent();
        content.putString(newlineJoiner.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }

    /**
     * Overridden to paste content as tab-separated doubles, instead of as a list of {@code GridChange}s.
     * <p>
     * This assumes no cell spanning, as it is not needed at time of writing.
     */
    @Override
    public void pasteClipboard() {
        final TablePosition<?, ?> pos = getSelectionModel().getFocusedCell();
        String content = Clipboard.getSystemClipboard().getString();
        String[] lines = content.split(System.getProperty("line.separator"));
        SpreadsheetCell cell;

        for (int row = pos.getRow(); row < pos.getRow() || row < data.rowCount(); row++) {
            String[] cells = lines[row - pos.getRow()].split("\t");
            for (int col = pos.getColumn(); col < pos.getColumn() || col < data.colCount(); col++) {
                cell = getGrid().getRows().get(getModelRow(row)).get(getModelColumn(col));
                try {
                    cell.setItem(Double.parseDouble(cells[col - pos.getColumn()]));
                } catch (NumberFormatException e) {
                    cell.setItem(Double.NaN);
                }
            }
        }
    }

    public void updateDataValue(int row, int col, double value) {
        row -= DATA_ROW_OFFSET;
        data.setValue(row, col, value);
        dataCellFormatter.formatColumn(col);
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    /**
     * Creates and returns the {@code GridBase} for the spreadsheet based on the provided data.
     *
     * @param   data
     *          ObservableDataTable
     * @return  GridBase for data
     */
    private GridBase makeGrid(ObservableDataTable data) {

        GridBase grid = new GridBase(data.rowCount() + DATA_ROW_OFFSET, data.colCount());
        ObservableList<ObservableList<SpreadsheetCell>> gridRows = FXCollections.observableArrayList();

        // Create fixed header rows
	    gridRows.addAll(customFixedRows());

	    // Create data rows
        ObservableList<ObservableDataRow> dataRows = data.getRows();
        for (int rowIndex = DATA_ROW_OFFSET; rowIndex < grid.getRowCount(); rowIndex++) {
            ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
            for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
                row.add(new TopsoilNumberCell(
                        this,
                        rowIndex,
                        colIndex,
                        dataRows.get(rowIndex - DATA_ROW_OFFSET).get(colIndex)
                ));
            }
            gridRows.add(row);

            // Create row pickers
            getRowPickers().put(rowIndex, new DataRowPicker(this, rowIndex));
        }
        grid.setRows(gridRows);

//        // Create column pickers
//        for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
//            getColumnPickers().put(colIndex, new ColumnVariablePicker(colIndex));
//        }

        return grid;
    }

    private ObservableList<ObservableList<SpreadsheetCell>> customFixedRows() {
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        ObservableList<ObservableDataColumn> dataColumns = data.getColumns();

        // Variable row
        ObservableList<SpreadsheetCell> varRow = FXCollections.observableArrayList();
        for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
            ObservableDataColumn column = dataColumns.get(colIndex);
            varRow.add(new TopsoilVariableChooserCell(
                    this,
                    0,
                    colIndex,
                    Bindings.createStringBinding(() -> {
                            if (column.variableProperty().get() == null) {
                                return "[variable]";
                            } else {
                                return column.variableProperty().get().getAbbreviation();
                            }
                        }, column.variableProperty())
            ));
        }
        rows.add(varRow);

        // Header row
        ObservableList<SpreadsheetCell> headerRow = FXCollections.observableArrayList();
        for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
            headerRow.add(new TopsoilHeaderCell(
                    this,
                    1,
                    colIndex,
                    dataColumns.get(colIndex).headerProperty())
            );
        }
        rows.add(headerRow);

        return rows;
    }

	/**
	 * Responsible for maintaining the text formatting of data cells. Double values within a column should be
     * justified to the right, and aligned vertically by their decimal separator. For example, if two cells in a
     * column have the values:
     *
     *      |1.123     |
     *      |1.12345   |
     *
     * ... then they should be formatted like this:
     *
     *      |   1.123  |
     *      |   1.12345|
	 */
	private class DataCellFormatter extends ListenerHandlerBase<TopsoilNumberCell> {

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

    	private TopsoilSpreadsheetView spreadsheet;
        private Map<SpreadsheetCell, ChangeListener<Object>> cellUpdatedListeners = new HashMap<>();
        private ListChangeListener<ObservableList<SpreadsheetCell>> rowAddedRemovedListener = c -> {
				    while (c.next()) {
				    	if (c.wasUpdated()) {
						    format();
					    } else {
						    for (ObservableList<SpreadsheetCell> row : c.getRemoved()) {
							    for (SpreadsheetCell cell : row) {
							        if (cell instanceof TopsoilNumberCell) {
                                        forget((TopsoilNumberCell) cell);
                                    }
							    }
						    }
						    for (ObservableList<SpreadsheetCell> row : c.getAddedSubList()) {
							    for (SpreadsheetCell cell : row) {
                                    if (cell instanceof TopsoilNumberCell) {
                                        listen((TopsoilNumberCell) cell);
                                    }
							    }
						    }
					    }
				    }
			    };

	    //**********************************************//
	    //                 CONSTRUCTORS                 //
	    //**********************************************//

        /**
         * Constructs a {@code DataCellFormatter} for the specified {@code TopsoilSpreadsheetView}.
         *
         * @param   tsview
         *          TopsoilSpreadsheetView
         */
    	private DataCellFormatter(TopsoilSpreadsheetView tsview) {
    		this.spreadsheet = tsview;
            ObservableList<ObservableList<SpreadsheetCell>> rows = spreadsheet.getItems();

            // Listen for row additions/deletions
            rows.addListener(rowAddedRemovedListener);

            // Listen to existing cells
            for (ObservableList<SpreadsheetCell> row : rows) {
                for (SpreadsheetCell cell : row) {
                    if (cell instanceof TopsoilNumberCell) {
                        listen( (TopsoilNumberCell) cell );
                    }
                }
            }
	    }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        /**
         * Formats every cell in the spreadsheet.
         */
	    public void format() {
    		for (int i = 0; i < spreadsheet.getGrid().getColumnCount(); i++) {
    			formatColumn(i);
		    }
	    }

        /**
         * Starts listening on the specified cell.
         *
         * @param   cell
         *          TopsoilNumberCell
         */
	    public void listen(TopsoilNumberCell cell) {
    	    if (! cellUpdatedListeners.containsKey(cell)) {
                ChangeListener<Object> listener = (observable, oldValue, newValue) -> formatColumn(cell.getColumn());
                cell.itemProperty().addListener(listener);
                cellUpdatedListeners.put(cell, listener);
            }
	    }

        /**
         * Stops listening on the specified cell.
         *
         * @param   cell
         *          TopsoilNumberCell
         */
	    public void forget(TopsoilNumberCell cell) {
    	    if (cellUpdatedListeners.containsKey(cell)) {
                cell.itemProperty().removeListener(cellUpdatedListeners.get(cell));
                cellUpdatedListeners.remove(cell);
            }
	    }

        //**********************************************//
        //                PRIVATE METHODS               //
        //**********************************************//

        /**
         * Formats the cells in the column at the specified index.
         *
         * @param   colIndex
         *          column index
         */
	    private void formatColumn(int colIndex) {
			ObservableList<ObservableList<SpreadsheetCell>> rows = spreadsheet.getItems();
		    SpreadsheetCell cell;
			String text;
		    int placesAfterDecimal;
			int maxPlacesAfterDecimal = 0;

			// Find the maximum number of characters following the decimal point
			for (int rowIndex = DATA_ROW_OFFSET; rowIndex < rows.size(); rowIndex++) {
			    cell = rows.get(rowIndex).get(colIndex);
                text = cell.getItem().toString();
                placesAfterDecimal = text.contains(".") ? text.substring(text.lastIndexOf(".") + 1).length() : 0;
                maxPlacesAfterDecimal = Math.max(maxPlacesAfterDecimal, placesAfterDecimal);
			}

			// Add the appropriate amount of whitespace to each cell format
			StringBuilder builder;
			for (int rowIndex = DATA_ROW_OFFSET; rowIndex < rows.size(); rowIndex++) {
                cell = rows.get(rowIndex).get(colIndex);
                text = cell.getItem().toString();
                placesAfterDecimal = text.contains(".") ? text.substring(text.lastIndexOf(".") + 1).length() : 0;

                builder = new StringBuilder(spreadsheet.getFormat());
                if (maxPlacesAfterDecimal - placesAfterDecimal > 0) {
                    builder.append("\'");    // open single quote to start special characters
                    if ( placesAfterDecimal == 0 ) {
                        builder.append(" ");  // append one space to pad missing decimal, if integer
                    }
                    for (int i = 0; i < maxPlacesAfterDecimal - placesAfterDecimal; i++) {
                        builder.append(" ");
                    }
                    builder.append("\'");    // close single quote to end special characters
                }
                cell.setFormat(builder.toString());
			}
	    }
    }

    /**
     * This class handles the spreadsheet and its cells in response to changes in the data model.
     */
    public class SpreadsheetHandler implements Observer {

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private TopsoilSpreadsheetView spreadsheet;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        /**
         * Constructs a {@code SpreadsheetHandler} for the provided {@code TopsoilSpreadsheetView}.
         *
         * @param   view
         *          TopsoilSpreadsheetView
         */
        private SpreadsheetHandler(TopsoilSpreadsheetView view) {
            this.spreadsheet = view;
        }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        @Override
        public void update( Observable o, Object arg ) {

            if (o instanceof ObservableDataTable) {
                ObservableDataTable.DataOperation op = (ObservableDataTable.DataOperation) arg;
                Grid newGrid;
                int row;
                int col;
                DataRowPicker picker;

                switch (op.getType()) {
                    case INSERT_ROW:
                        newGrid = addRow();
                        spreadsheet.setGrid(newGrid);
                        refreshDataCells();
                        refreshDataPickers();
                        break;
                    case DELETE_ROW:
                        newGrid = removeRow();
                        spreadsheet.setGrid(newGrid);
                        refreshDataCells();
                        refreshDataPickers();
                        break;
                    case INSERT_COLUMN:
                        newGrid = addColumn();
                        spreadsheet.setGrid(newGrid);
                        refreshDataCells();
//                        refreshColumnPickers();
                        break;
                    case DELETE_COLUMN:
                        newGrid = removeColumn();
                        spreadsheet.setGrid(newGrid);
                        refreshDataCells();
//                        refreshColumnPickers();
                        break;
                    case SELECT_ROW:
                        row = op.getRowIndex();
                        picker = (DataRowPicker) spreadsheet.getRowPickers().get(row + DATA_ROW_OFFSET);
                        if (! picker.isSelected()) {
                            picker.setSelected(true);
                        }
                        break;
                    case DESELECT_ROW:
                        row = op.getRowIndex();
                        picker = (DataRowPicker) spreadsheet.getRowPickers().get(row + DATA_ROW_OFFSET);
                        if (picker.isSelected()) {
                            picker.setSelected(false);
                        }
                        break;
                    case UPDATE_VARIABLES:
//                        refreshColumnPickers();
                        break;
                    case UPDATE_PROPERTY:
                        Grid grid = getGrid();
                        TopsoilSpreadsheetCell<?> cell;
                        Property<?> source;
                        row = op.getRowIndex();
                        col = op.getColIndex();

                        cell = (TopsoilSpreadsheetCell) grid.getRows().get(row + DATA_ROW_OFFSET).get(col);
                        source = data.get(row, col);

                        if (cell.getSource() != source) {
                            refreshDataCell(row + DATA_ROW_OFFSET, col);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        //**********************************************//
        //               PRIVATE METHODS                //
        //**********************************************//

        private Grid addRow() {

            Grid oldGrid = spreadsheet.getGrid();
            Grid newGrid = new GridBase(oldGrid.getRowCount() + 1, oldGrid.getColumnCount());

            ObservableList<ObservableList<SpreadsheetCell>> rows = copyGridRows(oldGrid);
            DoubleProperty dummy = new SimpleDoubleProperty();

            // Create new row of cells at the bottom of the grid
            int rowIndex = oldGrid.getRowCount();
            ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
            for (int colIndex = 0; colIndex < oldGrid.getColumnCount(); colIndex++) {
                row.add(new TopsoilNumberCell(
                        spreadsheet,
                        rowIndex,
                        colIndex,
                        dummy    // will be assigned by refreshDataCells()
                ));
            }
            rows.add(row);
            newGrid.setRows(rows);
            spreadsheet.getRowPickers().put(rowIndex, new DataRowPicker(spreadsheet, rowIndex));

            return newGrid;
        }

        private Grid removeRow() {
            Grid oldGrid = spreadsheet.getGrid();
            Grid newGrid = new GridBase(oldGrid.getRowCount() - 1, oldGrid.getColumnCount());

            ObservableList<ObservableList<SpreadsheetCell>> rows = copyGridRows(oldGrid);
            rows.remove(oldGrid.getRowCount() - 1);
            newGrid.setRows(rows);
            spreadsheet.getRowPickers().remove(oldGrid.getRowCount() - 1);

            return newGrid;
        }

        private Grid addColumn() {
            Grid oldGrid = spreadsheet.getGrid();
            Grid newGrid = new GridBase(oldGrid.getRowCount(), oldGrid.getColumnCount() + 1);

            ObservableList<ObservableList<SpreadsheetCell>> rows = copyGridRows(oldGrid);
            ObservableList<SpreadsheetCell> row;

            int colIndex = oldGrid.getColumnCount();
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                row = rows.get(rowIndex);
                row.add(new TopsoilNumberCell(
                        spreadsheet,
                        rowIndex,
                        colIndex,
                        null    // will be assigned by refreshDataCells()
                ));
            }
            newGrid.setRows(rows);
//            spreadsheet.getColumnPickers().put(colIndex, new ColumnVariablePicker(colIndex));

            return newGrid;
        }

        private Grid removeColumn() {
            Grid oldGrid = spreadsheet.getGrid();
            Grid newGrid = new GridBase(oldGrid.getRowCount() - 1, oldGrid.getColumnCount());

            ObservableList<ObservableList<SpreadsheetCell>> rows = copyGridRows(oldGrid);
            for (ObservableList<SpreadsheetCell> row : rows) {
                row.remove(data.colCount());
            }
            newGrid.setRows(rows);
//            spreadsheet.getColumnPickers().remove(newGrid.getColumnCount() - 1);

            return newGrid;
        }

        private ObservableList<ObservableList<SpreadsheetCell>> copyGridRows(Grid grid) {
            ObservableList<ObservableList<SpreadsheetCell>> copy = FXCollections.observableArrayList();
            copy.addAll(grid.getRows());
            return copy;
        }

        private void refreshAllPickers() {
            refreshDataPickers();
//            refreshColumnPickers();
        }

        /**
         * For each row in the data, set the picker's {@code selected} property to the value of that {@code
         * ObservableDataRow}'s {@code selected} property.
         */
        private void refreshDataPickers() {
            DataRowPicker picker;
            for (int rowIndex = DATA_ROW_OFFSET; rowIndex < spreadsheet.getGrid().getRowCount(); rowIndex++) {
                picker = (DataRowPicker) spreadsheet.getRowPickers().get(rowIndex);
                ObservableDataRow row = spreadsheet.getData().getRow(rowIndex - DATA_ROW_OFFSET);

                picker.setSelected(row.isSelected());
                if (! picker.isSelected()) {
                    for (SpreadsheetCell cell : spreadsheet.getGrid().getRows().get(rowIndex - DATA_ROW_OFFSET)) {
                        ((TopsoilNumberCell) cell).setSelected(false);
                    }
                }
            }
        }

//        /**
//         * For each column in the data, set the picker's {@code variable} property to the value of that {@code
//         * ObservableDataColumn}'s {@code variable} property.
//         */
//        private void refreshColumnPickers() {
//            if (! spreadsheet.getColumnPickers().isEmpty()) {
//                for (int colIndex = 0; colIndex < spreadsheet.getGrid().getColumnCount(); colIndex++) {
//                    refreshColumnPicker(colIndex);
//                }
//                Map<Integer, Picker> pickers = new HashMap<>(spreadsheet.getColumnPickers());
//                spreadsheet.getColumnPickers().putAll(pickers);
//            }
//        }
//
//        private void refreshColumnPicker(int index) {
//            ColumnVariablePicker picker = (ColumnVariablePicker) spreadsheet.getColumnPickers().get(index);
//            ObservableDataColumn column = spreadsheet.getData().getColumns().get(index);
//            picker.setVariable(column.getVariable());
//        }

        /**
         * For each data cell in the spreadsheet, update its source property and format.
         */
        private void refreshDataCells() {
            Grid grid = spreadsheet.getGrid();
            if (grid.getRowCount() > DATA_ROW_OFFSET) {
                for (int row = DATA_ROW_OFFSET; row < grid.getRowCount(); row++) {
                    for (int col = 0; col < grid.getColumnCount(); col++) {
                        TopsoilNumberCell cell = (TopsoilNumberCell) spreadsheet.getGrid().getRows().get(row).get(col);
                        DoubleProperty source = spreadsheet.getData().get(row - DATA_ROW_OFFSET, col);
                        if (cell.getSource() != source) {
                            cell.setSource(source);
                        }
                    }
                }
                spreadsheet.dataCellFormatter.format();
            }
        }

        private void refreshDataCell(int row, int column) {
            TopsoilNumberCell cell = (TopsoilNumberCell) spreadsheet.getGrid().getRows().get(row).get(column);
            DoubleProperty source = spreadsheet.getData().get(row - DATA_ROW_OFFSET, column);
            if (cell.getSource() != source) {
                cell.setSource(source);
            }
            spreadsheet.dataCellFormatter.formatColumn(column);
        }
    }
}
