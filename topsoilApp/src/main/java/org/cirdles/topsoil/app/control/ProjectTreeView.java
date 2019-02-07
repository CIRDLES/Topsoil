package org.cirdles.topsoil.app.control;

import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.cirdles.topsoil.app.model.DataRow;
import org.cirdles.topsoil.app.model.DataSegment;
import org.cirdles.topsoil.app.model.DataTable;
import org.cirdles.topsoil.app.model.TopsoilProject;
import org.cirdles.topsoil.app.model.node.BranchNode;
import org.cirdles.topsoil.app.model.node.DataNode;
import org.cirdles.topsoil.app.model.node.LeafNode;

import java.util.*;

/**
 * @author marottajb
 */
public class ProjectTreeView extends TreeView<DataNode> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Map<DataNode, TreeItem<DataNode>> treeItemMap = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ProjectTreeView() {
        super();
        final CheckBoxTreeItem<DataNode> rootItem = new CheckBoxTreeItem<>(new BranchNode<>("dummy"));
        this.setCellFactory(CheckBoxTreeCell.forTreeView());
        this.setRoot(rootItem);
        this.setShowRoot(false);
    }

    public ProjectTreeView(TopsoilProject project) {
        this();
        setProject(project);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void setProject(TopsoilProject project) {
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

    public void addDataTable(DataTable table) {
        TreeItem<DataNode> tableItem, segmentItem, rowItem;
        tableItem = new CheckBoxTreeItem<>(new BranchNode<>(table.getLabel()));
        ((CheckBoxTreeItem<DataNode>) tableItem).setSelected(true);
        tableItem.setExpanded(true);
        treeItemMap.put(table, tableItem);
        for (DataSegment segment : table.getChildren()) {
            segmentItem = new CheckBoxTreeItem<>(new BranchNode(segment.getLabel()));
            ((CheckBoxTreeItem<DataNode>) segmentItem).setSelected(true);
            treeItemMap.put(segment, segmentItem);
            for (DataRow row : segment.getChildren()) {
                rowItem = new CheckBoxTreeItem<>(new LeafNode(row.getLabel()));
                ((CheckBoxTreeItem<DataNode>) rowItem).setSelected(true);
                treeItemMap.put(row, rowItem);
                segmentItem.getChildren().add(rowItem);
            }
            tableItem.getChildren().add(segmentItem);
        }
        this.getRoot().getChildren().add(tableItem);
    }

    public void removeDataTable(DataTable table) {
        TreeItem<DataNode> tableItem = treeItemMap.get(table);
        this.getRoot().getChildren().remove(tableItem);
    }

}
