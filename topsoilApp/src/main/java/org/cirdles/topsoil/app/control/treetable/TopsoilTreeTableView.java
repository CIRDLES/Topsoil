package org.cirdles.topsoil.app.control.treetable;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import org.cirdles.topsoil.app.model.*;
import org.cirdles.topsoil.app.model.generic.BranchNode;
import org.cirdles.topsoil.app.model.generic.DataNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilTreeTableView extends TreeTableView<DataNode> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilTreeTableView() {
        super();
    }

    public TopsoilTreeTableView(DataTable table) {
        this();
        setDataTable(table);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void setDataTable(DataTable table) {
        this.getChildren().clear();

        CheckBoxTreeItem<DataNode> tableItem;
        tableItem = new CheckBoxTreeItem<>(table);

        // Add TreeItems for model
        for (DataSegment segment : table.getChildren()) {
            tableItem.getChildren().add(treeItemForDataSegment(segment));
        }

        // Add columns
        this.getColumns().add(getLabelColumn());
        this.getColumns().add(getCheckBoxColumn());
        this.getColumns().addAll(getTableColumnsForBranchNode(table.getColumnTree()));

        this.setRoot(tableItem);
        this.setShowRoot(false);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private TreeTableColumn<DataNode, ObservableValue<Boolean>> getCheckBoxColumn() {
        TreeTableColumn<DataNode, ObservableValue<Boolean>> column = new TreeTableColumn<>("Selected");
        column.setCellFactory(col -> {
            CheckBoxTreeTableCell<DataNode, ObservableValue<Boolean>> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            cell.setEditable(true);
            cell.setDisable(false);
            cell.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                cell.getTreeTableRow().getTreeItem().getValue().setSelected(newValue);
            }));
            return cell;
        });
        column.setPrefWidth(50);
        return column;
    }

    private TreeTableColumn<DataNode, String> getLabelColumn() {
        TreeTableColumn<DataNode, String> column = new TreeTableColumn<>("Label");
        column.setCellValueFactory(value -> value.getValue().getValue().labelProperty());
        column.setPrefWidth(150);
        return column;
    }

    private List<TreeTableColumn<DataNode, String>> getTableColumnsForBranchNode(BranchNode<DataNode> branchNode) {
        List<TreeTableColumn<DataNode, String>> tableColumns = new ArrayList<>();
        TreeTableColumn<DataNode, String> newColumn;
        for (DataNode node : branchNode.getChildren()) {
            if (node instanceof DataColumn) {
                DataColumn dataColumn = (DataColumn<?>) node;
                newColumn = new TreeTableColumn<>(dataColumn.getLabel());
                newColumn.setCellValueFactory(param -> {
                    if (param.getValue().getValue() instanceof DataRow) {
                        DataRow row = (DataRow) param.getValue().getValue();
                        return row.getValueForColumn(dataColumn).labelProperty();
                    }
                    return null;
                });
            } else {
                BranchNode<DataNode> childBranch = (BranchNode<DataNode>) node;
                newColumn = new TreeTableColumn<>(childBranch.getLabel());
                newColumn.getColumns().addAll(getTableColumnsForBranchNode(childBranch));
            }
            tableColumns.add(newColumn);
        }
        return tableColumns;
    }

    private CheckBoxTreeItem<DataNode> treeItemForDataSegment(DataSegment segment) {
        CheckBoxTreeItem<DataNode> segmentItem = new CheckBoxTreeItem<>(segment);
        for (DataRow row : segment.getChildren()) {
            segmentItem.getChildren().add(treeItemForDataRow(row));
        }
        return segmentItem;
    }

    private CheckBoxTreeItem<DataNode> treeItemForDataRow(DataRow row) {
        return new CheckBoxTreeItem<>(row);
    }

}
