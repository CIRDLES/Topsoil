package org.cirdles.topsoil.app.data;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.util.ListenerHandlerBase;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.plot.TopsoilPlotView;
import org.cirdles.topsoil.variable.DependentVariable;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;

import java.util.*;

import static org.cirdles.topsoil.app.uncertainty.UncertaintyFormat.*;

/**
 * Topsoil table data stored as DoubleProperties.
 *
 * @author marottajb
 */
public class ObservableDataTable extends Observable {

    /*
        @TODO Divide functionality into smaller objects so this file may be less cluttered.
     */

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final RowSelectionHandler rowSelectionHandler;      // reference to avoid garbage collection; no others ATOW

    private ObservableList<ObservableDataColumn> columns;
    private ObservableList<ObservableDataRow> rows;
    private int rowCount;
    private int colCount;

    private HashMap<Variable<Number>, ObservableDataColumn> varMap;
    private Map<TopsoilPlotType, TopsoilPlotView> openPlots = new HashMap<>();


    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /**
     * The title of the data setValue.
     */
    private final StringProperty title = new SimpleStringProperty("");
    public StringProperty titleProperty() {
        return title;
    }
    public final String getTitle() {
        return title.get();
    }
    public final void setTitle(String str) {
        title.set(str);
    }

    /**
     * The {@code isotope system} of the data provided.
     */
    private final ObjectProperty<IsotopeSystem> isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() {
        return isotopeSystem.get();
    }
    public final void setIsotopeSystem(IsotopeSystem type ) {
        isotopeSystem.set(type);
    }

    /**
     * The {@code UncertaintyFormat} of provided uncertainty values.
     */
    private final ObjectProperty<UncertaintyFormat> unctFormat = new SimpleObjectProperty<>(UncertaintyFormat.ONE_SIGMA_ABSOLUTE);
    public ObjectProperty<UncertaintyFormat> unctFormatProperty() {
        return unctFormat;
    }
    public final UncertaintyFormat getUnctFormat() {
        return unctFormat.get();
    }
    public final void setUnctFormat(UncertaintyFormat format) {
        unctFormat.set(format);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ObservableDataTable() {
        this(new Double[][]{});
    }

    /**
     * Constructs a table from the provided data.
     * <p>
     * rowFormat should be true if the provided data is a list of rows, false if the provided data is in columns.
     *
     * @param   data
     *          List of Lists of Double values
     */
    public ObservableDataTable(Double[][] data) {
        this(
                (data != null ? data : new Double[][]{}),
                null,
                IsotopeSystem.GENERIC,
                UncertaintyFormat.ONE_SIGMA_ABSOLUTE
        );
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
     * @param   isotopeSystem
     *          List of Lists of Double values
     * @param   unctFormat
     *          true if data in rows, false if in columns
     */
    public ObservableDataTable(Double[][] data, String[] headers, IsotopeSystem isotopeSystem,
                               UncertaintyFormat unctFormat ) {
        if (data == null) {
            data = new Double[][]{};
        }
        if (headers == null) {
            headers = new String[]{};
        }

        // Make ObservableDataColumns and ObservableDataRows with shared DoubleProperties for the data
        this.columns = getDataColumns(data, headers);
        this.rows = FXCollections.observableArrayList();
        ObservableDataRow newRow;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            newRow = new ObservableDataRow();
            for (ObservableDataColumn column : columns) {
                newRow.add(column.get(rowIndex));
            }
            rows.add(newRow);
        }

        rowSelectionHandler = new RowSelectionHandler(this);
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
        addRow(rowCount, row);
    }

    /**
     * Adds a row to the table data at the specified index with the provided {@code Double} values.
     *
     * @param   index
     *          index to insert row at
     * @param   newRow
     *          List of Double values
     */
    public void addRow(int index, List<Double> newRow) {
        addRow(index, new ObservableDataRow(newRow.toArray(new Double[]{})));
    }

    /**
     * Adds the provided {@code ObservableDataRow} to the table.
     *
     * @param   newRow
     *          ObservableDataRow
     */
    public void addRow(ObservableDataRow newRow) {
        addRow(rowCount, newRow);
    }

    /**
     * Adds the provided {@code ObservableDataRow} to the table at the specified index.
     *
     * @param   index
     *          index to insert row at
     * @param   newRow
     *          ObservableDataRow
     */
    public void addRow(int index, ObservableDataRow newRow) {

        // Expand new row, if row size is less than the current number of columns
        for (int underIndex = newRow.size(); underIndex < colCount; underIndex++) {
            newRow.add(new SimpleDoubleProperty(0.0));
        }

        // Expand data table, if row size exceeds number of columns
        ObservableDataColumn newColumn;
        DoubleProperty newProp;
        for (int overIndex = colCount; overIndex < newRow.size(); overIndex++) {   // creates new columns
            newColumn = new ObservableDataColumn();
            for (ObservableDataRow row : rows) {  // creates new DoubleProperties and adds them to the proper row and col
                newProp = new SimpleDoubleProperty(0.0);
                newColumn.add(newProp);
                row.add(newProp);
            }
            this.addColumn(overIndex, newColumn);
        }

        // Add DoubleProperties from row to proper columns
        for (int colIndex = 0; colIndex < colCount; colIndex++) {
            // If index == rowCount, append row
            if (index == rowCount) {
                columns.get(colIndex).add(newRow.get(colIndex));
            } else {
                columns.get(colIndex).add(index, newRow.get(colIndex));
            }
        }

        rows.add(index, newRow);
        rowCount++;
        setChanged();
        notifyObservers(new DataOperation(index, -1, OperationType.INSERT_ROW));
        updateOpenPlots();
    }

    /**
     * Removes the row at the specified index.
     *
     * @param   index
     *          index of the row to be removed
     *
     * @return  the removed ObservableDataRow
     */
    public ObservableDataRow removeRow(int index) {

        // Remove the row's DoubleProperties from each column
        for (ObservableDataColumn column : columns) {
            column.remove(index);
        }

        ObservableDataRow removedRow = rows.remove(index);
        rowCount--;
	    setChanged();
        notifyObservers(new DataOperation(index, -1, OperationType.DELETE_ROW));
        updateOpenPlots();

        return removedRow;
    }

    /**
     * Returns the {@code ObservableDataRow} at the provided index.
     *
     * @param   index
     *          index of desired ObservableDataRow
     * @return  ObservableDataRow at index
     */
    public ObservableDataRow getRow(int index) {
        return rows.get(index);
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
     * @param   values
     *          List of Double values
     */
    public void addColumn(int index, List<Double> values) {
	    ObservableDataColumn column = new ObservableDataColumn("Untitled");
	    for (Double val : values) {
	        column.add(new SimpleDoubleProperty(val));
        }
        addColumn(index, column);
    }

    /**
     * Adds the provided {@code ObservableDataColumn} to the table.
     *
     * @param   newColumn
     *          ObservableDataColumn
     */
    public void addColumn(ObservableDataColumn newColumn) {
        addColumn(colCount, newColumn);
    }

    /**
     * Adds the provided {@code ObservableDataColumn} to the table at the specified index.
     *
     * @param   index
     *          index of new column
     * @param   newColumn
     *          ObservableDataColumn
     */
    public void addColumn(int index, ObservableDataColumn newColumn) {

        // Expand new column, if column size is less than the number of rows
        for (int underIndex = newColumn.size(); underIndex < rowCount; underIndex++) {
            newColumn.get(index).add(new SimpleDoubleProperty(0.0));
        }

        // Expand data table rows, if column size exceeds number of rows
        ObservableDataRow newRow;
        DoubleProperty newProp;
        for (int overIndex = rowCount; overIndex < newColumn.size(); overIndex++) {
            newRow = new ObservableDataRow();
            for (ObservableDataColumn col : columns) {  // creates new DoubleProperties and adds them to the proper row
                // and col
                newProp = new SimpleDoubleProperty(0.0);
                newRow.add(newProp);
                col.add(newProp);
            }
            this.addRow(overIndex, newRow);
        }

        // Add DoubleProperties from col to proper rows
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            rows.get(rowIndex).add(index, newColumn.get(rowIndex));
        }

        columns.add(index, newColumn);
        colCount++;
        setChanged();
        notifyObservers(new DataOperation(-1, index, OperationType.INSERT_COLUMN));
        updateOpenPlots();
    }

    /**
     * Removes the column at the specified index.
     *
     * @param   index
     *          index of the column to be removed
     *
     * @return  List of Double values in the removed column
     */
    public ObservableDataColumn removeColumn(int index) {

        // Remove the column's DoubleProperties from each row
        for (ObservableDataRow row : rows) {
            row.remove(index);
        }

        ObservableDataColumn oldColumn = columns.remove(index);
        colCount--;
        setChanged();
        notifyObservers(new DataOperation(-1, index, OperationType.DELETE_COLUMN));
        updateOpenPlots();

        return oldColumn;
    }

    /**
     * Sets the {@code Variable} of the {@link ObservableDataColumn} at the provided index.
     *
     * @param   index
     *          the index of the data column
     * @param   variable
     *          the column's new Variable
     */
    public void setVariableForColumn(int index, Variable<Number> variable) {
        ObservableDataColumn column = columns.get(index);
        if (column.getVariable() != variable) {
            setVariableForColumn(variable, column);

            setChanged();
            notifyObservers(new DataOperation(-1, -1, OperationType.UPDATE_VARIABLES));
            updateOpenPlots();
        }
    }

    /**
     * @param   map
     *          Map containing entries for variable assignments
     */
    public void setVariablesForColumns(Map<Integer, Variable<Number>> map) {
        Variable<Number> variable;
        ObservableDataColumn column;

        for (Map.Entry<Integer, Variable<Number>> entry : map.entrySet()) {
            variable = entry.getValue();
            column = (entry.getKey() >= 0 ? columns.get(entry.getKey()) : null);
            setVariableForColumn(variable, column);
        }

        setChanged();
        notifyObservers(new DataOperation(-1, -1, OperationType.UPDATE_VARIABLES));
        updateOpenPlots();
    }

    /**
     * Returns a {@code Map} containing mappings for available {@code Variable}s to {@code ObservableDataColumn}s.
     *
     * @return  Map of Variables to TopsoilDataColumns
     */
    public Map<Variable<Number>, ObservableDataColumn> getVarMap() {
        return varMap;
    }

    /**
     * Unassigns all variables from all columns.
     */
    public void clearVariableAssignments() {
        for (ObservableDataColumn column : varMap.values()) {
            column.setVariable(null);
        }
        varMap.clear();
        setChanged();
        notifyObservers(new DataOperation(-1, -1, OperationType.UPDATE_VARIABLES));
        updateOpenPlots();
    }

    /**
     * Gets the {@code DoubleProperty} at the specified row and column.
     *
     * @param   row
     *          row index
     * @param   column
     *          column index
     *
     * @return  DoubleProperty at indices
     */
    public DoubleProperty get(int row, int column) {
        return rows.get(row).get(column);
    }

    /**
     * Returns the value at the specified row and column.
     *
     * @param   column
     *          column index
     * @param   row
     *          row index
     *
     * @return  double data value
     */
    public double getValue(int row, int column) {
        return get(row, column).get();
    }

    /**
     * Sets the {@code DoubleProperty} at the specified row and column.
     *
     * @param   row
     *          row index
     * @param   column
     *          column index
     * @param   property
     *          new DoubleProperty
     *
     * @return  old DoubleProperty
     */
    public DoubleProperty set(int row, int column, DoubleProperty property) {
        DoubleProperty rtnval = rows.get(row).set(column, property);
        setChanged();
        notifyObservers(new DataOperation(row, column, OperationType.UPDATE_PROPERTY));
        updateOpenPlots();
        return rtnval;
    }

    /**
     * Sets the value at the specified row and column.
     *
     * @param   column
     *          column index
     * @param   row
     *          row index
     * @param   value
     *          new double value
     */
    public void setValue(int row, int column, double value) {
        get(row, column).set(value);
        updateOpenPlots();
    }

    /**
     * Returns the table's data values as a {@code Double[]} containing the table's rows as {@code Double[]}'s.
     *
     * @return  Double[][] table data rows
     */
    public Double[][] getData() {
        Double[][] rows = new Double[rowCount][colCount];

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                rows[row][col] = columns.get(col).get(row).get();
            }
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
     *                  x        y              sigma_x  sigma_y    rho
     *       [ON]  |  1.33  |  0.67  |  33.0  |  2.03  |  0.08  |  0.92  |
     *
     * ... would translate to a {@code Map} like this:
     *
     *      { "Selected"=true, "x"=1.33, "y"=0.67, "sigma_x"=2.03, "sigma_y"=0.08, "rho"=0.92 }
     *
     * @return  a List of data entry Maps
     */
    public List<Map<String, Object>> getPlotEntries() {
        List<Map<String, Object>> data = new ArrayList<>();

        // For each row in the data...
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Map<String, Object> entry = new HashMap<>();

            // put selected/unselected
            entry.put("Selected", rows.get(rowIndex).isSelected());

            // put assigned variable values; if variable is unassigned, use 0.0
            for (Variable<Number> variable : Variables.VARIABLE_LIST) {
                Double value;
                if (varMap.containsKey(variable)) {
	                value = varMap.get(variable).get(rowIndex).get();
                    if (variable instanceof DependentVariable && UncertaintyFormat.PERCENT_FORMATS.contains(getUnctFormat())) {
                        value /= 100;
                        value *= varMap.get(((DependentVariable<Number>) variable).getDependency()).get(rowIndex).get();
                    }
                } else {
                    // variable is unassigned, use zero value
                    value = 0.0;
                }
                entry.put(variable.getName(), value);
            }
            data.add(entry);
        }
        return data;
    }

    /**
     * Returns a read-only {@code ObservableList} containing the {@code ObservableDataColumn}s for the data.
     *
     * @return  ObservableList of TopsoilDataColumns
     */
    public ObservableList<ObservableDataColumn> getColumns() {
        return FXCollections.unmodifiableObservableList(columns);
    }

    /**
     * Returns a read-only {@code ObservableList} containing the {@code ObservableDataColumn}s for the data.
     *
     * @return  ObservableList of TopsoilDataColumns
     */
    public ObservableList<ObservableDataRow> getRows() {
        return FXCollections.unmodifiableObservableList(rows);
    }

    /**
     * Returns an array containing the table's column headers.
     *
     * @return  String[] of column headers
     */
    public String[] getColumnHeaders() {
        String[] headers = new String[colCount];
        for (int i = 0; i < headers.length; i++) {
            headers[i] = columns.get(i).getHeader();
        }
        return headers;
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

    /**
     * Returns a {@code Map} containing {@code TopsoilPlotView}s for this table as values, with their {@code
     * TopsoilPlotType} as the key.
     *
     * @return  map of open plot controllers
     */
    public Map<TopsoilPlotType, TopsoilPlotView> getOpenPlots() {
        return openPlots;
    }

    /**
     * Updates the data in all open plots for this table.
     */
    private void updateOpenPlots() {
        for (TopsoilPlotView plotView : openPlots.values()) {
            plotView.getPlot().setData(getPlotEntries());
        }
    }

    /**
     * Adds an open plot for this table to {@code openPlots}.
     *
     * @param   type
     *          the plot's TopsoilPlotType
     * @param   plotView
     *          the TopsoilPlotView for the plot
     */
    public void addPlot(TopsoilPlotType type, TopsoilPlotView plotView ) {
        openPlots.put(type, plotView);
    }

    /**
     * Removes a plot from {@code openPlots} and closes the plot's {@code Stage}.
     *
     * @param   type
     *          the plot's TopsoilPlotType
     */
    public void removePlot(TopsoilPlotType type) {
	    ((Stage) openPlots.get(type).getScene().getWindow()).close();
        openPlots.remove(type);
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    /**
     * @param   data
     *          Double[][] of data values
     * @param   headers
     *          String[] of column headers
     *
     * @return  ObservableList of ObservableDataColumns
     */
    private ObservableList<ObservableDataColumn> getDataColumns(Double[][] data, String[] headers) {
        ObservableList<ObservableDataColumn> dataColumns = FXCollections.observableArrayList();

        for (Double[] row : data) {
            colCount = Math.max(row.length, colCount);
        }
        rowCount = data.length;

        for (int i = 0; i < colCount; i++) {
            if (i < headers.length) {
                dataColumns.add(new ObservableDataColumn(headers[i]));
            } else {
                dataColumns.add(new ObservableDataColumn("Untitled"));
            }
        }

        for (Double[] row : data) {
            for (int i = 0; i < colCount; i++) {
                dataColumns.get(i).add(new SimpleDoubleProperty(
                        (i >= row.length) ? 0.0 : row[i])
                );
            }
        }
        return dataColumns;
    }

    private void setVariableForColumn(Variable<Number> variable, ObservableDataColumn column) {
        if (column != null) {
            if (variable == null) {
                if (column.hasVariable()) {
                    varMap.remove(column.getVariable());
                    column.setVariable(null);
                }
            } else {
                if (variable != column.getVariable()) {
                    if (column.hasVariable()) {
                        varMap.remove(column.getVariable());        // remove assignment to column's old variable
                    }
                    if (varMap.containsKey(variable)) {
                        varMap.get(variable).setVariable(null);     // remove variable from a previously assigned column
                        varMap.remove(variable);
                    }
                    Variable oldVariable = column.getVariable();
                    column.setVariable(variable);
                    if (Variables.UNCERTAINTY_VARIABLES.contains(variable)) {
                        if (! Variables.UNCERTAINTY_VARIABLES.contains(oldVariable)) {
                            uncertainifyColumn(column, ONE_SIGMA_ABSOLUTE, getUnctFormat());
                        }
                    } else {
                        if (Variables.UNCERTAINTY_VARIABLES.contains(oldVariable)) {
                            uncertainifyColumn(column, getUnctFormat(), ONE_SIGMA_ABSOLUTE);
                        }
                    }
                    varMap.put(variable, column);
                }
            }
            column.setVariable(variable);
        } else {
            if (variable != null) {
                varMap.get(variable).setVariable(null);
                varMap.remove(variable);
            }
        }
    }

    /**
     * Each {@code DoubleProperty} in the column is converted to some uncertainty format.
     *
     * @param   column
     *          ObservableDataColumn
     * @param   oldFormat
     *          the former UncertaintyFormat of the values in the column
     * @param   newFormat
     *          the former UncertaintyFormat of the values in the column
     */
    private void uncertainifyColumn(ObservableDataColumn column, UncertaintyFormat oldFormat,
                                    UncertaintyFormat newFormat) {
        if (oldFormat != newFormat) {
            double value;
            for (DoubleProperty property : column) {
                value = property.get();
                if (Double.compare(oldFormat.getValue(), newFormat.getValue()) != 0) {
                    value *= oldFormat.getValue();      // un-convert
                    value /= newFormat.getValue();      // convert to newFormat
                }
                property.set(value);
            }
        }
    }

	//**********************************************//
	//                INNER CLASSES                 //
	//**********************************************//

    /**
     * Information objects that are passed to {@code Observer}s when {@code notifyObservers()} is called.
     * <p>
     * Whenever an {@link ObservableDataTable} is modified, it notifies its observers and passes a {@code
     * DataOperation} object containing the change's row index (if applicable, -1 if not), the change's column index
     * (if applicable, -1 if not), and the {@code OperationType} denoting the kind of change that took place.
     */
    public class DataOperation {

	    private int row;
	    private int col;
    	private OperationType type;

    	private DataOperation(int row, int col, OperationType type) {
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
        SELECT_ROW,
        DESELECT_ROW,
        UPDATE_VARIABLES,
        UPDATE_PROPERTY
	}

    /**
     * Responsible for notifying its {@link ObservableDataTable} of changes in the selection of its
     * {@link ObservableDataRow}s.
     */
	private class RowSelectionHandler extends ListenerHandlerBase<ObservableDataRow> {

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private ObservableDataTable data;
        private Map<ObservableDataRow, ChangeListener<Object>> rowSelectionListeners = new HashMap<>();
        private ListChangeListener<ObservableDataRow> rowAddedRemovedListener = c -> {
            while (c.next()) {
                for (ObservableDataRow row : c.getRemoved()) {
                    forget(row);
                }
                for (ObservableDataRow row : c.getAddedSubList()) {
                    listen(row);
                }
            }
        };

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        /**
         * Constructs a {@code RowSelectionHandler} for the specified {@code ObservableTableData}.
         *
         * @param   table
         *          ObservableTableData
         */
        private RowSelectionHandler(ObservableDataTable table) {
            data = table;

            // Listen for row additions/deletions
            data.rows.addListener(rowAddedRemovedListener);

            // Listen to existing rows
            for (ObservableDataRow row : data.rows) {
                listen(row);
            }
        }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        /**
         * Starts listening on the specified row.
         *
         * @param   row
         *          ObservableDataRow
         */
        public void listen(ObservableDataRow row) {
            ChangeListener<Object> selectionListener = (observable, oldValue, newValue) -> {
                setChanged();
                data.notifyObservers(
                        new DataOperation(
                                data.rows.indexOf(row),
                                -1,
                                (row.isSelected() ? OperationType.SELECT_ROW : OperationType.DESELECT_ROW)
                        )
                );
                data.updateOpenPlots();
            };
            row.selectedProperty().addListener(selectionListener);
            rowSelectionListeners.put(row, selectionListener);
        }

        /**
         * Stops listening on the specified row.
         *
         * @param   row
         *          ObservableDataRow
         */
        public void forget(ObservableDataRow row) {
            row.selectedProperty().removeListener(rowSelectionListeners.get(row));
            rowSelectionListeners.remove(row);
        }
    }
}
