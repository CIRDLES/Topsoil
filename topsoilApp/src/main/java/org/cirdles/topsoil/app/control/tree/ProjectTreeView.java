package org.cirdles.topsoil.app.control.tree;

import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataLeaf;

/**
 * @author marottajb
 */
public class ProjectTreeView extends TreeView<DataComponent> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ProjectTreeView(TopsoilProject project) {
        this.setCellFactory(CheckBoxTreeCell.forTreeView());
        final CheckBoxTreeItem<DataComponent> rootItem = new CheckBoxTreeItem<>(new DataComposite<>("dummy"));
        this.setRoot(rootItem);
        this.setShowRoot(false);

        for (DataTable table : project.getDataTableList()) {
            addDataTable(table);
        }
        project.dataTableListProperty().addListener((ListChangeListener<? super DataTable>) c -> {
            c.next();
            if (c.wasAdded()) {
                for (DataTable table : c.getAddedSubList()) {
                    addDataTable(table);
                }
            }
            if (c.wasRemoved()) {
                for (DataTable table : c.getRemoved()) {
                    removeDataTable(table);
                }
            }
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    private void addDataTable(DataTable table) {
        CheckBoxTreeItem<DataComponent> tableItem, segmentItem, rowItem;
        tableItem = new CheckBoxTreeItem<>(table);
        tableItem.selectedProperty().bindBidirectional(table.selectedProperty());
        tableItem.setExpanded(true);
        for (DataSegment segment : table.getChildren()) {
            segmentItem = new CheckBoxTreeItem<>(segment);
            segmentItem.selectedProperty().bindBidirectional(segment.selectedProperty());
            for (DataRow row : segment.getChildren()) {
                rowItem = new CheckBoxTreeItem<>(row);
                rowItem.selectedProperty().bindBidirectional(row.selectedProperty());
                segmentItem.getChildren().add(rowItem);
            }
            tableItem.getChildren().add(segmentItem);
        }
        this.getRoot().getChildren().add(tableItem);
    }

    private void removeDataTable(DataTable table) {
        DataTable t;
        for (TreeItem<DataComponent> item : getRoot().getChildren()) {
            boolean isDataTable = item.getValue() instanceof DataTable;
            if (isDataTable) {
                t = (DataTable) item.getValue();
                if (t.equals(table)) {
                    System.out.println(this.getRoot().getChildren().remove(item));
                }
            }
        }
    }

}
