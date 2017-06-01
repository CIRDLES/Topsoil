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

import java.util.*;

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
    private Map<Variable<Number>, TopsoilDataColumn> columnForVariable;

    /**
     * The table data in the form of {@code TopsoilDataColumn}s.
     */
    private ObservableList<TopsoilDataColumn> dataColumns;

    /**
     * The number of rows of data.
     */
    private int numRows;

    /**
     * A {@code List} of {@code StringProperty}s for each of the column names.
     */
    private ObservableList<StringProperty> columnNameProperties;

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
     * @param rows  TopsoilDataEntries of data rows
     */
    public TopsoilDataTable(String[] columnNames, IsotopeType isotopeType, UncertaintyFormat format,
                            TopsoilDataEntry... rows) {
        this.numRows = rows.length;
        this.isotopeType = new SimpleObjectProperty<>(isotopeType);
        this.uncertaintyFormat = format;
        this.columnForVariable = new HashMap<>();
        this.dataColumns = convertRowData(rows);
        this.columnNameProperties = FXCollections.observableArrayList(createColumnHeaderProperties(columnNames));
        this.openPlots = new HashMap<>();

        resetVariableMapping();
    }

    /**
     * Constructs a TopsoilDataTable with the specified column names, IsotopeType, and table data (in the form of
     * Double arrays).
     *
     * @param columnNames   a String[] of column names
     * @param isotopeType   IsotopeType
     * @param rows  Double[]s of data rows
     */
    public TopsoilDataTable(String[] columnNames, IsotopeType isotopeType, UncertaintyFormat format,
                            Double[]... rows) {
        this.numRows = rows.length;
        this.isotopeType = new SimpleObjectProperty<>(isotopeType);
        this.uncertaintyFormat = format;
        this.columnForVariable = new HashMap<>();
        this.dataColumns = convertRowData(rows);
        this.columnNameProperties = FXCollections.observableArrayList(createColumnHeaderProperties(columnNames));
        this.openPlots = new HashMap<>();

        resetVariableMapping();
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
    private StringProperty[] createColumnHeaderProperties(String[] columnNames) {

        String[] defaultNames = new String[]{"Column1", "Column2", "Column3", "Column4", "Column5"};
        String[] resultArr;
        StringProperty[] result;

        if (columnNames == null) {
//            resultArr = isotopeType.getHeaders();
            resultArr = defaultNames;

            // if some column names are provided, populate
        } else if (columnNames.length < defaultNames.length) {
            int difference = defaultNames.length - columnNames.length;
            resultArr = new String[defaultNames.length];
            int numHeaders = defaultNames.length;

            // Copy provided column names to resultArr.
            System.arraycopy(columnNames, 0, resultArr, 0, numHeaders - difference);

            // Fill in with default column names.
            System.arraycopy(defaultNames, (numHeaders - difference),
                             resultArr, (numHeaders - difference),
                             (numHeaders - (numHeaders - difference)));
        }
        else {
            resultArr = columnNames.clone();
        }

        result = new SimpleStringProperty[resultArr.length];

        for (int i = 0; i < resultArr.length; i++) {
            result[i] = new SimpleStringProperty(resultArr[i]);
        }

        return result;
    }

    /**
     * Returns the column names as their associated StringProperties.
     *
     * @return  an ObservableList of StringProperties
     */
    public ObservableList<StringProperty> getColumnNameProperties() {
        return columnNameProperties;
    }

    /**
     * Returns the column names as a String[].
     *
     * @return  String[] of column names
     */
    public String[] getColumnNames() {
        String[] result = new String[columnNameProperties.size()];
        for (int i = 0; i < columnNameProperties.size(); i++) {
            result[i] = columnNameProperties.get(i).get();
        }
        return result;
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
    public Map<Variable<Number>, TopsoilDataColumn> getColumnBindings() {
        return columnForVariable;
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

    /**
     * Adds a row of DoubleProperties to the existing data.
     *
     * @param index the index where the row is being added
     * @param row   a DoubleProperty array
     */
    public void addRow(int index, DoubleProperty[] row) {
        for (int i = 0; i < Math.min(row.length, dataColumns.size()); i++) {
            dataColumns.get(i).add(index, row[i]);
        }
        numRows++;
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

    /**
     * Converts the supplied row data ({@code TopsoilDataEntries}) into column data.
     *
     * @param rows  TopsoilDataEntry rows
     * @return  ObservableList of TopsoilDataColumns
     */
    private ObservableList<TopsoilDataColumn> convertRowData(TopsoilDataEntry... rows) {

        ObservableList<TopsoilDataColumn> dataColumns = FXCollections.observableArrayList();

        // TODO Read in any number of columns.
//        for (int colIndex = 0; colIndex < rows[0].getProperties().size(); colIndex++) {
        for (int colIndex = 0; colIndex < 5; colIndex++) {
            TopsoilDataColumn dataColumn = new TopsoilDataColumn();
            for (TopsoilDataEntry row : rows) {
                dataColumn.add(row.getProperties().get(colIndex));
            }
            dataColumns.add(dataColumn);
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

        // TODO Read in any number of columns.
//        for (int colIndex = 0; colIndex < rows[0].length; colIndex++) {
        for (int colIndex = 0; colIndex < 5; colIndex++) {
            TopsoilDataColumn dataColumn = new TopsoilDataColumn();
            for (Double[] row : rows) {
                dataColumn.add(new SimpleDoubleProperty(row[colIndex]));
            }
            dataColumns.add(dataColumn);
        }

        return dataColumns;
    }

    /**
     * Resets the mapping of plotting variables to the columns in their corresponding slots.
     */
    public void resetVariableMapping() {
        columnForVariable.put(Variables.X, dataColumns.get(0));
        columnForVariable.put(Variables.Y, dataColumns.get(1));
        columnForVariable.put(Variables.SIGMA_X, dataColumns.get(2));
        columnForVariable.put(Variables.SIGMA_Y, dataColumns.get(3));
        columnForVariable.put(Variables.RHO, dataColumns.get(4));
    }

    /**
     * Returns true if the columnForVariable are empty.
     *
     * @return  true or false
     */
    public boolean isCleared() {
        return columnForVariable.get(Variables.X).isEmpty();
    }

    /**
     * Returns a Collection of {@code PlotInformation}, each containing information on an open plot associated with
     * this columnForVariable tableView.
     *
     * @return  a Collection of PlotInformation objects
     */
    public Collection<PlotInformation> getOpenPlots() {
        return this.openPlots.values();
    }

    /**
     * Adds information about an open plot to this columnForVariable tableView.
     *
     * @param plotInfo  a new PlotInformation
     */
    public void addOpenPlot(PlotInformation plotInfo) {
        this.openPlots.put(plotInfo.getTopsoilPlotType(), plotInfo);
    }

    /**
     * Removes information about an open plot to this columnForVariable tableView (typically once the tableView has been closed).
     *
     * @param plotType  the TopsoilPlotType of the plot to be removed
     */
    public void removeOpenPlot(TopsoilPlotType plotType) {
        this.openPlots.get(plotType).killPlot();
        this.openPlots.remove(plotType);
    }

    // **********************************************************************************
    // **********************************************************************************

    /**
     * A custom {@code ArrayList} for storing data columns. The main purpose is to provide the ability to associate a data
     * column with a {@code Variable}.
     *
     * @author Jake Marotta
     */
    public class TopsoilDataColumn extends SimpleListProperty<DoubleProperty> {

        //***********************
        // Properties
        //***********************

        /**
         * An {@code ObjectProperty} containing the {@code Variable} that is set to this column, if one exists.
         */
        private ObjectProperty<Variable> variable;
        public ObjectProperty<Variable> variableProperty() {
            if (variable == null) {
                variable = new SimpleObjectProperty<>(null);
                variable.addListener(c -> {
                    if (variable.get() == null) {
                        hasVariableProperty().set(false);
                    } else {
                        hasVariableProperty().set(true);
                    }
                });
            }

            return variable;
        }
        public Variable getVariable() {
            return variableProperty().get();
        }
        private void setVariable(Variable v) {
            variableProperty().set(v);
        }

        /**
         * A {@code BooleanProperty} tracking whether or not a {@code Variable} is set to this column.
         */
        private BooleanProperty hasVariable;
        public BooleanProperty hasVariableProperty() {
            if (hasVariable == null) {
                hasVariable = new SimpleBooleanProperty(variableProperty().get() != null);
            }
            return hasVariable;
        }
        public Boolean hasVariable() {
            return hasVariableProperty().get();
        }

        //***********************
        // Constructors
        //***********************

        /**
         * Constructs an empty {@code TopsoilDataColumn}.
         */
        public TopsoilDataColumn() {
            super(FXCollections.observableArrayList());
        }

        /**
         * Constructs a new {@code TopsoilDataColumn} with the specified {@code DoubleProperty}s as contents.
         *
         * @param properties    DoubleProperties
         */
        public TopsoilDataColumn(DoubleProperty... properties) {
            this();
            this.addAll(properties);
        }

        /**
         * Constructs a new {@code TopsoilDataColumn}, adding a {@code DoubleProperty} for each supplied {@code Double}.
         *
         * @param values    Double values
         */
        public TopsoilDataColumn(Double... values) {
            this();
            for (Double value : values) {
                this.add(new SimpleDoubleProperty(value));
            }
        }
    }
}
