package org.cirdles.topsoil.app.progress.table;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.progress.util.serialization.PlotInformation;

import java.util.*;

/**
 * This is the core data model for a tableView. It contains the <tt>TableView</tt> used to display the data, as well as
 * any information about open plots which reference this data model.
 *
 * @author benjaminmuldrow
 *
 * @see TableView
 * @see TopsoilDataEntry
 */
public class TopsoilTable {

    private ObservableList<TopsoilDataEntry> data;
    private ObservableList<StringProperty> columnNameProperties;
    private SimpleStringProperty titleProperty = new SimpleStringProperty("Untitled Table");
    private HashMap<String, PlotInformation> openPlots;

    public TopsoilTable(String[] columnNames, IsotopeType isotopeType, TopsoilDataEntry... dataEntries) {

//        this.dataEntries = dataEntries;
        this.data = FXCollections.observableArrayList(dataEntries);

        // initialize isotope type
        this.isotopeTypeObjectProperty = new SimpleObjectProperty<>(isotopeType);

        // populate columnNameProperties
        this.columnNameProperties = FXCollections.observableArrayList(createColumnHeaderProperties(columnNames));

        // Create hashmap for storing information on open plots for this tableView.
        this.openPlots = new HashMap<>();
    }

    private ObjectProperty<IsotopeType> isotopeTypeObjectProperty;
    /**
     * Returns the <tt>IsotopeType</tt> of the current <tt>TopsoilTable</tt>.
     *
     * @return  the tableView's IsotopeType
     */
    public IsotopeType getIsotopeType() {
        return isotopeTypeObjectProperty.get();
    }
    /**
     * Sets the <tt>IsotopeType</tt> of the current <tt>TopsoilTable</tt>.
     *
     * @param isotopeType   the new IsotopeType
     */
    public void setIsotopeType(IsotopeType isotopeType) {
        isotopeTypeObjectProperty.set(isotopeType);
    }
    public final ObjectProperty<IsotopeType> isotopeTypeObjectProperty() {
        return isotopeTypeObjectProperty;
    }

    /**
     * Create columnNameProperties based on both isotope flavor and user input
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
        this.openPlots.put(plotInfo.getTopsoilPlotType().getName(), plotInfo);
    }

    /**
     * Removes information about an open plot to this data tableView (typically once the tableView has been closed).
     *
     * @param plotType  the TopsoilPlotType of the plot to be removed
     */
    public void removeOpenPlot(TopsoilPlotType plotType) {
        this.openPlots.get(plotType.getName()).killPlot();
        this.openPlots.remove(plotType.getName());
    }

    public StringProperty titleProperty() {
        return titleProperty;
    }

    public String getTitle() {
        return titleProperty.get();
    }

    public void setTitle(String title) {
        this.titleProperty.set(title);
    }

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
}
