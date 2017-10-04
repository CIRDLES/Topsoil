package org.cirdles.topsoil.app.table;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.command.ColumnRenameCommand;
import org.cirdles.topsoil.app.table.command.DeleteColumnCommand;
import org.cirdles.topsoil.app.table.command.DeleteRowCommand;
import org.cirdles.topsoil.app.table.command.InsertColumnCommand;
import org.cirdles.topsoil.app.table.command.InsertRowCommand;
import org.cirdles.topsoil.app.util.dialog.TopsoilTextInputDialog;

/**
 * A custom {@code ContextMenu} requested by {@link TopsoilTableCell}s. It contains options for manipulating cells,
 * rows, and columns in the {@link TableView}.
 *
 * @author Benjamin Muldrow
 */
class TopsoilTableCellContextMenu extends ContextMenu {

    //***********************
    // Attributes
    //***********************

    /**
     * When clicked, adds a row above the cell's row.
     */
    private MenuItem addRowAboveItem;

    /**
     * When clicked, adds a row below the cell's row.
     */
    private MenuItem addRowBelowItem;

    /**
     * When clicked, deletes the cell's row.
     */
    private MenuItem deleteRowItem;

    /**
     * When clicked, deletes the cell's column.
     */
    private MenuItem deleteColumnItem;

    /**
     * When clicked, inserts a column at the cell's index.
     */
    private MenuItem insertColumnItem;

    /**
     * When clicked, prompts the user to rename the cell's column.
     */
    private MenuItem renameColumnItem;

//    /**
//     * When clicked, copies the value in the cell to the system {@code Clipboard}.
//     */
//    private MenuItem copyCellItem;
//
//    /**
//     * When clicked, copies the values in the cell's row to the system {@code Clipboard}.
//     */
//    private MenuItem copyRowItem;
//
//    /**
//     * When clicked, copies the values in the cell's column to the system {@code Clipboard}.
//     */
//    private MenuItem copyColumnItem;
//
//    /**
//     * When clicked, sets the value in the cell to 0.0.
//     */
//    private MenuItem clearCellItem;
//
//    /**
//     * When clicked, sets the values of all cells in the cell's row to 0.0.
//     */
//    private MenuItem clearRowItem;
//
//    /**
//     * When clicked, sets the values of all cells in the cell's column to 0.0.
//     */
//    private MenuItem clearColumnItem;

    //***********************
    // Constructors
    //***********************

    /**
     * Creates a new TopsoilTableCellContextMenu for the specified cell.
     *
     * @param cell  the TopsoilTableCell requesting a context menu
     */
    TopsoilTableCellContextMenu(TopsoilTableCell cell) {
        super();

        // initialize menu items
        addRowAboveItem = new MenuItem("Add Row Above");
        addRowBelowItem = new MenuItem("Add Row Below");
        deleteRowItem = new MenuItem("Delete Row");
//        copyRowItem = new MenuItem("Copy Row");
//        clearRowItem = new MenuItem("Clear Row");

        deleteColumnItem = new MenuItem("Delete Column");
        insertColumnItem = new MenuItem("Insert Column");
        renameColumnItem = new MenuItem("Rename Column");
//        copyColumnItem = new MenuItem("Copy Column");
//        clearColumnItem = new MenuItem("Clear Column");

//        copyCellItem = new MenuItem("Copy Cell");
//        clearCellItem = new MenuItem("Clear Cell");

        setOnShown(event -> {
            if (((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab()
                                                                                 .getTableController().getTable()
                                                                                 .isCleared()) {
                deleteRowItem.setDisable(true);
            } else {
                deleteRowItem.setDisable(false);
            }
        });

        //********************//
        //    ROW ACTIONS     //
        //********************//

        addRowAboveItem.setOnAction(action -> {
            InsertRowCommand insertRowCommand = new InsertRowCommand(cell, cell.getIndex());
            insertRowCommand.execute();
            ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(insertRowCommand);
        });

        addRowBelowItem.setOnAction(action -> {
            InsertRowCommand insertRowCommand = new InsertRowCommand(cell, cell.getIndex() + 1);
            insertRowCommand.execute();
            ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(insertRowCommand);
        });

        deleteRowItem.setOnAction(action -> {
            DeleteRowCommand deleteRowCommand = new DeleteRowCommand(cell);
            deleteRowCommand.execute();
            ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(deleteRowCommand);
        });

//        copyRowItem.setOnAction(action -> {
//            String copyValues = "";
//            TopsoilDataEntry row = cell.getDataEntry();
//            for (int i = 0; i < row.getProperties().size(); i++) {
//                copyValues += Double.toString(row.getProperties().get(i).get());
//                if (i < row.getProperties().size() - 1) {
//                    copyValues += "\t";
//                }
//            }
//            ClipboardContent content = new ClipboardContent();
//            content.putString(copyValues);
//            Clipboard.getSystemClipboard().setContent(content);
//        });
//
//        clearRowItem.setOnAction(action -> {
//            ClearRowCommand clearRowCommand = new ClearRowCommand(cell);
//            clearRowCommand.execute();
//            ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(clearRowCommand);
//        });

        //***********************//
        //    COLUMN ACTIONS     //
        //***********************//

        deleteColumnItem.setOnAction(action -> {
            TopsoilTab tab = ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab();
            DeleteColumnCommand deleteColumnCommand = new DeleteColumnCommand(tab.getTableController(), cell.getColumnIndex());
            deleteColumnCommand.execute();
            tab.addUndo(deleteColumnCommand);
        });

        insertColumnItem.setOnAction(action -> {
            TopsoilTab tab = ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab();

            String title = TopsoilTextInputDialog.showDialog("Insert New Column",
                                                             "What is the name of the new column?");
            if (title != null) {
                TopsoilDataColumn column = new TopsoilDataColumn(title);

                for (int i = 0; i < tab.getTableController().getTable().getDataEntries().size(); i++) {
                    column.add(new SimpleDoubleProperty(0.0));
                }

                InsertColumnCommand insertColumnCommand = new InsertColumnCommand(tab.getTableController(), cell
                        .getColumnIndex(), column);
                insertColumnCommand.execute();
                tab.addUndo(insertColumnCommand);
            }
        });

        renameColumnItem.setOnAction(action -> {
            Dialog<String> columnRenameDialog = new Dialog<>();
            TopsoilTab tab = ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab();
            TopsoilDataColumn dataColumn = tab.getTableController().getTable().getDataColumns().get(cell.getColumnIndex());

            HBox hbox = new HBox(10.0);
            Label columnNameLabel = new Label("Column Name: ");

            TextField columnNameTextField = new TextField(dataColumn.getName());
            hbox.getChildren().addAll(columnNameLabel, columnNameTextField);

            columnRenameDialog.getDialogPane().setContent(hbox);
            columnRenameDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);

            columnRenameDialog.setResultConverter(value -> {
                if (value == ButtonType.APPLY) {
                    return columnNameTextField.getText().trim();
                }
                return null;
            });

            String result = columnRenameDialog.showAndWait().orElse(null);

            if (result != null) {
                ColumnRenameCommand columnRenameCommand = new ColumnRenameCommand(dataColumn, dataColumn.getName(), result);
                columnRenameCommand.execute();
                tab.addUndo(columnRenameCommand);
            }
        });

//        copyColumnItem.setOnAction(action -> {
//            String copyValues = "";
//            TableColumn<TopsoilDataEntry, Double> column = cell.getTableColumn();
//            for (int i = 0; i < cell.getTableView().getItems().size(); i++) {
//                copyValues += Double.toString(column.getCellData(i));
//                if (i < cell.getTableView().getItems().size() - 1) {
//                    copyValues += "\n";
//                }
//            }
//            ClipboardContent content = new ClipboardContent();
//            content.putString(copyValues);
//            Clipboard.getSystemClipboard().setContent(content);
//        });
//
//        clearColumnItem.setOnAction(action -> {
//            ClearColumnCommand clearColumnCommand = new ClearColumnCommand(cell);
//            clearColumnCommand.execute();
//            ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(clearColumnCommand);
//        });

        //*********************//
        //    CELL ACTIONS     //
        //*********************//

//        copyCellItem.setOnAction(action -> {
//            ClipboardContent content = new ClipboardContent();
//            content.putString(Double.toString(cell.getItem()));
//            Clipboard.getSystemClipboard().setContent(content);
//        });
//
//        clearCellItem.setOnAction(action -> {
//            ClearCellCommand clearCellCommand = new ClearCellCommand(cell);
//            clearCellCommand.execute();
//            ((TopsoilTabPane) cell.getScene().lookup("#TopsoilTabPane"))
//                    .getSelectedTab().addUndo(clearCellCommand);
//        });

        // Add items to context menu
        getItems().addAll(
                addRowAboveItem,
                addRowBelowItem,
                deleteRowItem,
//                copyRowItem,
//                clearRowItem,
                new SeparatorMenuItem(),
                renameColumnItem,
                insertColumnItem,
                deleteColumnItem
//                copyColumnItem,
//                clearColumnItem,
//                new SeparatorMenuItem(),
//                copyCellItem,
//                clearCellItem
        );
    }
}
