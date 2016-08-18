package org.cirdles.topsoil.app.progress.table;

import javafx.scene.control.TableView;

/**
 * Created by benjaminmuldrow on 8/3/16.
 */
public interface GenericTable {
    void deleteRow(int index);

    void addRow();

    void clear();

    TableView getTable();

    String getTitle();

    String [] getHeaders();
}
