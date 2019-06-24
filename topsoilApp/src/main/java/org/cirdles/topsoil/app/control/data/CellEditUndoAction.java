package org.cirdles.topsoil.app.control.data;

import javafx.scene.control.TreeTableColumn;
import org.cirdles.topsoil.app.control.undo.UndoAction;
import org.cirdles.topsoil.app.data.FXDataRow;
import org.cirdles.topsoil.data.DataColumn;

public class CellEditUndoAction<T> implements UndoAction {

    private FXDataRow row;
    private DataColumn<T> dataColumn;
    private TreeTableColumn<FXDataRow, T> treeTableColumn;
    private T oldValue;
    private T newValue;

    CellEditUndoAction(DataColumn<T> dataColumn, TreeTableColumn.CellEditEvent<FXDataRow, T> cellEditEvent) {
        this.row = cellEditEvent.getRowValue().getValue();
        this.dataColumn = dataColumn;
        this.treeTableColumn = cellEditEvent.getTableColumn();
        this.oldValue = cellEditEvent.getOldValue();
        this.newValue = cellEditEvent.getNewValue();
    }

    @Override
    public void execute() {
        row.setValueForColumn(dataColumn, newValue);
        refreshTreeTableColumn();
    }

    @Override
    public void undo() {
        row.setValueForColumn(dataColumn, oldValue);
        refreshTreeTableColumn();
    }

    @Override
    public String getActionName() {
        return "Cell Edit";
    }

    private void refreshTreeTableColumn() {
        treeTableColumn.setVisible(false);
        treeTableColumn.setVisible(true);
    }
}
