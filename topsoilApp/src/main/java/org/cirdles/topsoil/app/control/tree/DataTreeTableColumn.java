package org.cirdles.topsoil.app.control.tree;

import org.cirdles.topsoil.app.data.column.DataColumn;

public class DataTreeTableColumn<T> extends MultilineHeaderTreeTableColumn<T> {

    public DataTreeTableColumn(DataColumn<T> dataColumn) {
        super("");
        label.textProperty().bind(dataColumn.labelProperty());
    }

}
