package org.cirdles.topsoil.app.control.data;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.cirdles.topsoil.app.data.FXDataColumn;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.data.SimpleDataColumn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom control for toggling the selected property of columns.
 *
 * @author marottajb
 */
public class ColumnTreeView extends TreeView<FXDataColumn<?>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ColumnTreeView(FXDataTable table) {
        this.setCellFactory(CheckBoxTreeCell.forTreeView());
        final CheckBoxTreeItem<FXDataColumn<?>> rootItem = new CheckBoxTreeItem<>(new FXDataColumn<>(new SimpleDataColumn<>("all columns")));
        rootItem.setSelected(true);
        rootItem.setExpanded(true);
        this.setRoot(rootItem);
        for (FXDataColumn<?> column : table.getColumns()) {
            addTreeItem(column, getRoot());
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns the column visibility selections for the table.
     *
     * @return  Map of DataComponent to Boolean values, true if column should be visible
     */
    public Map<FXDataColumn<?>, Boolean> getColumnSelections() {
        return getColumnSelections((CheckBoxTreeItem<FXDataColumn<?>>) getRoot());
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void addTreeItem(FXDataColumn<?> column, TreeItem<FXDataColumn<?>> parent) {
        CheckBoxTreeItem<FXDataColumn<?>> item = new CheckBoxTreeItem<>(column, null, column.isSelected());
        List<FXDataColumn<?>> children = column.getChildren();
        if (children.size() > 0) {
            for (FXDataColumn<?> child : children) {
                if (! child.isSelected() && item.isSelected()) {
                    item.setIndeterminate(true);
                }
                addTreeItem(child, item);
            }
            item.setIndeterminate(shouldBeIndeterminate(item));
        }
        parent.getChildren().add(item);
    }

    private boolean shouldBeIndeterminate(CheckBoxTreeItem<FXDataColumn<?>> item) {
        boolean allTrue = true;
        boolean allFalse = true;
        for (TreeItem<FXDataColumn<?>> child : item.getChildren()) {
            CheckBoxTreeItem<FXDataColumn<?>> cBChild = (CheckBoxTreeItem<FXDataColumn<?>>) child;
            if (cBChild.isIndeterminate()) {
                allTrue = false;
                allFalse = false;
                break;
            }
            if (cBChild.isSelected()) {
                allFalse = false;
            } else {
                allTrue = false;
            }
        }
        return (! allTrue) && (! allFalse);
    }

    private Map<FXDataColumn<?>, Boolean> getColumnSelections(CheckBoxTreeItem<FXDataColumn<?>> root) {
        Map<FXDataColumn<?>, Boolean> selections = new HashMap<>();
        for (TreeItem<FXDataColumn<?>> item : root.getChildren()) {
            CheckBoxTreeItem<FXDataColumn<?>> child = (CheckBoxTreeItem<FXDataColumn<?>>) item;
            selections.put(child.getValue(), child.isSelected());
            if (child.getValue().getChildren().size() > 0) {
                selections.putAll(getColumnSelections(child));
            }
        }
        return selections;
    }

}
