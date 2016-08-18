package org.cirdles.topsoil.app.progress.table;

import javafx.collections.ObservableList;

/**
 * Created by benjaminmuldrow on 8/3/16.
 */
public interface GenericDataEntry {
    void addEntries(Double... entries);

    ObservableList getProperties();

    String [] getHeaders();
}
