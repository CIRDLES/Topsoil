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
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.plot.Plot;

import java.util.*;

/**
 * {@code TopsoilDataTable} is the core data model for Topsoil. It contains data in the form of an
 * {@link ObservableList} of {@link TopsoilDataEntry}s, as well as supplemental information about that data, such as a
 * title, column names, an {@link IsotopeType} and any open {@link Plot}s that represent data in the
 * {@code TopsoilDataTable}.
 *
 * @author Benjamin Muldrow
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
     * The table data, in the form of {@code TopsoilDataEntry}s.
     */
    private ObservableList<TopsoilDataEntry> data;

    /**
     * A {@code List} of {@code StringProperty}s for each of the column names.
     */
    private ObservableList<StringProperty> columnNameProperties;

    /**
     * The {@code StringProperty} for the title of the table.
     */
    private StringProperty title;

    /**
     * An {@code ObjectProperty} containing the {@code IsotopeType} of the TopsoilTable.
     */
    private ObjectProperty<IsotopeType> isotopeType;

    /**
     * A {@code Map} containing {@code PlotInformation} for an open plot of each type.
     */
    private HashMap<TopsoilPlotType, PlotInformation> openPlots;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a TopsoilDataTable with the specified column names, IsotopeType, and data (in the form of
     * TopsoilDataEntries).
     *
     * @param columnNames
     * @param isotopeType
     * @param dataEntries
     */
    public TopsoilDataTable(String[] columnNames, IsotopeType isotopeType, TopsoilDataEntry... dataEntries) {
        this.data = FXCollections.observableArrayList(dataEntries);
        this.isotopeType = new SimpleObjectProperty<>(isotopeType);
        this.columnNameProperties = FXCollections.observableArrayList(createColumnHeaderProperties(columnNames));
        this.openPlots = new HashMap<>();
    }

    //***********************
    // Methods
    //***********************

    public StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty("Untitled Table");
        }
        return title;
    }
    /**
     * Returns the title of the TopsoilDataTable.
     *
     * @return  the String title
     */
    public String getTitle() {
        return titleProperty().get();
    }

    /**
     * Sets the title of the TopsoilDataTable to the specified String.
     *
     * @param title the new String title
     */
    public void setTitle(String title) {
        titleProperty().set(title);
    }

    public ObjectProperty<IsotopeType> isotopeTypeObjectProperty() {
        if (isotopeType == null) {
            isotopeType = new SimpleObjectProperty<>(IsotopeType.Generic);
        }
        return isotopeType;
    }
    /**
     * Returns the <tt>IsotopeType</tt> of the current <tt>TopsoilDataTable</tt>.
     *
     * @return  the tableView's IsotopeType
     */
    public IsotopeType getIsotopeType() {
        return isotopeTypeObjectProperty().get();
    }

    /**
     * Sets the <tt>IsotopeType</tt> of the current <tt>TopsoilDataTable</tt>.
     *
     * @param isotopeType   the new IsotopeType
     */
    public void setIsotopeType(IsotopeType isotopeType) {
        isotopeTypeObjectProperty().set(isotopeType);
    }

    /**
     * Create columnNameProperties based on both isotope flavor and user input
     *
     * @param columnNames array of provided columnNameProperties
     * @return updated array of columnNameProperties
     */
    private StringProperty[] createColumnHeaderProperties(String [] columnNames) {

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
        // If too many columnNameProperties are provided, only use the first X (depending on isotope flavor)
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
     * Returns the data as TopsoilDataEntries.
     *
     * @return  an ObservableList of TopsoilDataEntry
     */
    public ObservableList<TopsoilDataEntry> getData() {
        return data;
    }

    public List<Field<Number>> getFields() {
        List<Field<Number>> fields = new ArrayList<>();

        for (String header : getColumnNames()) {
            Field<Number> field = new NumberField(header);
            fields.add(field);
        }

        return fields;
    }

    /**
     * Returns the column names as their associated StringProperties.
     *
     * @return  an ObservableList of StringProperties
     */
    public ObservableList<StringProperty> getColumnNameProperties() {
        return columnNameProperties;
    }

    /** {@inheritDoc} */
    public String[] getColumnNames() {
        String[] result = new String[columnNameProperties.size()];
        for (int i = 0; i < columnNameProperties.size(); i++) {
            result[i] = columnNameProperties.get(i).get();
        }
        return result;
    }

    /**
     * Returns a copy of the data as a new ObservableList of TopsoilDataEntries.
     *
     * @return  the copied ObservableList of TopsoilDataEntries
     */
    public ObservableList<TopsoilDataEntry> getCopyOfDataAsEntries() {
        ObservableList<TopsoilDataEntry> newData = FXCollections.observableArrayList();
        TopsoilDataEntry newEntry;
        for (TopsoilDataEntry entry : data) {
            newEntry = new TopsoilDataEntry();
            for (DoubleProperty property : entry.getProperties()) {
                newEntry.addValues(property.get());
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
        for (TopsoilDataEntry entry : data) {
            tableEntries.add(entry.toArray());
        }
        return tableEntries;
    }

    /**
     * Returns true if the <tt>TableView</tt> is empty. In this case, "empty" means that it has one data entry filled
     * with 0.0s.
     *
     * @return  true or false
     */
    public boolean isCleared() {
        return data.isEmpty();
    }

    /**
     * Returns a Collection of <tt>PlotInformation</tt>, each containing information on an open plot associated with
     * this data tableView.
     *
     * @return  a Collection of PlotInformation objects
     */
    public Collection<PlotInformation> getOpenPlots() {
        return this.openPlots.values();
    }

    /**
     * Adds information about an open plot to this data tableView.
     *
     * @param plotInfo  a new PlotInformation
     */
    public void addOpenPlot(PlotInformation plotInfo) {
        this.openPlots.put(plotInfo.getTopsoilPlotType(), plotInfo);
    }

    /**
     * Removes information about an open plot to this data tableView (typically once the tableView has been closed).
     *
     * @param plotType  the TopsoilPlotType of the plot to be removed
     */
    public void removeOpenPlot(TopsoilPlotType plotType) {
        this.openPlots.get(plotType).killPlot();
        this.openPlots.remove(plotType);
    }
}
