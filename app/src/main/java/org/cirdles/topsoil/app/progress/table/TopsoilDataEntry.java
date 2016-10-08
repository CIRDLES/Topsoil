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
        addEntries(entries);
    }

    @Override
    public void addEntries(Double... entries) {
        for (Double value : entries) {
            this.properties.add(
                    new SimpleDoubleProperty(value)
            );
        }
    }

    void changeEntry(int index, DoubleProperty value) {
        this.getProperties().set(index, value);
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
            dataEntry.addEntries(0.0);
        }
        return dataEntry;
    }
}
