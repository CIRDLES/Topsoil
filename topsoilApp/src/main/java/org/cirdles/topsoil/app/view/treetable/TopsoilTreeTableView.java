package org.cirdles.topsoil.app.view.treetable;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.node.BranchNode;
import org.cirdles.topsoil.app.data.node.DataNode;

import java.util.ArrayList;
import java.util.Date;
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

        // Add TreeItems for data
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

    private List<TreeTableColumn<DataNode, Object>> getTableColumnsForBranchNode(BranchNode<DataNode> branchNode) {
        List<TreeTableColumn<DataNode, Object>> tableColumns = new ArrayList<>();
        TreeTableColumn<DataNode, Object> newColumn;
        for (DataNode node : branchNode.getChildren()) {
            if (node instanceof DataColumn) {
                DataColumn dataColumn = (DataColumn) node;
                newColumn = new TreeTableColumn<>(dataColumn.getLabel());
                newColumn.setCellValueFactory(value -> {
                    if (value.getValue().getValue() instanceof DataRow) {
                        DataRow row = (DataRow) value.getValue().getValue();
                        return row.getValuePropertyForColumn(dataColumn);
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
