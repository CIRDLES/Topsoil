package org.cirdles.topsoil.app.table;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.plot.Plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {@code TopsoilDataTable} is the core data model for Topsoil. It contains table data as an {@code ObservableList}
 * of {@link TopsoilDataColumn}s, as well as supplemental information about that data, such as a title, column names,
 * an {@link IsotopeType} and any open {@link Plot}s that represent data in the {@code TopsoilDataTable}.
 *
 * @author Benjamin Muldrow
 * @author Jake Marotta
 *
 * @see TableView
 * @see TopsoilDataEntry
 * @see IsotopeType
 * @see Plot
 */
public class TopsoilDataTable {

    //***********************
    // Attributes
    //***********************

    /**
     * Table columns which have been associated with a {@code Variable}.
     */
    private Map<Variable<Number>, TopsoilDataColumn> variableColumnMap;

    /**
     * The table data in the form of {@code TopsoilDataColumn}s.
     */
    private ObservableList<TopsoilDataColumn> dataColumns;

    /**
     * The number of rows of data.
     */
    private int numRows;

    /**
     * The {@code StringProperty} for the title of the table.
     */
    private StringProperty title;
    public StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty("Untitled Table");
        }
        return title;
    }
    public String getTitle() {
        return titleProperty().get();
    }
    public void setTitle(String title) {
        titleProperty().set(title);
    }

    /**
     * An {@code ObjectProperty} containing the {@code IsotopeType} of the TopsoilTable.
     */
    private ObjectProperty<IsotopeType> isotopeType;
    public ObjectProperty<IsotopeType> isotopeTypeObjectProperty() {
        if (isotopeType == null) {
            isotopeType = new SimpleObjectProperty<>(IsotopeType.Generic);
        }
        return isotopeType;
    }
    public IsotopeType getIsotopeType() {
        return isotopeTypeObjectProperty().get();
    }
    public void setIsotopeType(IsotopeType isotopeType) {
        isotopeTypeObjectProperty().set(isotopeType);
    }

    /**
     * An {@code UncertaintyFormat} for application to the values in uncertainty columns.
     */
    private UncertaintyFormat uncertaintyFormat;

    /**
     * A {@code Map} containing {@code PlotInformation} for an open plot of each type.
     */
    private HashMap<TopsoilPlotType, PlotInformation> openPlots;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a TopsoilDataTable with the specified column names, IsotopeType, and table data (in the form of
     * TopsoilDataEntries).
     *
     * @param columnNames   a String[] of column names
     * @param isotopeType   IsotopeType
     * @param format    the UncertaintyFormat of the uncertainty values in the data
     * @param rows  TopsoilDataEntries of data rows
     */
    public TopsoilDataTable(String[] columnNames, IsotopeType isotopeType, UncertaintyFormat format,
                            TopsoilDataEntry... rows) {
        this.numRows = rows.length == 0 ? 1 : rows.length;
        this.isotopeType = new SimpleObjectProperty<>(isotopeType);
        this.uncertaintyFormat = format;
        this.variableColumnMap = new HashMap<>();
        this.dataColumns = convertRowData(rows);
        addNamesToColumns(columnNames);
        this.openPlots = new HashMap<>();
    }

    /**
     * Constructs a TopsoilDataTable with the specified column names, IsotopeType, and table data (in the form of
     * Double arrays).
     *
     * @param columnNames   a String[] of column names
     * @param isotopeType   IsotopeType
     * @param format    the UncertaintyFormat of the uncertainty values in the data
     * @param rows  Double[]s of data rows
     */
    public TopsoilDataTable(String[] columnNames, IsotopeType isotopeType, UncertaintyFormat format,
                            Double[]... rows) {
        this.numRows = rows.length;
        this.isotopeType = new SimpleObjectProperty<>(isotopeType);
        this.uncertaintyFormat = format;
        this.variableColumnMap = new HashMap<>();
        this.dataColumns = convertRowData(rows);
        addNamesToColumns(columnNames);
        this.openPlots = new HashMap<>();
    }

    //***********************
    // Methods
    //***********************

    /**
     * Gets the {@code UncertaintyFormat} of the table.
     *
     * @return  the table's UncertaintyFormat
     */
    public UncertaintyFormat getUncertaintyFormat() {
        return uncertaintyFormat;
    }

    /**
     * Sets the {@code UncertaintyFormat} of the {@code TopsoilDataTable}.
     *
     * @param format    the new UncertaintyFormat
     */
    public void setUncertaintyFormat(UncertaintyFormat format) {
        this.uncertaintyFormat = format;
    }

    /**
     * Create columnNameProperties based on both isotope flavor and user input
     *
     * @param columnNames array of provided columnNameProperties
     * @return updated array of columnNameProperties
     */
    private void addNamesToColumns(String[] columnNames) {

        String title;
        int i = 0;
        while (i < dataColumns.size()) {
            if (i >= columnNames.length) {
                title = "Column" + i;
            } else {
                title = columnNames[i];
            }
            dataColumns.get(i).setName(title);
            i++;
        }
    }

    /**
     * Returns the column names as a String[].
     *
     * @return  String[] of column names
     */
    public String[] getColumnNames() {
        String[] names = new String[dataColumns.size()];
        for (int i = 0; i < dataColumns.size(); i++) {
            names[i] = dataColumns.get(i).getName();
        }
        return names;
    }

    /**
     * Returns a {@code List} of Fields describing each of the columns of data.
     *
     * @return  List of Field
     */
    public List<Field<Number>> getFields() {
        List<Field<Number>> fields = new ArrayList<>();

        for (String header : getColumnNames()) {
            Field<Number> field = new NumberField(header);
            fields.add(field);
        }

        return fields;
    }

    /**
     * Returns the data columns as an {@code ObservableList} of {@code TopsoilDataColumn}s.
     *
     * @return  List of TopsoilDataColumn
     */
    public ObservableList<TopsoilDataColumn> getDataColumns() {
        return dataColumns;
    }

    /**
     * Returns a {@code Map} of {@code Variable}s to their corresponding {@code TopsoilDataColumn}.
     *
     * @return  Map of Variables to column data
     */
    public Map<Variable<Number>, TopsoilDataColumn> getVariableAssignments() {
        return variableColumnMap;
    }

    public void setVariableAssignments(Map<Variable<Number>, TopsoilDataColumn> assignments) {
        variableColumnMap = assignments;
        // TODO Check if TopsoilDataColumns have had their variables set correctly.
    }

    /**
     * Returns the data as an {@code ObservableList} of {@code TopsoilDataEntries}.
     *
     * @return  the copied ObservableList of TopsoilDataEntries
     */
    public ObservableList<TopsoilDataEntry> getDataEntries() {
        ObservableList<TopsoilDataEntry> newData = FXCollections.observableArrayList();
        TopsoilDataEntry newEntry;

        for (int i = 0; i < numRows; i++) {
            newEntry = new TopsoilDataEntry();
            for (List<DoubleProperty> column : dataColumns) {
                newEntry.addValues(column.get(i).get());
            }
            newData.add(newEntry);
        }

        return newData;
    }

    /**
     * Returns the data as a List of Double arrays.
     *
     * @return  a List of Double[]
     */
    public List<Double[]> getDataAsArrays() {
        ArrayList<Double[]> tableEntries = new ArrayList<>();
        Double[] arr;

        for (int i = 0; i < numRows; i++) {
            arr = new Double[dataColumns.size()];
            for (int j = 0; j < dataColumns.size(); j++) {
                arr[j] = dataColumns.get(j).get(i).get();
            }
            tableEntries.add(arr);
        }
        return tableEntries;
    }

    public List<Double[]> getFormattedDataAsArrays() {
        ArrayList<Double[]> tableEntries = new ArrayList<>();
        Double[] arr;

        for (int i = 0; i < numRows; i++) {
            arr = new Double[dataColumns.size()];
            for (int j = 0; j < dataColumns.size(); j++) {
                if  (Variables.UNCERTAINTY_VARIABLES.contains(dataColumns.get(j).getVariable())) {
                    arr[j] = dataColumns.get(j).get(i).get() * uncertaintyFormat.getValue();
                } else {
                    arr[j] = dataColumns.get(j).get(i).get();
                }
            }
            tableEntries.add(arr);
        }
        return tableEntries;
    }

    /**
     * Adds a row to the existing data.
     *
     * @param index the index where the row is being added
     * @param row   a TopsoilDataEntry
     */
    public void addRow(int index, TopsoilDataEntry row) {
        for (int i = 0; i < Math.min(row.getProperties().size(), dataColumns.size()); i++) {
            dataColumns.get(i).add(index, new SimpleDoubleProperty(row.getProperties().get(i).get()));
        }
        numRows++;
    }

    /**
     * Removes a row of data at the specified index.
     *
     * @param index the index of the row to remove
     */
    public void removeRow(int index) {
        for (int i = 0; i < dataColumns.size(); i++) {
            dataColumns.get(i).remove(index);
        }
        numRows--;
    }

    void addColumn(int index, TopsoilDataColumn column) {
        dataColumns.add(index, column);

        if (column.hasVariable()) {
            variableColumnMap.put(column.getVariable(), column);
        }
    }

    void removeColumn(int index) {
        if (variableColumnMap.containsValue(dataColumns.get(index))) {
            variableColumnMap.remove(dataColumns.get(index).getVariable());
        }
        dataColumns.remove(index);
    }

    /**
     * Converts the supplied row data ({@code TopsoilDataEntries}) into column data.
     *
     * @param rows  TopsoilDataEntry rows
     * @return  ObservableList of TopsoilDataColumns
     */
    private ObservableList<TopsoilDataColumn> convertRowData(TopsoilDataEntry... rows) {

        ObservableList<TopsoilDataColumn> dataColumns = FXCollections.observableArrayList();

        int maxRowLength = 0;
        for (TopsoilDataEntry row : rows) {
            maxRowLength = Math.max(maxRowLength, row.getProperties().size());
        }

        if (rows.length <= 0) {
            for (int colIndex = 0; colIndex < maxRowLength; colIndex++) {
                TopsoilDataColumn dataColumn = new TopsoilDataColumn();
                dataColumn.add(new SimpleDoubleProperty(0.0));
                dataColumns.add(dataColumn);
            }
        } else {
            for (int colIndex = 0; colIndex < maxRowLength; colIndex++) {
                TopsoilDataColumn dataColumn = new TopsoilDataColumn();
                for (TopsoilDataEntry row : rows) {
                    if (row.getProperties().size() <= colIndex) {
                        dataColumn.add(new SimpleDoubleProperty(0.0));
                    } else {
                        dataColumn.add(row.getProperties().get(colIndex));
                    }
                }
                dataColumns.add(dataColumn);
            }
        }

        return dataColumns;
    }

    /**
     * Converts the supplied row data ({@code Double} arrays) into column data.
     *
     * @param rows  Double[] rows
     * @return  ObservableList of TopsoilDataColumns
     */
    private ObservableList<TopsoilDataColumn> convertRowData(Double[]... rows) {

        ObservableList<TopsoilDataColumn> dataColumns = FXCollections.observableArrayList();

        int maxRowLength = 0;
        for (Double[] row : rows) {
            maxRowLength = Math.max(maxRowLength, row.length);
        }

        if (rows.length <= 0) {
            for (int colIndex = 0; colIndex < maxRowLength; colIndex++) {
                TopsoilDataColumn dataColumn = new TopsoilDataColumn();
                dataColumn.add(new SimpleDoubleProperty(0.0));
                dataColumns.add(dataColumn);
            }
        } else {
            for (int colIndex = 0; colIndex < maxRowLength; colIndex++) {
                TopsoilDataColumn dataColumn = new TopsoilDataColumn();
                for (Double[] row : rows) {
                    if (row.length <= colIndex) {
                        dataColumn.add(new SimpleDoubleProperty(0.0));
                    } else {
                        dataColumn.add(new SimpleDoubleProperty(row[colIndex]));
                    }
                }
                dataColumns.add(dataColumn);
            }
        }

        return dataColumns;
    }

    /**
     * Returns true if the variableColumnMap are empty.
     *
     * @return  true or false
     */
    public boolean isCleared() {

        if (numRows == 1) {
            for (int i = 0; i < dataColumns.size(); i++) {
                if (Double.compare(dataColumns.get(i).get(0).get(), 0.0) != 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns a Collection of {@code PlotInformation}, each containing information on an open plot associated with
     * this variableColumnMap tableView.
     *
     * @return  a Collection of PlotInformation objects
     */
    public Collection<PlotInformation> getOpenPlots() {
        return this.openPlots.values();
    }

    /**
     * Adds information about an open plot to this variableColumnMap tableView.
     *
     * @param plotInfo  a new PlotInformation
     */
    public void addOpenPlot(PlotInformation plotInfo) {
        this.openPlots.put(plotInfo.getTopsoilPlotType(), plotInfo);
    }

    /**
     * Removes information about an open plot to this variableColumnMap tableView (typically once the tableView has been closed).
     *
     * @param plotType  the TopsoilPlotType of the plot to be removed
     */
    public void removeOpenPlot(TopsoilPlotType plotType) {
        this.openPlots.get(plotType).killPlot();
        this.openPlots.remove(plotType);
    }
}
