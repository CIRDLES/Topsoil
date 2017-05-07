package org.cirdles.topsoil.app.progress.table;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.command.*;

/**
 * Created by benjaminmuldrow on 8/1/16.
 */
class TopsoilTableCellContextMenu extends ContextMenu {

    private MenuItem addRowAboveItem;
    private MenuItem addRowBelowItem;
    private MenuItem deleteRowItem;
//    private MenuItem deleteColumnItem;
    //TODO Enable Column renaming
    private MenuItem renameColumnItem;
    private MenuItem copyCellItem;
    private MenuItem copyRowItem;
    private MenuItem copyColumnItem;
    private MenuItem clearCellItem;
    private MenuItem clearRowItem;
    private MenuItem clearColumnItem;

    private TopsoilTableCell cell;

    TopsoilTableCellContextMenu(TopsoilTableCell cell) {
        super();
        this.cell = cell;

        // initialize menu items
        // TODO rearrange in a more logical sense
        addRowAboveItem = new MenuItem("Add Row Above");
        addRowBelowItem = new MenuItem("Add Row Below");
        deleteRowItem = new MenuItem("Delete Row");
        copyRowItem = new MenuItem("Copy Row");
        clearRowItem = new MenuItem("Clear Row");

//        deleteColumnItem = new MenuItem("Delete Column");
        renameColumnItem = new MenuItem("Rename Column");
        copyColumnItem = new MenuItem("Copy Column");
        clearColumnItem = new MenuItem("Clear Column");

        copyCellItem = new MenuItem("Copy Cell");
        clearCellItem = new MenuItem("Clear Cell");

        // add actions
        addRowAboveItem.setOnAction(action -> {
            InsertRowCommand insertRowCommand = new InsertRowCommand(this.cell, this.cell.getIndex());
            insertRowCommand.execute();
            ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(insertRowCommand);
        });

        addRowBelowItem.setOnAction(action -> {
            InsertRowCommand insertRowCommand = new InsertRowCommand(this.cell, this.cell.getIndex() + 1);
            insertRowCommand.execute();
            ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(insertRowCommand);
        });

        deleteRowItem.setOnAction(action -> {
            DeleteRowCommand deleteRowCommand = new DeleteRowCommand(this.cell);
            deleteRowCommand.execute();
            ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(deleteRowCommand);
        });

        copyRowItem.setOnAction(action -> {
            String copyValues = "";
            TopsoilDataEntry row = this.cell.getDataEntry();
            for (int i = 0; i < row.getProperties().size(); i++) {
                copyValues += Double.toString(row.getProperties().get(i).get());
                if (i < row.getProperties().size() - 1) {
                    copyValues += "\t";
                }
            }
            ClipboardContent content = new ClipboardContent();
            content.putString(copyValues);
            Clipboard.getSystemClipboard().setContent(content);
        });

        clearRowItem.setOnAction(action -> {
            ClearRowCommand clearRowCommand = new ClearRowCommand(this.cell);
            clearRowCommand.execute();
            ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(clearRowCommand);
        });

//        deleteColumnItem.setOnAction(action -> {
//            DeleteColumnCommand deleteColumnCommand = new DeleteColumnCommand(this.cell);
//            deleteColumnCommand.execute();
//            ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(deleteColumnCommand);
//        });

        renameColumnItem.setOnAction(action -> {
            Dialog<String> columnRenameDialog = new Dialog<>();

            HBox hbox = new HBox(10.0);
            Label columnNameLabel = new Label("Column Name: ");
            TextField columnNameTextField = new TextField(this.cell.getTableColumn().getText());
            hbox.getChildren().addAll(columnNameLabel, columnNameTextField);

            columnRenameDialog.getDialogPane().setContent(hbox);
            columnRenameDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);

            columnRenameDialog.setResultConverter(value -> {
                if (value == ButtonType.APPLY) {
                    return columnNameTextField.getText().trim();
                }
                return null;
            });

            columnRenameDialog.showAndWait().ifPresent(result -> {
                ColumnRenameCommand columnRenameCommand = new ColumnRenameCommand(cell.getTableColumn(), cell
                        .getTableColumn().getText(), result);
                columnRenameCommand.execute();
                ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(columnRenameCommand);
                this.cell.getTableColumn().setText(result);
            });
        });

        copyColumnItem.setOnAction(action -> {
            String copyValues = "";
            TableColumn<TopsoilDataEntry, Double> column = this.cell.getTableColumn();
            for (int i = 0; i < this.cell.getTableView().getItems().size(); i++) {
                copyValues += Double.toString(column.getCellData(i));
                if (i < this.cell.getTableView().getItems().size() - 1) {
                    copyValues += "\n";
                }
            }
            ClipboardContent content = new ClipboardContent();
            content.putString(copyValues);
            Clipboard.getSystemClipboard().setContent(content);
        });

        clearColumnItem.setOnAction(action -> {
            ClearColumnCommand clearColumnCommand = new ClearColumnCommand(this.cell);
            clearColumnCommand.execute();
            ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(clearColumnCommand);
        });

        copyCellItem.setOnAction(action -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(Double.toString(this.cell.getItem()));
            Clipboard.getSystemClipboard().setContent(content);
        });

        clearCellItem.setOnAction(action -> {
            ClearCellCommand clearCellCommand = new ClearCellCommand(this.cell);
            clearCellCommand.execute();
            ((TopsoilTabPane) this.cell.getScene().lookup("#TopsoilTabPane"))
                    .getSelectedTab().addUndo(clearCellCommand);
        });

        // add items to context menu
        this.getItems().addAll(
                addRowAboveItem,
                addRowBelowItem,
                deleteRowItem,
                copyRowItem,
                clearRowItem,
                new SeparatorMenuItem(),
//                deleteColumnItem,
                renameColumnItem,
                copyColumnItem,
                clearColumnItem,
                new SeparatorMenuItem(),
                copyCellItem, clearCellItem
        );
    }
}
