package org.cirdles.topsoil.app.control.treetable;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import org.cirdles.topsoil.app.model.*;
import org.cirdles.topsoil.app.model.composite.DataComposite;
import org.cirdles.topsoil.app.model.composite.DataComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilTreeTableView extends TreeTableView<DataComponent> {

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

        CheckBoxTreeItem<DataComponent> tableItem;
        tableItem = new CheckBoxTreeItem<>(table);

        // Add TreeItems for model
        for (DataSegment segment : table.getChildren()) {
            tableItem.getChildren().add(makeTreeItemForDataSegment(segment));
        }

        // Add columns
        this.getColumns().add(makeLabelColumn());
        this.getColumns().add(makeCheckBoxColumn());
        this.getColumns().addAll(makeTableColumnsForComposite(table.getColumnTree()));

        this.setRoot(tableItem);
        this.setShowRoot(false);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Returns the column of {@link javafx.scene.control.CheckBox}es for row/segment selection.
     *
     * @return  new TreeTableColumn
     */
    private TreeTableColumn<DataComponent, ObservableValue<Boolean>> makeCheckBoxColumn() {
        TreeTableColumn<DataComponent, ObservableValue<Boolean>> column = new TreeTableColumn<>("Selected");
        column.setCellFactory(col -> {
            CheckBoxTreeTableCell<DataComponent, ObservableValue<Boolean>> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
//            cell.selectedProperty().addListener(((observable, oldValue, newValue) -> {
//                cell.getTreeTableRow().getTreeItem().getValue().setSelected(newValue);
//            }));
            return cell;
        });
        return column;
    }

    /**
     * Returns the column of {@code String} labels for rows/segments.
     *
     * @return  new TreeTableColumn
     */
    private TreeTableColumn<DataComponent, String> makeLabelColumn() {
        TreeTableColumn<DataComponent, String> column = new TreeTableColumn<>("Label");
        column.setCellValueFactory(value -> value.getValue().getValue().labelProperty());
        column.setCellFactory(value -> {
            TreeTableCell<DataComponent, String> cell = new TextFieldTreeTableCell<>();
            cell.setAlignment(Pos.CENTER_LEFT);
            cell.setStyle("-fx-font-style: italic;");
            return cell;
        });
        column.setPrefWidth(150);
        return column;
    }

    /**
     * Returns a {@code List} of {@code TreeTableColumn}s created based on the provided {@code DataComposite}.
     *
     * @param dataComposite     DataComposite
     * @return                  List of new TreeTableColumns
     */
    private List<TreeTableColumn<DataComponent, String>> makeTableColumnsForComposite(DataComposite<DataComponent> dataComposite) {
        List<TreeTableColumn<DataComponent, String>> tableColumns = new ArrayList<>();
        TreeTableColumn<DataComponent, String> newColumn;
        for (DataComponent node : dataComposite.getChildren()) {
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
                newColumn.setCellFactory(value -> {
                    TreeTableCell<DataComponent, String> cell = new TextFieldTreeTableCell<>();
                    if (dataColumn.getType() == Double.class) {
                        cell.setAlignment(Pos.CENTER_RIGHT);
                    } else if (dataColumn.getType() == String.class) {
                        cell.setAlignment(Pos.CENTER_LEFT);
                        cell.setStyle("-fx-font-style: italic;");
                    }
                    return cell;
                });
            } else {
                DataComposite<DataComponent> childBranch = (DataComposite<DataComponent>) node;
                newColumn = new TreeTableColumn<>(childBranch.getLabel());
                newColumn.getColumns().addAll(makeTableColumnsForComposite(childBranch));
            }
            newColumn.setPrefWidth(100.0);
            tableColumns.add(newColumn);
        }
        return tableColumns;
    }

    /**
     * Returns a new {@code CheckBoxTreeItem} for the provided {@code DataSegment}.
     *
     * @param segment   DataSegment
     * @return          CheckBoxTreeItem
     */
    private CheckBoxTreeItem<DataComponent> makeTreeItemForDataSegment(DataSegment segment) {
        CheckBoxTreeItem<DataComponent> segmentItem = new CheckBoxTreeItem<>(segment);
        for (DataRow row : segment.getChildren()) {
            segmentItem.getChildren().add(makeTreeItemForDataRow(row));
        }
        return segmentItem;
    }

    /**
     * Returns a new {@code CheckBoxTreeItem} for the provided {@code DataRow}.
     *
     * @param row       DataRow
     * @return          CheckBoxTreeItem;
     */
    private CheckBoxTreeItem<DataComponent> makeTreeItemForDataRow(DataRow row) {
        return new CheckBoxTreeItem<>(row);
    }

}
