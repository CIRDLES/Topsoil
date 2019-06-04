package org.cirdles.topsoil.app.control.data;

import org.cirdles.topsoil.app.data.FXDataColumn;

class DataTreeTableColumn<S, T> extends MultilineHeaderTreeTableColumn<S, T> {

    DataTreeTableColumn(FXDataColumn<T> dataColumn) {
        super("");
        label.textProperty().bind(dataColumn.titleProperty());
    }

}
