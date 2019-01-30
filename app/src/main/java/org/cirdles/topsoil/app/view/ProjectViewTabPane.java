package org.cirdles.topsoil.app.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.controls.TopsoilTreeTableColumn;
import org.cirdles.topsoil.app.data.*;
import org.cirdles.topsoil.app.data.node.BranchNode;
import org.cirdles.topsoil.app.data.node.DataNode;

import java.io.IOException;
import java.util.*;

/**
 * @author marottajb
 */
public class ProjectViewTabPane extends TabPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "project-view-tab.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private TreeTableView<DataRow> treeTableView;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private MapProperty<DataNode, BooleanProperty> nodeSelectionMap =
            new SimpleMapProperty<>(FXCollections.observableHashMap());
    public MapProperty<DataNode, BooleanProperty> nodeSelectionMapProperty() {
        return nodeSelectionMap;
    }
    public Map<DataNode, BooleanProperty> getNodeSelections() {
        return nodeSelectionMap.get();
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private DataTable dataTable;
    private Map<DataNode, TreeItem<DataRow>> dataNodeTreeItemMap = new HashMap<>();
    private Map<DataNode, TopsoilTreeTableColumn<DataRow, ?>> dataColumnTableColumnMap = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private ProjectViewTabPane() {}

    ProjectViewTabPane(DataTable data) {
        this.dataTable = data;
        try {
            final ResourceExtractor re = new ResourceExtractor(ProjectViewTabPane.class);
            final FXMLLoader loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
    }

    @FXML
    protected void initialize() {
        //   1. Root Element (set showRoot false)
        final TreeItem<DataRow> rootItem = new TreeItem<>(new DataRow("dummy"));
        treeTableView.setRoot(rootItem);
        treeTableView.setShowRoot(false);

        //   2. Table/Segments/Rows -> TreeItems
        TreeItem<DataRow> tableItem, segmentItem, rowItem;
        tableItem = new TreeItem<>(new DataRow(dataTable.getLabel()));
        dataNodeTreeItemMap.put(dataTable, tableItem);
        for (DataSegment segment : dataTable.getChildren()) {
            segmentItem = new CheckBoxTreeItem<>(new DataRow(segment.getLabel()));
            dataNodeTreeItemMap.put(segment, segmentItem);
            for (DataRow row : segment.getChildren()) {
                rowItem = new CheckBoxTreeItem<>(row);
                segmentItem.getChildren().add(rowItem);
                dataNodeTreeItemMap.put(row, rowItem);
            }
            tableItem.getChildren().add(segmentItem);
        }
        treeTableView.getRoot().getChildren().add(tableItem);

        //   3. Categories/Columns -> TreeTableColumns
        treeTableView.getColumns().addAll(getTreeTableColumnsForChildren(dataTable.getColumnTree()));

        //   4. Figure out CellValueFactory for DataRow's Map
        for (TreeTableColumn<DataRow, ?> column : treeTableView.getColumns()) {
            configureColumnCellValueFactory((TopsoilTreeTableColumn<DataRow, ?>) column);
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public DataTable getDataTable() {
        return dataTable;
    }

    TreeTableView<DataRow> getTreeTableView() {
        return getTreeTableView();
    }

    public TreeItem<DataRow> getTreeItemForNode(DataNode node) {
        return dataNodeTreeItemMap.get(node);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private List<TopsoilTreeTableColumn<DataRow, ?>> getTreeTableColumnsForChildren(BranchNode<? extends DataNode> root) {
        List<TopsoilTreeTableColumn<DataRow, ?>> columnList = new ArrayList<>();
        DataCategory category;
        TopsoilTreeTableColumn<DataRow, String> newTableColumn;
        for (DataNode node : root.getChildren()) {
            if (node instanceof DataCategory) {
                category = (DataCategory) node;
                newTableColumn = getTreeTableColumnForRoot(category);
            } else {
                newTableColumn = new TopsoilTreeTableColumn<>(node.getLabel(), node);
                dataColumnTableColumnMap.put(node, newTableColumn);
            }
            columnList.add(newTableColumn);
        }
        return columnList;
    }

    private TopsoilTreeTableColumn<DataRow, String> getTreeTableColumnForRoot(BranchNode<? extends DataNode> root) {
        TopsoilTreeTableColumn<DataRow, String> rtnval = new TopsoilTreeTableColumn<>(root.getLabel(), root);
        DataCategory category;
        TopsoilTreeTableColumn<DataRow, String> newTableColumn;
        for (DataNode node : root.getChildren()) {
            if (node instanceof DataCategory) {
                category = (DataCategory) node;
                if (category.getChildren().size() == 0) {
                    newTableColumn = new TopsoilTreeTableColumn<>(category.getLabel(), node);
                } else {
                    newTableColumn = getTreeTableColumnForRoot(category);
                }
            } else {
                newTableColumn = new TopsoilTreeTableColumn<>(node.getLabel(), node);
            }
            rtnval.getColumns().add(newTableColumn);
            dataColumnTableColumnMap.put(node, newTableColumn);
        }
        return rtnval;
    }

    private <T> void configureColumnCellValueFactory(TopsoilTreeTableColumn<DataRow, T> column) {
        if (column.getDataNode() instanceof DataColumn) {
            DataColumn dCol = (DataColumn) column.getDataNode();
            column.setCellValueFactory(param -> {
                return (ObjectProperty<T>) param.getValue().getValue().getValuePropertyForColumn(dCol);
            });
        }
    }

}
