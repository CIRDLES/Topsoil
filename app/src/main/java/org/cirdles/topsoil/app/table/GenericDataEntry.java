package org.cirdles.topsoil.app.table;

import javafx.collections.ObservableList;

/**
 * Created by benjaminmuldrow on 8/3/16.
 */
public interface GenericDataEntry {

    /**
     * Add value entries to the data etry
     * @param entries simple collection of Double values to enter
     */
    void addValues(Double... entries);

    /**
     * get all properties contained in the data entry
     * @return observable list of properties
     */
    ObservableList getProperties();

    /**
     * Get an array of header strings
     * @return array of strings
     */
    String [] getHeaders();
}
