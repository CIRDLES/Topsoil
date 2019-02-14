package org.cirdles.topsoil.app.control.treetable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author marottajb
 */
public class TopsoilTreeTableView extends TreeTableView<DataComponent> {

    private DataTable table;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilTreeTableView() {
        super();
    }

    public TopsoilTreeTableView(DataTable table) {
        this();
        this.setEditable(true);
        setDataTable(table);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void setDataTable(DataTable table) {
        this.table = table;
        this.getChildren().clear();

        CheckBoxTreeItem<DataComponent> tableItem;
        tableItem = new CheckBoxTreeItem<>(table);

        // Add TreeItems for model
        for (DataSegment segment : table.getChildren()) {
            tableItem.getChildren().add(makeTreeItemForDataSegment(segment));
        }
        if (tableItem.getChildren().size() == 1) {
            tableItem.getChildren().get(0).setExpanded(true);
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
    private TreeTableColumn<DataComponent, Boolean> makeCheckBoxColumn() {
        TreeTableColumn<DataComponent, Boolean> column = new TreeTableColumn<>("Selected");
        column.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(column));
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
     * Returns the column of {@code String} labels for rows/segments.
     *
     * @return  new TreeTableColumn
     */
    private TreeTableColumn<DataComponent, String> makeLabelColumn() {
        TreeTableColumn<DataComponent, String> column = new TreeTableColumn<>("Label");
        column.setCellFactory(param -> {
            TextFieldTreeTableCell<DataComponent, String> cell = new TextFieldTreeTableCell<>();
            cell.setAlignment(Pos.CENTER_LEFT);
            cell.setStyle("-fx-font-style: italic;");
            return cell;
        });
        column.setCellValueFactory(param -> {
            DataComponent component = param.getValue().getValue();
            StringProperty property = new SimpleStringProperty("");
            property.bind(component.labelProperty());
            return property;
        });
        column.setPrefWidth(150);
        column.setEditable(true);
        return column;
    }

    /**
     * Returns a {@code List} of {@code TreeTableColumn}s created based on the provided {@code DataComposite}.
     *
     * @param composite     DataComposite
     * @return                  List of new TreeTableColumns
     */
    private List<TreeTableColumn<DataComponent, String>> makeTableColumnsForComposite(DataComposite<DataComponent> composite) {
        List<TreeTableColumn<DataComponent, String>> tableColumns = new ArrayList<>();
        for (DataComponent node : composite.getChildren()) {
            TreeTableColumn<DataComponent, String> newColumn;
            if (node instanceof DataColumn) {
                if (((DataColumn) node).getType().equals(Double.class)) {
                    newColumn = makeTreeTableColumn((DataColumn<Double>) node);
                } else {
                    newColumn = makeTreeTableColumn((DataColumn<String>) node);
                }
            } else {
                newColumn = makeTreeTableColumn((DataCategory) node);
            }
            newColumn.setPrefWidth(100.0);
            tableColumns.add(newColumn);
        }
        return tableColumns;
    }

    private <T extends Serializable> TreeTableColumn<DataComponent, String> makeTreeTableColumn(DataColumn<T> dataColumn) {
        TreeTableColumn<DataComponent, String> newColumn = new TreeTableColumn<>(dataColumn.getLabel());
        newColumn.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof DataSegment) {
                return new SimpleStringProperty("");
            }
            if (param.getValue().getValue() instanceof DataRow) {
                DataValue<T> dataValue = ((DataRow) param.getValue().getValue()).getValueForColumn(dataColumn);
                return dataValue.labelProperty();
            }
            return null;
        });
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
