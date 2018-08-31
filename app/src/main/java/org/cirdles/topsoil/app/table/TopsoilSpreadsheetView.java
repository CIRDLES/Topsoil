package org.cirdles.topsoil.app.table;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.cirdles.topsoil.app.tab.TabPaneHandler;
import org.cirdles.topsoil.app.table.command.DeleteColumnCommand;
import org.cirdles.topsoil.app.table.command.DeleteRowCommand;
import org.cirdles.topsoil.app.table.command.InsertColumnCommand;
import org.cirdles.topsoil.app.table.command.InsertRowCommand;
import org.controlsfx.control.spreadsheet.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author marottajb
 */
public class TopsoilSpreadsheetView extends SpreadsheetView {

    private ObservableTableData data;
    private TableHandler tableHandler;      // reference to avoid garbage collection; no others ATOW
	private DataCellFormatter dataCellFormatter;

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

    public TopsoilSpreadsheetView(ObservableTableData data) {
        this(data, SpreadsheetConstants.DEFAULT_REGULAR_DOUBLE_PATTERN);
    }

    public TopsoilSpreadsheetView(ObservableTableData data, String format) {
        super();

        this.data = data;
        setFormat(format);

        setGrid(makeGrid(this.data));
        getFixedRows().add(0);

	    tableHandler = new TableHandler(this);
	    data.addObserver(tableHandler);

	    dataCellFormatter = new DataCellFormatter(this);
	    this.format.addListener(c -> dataCellFormatter.format() );

        for (SpreadsheetColumn column : getColumns()) {
            column.setPrefWidth(SpreadsheetConstants.INIT_COLUMN_WIDTH);
        }

        dataCellFormatter.format();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ObservableTableData getData() {
        return data;
    }

    @Override
    public ContextMenu getSpreadsheetViewContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();

        final Menu addRowMenu = new Menu("Insert Row");
        final MenuItem addRowAboveItem = new MenuItem("Above");
        addRowAboveItem.setOnAction(event -> {
            TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
            int index = getModelRow(pos.getRow());

            List<Double> row = new ArrayList<>(data.colCount());
            for (int i = 0; i < data.colCount(); i++) {
                row.add(0.0);
            }

            InsertRowCommand command = new InsertRowCommand(data, index, row);
            command.execute();
            TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
        });
        final MenuItem addRowBelowItem = new MenuItem("Below");
        addRowBelowItem.setOnAction(event -> {
            TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
            int index = getModelRow(pos.getRow()) + 1;

            List<Double> row = new ArrayList<>(data.colCount());
            for (int i = 0; i < data.colCount(); i++) {
                row.add(0.0);
            }

            InsertRowCommand command = new InsertRowCommand(data, index, row);
            command.execute();
            TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
        });
        addRowMenu.getItems().addAll(addRowAboveItem, addRowBelowItem);

        final MenuItem deleteRowItem = new MenuItem("Delete Row");
        deleteRowItem.setOnAction(event -> {
            TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
            int index = getModelRow(pos.getRow());

            DeleteRowCommand command = new DeleteRowCommand(data, index);
            command.execute();
            TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
        });

        final Menu addColumnMenu = new Menu("Add Column");
        final MenuItem addColumnLeftItem = new MenuItem("Left");
        addColumnLeftItem.setOnAction(event -> {
            TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
            int index = getModelRow(pos.getColumn());

            List<Double> column = new ArrayList<>(data.rowCount());
            for (int i = 0; i < data.rowCount(); i++) {
                column.add(0.0);
            }

            InsertColumnCommand command = new InsertColumnCommand(data, index, column);
            command.execute();
            TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
        });
        final MenuItem addColumnRightItem = new MenuItem("Right");
        addColumnRightItem.setOnAction(event -> {
            TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
            int index = getModelRow(pos.getColumn()) + 1;

            List<Double> column = new ArrayList<>(data.rowCount());
            for (int i = 0; i < data.rowCount(); i++) {
                column.add(0.0);
            }

            InsertColumnCommand command = new InsertColumnCommand(data, index, column);
            command.execute();
            TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
        });
        addColumnMenu.getItems().addAll(addColumnLeftItem, addColumnRightItem);

        final MenuItem deleteColumnItem = new MenuItem("Delete Column");
        deleteColumnItem.setOnAction(event -> {
            TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getSelectionModel().getFocusedCell();
            int index = getModelRow(pos.getColumn());

            DeleteColumnCommand command = new DeleteColumnCommand(data, index);
            command.execute();
            TabPaneHandler.getTabPane().getSelectedTab().addUndo(command);
        });

        contextMenu.getItems().addAll(
                addRowMenu,
                deleteRowItem,
                new SeparatorMenuItem(),
                addColumnMenu,

                deleteColumnItem);

        return contextMenu;
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

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private GridBase makeGrid(ObservableTableData data) {

        GridBase grid = new GridBase(data.rowCount(), data.colCount());

        ObservableList<ObservableList<DoubleProperty>> dataRows = data.getObservableRows();
        ObservableList<ObservableList<SpreadsheetCell>> cellRows = FXCollections.observableArrayList();
        SpreadsheetCell cell;

	    ObservableList<TopsoilDataColumn> columns = data.getDataColumns();
	    ObservableList<SpreadsheetCell> headerRow = FXCollections.observableArrayList();
	    for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
		    cell = SpreadsheetCellType.STRING.createCell(
				    data.rowCount(),
				    colIndex,
				    1,
				    1,
				    columns.get(colIndex).getName()
		                                                );
		    columns.get(colIndex).nameProperty().bindBidirectional(cell.itemProperty(), new StringConverterWithFormat<Object>() {
			    @Override
			    public Object fromString(String arg0) {
				    return arg0;
			    }
			    @Override
			    public String toString(Object arg0) {
				    return arg0 == null ? "" : arg0.toString();
			    }
		    });
		    cell.setStyle("-fx-background-color: lightgray;");

		    headerRow.add(cell);
	    }
	    cellRows.add(headerRow);

        for (int rowIndex = 1; rowIndex <= data.rowCount(); rowIndex++) {
            ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();

            for (int colIndex = 0; colIndex < data.colCount(); colIndex++) {
                cell = SpreadsheetCellType.DOUBLE.createCell(
                        rowIndex,
                        colIndex,
                        1,
                        1,
                        dataRows.get(rowIndex - 1).get(colIndex).get()
                );
//                cell.formatProperty().bind(format);
                cell.setStyle("-fx-alignment: CENTER_RIGHT; -fx-font-family: monospaced");

	            AtomicReference<SpreadsheetCell> cellRef = new AtomicReference<>(cell);
                cell.itemProperty().addListener((observable, oldValue, newValue) ->
		                                                updateDataValue(cellRef.get(), (Double) newValue));

                row.add(cell);
            }
            cellRows.add(row);
        }
        grid.setRows(cellRows);
        return grid;
    }

    private void updateDataValue(SpreadsheetCell cell, Double value) {
    	int row = cell.getRow() - 1;
    	int col = cell.getColumn();
	    data.set(col, row, value);
    	dataCellFormatter.formatColumn(cell.getColumn());
    }

	/**
	 *
	 */
	private class DataCellFormatter {

    	private TopsoilSpreadsheetView spreadsheet;
    	private ChangeListener<Object> cellUpdatedListener = (observable, oldValue, newValue) -> {

	    };
    	private ListChangeListener<ObservableList<SpreadsheetCell>> rowAddedRemovedListener =
			    (ListChangeListener<ObservableList<SpreadsheetCell>>) c -> {
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

    	private DataCellFormatter(TopsoilSpreadsheetView tsv) {
    		spreadsheet = tsv;

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

	    void format() {
    		for (int i = 0; i < spreadsheet.getGrid().getColumnCount(); i++) {
    			formatColumn(i);
		    }
	    }

	    //**********************************************//
	    //                PRIVATE METHODS               //
	    //**********************************************//

	    private void listen(SpreadsheetCell cell) {
		    cell.itemProperty().addListener(cellUpdatedListener);
	    }

	    private void forget(SpreadsheetCell cell) {
		    cell.itemProperty().removeListener(cellUpdatedListener);
	    }

	    private void formatColumn(int col) {
			ObservableList<ObservableList<SpreadsheetCell>> rows = spreadsheet.getItems();
		    SpreadsheetCell cell;
			String text;
		    int placesAfterDecimal;
			int maxPlacesAfterDecimal = 0;

			// find the maximum number of characters following the decimal point
			for (ObservableList<SpreadsheetCell> row : rows) {
				cell = row.get(col);
				if (cell.getCellType() instanceof SpreadsheetCellType.DoubleType) {
					text = cell.getText();
					placesAfterDecimal = text.contains(".") ? text.substring(text.lastIndexOf(".") + 1).length() : 0;
					maxPlacesAfterDecimal = Math.max(maxPlacesAfterDecimal, placesAfterDecimal);
				}
			}

			// add the appropriate amount of whitespace to each cell format
			StringBuilder builder;
			for (ObservableList<SpreadsheetCell> row : rows) {
				cell = row.get(col);
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
}
