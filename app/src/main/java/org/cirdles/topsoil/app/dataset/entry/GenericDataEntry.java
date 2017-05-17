package org.cirdles.topsoil.app.dataset.entry;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;

/**
 * A template for Topsoil's row model.
 *
 * @author Benjamin Muldrow
 */
public interface GenericDataEntry {

    /**
     * Adds {@code Double} values to the data entry.
     *
     * @param values    simple collection of Double values to enter
     */
    void addValues(Double... values);

    /**
     * Gets all {@code DoubleProperty}s stored in the entry.
     *
     * @return  ObservableList of properties
     */
    ObservableList<DoubleProperty> getProperties();

    /**
     * Gets an array of header {@code String}s.
     *
     * @return array of strings
     */
    String[] getHeaders();
}
