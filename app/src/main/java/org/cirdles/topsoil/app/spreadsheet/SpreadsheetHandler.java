package org.cirdles.topsoil.app.spreadsheet;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilDoubleCell;
import org.cirdles.topsoil.app.spreadsheet.picker.DataRowPicker;
import org.controlsfx.control.spreadsheet.*;

import org.cirdles.topsoil.app.data.ObservableDataTable.DataOperation;

import java.util.*;

/**
 * This class handles the spreadsheet and its cells in response to changes in the data model.
 */
public class SpreadsheetHandler implements Observer {

	private TopsoilSpreadsheetView spreadsheet;

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	SpreadsheetHandler(TopsoilSpreadsheetView view) {
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
			DataOperation op = (DataOperation) arg;

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
