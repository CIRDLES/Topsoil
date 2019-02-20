package org.cirdles.topsoil.app.control.tree;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.cirdles.topsoil.app.data.column.ColumnTree;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.composite.DataLeaf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author marottajb
 */
public class ColumnTreeView extends TreeView<DataComponent> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ColumnTreeView(ColumnTree columnTree) {
        this.setCellFactory(CheckBoxTreeCell.forTreeView());
        final CheckBoxTreeItem<DataComponent> rootItem = new CheckBoxTreeItem<>(new DataComposite<>("dummy"));
        this.setRoot(rootItem);
        this.setShowRoot(false);

        for (DataComponent component : columnTree.getChildren()) {
            addTreeItem(component, getRoot());
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Map<DataColumn<?>, Boolean> getColumnSelections() {
        return getColumnSelections((CheckBoxTreeItem<DataComponent>) getRoot());
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void addTreeItem(DataComponent component, TreeItem<DataComponent> parent) {
        CheckBoxTreeItem<DataComponent> item = new CheckBoxTreeItem<>(component);
        if (component instanceof DataComposite) {
            for (DataComponent child : ((DataComposite<DataComponent>) component).getChildren()) {
                addTreeItem(child, item);
            }
        }
        item.setSelected(component.isSelected());
        parent.getChildren().add(item);
    }

    private Map<DataColumn<?>, Boolean> getColumnSelections(CheckBoxTreeItem<DataComponent> root) {
        Map<DataColumn<?>, Boolean> selections = new HashMap<>();
        for (TreeItem<DataComponent> item : root.getChildren()) {
            CheckBoxTreeItem<DataComponent> child = (CheckBoxTreeItem<DataComponent>) item;
            if (child.getValue() instanceof DataCategory) {
                selections.putAll(getColumnSelections(child));
            } else if (child.getValue() instanceof DataColumn) {
                selections.put((DataColumn<?>) child.getValue(), child.isSelected());
            }
        }
        return selections;
    }

}
