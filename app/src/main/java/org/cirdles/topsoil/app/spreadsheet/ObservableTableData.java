package org.cirdles.topsoil.app.spreadsheet;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.plot.TopsoilPlotView;
import org.cirdles.topsoil.variable.DependentVariable;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;

import java.util.*;

/**
 * Topsoil table data stored as columns of DoubleProperties.
 *
 * @author marottajb
 */
public class ObservableTableData extends Observable {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private ObservableList<TopsoilDataColumn> columns;
    private ObservableList<BooleanProperty> rowSelection;
    private int rowCount;
    private int colCount;

    private HashMap<Variable<Number>, TopsoilDataColumn> varMap;
    private Map<TopsoilPlotType, TopsoilPlotView> openPlots = new HashMap<>();

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private final StringProperty title = new SimpleStringProperty("");
    public final StringProperty titleProperty() {
        return title;
    }
    public String getTitle() {
        return title.get();
    }
    public void setTitle(String str) {
        title.set(str);
    }

    private final ObjectProperty<IsotopeSystem> isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
    public final ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        return isotopeSystem;
    }
    public IsotopeSystem getIsotopeSystem() {
        return isotopeSystem.get();
    }
    public void setIsotopeSystem(IsotopeSystem type ) {
        isotopeSystem.set(type);
    }

    private final ObjectProperty<UncertaintyFormat> unctFormat = new SimpleObjectProperty<>(UncertaintyFormat.ONE_SIGMA_ABSOLUTE);
    public final ObjectProperty<UncertaintyFormat> unctFormatProperty() {
        return unctFormat;
    }
    public UncertaintyFormat getUnctFormat() {
        return unctFormat.get();
    }
    public void setUnctFormat(UncertaintyFormat format) {
        unctFormat.set(format);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs an empty table of data.
     */
    public ObservableTableData() {
        this(null, true);
    }

    /**
     * Constructs a table from the provided data.
     * <p>
     * rowFormat should be true if the provided data is a list of rows, false if the provided data is in columns.
     *
     * @param   data
     *          List of Lists of Double values
     * @param   rowFormat
     *          true if data in rows, false if in columns
     */
    public ObservableTableData(Double[][] data, boolean rowFormat) {
        this(data, rowFormat, null, IsotopeSystem.GENERIC, UncertaintyFormat.TWO_SIGMA_ABSOLUTE);
    }

    /**
     * Constructs a table from the provided data.
     * <p>
     * rowFormat should be true if the provided data is a list of rows, false if the provided data is in columns.
     *
     * @param   data
     *          Lists of Double data rows
     * @param   headers
     *          a List of String headers for the columns
     * @param   rowFormat
     *          true if data in rows, false if in columns
     * @param   isotopeSystem
     *          List of Lists of Double values
     * @param   unctFormat
     *          true if data in rows, false if in columns
     */
    public ObservableTableData( Double[][] data, boolean rowFormat, String[] headers,
                                IsotopeSystem isotopeSystem, UncertaintyFormat unctFormat ) {
        this.columns = makeDataColumns(data, headers, rowFormat);
        this.rowSelection = FXCollections.observableArrayList();
        for (int i = 0; i < rowCount(); i++) {
            rowSelection.add(new SimpleBooleanProperty(true));
        }
        this.varMap = new HashMap<>();
        setIsotopeSystem(isotopeSystem);
        setUnctFormat(unctFormat);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Adds a row to the end of the table data with the provided {@code Double} values.
     *
     * @param row   List of Double values
     */
    public void addRow(List<Double> row) {
        addRow(rowCount - 1, row);
    }

    /**
     * Adds a row to the table data at the specified index with the provided {@code Double} values.
     *
     * @param   index
     *          index to insert row at
     * @param   row
     *          List of Double values
     */
    public void addRow(int index, List<Double> row) {
        for (int i = colCount; i < row.size(); i++) {
            columns.add(i, new TopsoilDataColumn());
            colCount++;
            for (int j = 0; j < rowCount; j++)  {
                columns.get(i).add(new SimpleDoubleProperty(0.0));
            }
        }

        for (int i = 0; i < colCount; i++) {
            columns.get(i).add(index, new SimpleDoubleProperty(
                    (i >= row.size()) ? 0.0 : row.get(i) )
            );
        }
        rowCount++;
        rowSelection.add(index, new SimpleBooleanProperty(true));

        setChanged();
        notifyObservers(new DataOperation(index, -1, OperationType.INSERT_ROW));
    }

    /**
     * Removes the row at the specified index.
     *
     * @param   index
     *          index of the row to be removed
     *
     * @return  List of the Double values in the removed row
     */
    public List<Double> removeRow(int index) {
        List<Double> oldRow = new ArrayList<>(colCount);

        for (TopsoilDataColumn column : columns) {
            oldRow.add( column.remove(index).get() );
        }
        rowCount--;
        rowSelection.remove(index);

	    setChanged();
        notifyObservers(new DataOperation(index, -1, OperationType.DELETE_ROW));

        return oldRow;
    }

    public List<DoubleProperty> getRow(int index) {
        List<DoubleProperty> row = new ArrayList<>(colCount);
        for (TopsoilDataColumn column : columns) {
            row.add(column.get(index));
        }
        return row;
    }

    /**
     * Adds a column to the end of the table data with the provided {@code Double} values.
     *
     * @param   column
     *          List of Double values
     */
    public void addColumn(List<Double> column) {
        addColumn(colCount - 1, column);
    }

    /**
     * Adds a column to the table data at the specified index with the provided {@code Double} values.
     *
     * @param   index
     *          index to insert column at
     * @param   col
     *          List of Double values
     */
    public void addColumn(int index, List<Double> col) {
        for (int i = rowCount; i < col.size(); i++) {
            for (TopsoilDataColumn column : columns) {
                column.add(new SimpleDoubleProperty(0.0));
            }
            rowCount++;
        }

        columns.add(index, new TopsoilDataColumn());
        colCount++;

        for (int i = 0; i < rowCount; i++) {
            columns.get(index).add(new SimpleDoubleProperty(
                    (i >= col.size()) ? 0.0 : col.get(i) )
            );
        }

	    setChanged();
	    notifyObservers(new DataOperation(-1, index, OperationType.INSERT_COLUMN));
    }
    public void addColumn(TopsoilDataColumn col) {
        addColumn(colCount, col);
    }

    public void addColumn(int index, TopsoilDataColumn col) {

        List<Double> values = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            values.add( (i >= col.size()) ? 0.0 : col.get(i).get() );
        }
        addColumn(index, values);
        columns.get(index).setName(col.getName());
    }

    /**
     * Removes the column at the specified index.
     *
     * @param   index
     *          index of the column to be removed
     *
     * @return  List of Double values in the removed column
     */
    public TopsoilDataColumn removeColumn(int index) {
        TopsoilDataColumn oldColumn = columns.remove(index);
        colCount--;

        setChanged();
        notifyObservers(new DataOperation(-1, index, OperationType.DELETE_COLUMN));

        return oldColumn;
    }

    /**
     * Sets the {@code Variable} of the {@link TopsoilDataColumn} at the provided index.
     *
     * @param   index
     *          the index of the data column
     * @param   variable
     *          the column's new Variable
     */
    public void setVariableForColumn(int index, Variable<Number> variable) {
        TopsoilDataColumn column = columns.get(index);

        if (varMap.containsKey(variable)) {
            varMap.get(variable).setVariable(null);
        }

        column.setVariable(variable);
        varMap.put(variable, column);
    }

    /**
     * Returns a {@code Map} containing mappings for available {@code Variable}s to {@code TopsoilDataColumn}s.
     *
     * @return  Map of Variables to TopsoilDataColumns
     */
    public Map<Variable<Number>, TopsoilDataColumn> variableToColumnMap() {
        return varMap;
    }

    /**
     * Gets the {@code DoubleProperty} at the specified row and column.
     *
     * @param   column
     *          column index
     * @param   row
     *          row index
     *
     * @return  DoubleProperty at indices
     */
    public DoubleProperty get(int column, int row) {
        return columns.get(column).get(row);
    }

    public void set(int column, int row, double value) {
        columns.get(column).get(row).set(value);
//        notifyObservers(new DataOperation(row, column, OperationType.CHANGE_VALUE));
    }

    public Double[][] getData() {
        Double[][] rows = new Double[rowCount][colCount];

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                rows[row][col] = columns.get(col).get(row).get();
            }
        }

        return rows;
    }

    public ObservableList<ObservableList<DoubleProperty>> getObservableRows() {

        ObservableList<ObservableList<DoubleProperty>> rows = FXCollections.observableArrayList();
        ObservableList<DoubleProperty> row;

        for (int i = 0; i < rowCount; i++) {
            row = FXCollections.observableArrayList();
            for (TopsoilDataColumn column : columns) {
                row.add(column.get(i));
            }
            rows.add(row);
        }

        return rows;
    }

    /**
     * Returns the data in the table as entries to be passed to a {@code Plot}.
     * <p>
     * Each data entry corresponds to a row in the data table. For each row, a {@code Map} is created containing
     * mappings from {@code String} keys representing {@code Variable}s to those variables' respective data values in
     * the row. For example, a row looking like this...
     *
     *            x        y              sigma_x  sigma_y    rho
     *       |  1.33  |  0.67  |  33.0  |  2.03  |  0.08  |  0.92  |
     *
     * ... would translate to a {@code Map} like this:
     *
     *      { "Selected"=true, "x"=1.33, "y"=0.67, "sigma_x"=2.03, "sigma_y"=0.08, "rho"=0.92 }
     *
     * @return  a List of data entry Maps
     */
    public List<Map<String, Object>> getPlotEntries() {
        List<Map<String, Object>> data = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Map<String, Object> entry = new HashMap<>();

            // TODO Customize entry selection.
            entry.put("Selected", rowSelection.get(rowIndex).get());

            for (Variable<Number> variable : Variables.VARIABLE_LIST) {
                Double value;
                if (varMap.containsKey(variable)) {
	                value = varMap.get(variable).get(rowIndex).get();
	                if (Variables.UNCERTAINTY_VARIABLES.contains(variable)) {
	                	value /= getUnctFormat().getValue();
		                if (variable instanceof DependentVariable && UncertaintyFormat.PERCENT_FORMATS.contains(getUnctFormat())) {
			                value *= varMap.get(((DependentVariable<Number>) variable).getDependency()).get(rowIndex).get();
		                }
	                }
                } else {
                    // Variable is unassigned, use zero value
                    value = 0.0;
                }
                entry.put(variable.getName(), value);
            }
            data.add(entry);
        }
        return data;
    }

    /**
     * Returns a read-only {@code ObservableList} containing the {@code TopsoilDataColumn}s for the data.
     *
     * @return  ObservableList of TopsoilDataColumns
     */
    public ObservableList<TopsoilDataColumn> getDataColumns() {
        return FXCollections.unmodifiableObservableList(columns);
    }

    public String[] getColumnHeaders() {
        String[] headers = new String[colCount];
        for (int i = 0; i < headers.length; i++) {
            headers[i] = columns.get(i).getName();
        }
        return headers;
    }

    public ObservableList<BooleanProperty> getRowSelection() {
        return rowSelection;
    }

    public void setRowSelected(int index, boolean selected) {
        rowSelection.get(index).set(selected);
        notifyObservers(new DataOperation(index, -1, selected ? OperationType.SELECT_ROW : OperationType.DESELECT_ROW));
    }

    /**
     * Returns the number of rows in the table data.
     *
     * @return  rowCount
     */
    public int rowCount() {
        return rowCount;
    }

    /**
     * Returns the number of columns in the table data.
     *
     * @return  colCount
     */
    public int colCount() {
        return colCount;
    }

    public Map<TopsoilPlotType, TopsoilPlotView> getOpenPlots() {
        return openPlots;
    }

    public void addPlot(TopsoilPlotType type, TopsoilPlotView plotView ) {
        openPlots.put(type, plotView);
    }

    public void removePlot(TopsoilPlotType type) {
	    ((Stage) openPlots.get(type).getScene().getWindow()).close();
        openPlots.remove(type);
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private ObservableList<TopsoilDataColumn> makeDataColumns(Double[][] data,
                                                              String[] headers,
                                                              boolean rowFormat) {
        ObservableList<TopsoilDataColumn> dataColumns = FXCollections.observableArrayList();

        if (rowFormat) {
            for (Double[] row : data) {
                colCount = Math.max(row.length, colCount);
            }
            rowCount = data.length;

            for (int i = 0; i < colCount; i++) {
                if (i < headers.length) {
                    dataColumns.add(new TopsoilDataColumn(headers[i]));
                } else {
                    dataColumns.add(new TopsoilDataColumn("Untitled"));
                }
            }

            for (Double[] row : data) {
                for (int i = 0; i < colCount; i++) {
                    dataColumns.get(i).add(new SimpleDoubleProperty(
                            (i >= row.length) ? 0.0 : row[i])
                    );
                }
            }

        } else {
            for (int colIndex = 0; colIndex < data.length; colIndex++) {
                dataColumns.add(new TopsoilDataColumn(headers[colIndex]));
                rowCount = Math.max(data[colIndex].length, rowCount);
            }
            colCount = data.length;

            for (int colIndex = 0; colIndex < colCount; colIndex++) {
                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                    dataColumns.get(colIndex).add(new SimpleDoubleProperty(
                            (rowIndex >= data[colIndex].length) ? 0.0 : data[colIndex][rowIndex])
                    );
                }
            }
        }
        return dataColumns;
    }

	//**********************************************//
	//                INNER CLASSES                 //
	//**********************************************//

    public class DataOperation {

	    private int row;
	    private int col;
    	private OperationType type;

    	public DataOperation(int row, int col, OperationType type) {
    		this.row = row;
    		this.col = col;
    		this.type = type;
	    }

	    public int getRowIndex() {
    		return row;
	    }

	    public int getColIndex() {
    		return col;
	    }

	    public OperationType getType() {
		    return type;
	    }

    }

	public enum OperationType {
		INSERT_ROW,
		DELETE_ROW,
		INSERT_COLUMN,
		DELETE_COLUMN,
//		CHANGE_VALUE,
        SELECT_ROW,
        DESELECT_ROW
	}

}
