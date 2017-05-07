package org.cirdles.topsoil.app.progress.table;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjaminmuldrow on 7/13/16.
 */
public class TopsoilDataEntry implements GenericDataEntry {

    ObservableList<DoubleProperty> properties;
    // TODO: change this to a list of fields
    List<String> headers;

    public TopsoilDataEntry(Double... entries) {
        this.headers = new ArrayList<String>();
        this.properties = FXCollections.observableArrayList(
                new ArrayList<DoubleProperty>()
        );
        addValues(entries);
    }

    @Override
    public void addValues(Double... entries) {
        for (Double value : entries) {
            this.properties.add(
                    new SimpleDoubleProperty(value)
            );
        }
    }

    public void setValue(int index, Double value) {
        this.getProperties().get(index).set(value);
    }

    public void swap(int index1, int index2) {
        DoubleProperty d1 = properties.get(index1);
        DoubleProperty d2 = properties.get(index2);

        properties.remove(index1);
        properties.add(index1, d2);

        properties.remove(index2);
        properties.add(index2, d1);
    }

    @Override
    public ObservableList<DoubleProperty> getProperties() {
        return properties;
    }

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

    public TopsoilDataEntry cloneEntry() {
        TopsoilDataEntry newEntry = new TopsoilDataEntry();
        for (DoubleProperty p : this.getProperties()) {
            newEntry.addValues(p.get());
        }
        return newEntry;
    }

    public Double[] toArray() {
        Double[] arr = new Double[this.properties.size()];
        for (int i = 0; i < this.properties.size(); i++) {
            arr[i] = this.properties.get(i).doubleValue();
        }
        return arr;
    }
}
