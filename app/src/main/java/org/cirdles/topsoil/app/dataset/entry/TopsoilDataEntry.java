package org.cirdles.topsoil.app.dataset.entry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

/**
 * The row model used in each table's {@link TableView}. Each {@code TopsoilDataEntry} consists of an
 * {@link ObservableList} of {@link DoubleProperty}s,
 *
 * @author Benjamin Muldrow
 */
public class TopsoilDataEntry implements GenericDataEntry {

    ObservableList<DoubleProperty> properties;
    // TODO: change this to a list of fields
    List<String> headers;

    /**
     * Constructs a {@code TopsoilDataEntry} with the specified collection of {@code Double} values.
     *
     * @param values    a collection of Doubles
     */
    public TopsoilDataEntry(Double... values) {
        this.headers = new ArrayList<String>();
        this.properties = FXCollections.observableArrayList(
                new ArrayList<DoubleProperty>()
        );
        addValues(values);
    }

    /**
     * Appends a collection of {@code Double} values as {@code DoubleProperty}s to the {@code TopsoilDataEntry}.
     *
     * @param values    a collection of Doubles
     */
    @Override
    public void addValues(Double... values) {
        for (Double value : values) {
            this.properties.add(
                    new SimpleDoubleProperty(value)
            );
        }
    }

    /**
     * Appends a collection of {@code DoubleProperty} values to the {@code TopsoilDataEntry}.
     *
     * @param properties    a collection of DoubleProperties
     */
    public void add(DoubleProperty... properties) {
        if (properties.length > 1) {
            this.properties.addAll(properties);
        } else if (properties.length == 1) {
            this.properties.add(properties[0]);
        }
    }

    /**
     * Changes the value of the {@code DoubleProperty} at index to the specified {@code Double} value.
     *
     * @param index the index of the DoubleProperty to set
     * @param value the value to set the DoubleProperty to
     */
    public void setValue(int index, Double value) {
        this.getProperties().get(index).set(value);
    }

    /**
     * Gets the {@code ObservableList} of {@code DoubleProperty}s from the {@code TopsoilDataEntry}.
     *
     * @return  an ObservableList of DoubleProperties
     */
    @Override
    public ObservableList<DoubleProperty> getProperties() {
        return properties;
    }

    /**
     * Gets a {@code String} array of the {@code TopsoilDataEntry}'s headers.
     *
     * @return  a String[] of headers
     */
    @Override
    public String [] getHeaders() {
        return headers.toArray(new String[this.headers.size()]);
    }

    public static TopsoilDataEntry newEmptyDataEntry(TableView tableView) {
        TopsoilDataEntry dataEntry = new TopsoilDataEntry();
        for (Object column : tableView.getColumns()) {
            dataEntry.addValues(0.0);
        }
        return dataEntry;
    }

    /**
     * Returns a clone of the {@code TopsoilDataEntry}.
     *
     * @return  the cloned TopsoilDataEntry
     */
    public TopsoilDataEntry cloneEntry() {
        TopsoilDataEntry newEntry = new TopsoilDataEntry();
        for (DoubleProperty p : this.getProperties()) {
            newEntry.addValues(p.get());
        }
        return newEntry;
    }

    /**
     * Returns a {@code Double} array of the data values in the {@code TopsoilDataEntry}.
     *
     * @return  Double[]
     */
    public Double[] toArray() {
        Double[] arr = new Double[this.properties.size()];
        for (int i = 0; i < this.properties.size(); i++) {
            arr[i] = this.properties.get(i).doubleValue();
        }
        return arr;
    }
}
