package org.cirdles.topsoil.app.spreadsheet;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.cirdles.topsoil.app.data.ObservableDataRow;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.app.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilDoubleCell;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilHeaderCell;
import org.cirdles.topsoil.app.spreadsheet.picker.DataRowPicker;
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
        getFixedRows().add(0);  // fixes header row

	    spreadsheetHandler = new SpreadsheetHandler(this);
	    this.data.addObserver(spreadsheetHandler);  // listening for data table changes

	    dataCellFormatter = new DataCellFormatter(this);
	    this.format.addListener(c -> dataCellFormatter.format() );  // listening for cell value changes

        for (SpreadsheetColumn column : getColumns()) {
            column.setPrefWidth(SpreadsheetConstants.INIT_COLUMN_WIDTH);
        }

        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);    // disable multi-cell selection

        dataCellFormatter.format();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ObservableDataTable getData() {
        return data;
    }

    @Override
    public ContextMenu getSpreadsheetViewContextMenu() {

        // @TODO Fix
//        TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
//        int rowIndex = getModelRow(pos.getRowIndex());
//        int colIndex = getModelColumn(pos.getColumn());
//
//        SpreadsheetCell targetCell = ((rowIndex >= 0 && colIndex >= 0) ?
//                this.getGrid().getRows().get(rowIndex).get(colIndex) : null);
//
//        if (targetCell instanceof TopsoilDoubleCell) {
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

    public void updateDataValue(SpreadsheetCell cell, Double value) {
        int row = cell.getRow() - 1;
        int col = cell.getColumn();
        data.set(col, row, value);
        dataCellFormatter.formatColumn(cell.getColumn());
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

        GridBase grid = new GridBase(data.rowCount(), data.colCount());
        ObservableList<ObservableList<SpreadsheetCell>> gridRows = FXCollections.observableArrayList();

        // Create header row
	    ObservableList<SpreadsheetCell> headerRow = FXCollections.observableArrayList();
        ObservableList<ObservableDataColumn> dataColumns = data.getColumns();
	    for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
	        headerRow.add(new TopsoilHeaderCell(
	                this,
                    0, colIndex,
                    1,
                    1,
                    dataColumns.get(colIndex).headerProperty())
            );
	    }
	    gridRows.add(headerRow);

	    // Create data rows
        ObservableList<ObservableDataRow> dataRows = data.getRows();
        for (int rowIndex = 1; rowIndex <= data.rowCount(); rowIndex++) {
            ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
            for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
                row.add(new TopsoilDoubleCell(
                        this,
                        rowIndex,
                        colIndex,
                        1,
                        1,
                        dataRows.get(rowIndex - 1).get(colIndex)
                ));
            }
            gridRows.add(row);

            // Create row pickers
            DataRowPicker picker = new DataRowPicker(this, rowIndex);
            getRowPickers().put(rowIndex, picker);
        }
        grid.setRows(gridRows);

        // Create column pickers
        // @TODO

        return grid;
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
	private class DataCellFormatter extends ChangeHandlerBase<SpreadsheetCell> {

    	private TopsoilSpreadsheetView spreadsheet;
        private Map<SpreadsheetCell, ChangeListener<Object>> cellUpdatedListeners = new HashMap<>();
        private ListChangeListener<ObservableList<SpreadsheetCell>> rowAddedRemovedListener = c -> {
				    while (c.next()) {
				    	if (c.wasUpdated()) {
						    format();
					    } else {
						    for (ObservableList<SpreadsheetCell> row : c.getRemoved()) {
							    for (SpreadsheetCell cell : row) {
								    forget(cell);
							    }
						    }
						    for (ObservableList<SpreadsheetCell> row : c.getAddedSubList()) {
							    for (SpreadsheetCell cell : row) {
								    listen(cell);
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
    		for (ObservableList<SpreadsheetCell> row : rows) {
    			for (SpreadsheetCell cell : row) {
    				if (cell.getCellType() instanceof SpreadsheetCellType.DoubleType) {
					    listen(cell);
				    }
			    }
		    }
		    rows.addListener(rowAddedRemovedListener);
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
         *          SpreadsheetCell
         */
	    public void listen(SpreadsheetCell cell) {
    	    if (cell instanceof TopsoilDoubleCell && (! cellUpdatedListeners.containsKey(cell))) {
                ChangeListener<Object> listener = (observable, oldValue, newValue) -> formatColumn(cell.getColumn());
                cell.itemProperty().addListener(listener);
                cellUpdatedListeners.put(cell, listener);
            }
	    }

        /**
         * Stops listening on the specified cell.
         *
         * @param   cell
         *          SpreadsheetCell
         */
	    public void forget(SpreadsheetCell cell) {
    	    if (cell instanceof TopsoilDoubleCell && cellUpdatedListeners.containsKey(cell)) {
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
         * @param   index
         *          column index
         */
	    private void formatColumn(int index) {
			ObservableList<ObservableList<SpreadsheetCell>> rows = spreadsheet.getItems();
		    SpreadsheetCell cell;
			String text;
		    int placesAfterDecimal;
			int maxPlacesAfterDecimal = 0;

			// Find the maximum number of characters following the decimal point
			for (ObservableList<SpreadsheetCell> row : rows) {
				cell = row.get(index);
				if (cell.getCellType() instanceof SpreadsheetCellType.DoubleType) {
					text = cell.getText();
					placesAfterDecimal = text.contains(".") ? text.substring(text.lastIndexOf(".") + 1).length() : 0;
					maxPlacesAfterDecimal = Math.max(maxPlacesAfterDecimal, placesAfterDecimal);
				}
			}

			// Add the appropriate amount of whitespace to each cell format
			StringBuilder builder;
			for (ObservableList<SpreadsheetCell> row : rows) {
				cell = row.get(index);
				if (cell.getCellType() instanceof SpreadsheetCellType.DoubleType) {
					text = cell.getText();
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
    }

    /**
     * This class handles the spreadsheet and its cells in response to changes in the data model.
     */
    private class SpreadsheetHandler implements Observer {

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

                // Update SpreadsheetView with new data values
                ObservableDataTable data = (ObservableDataTable) o;
                ObservableDataTable.DataOperation op = (ObservableDataTable.DataOperation) arg;

                Grid grid = spreadsheet.getGrid();
                int rowIndex;
                DataRowPicker picker;

                switch (op.getType()) {
                    case INSERT_ROW:
                        addRow();
                        break;
                    case DELETE_ROW:
                        removeRow();
                        break;
                    case INSERT_COLUMN:
                        addColumn();
                        break;
                    case DELETE_COLUMN:
                        removeColumn();
                        break;
//				case CHANGE_VALUE:
//					rowIndex = op.getRowIndex();
//					colIndex = op.getColIndex();
//					SpreadsheetCell cell = grid.getRows().get(rowIndex).get(colIndex);
//
//					if (cell.getCellType() == SpreadsheetCellType.DOUBLE) {
//						if (Double.compare(data.get(colIndex, rowIndex).getValue(), (Double) cell.getItem()) != 0) {
//							spreadsheet.getGrid().setCellValue(rowIndex, colIndex, data.get(colIndex, rowIndex).getValue());
//						}
//					}
//					break;
                    case SELECT_ROW:
                        rowIndex = op.getRowIndex();
                        picker = (DataRowPicker) spreadsheet.getRowPickers().get(rowIndex + 1);
                        if (! picker.isSelected()) {
                            picker.setSelected(true);
                        }
                        break;
                    case DESELECT_ROW:
                        rowIndex = op.getRowIndex();
                        picker = (DataRowPicker) spreadsheet.getRowPickers().get(rowIndex + 1);
                        if (picker.isSelected()) {
                            picker.setSelected(false);
                        }
                        break;
                }

                spreadsheet.setGrid(grid);  // reloads the grid so that changes are rendered

                // Update any open plots with new data
                for (TopsoilPlotView plotView : data.getOpenPlots().values()) {
                    plotView.getPlot().setData(data.getPlotEntries());
                }
            }

            resetDataCells();
            resetPickers();
        }

        //**********************************************//
        //               PRIVATE METHODS                //
        //**********************************************//

        private void addRow() {
            Grid grid = spreadsheet.getGrid();
            ObservableList<ObservableList<SpreadsheetCell>> gridRows = spreadsheet.getGrid().getRows();
            DoubleProperty dummy = new SimpleDoubleProperty();

            // Create new row of cells at the bottom of the grid
            ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
            for (int i = 0; i < grid.getColumnCount(); i++) {
                row.add(new TopsoilDoubleCell(
                        spreadsheet,
                        grid.getRowCount(),
                        i,
                        1,
                        1,
                        null    // will be assigned by resetDataCells()
                ));
            }
            gridRows.add(row);
            grid.setRows(gridRows);

            spreadsheet.getRowPickers().put(gridRows.size() - 1, new DataRowPicker(spreadsheet, gridRows.size() - 1));
        }

        private void removeRow() {
            Grid grid = spreadsheet.getGrid();
            ObservableList<ObservableList<SpreadsheetCell>> gridRows = spreadsheet.getGrid().getRows();

            gridRows.remove(grid.getRowCount() - 1);
            grid.setRows(gridRows);

            spreadsheet.getRowPickers().remove(gridRows.size() - 1, new DataRowPicker(spreadsheet, gridRows.size() - 1));
        }

        private void addColumn() {
            Grid grid = spreadsheet.getGrid();
            ObservableList<ObservableList<SpreadsheetCell>> gridRows = spreadsheet.getGrid().getRows();
            ObservableList<SpreadsheetCell> row;

            for (int rowIndex = 0; rowIndex < gridRows.size(); rowIndex++) {
                row = gridRows.get(rowIndex);
                row.add(new TopsoilDoubleCell(
                        spreadsheet,
                        rowIndex,
                        grid.getColumnCount(),
                        1,
                        1,
                        null    // will be assigned by resetDataCells()
                ));
            }
            grid.setRows(gridRows);
        }

        private void removeColumn() {
            Grid grid = spreadsheet.getGrid();
            ObservableList<ObservableList<SpreadsheetCell>> rows = spreadsheet.getGrid().getRows();

            for (ObservableList<SpreadsheetCell> row : rows) {
                row.remove(spreadsheet.getData().colCount() - 1);
            }
            grid.setRows(rows);
        }

        /**
         * For each row in the data, set the picker's selected property to the value of that data row's selected property.
         */
        private void resetPickers() {
            if (spreadsheet.getGrid().getRowCount() > 1) {
                DataRowPicker picker;
                for (int i = 1; i < spreadsheet.getGrid().getRowCount(); i++) {
                    picker = (DataRowPicker) spreadsheet.getRowPickers().get(i);
                    picker.setSelected(spreadsheet.getData().getRow(i - 1).isSelected());
                    if (!picker.isSelected()) {
                        for (SpreadsheetCell cell : spreadsheet.getGrid().getRows().get(i)) {
                            ((TopsoilDoubleCell) cell).setSelected(false);
                        }
                    }
                }
            }
        }

        /**
         * For each data cell in the spreadsheet, set its source to the corresponding value in the data.
         */
        private void resetDataCells() {
            Grid grid = spreadsheet.getGrid();
            if (grid.getRowCount() > 0) {
                ObservableList<ObservableList<SpreadsheetCell>> gridRows = grid.getRows();
                ObservableList<SpreadsheetCell> row;
                TopsoilDoubleCell cell;
                DoubleProperty source;

                for (int rowIndex = 0; rowIndex < spreadsheet.getData().rowCount(); rowIndex++) {
                    row = gridRows.get(rowIndex);
                    for (int colIndex = 0; colIndex < spreadsheet.getData().colCount(); colIndex++) {
                        source = spreadsheet.getData().get(colIndex, rowIndex);
                        cell = (TopsoilDoubleCell) row.get(colIndex);
                        if (cell.getSource() != source) {
                            cell.setSource(source);
                        }
                    }
                }
            }
        }
    }
}
