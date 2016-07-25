package org.cirdles.topsoil.app.progress;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjaminmuldrow on 7/13/16.
 */
public class TopsoilDataEntry {

    ObservableList<DoubleProperty> properties;
    List<String> headers;

    public TopsoilDataEntry(Double... entries) {
        this.headers = new ArrayList<String>();
        this.properties = FXCollections.observableArrayList(
                new ArrayList<DoubleProperty>()
        );
        addEntries(entries);
    }

    public void addEntries(Double... entries) {
        for (Double value : entries) {
            this.properties.add(
                    new SimpleDoubleProperty(value)
            );
        }
    }

    public ObservableList<DoubleProperty> getProperties() {
        return properties;
    }

    public String [] getHeaders() {
        return headers.toArray(new String[this.headers.size()]);
    }
}
