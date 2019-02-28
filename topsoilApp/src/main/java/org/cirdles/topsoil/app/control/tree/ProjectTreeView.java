package org.cirdles.topsoil.app.control.tree;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

/**
 * @author marottajb
 */
public class ProjectTreeView extends TreeView<DataComponent> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private ObservableList<DataTable> dataTableList = FXCollections.observableArrayList();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ProjectTreeView(TopsoilProject project) {
        this.setCellFactory(CheckBoxTreeCell.forTreeView());
        final CheckBoxTreeItem<DataComponent> rootItem = new CheckBoxTreeItem<>(new DataComposite<>("dummy"));
        this.setRoot(rootItem);
        this.setShowRoot(false);

        for (DataTable table : project.getDataTables()) {
            addDataTable(table);
        }
        project.getDataTables().addListener((ListChangeListener<? super DataTable>) c -> {
            while(c.next()) {
                for (DataTable table : c.getRemoved()) {
                    removeDataTable(table);
                }
                for (DataTable table : c.getAddedSubList()) {
                    addDataTable(table);
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

    private boolean removeDataTable(DataTable table) {
        for (TreeItem<DataComponent> item : getRoot().getChildren()) {
            if (item.getValue() instanceof DataTable) {
                if (table.equals(item.getValue())) {
                    return this.getRoot().getChildren().remove(item);
                }
            }
        }
        return false;
    }

}
