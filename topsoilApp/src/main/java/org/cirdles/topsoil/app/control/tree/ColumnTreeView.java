package org.cirdles.topsoil.app.control.tree;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.composite.DataComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * A custom control for toggling the selected property of columns.
 *
 * @author marottajb
 */
public class ColumnTreeView extends TreeView<DataComponent> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ColumnTreeView(ColumnRoot columnRoot) {
        this.setCellFactory(CheckBoxTreeCell.forTreeView());
        final CheckBoxTreeItem<DataComponent> rootItem = new CheckBoxTreeItem<>(new DataCategory("(all columns)"));
        rootItem.setSelected(true);
        rootItem.setExpanded(true);
        this.setRoot(rootItem);
        for (DataComponent component : columnRoot.getChildren()) {
            addTreeItem(component, getRoot());
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
    public Map<DataComponent, Boolean> getColumnSelections() {
        return getColumnSelections((CheckBoxTreeItem<DataComponent>) getRoot());
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void addTreeItem(DataComponent component, TreeItem<DataComponent> parent) {
        CheckBoxTreeItem<DataComponent> item = new CheckBoxTreeItem<>(component, null, component.isSelected());
        if (component instanceof DataCategory) {
            for (DataComponent child : ((DataCategory) component).getChildren()) {
                if (! child.isSelected() && item.isSelected()) {
                    item.setIndeterminate(true);
                }
                addTreeItem(child, item);
            }
            item.setIndeterminate(shouldBeIndeterminate(item));
        }
        parent.getChildren().add(item);
    }

    private boolean shouldBeIndeterminate(CheckBoxTreeItem<DataComponent> item) {
        boolean allTrue = true;
        boolean allFalse = true;
        for (TreeItem<DataComponent> child : item.getChildren()) {
            CheckBoxTreeItem<DataComponent> cBChild = (CheckBoxTreeItem<DataComponent>) child;
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

    private Map<DataComponent, Boolean> getColumnSelections(CheckBoxTreeItem<DataComponent> root) {
        Map<DataComponent, Boolean> selections = new HashMap<>();
        for (TreeItem<DataComponent> item : root.getChildren()) {
            CheckBoxTreeItem<DataComponent> child = (CheckBoxTreeItem<DataComponent>) item;
            selections.put(child.getValue(), child.isSelected());
            if (child.getValue() instanceof DataCategory) {
                selections.putAll(getColumnSelections(child));
            }
        }
        return selections;
    }

}
