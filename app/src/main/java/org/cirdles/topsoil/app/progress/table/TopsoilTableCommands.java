package org.cirdles.topsoil.app.progress.table;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.progress.util.Command;

import java.util.ArrayDeque;

/**
 * An undoable <tt>Command</tt> instance that is added to a TopsoilTab's
 * <tt>UndoManager</tt> when a cell is edited in the <tt>TableView</tt>. This
 * class stores the <tt>TopsoilTableCell</tt> that was edited, the
 * <tt>TopsoilDataEntry</tt> the cell's value is stored in, the former value of
 * the cell, and the new value of the cell.
 *
 * @author marottajb
 * @see org.cirdles.topsoil.app.progress.util.Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class TopsoilTableCellEditCommand implements Command {

    private TopsoilTableCell cell;
    private TopsoilDataEntry row;
    private Double formerValue;
    private Double newValue;

    /**
     * Constructs a cell edit command for the specified cell, its former value,
     * and its new value.
     *
     * @param cell  the TopsoilTableCell that was edited
     * @param formerValue   the former Double value of the cell
     * @param newValue  the new Double value of the cell
     */
    TopsoilTableCellEditCommand(TopsoilTableCell cell, Double formerValue,
                                Double newValue) {
        this.cell = cell;
        this.row = cell.getDataEntry();
        this.formerValue = formerValue;
        this.newValue = newValue;
    }

    /**
     * Called to execute the cell edit action.
     */
    public void execute() {
        changeCellValue(newValue);
    }

    /**
     * Called to undo the cell edit action.
     */
    public void undo() {
        changeCellValue(formerValue);
    }

    /**
     * Carries out a change in the data model of the <tt>TableView</tt> for the
     * cell, then updates the visible table.
     *
     * @param value the Double value to assign
     */
    private void changeCellValue(Double value) {

        this.row.changeEntry(cell.getColumnIndex(),
                new SimpleDoubleProperty(value));
        this.cell.updateItem(this.row.getProperties()
                .get(cell.getColumnIndex()).doubleValue(), false);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Change cell value";
    }
}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a row is inserted into the <tt>TableView</tt>.
 * This class creates an empty <tt>TopsoilDataEntry</tt> and inserts is above
 * the selected row.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class InsertRowCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;
    private int index;

    /**
     * Constructs a new insert row command from the selected cell.
     *
     * @param cell  the cell from which the command was called
     */
    InsertRowCommand(TopsoilTableCell cell) {
        this.tableView = cell.getTableView();
        this.index = cell.getIndex();
    }

    /**
     * Called to execute the row insertion.
     */
    public void execute() {
        this.tableView.getItems().add(index, TopsoilDataEntry.newEmptyDataEntry(this.tableView));
    }

    /**
     * Called to undo the row insertion.
     */
    public void undo() {
        this.tableView.getItems().remove(index);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Insert row";
    }

}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a row is deleted in the <tt>TableView</tt>. This
 * class stores the <tt>TopsoilDataEntry</tt> that was deleted and the index
 * of the <tt>TableView</tt> from which it was deleted.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class DeleteRowCommand implements Command {

    private int index;
    private TopsoilDataEntry dataEntry;
    private TableView tableView;

    /**
     * Constructs a new delete row command. Gets the row, its index, and the
     * table view from the specified cell.
     *
     * @param cell the TopsoilTableCell that the command came from
     */
    DeleteRowCommand(TopsoilTableCell cell) {
        this.index = cell.getIndex();
        this.dataEntry = cell.getDataEntry();
        this.tableView = cell.getTableView();
    }

    /**
     * Called to execute the row deletion.
     */
    public void execute() {
        this.tableView.getItems().remove(index);
    }

    /**
     * Called to undo the row deletion.
     */
    public void undo() {
        this.tableView.getItems().add(index, dataEntry);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Delete row";
    }
}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a new empty row is added to the
 * <tt>TableView</tt>. This class only stores the table view, the end of which
 * the new row is added or removed.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class NewRowCommand implements Command {

    private TableView tableView;

    /**
     * Constructs a new new row command for the specified table view.
     *
     * @param tableView the TableView in question
     */
    NewRowCommand(TableView tableView) {

        this.tableView = tableView;
    }

    /**
     * Called to execute the row creation.
     */
    public void execute() {
        this.tableView.getItems().add(TopsoilDataEntry.newEmptyDataEntry(this.tableView));
    }

    /**
     * Called to undo the row creation.
     */
    public void undo() {
        tableView.getItems()
                .remove(tableView.getItems().size() - 1);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Add new row";
    }

}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a row in the <tt>TableView</tt> is cleared. This
 * class stores a copy of the row's <tt>TopsoilDataEntry</tt>, and the row's
 * index in the table view.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class ClearRowCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;
    private TopsoilDataEntry row;
    private int index;

    /**
     * Constructs a new clear row command from the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    ClearRowCommand(TopsoilTableCell cell) {
        this.tableView = cell.getTableView();
        this.row = cell.getDataEntry();
        this.index = cell.getIndex();
    }

    /**
     * Called to execute the row creation.
     */
    public void execute() {
        this.tableView.getItems().remove(index);
        this.tableView.getItems().add(index, TopsoilDataEntry.newEmptyDataEntry(this.tableView));
    }

    /**
     * Called to undo the row creation.
     */
    public void undo() {
        TopsoilDataEntry dataEntry = new TopsoilDataEntry();
        int i = 0;
        for (Object column : this.tableView.getColumns()) {
            dataEntry.addEntries(this.row.getProperties().get(i).getValue());
            i++;
        }
        this.tableView.getItems().remove(index);
        this.tableView.getItems().add(index, dataEntry);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Clear row";
    }
}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TableColumn</tt> in the <tt>TableView</tt>
 * is deleted. This class stores a copy of the deleted column, and its index
 * in <tt>TableView</tt>.<i>getColumns()</i>.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class DeleteColumnCommand implements Command {

    private TableView tableView;
    private TableColumn<TopsoilDataEntry, Double> column;
    private int index;

    /**
     * Constructs a new delete column command from the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    DeleteColumnCommand(TopsoilTableCell cell) {
        this.tableView = cell.getTableView();
        this.column = cell.getTableColumn();
        this.index = cell.getColumnIndex();
    }

    /**
     * Called to execute the column deletion.
     */
    public void execute() {

        this.tableView.getColumns().remove(index);
    }

    /**
     * Called to undo the column deletion.
     */
    public void undo() {

        this.tableView.getColumns().add(index, this.column);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Delete column";
    }
}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TableColumn</tt> in the <tt>TableView</tt>
 * is cleared. This class stores the data from the cleared column, and its
 * index in <tt>TableView</tt>.<i>getColumns()</i>.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class ClearColumnCommand implements Command {

    private TableColumn<TopsoilDataEntry, Double> column;
    private int index;
    private ArrayDeque<SimpleDoubleProperty> columnData;

    /**
     * Constructs a new clear column command from the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    ClearColumnCommand(TopsoilTableCell cell) {

        this.column = cell.getTableColumn();
        this.index = cell.getColumnIndex();
        this.columnData = new ArrayDeque<>();
    }

    /**
     * Called to execute the column deletion.
     */
    public void execute() {

        this.column.setCellValueFactory(param -> {
            this.columnData.add((SimpleDoubleProperty)
                    param.getValue().getProperties().get(index));
            return (ObservableValue) new SimpleDoubleProperty(0.0);
        });
        this.column.setVisible(false);
        this.column.setVisible(true);
    }

    // TODO unknown bug breaks table on undo
    /**
     * Called to undo the column deletion.
     */
    public void undo() {

        this.column.setCellValueFactory(param -> (ObservableValue)
                this.columnData.poll());
        this.column.setVisible(false);
        this.column.setVisible(true);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Clear column";
    }
}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TopsoilTableCell</tt> in the
 * <tt>TableView</tt> is cleared. This class stores the cell, the row it
 * belongs to, and the former value of the cell.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class ClearCellCommand implements Command {

    private TopsoilTableCell cell;
    private Double formerValue;
    private TopsoilDataEntry row;

    /**
     * Constructs a new clear cell command for the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    ClearCellCommand(TopsoilTableCell cell) {
        this.cell = cell;
        this.formerValue = cell.getItem();
        this.row = cell.getDataEntry();
    }

    /**
     * Called to execute the cell clearing.
     */
    public void execute() {
        changeCellValue(0.0);
    }

    /**
     * Called to undo the cell clearing.
     */
    public void undo() {
        changeCellValue(formerValue);
    }

    /**
     * Carries out a change in the data model of the <tt>TableView</tt> for the
     * cell, then updates the visible table.
     *
     * @param value the Double value to assign
     */
    private void changeCellValue(Double value) {
        this.row.changeEntry(this.cell.getColumnIndex(),
                new SimpleDoubleProperty(value));
        this.cell.updateItem(this.row.getProperties()
                .get(this.cell.getColumnIndex()).doubleValue(), false);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Clear cell";
    }
}

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TableColumn</tt> in the <tt>TableView</tt>
 * is dragged to a new position. This class determines which column was moved,
 * where it was moved from, and where it was moved to, so the action can be
 * undone or replicated.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
class TableColumnReorderCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;
    private int fromIndex;
    private int toIndex;

    /**
     * Constructs a new column reorder command for the table view in which the
     * column was moved. The former and new index of the moved column is
     * determined by the order of the String ids of the columns, which
     * increment from 0.
     *
     * @param tableView the TableView that the columns belong to
     */
    TableColumnReorderCommand(TableView<TopsoilDataEntry> tableView) {

        this.tableView = tableView;

        int numColumns = this.tableView.getColumns().size();

        int[] newColumnOrder = new int[this.tableView.getColumns().size()];
        for (int i = 0; i < numColumns; i++) {
            newColumnOrder[i] = Integer.parseInt(this.tableView.getColumns()
                    .get(i).getId());
        }

        if (newColumnOrder[0] != 0) {
            // if a column was dragged to the beginning
            if (newColumnOrder[0] > 1) {
                this.toIndex = 0;
                this.fromIndex = newColumnOrder[0];
                // if the first column was dragged somewhere else
            } else {
                this.fromIndex = 0;
                this.toIndex = 0;
                while (newColumnOrder[toIndex] != this.fromIndex) {
                    this.toIndex++;
                }
            }
        } else if (newColumnOrder[numColumns - 1] != numColumns - 1) {
            // if a column was dragged to the end
            if (newColumnOrder[numColumns - 1] < numColumns - 2) {
                this.toIndex = numColumns - 1;
                this.fromIndex = newColumnOrder[numColumns - 1];
                // if the last column was dragged somewhere else
            } else {
                this.fromIndex = numColumns - 1;
                this.toIndex = newColumnOrder[numColumns - 1];
                while (newColumnOrder[this.toIndex] != this.fromIndex) {
                    this.toIndex--;
                }
            }
            // any other drag
        } else {
            findNonSpecificColumnDrag(newColumnOrder);
        }
    }

    /**
     * Used if a column wasn't dragged to or from the first or last position in
     * the table view. Primarily for reducing the cyclomatic complexity of the
     * constructor.
     *
     * @param columnOrder an int[] representing the current order of columns
     */
    private void findNonSpecificColumnDrag(int[] columnOrder) {
        for (int j = 1; j < columnOrder.length - 2; j++) {
            if ((columnOrder[j - 1] != columnOrder[j] - 1)
                    && (columnOrder[j + 1] != columnOrder[j] + 1)) {
                this.fromIndex = columnOrder[j];
                this.toIndex = j;
                break;
            }
        }
    }

    /**
     * Called to execute the column reorder.
     */
    public void execute() {
        TableColumn<TopsoilDataEntry, ?> temp = this.tableView.getColumns()
                .get(this.fromIndex);

        this.tableView.getColumns().remove(this.fromIndex);
        this.tableView.getColumns().add(this.toIndex, temp);
    }

    /**
     * Called to undo the column reorder.
     */
    public void undo() {
        TableColumn<TopsoilDataEntry, ?> temp = this.tableView.getColumns()
                .get(this.toIndex);

        this.tableView.getColumns().remove(this.toIndex);
        this.tableView.getColumns().add(this.fromIndex, temp);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Change column position";
    }

}
