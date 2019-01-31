package org.cirdles.topsoil.app.view;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.node.DataNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilTreeTableView extends TreeTableView<DataNode> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private DataTable table;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilTreeTableView() {
        super();
    }

    public TopsoilTreeTableView(DataTable table) {
        super();
        setDataTable(table);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void setDataTable(DataTable table) {
        this.table = table;

        CheckBoxTreeItem<DataNode> tableItem, segmentItem, rowItem;
        tableItem = new CheckBoxTreeItem<>(this.table);
        this.setRoot(tableItem);
        this.setShowRoot(false);

        // Add TreeItems for data
        for (DataSegment segment : this.table.getChildren()) {
            segmentItem = new CheckBoxTreeItem<>(segment);
            for (DataRow row : segment.getChildren()) {
                rowItem = new CheckBoxTreeItem<>(row);
                segmentItem.getChildren().add(rowItem);
            }
            tableItem.getChildren().add(segmentItem);
        }

        // Add columns
        this.getColumns().add(getCheckBoxColumn());
        this.getColumns().add(getLabelColumn());
        this.getColumns().addAll(getTableColumnsForTree(this.table.getColumnTree()));
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private TreeTableColumn<DataNode, Boolean> getCheckBoxColumn() {
        TreeTableColumn<DataNode, Boolean> column = new TreeTableColumn<>("Selected");
        column.setCellFactory(col -> {
            CheckBoxTreeTableCell<DataNode, Boolean> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        column.setCellValueFactory(value -> value.getValue().getValue().selectedProperty());
        return column;
    }

    private TreeTableColumn<DataNode, String> getLabelColumn() {
        TreeTableColumn<DataNode, String> column = new TreeTableColumn<>("Label");
        column.setCellValueFactory(value -> value.getValue().getValue().labelProperty());
        return column;
    }

    private List<TreeTableColumn<DataNode, Object>> getTableColumnsForTree(ColumnTree columnTree) {
        List<TreeTableColumn<DataNode, Object>> tableColumns = new ArrayList<>();
        TreeTableColumn<DataNode, Object> newColumn;
        for (DataColumn dataColumn : columnTree.getLeafNodes()) {
            newColumn = new TreeTableColumn<>(dataColumn.getLabel());
            newColumn.setCellValueFactory(value -> {
                if (value.getValue().getValue() instanceof DataRow) {
                    DataRow row = (DataRow) value.getValue().getValue();
                    return row.getValuePropertyForColumn(dataColumn);
                }
                return null;
            });
            tableColumns.add(newColumn);
        }
        return tableColumns;
    }

}
