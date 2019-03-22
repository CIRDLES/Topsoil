package org.cirdles.topsoil.app.control.tree;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.text.TextAlignment;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilTreeTableView extends TreeTableView<DataComponent> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilTreeTableView(DataTable table) {
        this.setEditable(true);
        this.setSortMode(TreeSortMode.ALL_DESCENDANTS);
        this.setShowRoot(false);

        CheckBoxTreeItem<DataComponent> rootItem;
        rootItem = new CheckBoxTreeItem<>(table.getDataRoot());
        // Add TreeItems for model
        for (DataSegment segment : table.getDataRoot().getChildren()) {
            rootItem.getChildren().add(makeTreeItemForDataSegment(segment));
        }
        if (rootItem.getChildren().size() == 1) {
            rootItem.getChildren().get(0).setExpanded(true);
        }
        this.setRoot(rootItem);

        // Add columns
        TreeTableColumn<DataComponent, String> labelColumn = makeLabelColumn();
        this.getColumns().add(labelColumn);
        this.getColumns().add(makeCheckBoxColumn());
        this.getColumns().addAll(makeTableColumnsForComposite(table.getColumnRoot()));

        this.getSortOrder().add(labelColumn);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Returns the column of {@code String} labels for rows/segments.
     *
     * @return  new TreeTableColumn
     */
    private TreeTableColumn<DataComponent, String> makeLabelColumn() {
        TreeTableColumn<DataComponent, String> column = new TreeTableColumn<>("Label");
        column.setCellFactory(param -> {
            TextFieldTreeTableCell<DataComponent, String> cell = new TextFieldTreeTableCell<>();
            cell.setTextAlignment(TextAlignment.LEFT);
            cell.setEditable(false);
            return cell;
        });
        column.setCellValueFactory(param -> {
            DataComponent component = param.getValue().getValue();
            return component.labelProperty();
        });
        column.setPrefWidth(150);
        return column;
    }

    /**
     * Returns the column of {@link javafx.scene.control.CheckBox}es for row/segment selection.
     *
     * @return  new TreeTableColumn
     */
    private TreeTableColumn<DataComponent, Boolean> makeCheckBoxColumn() {
        TreeTableColumn<DataComponent, Boolean> column = new TreeTableColumn<>("Selected");
        column.setCellFactory(param -> {
            CheckBoxTreeTableCell<DataComponent, Boolean> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            cell.setEditable(true);
            return cell;
        });
        column.setCellValueFactory(param -> {
            DataComponent component = param.getValue().getValue();
            BooleanProperty property = new SimpleBooleanProperty(component.isSelected());
            property.bindBidirectional(component.selectedProperty());
            return property;
        });
        column.setEditable(true);
        return column;
    }

    /**
     * Returns a {@code List} of {@code TreeTableColumn}s created based on the provided {@code DataComposite}.
     *
     * @param composite     DataComposite
     * @return                  List of new TreeTableColumns
     */
    private List<TreeTableColumn<DataComponent, ?>> makeTableColumnsForComposite(DataComposite<DataComponent> composite) {
        List<TreeTableColumn<DataComponent, ?>> tableColumns = new ArrayList<>();
        for (DataComponent node : composite.getChildren()) {
            TreeTableColumn<DataComponent, ?> newColumn;
            if (node instanceof DataColumn) {
                if (((DataColumn) node).getType().equals(Number.class)) {
                    newColumn = makeTreeTableColumn((DataColumn<Number>) node);
                } else {
                    newColumn = makeTreeTableColumn((DataColumn<String>) node);
                }
            } else {
                newColumn = makeTreeTableColumn((DataCategory) node);
            }
            newColumn.setPrefWidth(150.0);
            node.selectedProperty().addListener(((observable, oldValue, newValue) -> newColumn.setVisible(newValue)));
            tableColumns.add(newColumn);
        }
        return tableColumns;
    }

    private <T extends Serializable> TreeTableColumn<DataComponent, T> makeTreeTableColumn(DataColumn<T> dataColumn) {
        TreeTableColumn<DataComponent, T> newColumn = new TreeTableColumn<>(dataColumn.getLabel());

        newColumn.setCellFactory(param -> {
            TextFieldTreeTableCell<DataComponent, T> cell = new TextFieldTreeTableCell<>(dataColumn.getStringConverter());
            cell.setAlignment(Pos.CENTER_RIGHT);
            cell.setEditable(false);
            return cell;
        });
        newColumn.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof DataSegment) {
                return null;
            }
            if (param.getValue().getValue() instanceof DataRow) {
                return ((DataRow) param.getValue().getValue()).getValueForColumn(dataColumn).valueProperty();
            }
            return null;
        });
        newColumn.setVisible(dataColumn.isSelected());
        return newColumn;
    }

    private TreeTableColumn<DataComponent, String> makeTreeTableColumn(DataCategory dataCategory) {
        TreeTableColumn<DataComponent, String> newColumn = new TreeTableColumn<>(dataCategory.getLabel());
        newColumn.getColumns().addAll(makeTableColumnsForComposite(dataCategory));
        return newColumn;
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
