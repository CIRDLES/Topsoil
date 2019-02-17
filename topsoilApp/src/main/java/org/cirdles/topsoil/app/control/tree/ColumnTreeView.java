package org.cirdles.topsoil.app.control.tree;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.cirdles.topsoil.app.data.column.ColumnTree;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;

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
    //                PRIVATE METHODS               //
    //**********************************************//

    private void addTreeItem(DataComponent component, TreeItem<DataComponent> parent) {
        CheckBoxTreeItem<DataComponent> item = new CheckBoxTreeItem<>(component);
        item.selectedProperty().bindBidirectional(component.selectedProperty());
        if (component instanceof DataComposite) {
            for (DataComponent child : ((DataComposite<DataComponent>) component).getChildren()) {
                addTreeItem(child, item);
            }
        }
        parent.getChildren().add(item);
    }

}
