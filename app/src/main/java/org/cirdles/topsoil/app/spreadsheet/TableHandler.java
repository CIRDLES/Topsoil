package org.cirdles.topsoil.app.spreadsheet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilDoubleCell;
import org.cirdles.topsoil.app.util.TopsoilException;
import org.controlsfx.control.spreadsheet.*;

import org.cirdles.topsoil.app.spreadsheet.ObservableTableData.DataOperation;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TableHandler implements Observer {

	private TopsoilSpreadsheetView spreadsheet;

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	TableHandler(TopsoilSpreadsheetView view) {
		this.spreadsheet = view;
	}

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

	@Override
	public void update( Observable o, Object arg ) {

		if (o instanceof ObservableTableData) {

			// Update SpreadsheetView with new data values
			ObservableTableData data = (ObservableTableData) o;
			DataOperation op = (DataOperation) arg;

			Grid grid = spreadsheet.getGrid();
			int rowIndex;
			int colIndex;
			DataRowPicker picker;

			switch (op.getType()) {
				case INSERT_ROW:
					try {
						rowIndex = op.getRowIndex();
						grid = addRow(rowIndex, data.getRow(rowIndex));
					} catch (TopsoilException e) {
						e.printStackTrace();
					}
					break;
				case DELETE_ROW:
					grid = removeRow(op.getRowIndex());
					break;
				case INSERT_COLUMN:
					try {
						colIndex = op.getColIndex();
						grid = addColumn(colIndex, Arrays.asList(data.getDataColumns().get(colIndex).getData()));
					} catch (TopsoilException e) {
						e.printStackTrace();
					}
					break;
				case DELETE_COLUMN:
					grid = removeColumn(op.getColIndex());
					break;
				case CHANGE_VALUE:
					rowIndex = op.getRowIndex();
					colIndex = op.getColIndex();
					SpreadsheetCell cell = grid.getRows().get(rowIndex).get(colIndex);

					if (cell.getCellType() == SpreadsheetCellType.DOUBLE) {
						if (Double.compare(data.get(colIndex, rowIndex).getValue(), (Double) cell.getItem()) != 0) {
							cell.setItem(data.get(colIndex, rowIndex).getValue());
						}
					}
					break;
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

			spreadsheet.setGrid(grid);

			// Update any open plots with new data
			for (TopsoilPlotView plotView : data.getOpenPlots().values()) {
				plotView.getPlot().setData(data.getPlotEntries());
			}
		}
	}

	//**********************************************//
	//               PRIVATE METHODS                //
	//**********************************************//

	private ObservableList<SpreadsheetCell> dataToCellRow( int index, List<Double> values ) {
		ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
		for (int i = 0; i < values.size(); i++) {
			cells.add(new TopsoilDoubleCell(
					spreadsheet,
					index,
					i,
					1,
					1,
					values.get(i)
			));
		}
		return cells;
	}

	private ObservableList<SpreadsheetCell> dataToCellColumn( int index, List<Double> values ) {
		ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
		SpreadsheetCell cell;
		for (int i = 0; i < values.size(); i++) {
			cell = SpreadsheetCellType.DOUBLE.createCell(
					i,
					index,
					1,
					1,
					values.get(i)
			                                            );
			cells.add(cell);
		}
		return cells;
	}

	private Grid addRow(int index, List<Double> values) throws TopsoilException {
		checkRowSize(values);

		Grid grid = spreadsheet.getGrid();
		ObservableList<ObservableList<SpreadsheetCell>> rows = copyRows(grid);

		rows.add(index, dataToCellRow(index, values));

		for (int rowIndex = index + 1; rowIndex < rows.size(); rowIndex++) {

			ObservableList<SpreadsheetCell> row = rows.get(rowIndex);
			for (int colIndex = 0; colIndex < row.size(); colIndex++) {
				SpreadsheetCell cell = row.get(colIndex);
				SpreadsheetCell newCell = SpreadsheetCellType.DOUBLE.createCell(
						cell.getRow() + 1,
						cell.getColumn(),
						cell.getRowSpan(),
						cell.getColumnSpan(),
						(Double) cell.getItem()
				                                                               );

				AtomicReference<SpreadsheetCell> cellRef = new AtomicReference<>(newCell);
				cell.itemProperty().addListener((observable, oldValue, newValue) ->
														spreadsheet.updateDataValue(cellRef.get(), (Double) newValue));
				row.set(colIndex, newCell);
			}
		}
		spreadsheet.getRowPickers().put(rows.size() - 1, new DataRowPicker(spreadsheet, rows.size() - 1));
		resetPickers();

		grid.setRows(rows);
		return grid;
	}

	private Grid removeRow(int index) {
		Grid grid = spreadsheet.getGrid();
		ObservableList<ObservableList<SpreadsheetCell>> rows = copyRows(grid);

		rows.remove(index);

		for (int rowIndex = index; rowIndex < rows.size(); rowIndex++) {
			ObservableList<SpreadsheetCell> row = rows.get(rowIndex);

			for (int colIndex = 0; colIndex < row.size(); colIndex++) {
				SpreadsheetCell cell = row.get(colIndex);
				SpreadsheetCell newCell = SpreadsheetCellType.DOUBLE.createCell(
						cell.getRow() - 1,
						cell.getColumn(),
						cell.getRowSpan(),
						cell.getColumnSpan(),
						(Double) cell.getItem()
				                                                               );
				row.set(colIndex, newCell);
			}
		}
		spreadsheet.getRowPickers().remove(rows.size() - 1, new DataRowPicker(spreadsheet, rows.size() - 1));
		resetPickers();
		
		grid.setRows(rows);
		return grid;
	}

	private Grid addColumn(int index, List<Double> values) throws TopsoilException {
		checkColumnSize(values);

		Grid grid = spreadsheet.getGrid();
		ObservableList<ObservableList<SpreadsheetCell>> rows = copyRows(grid);

		ObservableList<SpreadsheetCell> cellColumn = dataToCellColumn(index, values);
		ObservableList<SpreadsheetCell> row;

		for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
			row = rows.get(rowIndex);
			row.add(index, cellColumn.get(rowIndex));

			for (int colIndex = index + 1; colIndex < row.size(); colIndex++) {
				SpreadsheetCell cell = row.get(colIndex);
				SpreadsheetCell newCell = SpreadsheetCellType.DOUBLE.createCell(
						cell.getRow(),
						cell.getColumn() + 1,
						cell.getRowSpan(),
						cell.getColumnSpan(),
						(Double) cell.getItem()
				                                                               );
				row.set(colIndex, newCell);
			}
		}

		grid.setRows(rows);
		return grid;
	}

	private Grid removeColumn(int index) {
		Grid grid = spreadsheet.getGrid();
		ObservableList<ObservableList<SpreadsheetCell>> rows = copyRows(grid);

		for (ObservableList<SpreadsheetCell> row : rows) {
			row.remove(index);

			for (int colIndex = index; colIndex < row.size(); colIndex++) {
				SpreadsheetCell cell = row.get(colIndex);
				SpreadsheetCell newCell = SpreadsheetCellType.DOUBLE.createCell(
						cell.getRow(),
						cell.getColumn() - 1,
						cell.getRowSpan(),
						cell.getColumnSpan(),
						(Double) cell.getItem()
				                                                               );
				row.set(colIndex, newCell);
			}
		}

		grid.setRows(rows);
		return grid;
	}

	private ObservableList<ObservableList<SpreadsheetCell>> copyRows(Grid grid) {
		ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
		for (ObservableList<SpreadsheetCell> row : grid.getRows()) {
			rows.add(FXCollections.observableArrayList(row));
		}
		return rows;
	}

	private void checkRowSize(List<Double> newRow) throws TopsoilException {
		if (spreadsheet.getGrid().getColumnCount() != newRow.size()) {
			throw new TopsoilException("Invalid row length. " +
			                           "Expected: " + spreadsheet.getGrid().getColumnCount() + ", " +
			                           "Actual: " + newRow.size());
		}
	}

	private void checkColumnSize(List<Double> newCol) throws TopsoilException {
		if ( spreadsheet.getGrid().getRowCount() != newCol.size() ) {
			throw new TopsoilException("Invalid column length. " +
			                           "Expected: " + spreadsheet.getGrid().getRowCount() + ", " +
			                           "Actual: " + newCol.size());
		}
	}

	private void resetPickers() {
		DataRowPicker picker;
		for (int i = 1; i <= spreadsheet.getData().rowCount(); i++) {
			picker = (DataRowPicker) spreadsheet.getRowPickers().get(i);
			picker.setSelected(spreadsheet.getData().getRowSelection().get(i - 1).get());
			if ( ! picker.isSelected() ) {
				for (SpreadsheetCell cell : spreadsheet.getGrid().getRows().get(i)) {
					cell.setStyle("-fx-background-color: lightgray;");
				}
			}
		}
	}
}
