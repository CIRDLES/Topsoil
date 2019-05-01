package org.cirdles.topsoil.app.control.tree;

import com.sun.javafx.scene.control.skin.TreeTableViewSkin;
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
import org.cirdles.topsoil.app.control.undo.UndoAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A customized {@code TreeTableView} that displays the data contained in a {@link DataTable}.
 *
 * @author marottajb
 */
public class TopsoilTreeTableView extends TreeTableView<DataComponent> {

    private static final double LABEL_COL_WIDTH = 150.0;
    private static final double SELECTED_COL_WIDTH = 65.0;
    private static final double DATA_COL_WIDTH = 135.0;

    private DataTable table;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new tree table view for the given data table.
     *
     * @param table     DataTable
     */
    public TopsoilTreeTableView(DataTable table) {
        this.table = table;

        this.setEditable(true);
        this.setSortMode(TreeSortMode.ALL_DESCENDANTS);
        this.setShowRoot(false);

        // Set custom Skin
        this.setSkin(new TopsoilTreeTableViewSkin(this));

        // Create root item
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

        // Add table columns
        TreeTableColumn<DataComponent, String> labelColumn = makeLabelColumn();
        this.getColumns().add(labelColumn);
        this.getColumns().add(makeCheckBoxColumn());
        this.getColumns().addAll(makeTableColumnsForComposite(table.getColumnRoot()));

        this.getSortOrder().add(labelColumn);

        // Refresh cells on fraction digit changes
        table.fracionDigitsProperty().addListener(c -> ((TopsoilTreeTableViewSkin) getSkin()).refreshCells());
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
        MultilineHeaderTreeTableColumn<String> column = new MultilineHeaderTreeTableColumn<>("Label");
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
        column.setPrefWidth(LABEL_COL_WIDTH);
        return column;
    }

    /**
     * Returns the column of {@link javafx.scene.control.CheckBox}es for row/segment selection.
     *
     * @return  new TreeTableColumn
     */
    private TreeTableColumn<DataComponent, Boolean> makeCheckBoxColumn() {
        MultilineHeaderTreeTableColumn<Boolean> column = new MultilineHeaderTreeTableColumn<>("Selected");
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
        column.setPrefWidth(SELECTED_COL_WIDTH);
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
            node.selectedProperty().addListener(((observable, oldValue, newValue) -> newColumn.setVisible(newValue)));
            tableColumns.add(newColumn);
        }
        this.layout();
        return tableColumns;
    }

    private <T extends Serializable> TreeTableColumn<DataComponent, T> makeTreeTableColumn(DataColumn<T> dataColumn) {
        DataTreeTableColumn<T> newColumn = new DataTreeTableColumn<>(dataColumn);
        newColumn.setCellFactory(param -> {
            TreeTableCell<DataComponent, T> cell = new TopsoilTreeTableCell<>(dataColumn, table);
            cell.addEventHandler(CellEditEvent.CELL_EDITED, event -> {
                DataRow.DataValue<T> dataValue = (DataRow.DataValue<T>) event.getDataValue();
                table.addUndoAction(new UndoAction() {
                    @Override
                    public void execute() {
                        dataValue.setValue((T) event.getNewValue());
                    }

                    @Override
                    public void undo() {
                        dataValue.setValue((T) event.getOldValue());
                    }

                    @Override
                    public String getActionName() {
                        return "Cell Edited";
                    }
                });
            });
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
        newColumn.setPrefWidth(DATA_COL_WIDTH);
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

    private class TopsoilTreeTableViewSkin extends TreeTableViewSkin<DataComponent> {

        TopsoilTreeTableViewSkin(TopsoilTreeTableView treeTableView) {
            super(treeTableView);
        }

        public void refreshCells() {
            super.flow.recreateCells();
        }

    }

}
