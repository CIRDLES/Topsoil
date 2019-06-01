package org.cirdles.topsoil.app.control.tree;

import org.cirdles.topsoil.app.data.FXDataColumn;
import org.cirdles.topsoil.data.DataColumn;

class DataTreeTableColumn<R, T> extends MultilineHeaderTreeTableColumn<R, T> {

    DataTreeTableColumn(FXDataColumn<T> dataColumn) {
        super("");
        label.textProperty().bind(dataColumn.titleProperty());
    }

}
