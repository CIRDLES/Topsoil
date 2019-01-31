package org.cirdles.topsoil.app.view;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.cirdles.topsoil.app.data.DataRow;
import org.cirdles.topsoil.app.data.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.node.BranchNode;
import org.cirdles.topsoil.app.data.node.DataNode;
import org.cirdles.topsoil.app.data.node.LeafNode;

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

    public ProjectTreeView(TopsoilProjectView dataView) {
        this();
        for (DataTable table : dataView.getDataTables()) {
            addDataTable(table);
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public TreeItem<DataNode> getTreeItemForDataNode(DataNode node) {
        return treeItemMap.get(node);
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
