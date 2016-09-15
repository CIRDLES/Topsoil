package org.cirdles.topsoil.app.progress.table;

import javafx.scene.control.TableView;

/**
 * Created by benjaminmuldrow on 8/3/16.
 */
public interface GenericTable {

    /**
     * Delete a row given the index
     * @param index index of row
     */
    void deleteRow(int index);

    /**
     * Add an empty row to the table
     */
    void addRow();

    /**
     * Clear the table and retain one empty line
     */
    void clear();

    /**
     * Get the JavaFX TableView from the Table class
     * @return TableView of the table
     */
    TableView getTable();

    /**
     * Get the title of the table
     * @return table title
     */
    String getTitle();

    /**
     * Get an array of headers for the table
     * @return array of header strings
     */
    String [] getHeaders();
}
