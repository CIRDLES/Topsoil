package org.cirdles.topsoil.app.table.command;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import java.util.List;

/**
 * An undoable {@link Command} instance that can be added to a {@link TopsoilTab}'s {@link UndoManager} when a
 * {@link TableColumn} in the {@link TableView} is dragged to a new position. This class determines which column
 * was moved, where it was moved from, and where it was moved to, so the action can be undone or replicated.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class TableColumnReorderCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * A {@code List} of the columns' names as {@code StringProperty}s, from the corresponding
     * {@link TopsoilDataTable}. These must also be reordered to reflect the change in the {@code TableView}.
     */
    private List<StringProperty> columnNameProperties;

    /**
     * The {@code TableView} whose columns where reordered.
     */
    private TableView<TopsoilDataEntry> tableView;

    /**
     * The data of the table as contained in the corresponding {@link TopsoilDataTable}. The data in {@code
     * TopsoilDataTable} must also be reordered to reflect the change in the {@code TableView}.
     */
    private ObservableList<TopsoilDataEntry> data;

    /**
     * The former index of the moved column.
     */
    private int fromIndex;

    /**
     * The new index of the moved column.
     */
    private int toIndex;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new column reorder command for the table view in which the
     * column was moved. The former and new index of the moved column is
     * determined by the order of the String ids of the columns, which
     * increment from 0.
     *
     * @param columnNameProperties a List of the column names as StringProperties
     * @param tableView the TableView that the columns belong to
     * @param data the data from the table as TopsoilDataEntries
     * @param fromIndex the former index of the moved column
     * @param toIndex   the new index of the moved column
     */
    public TableColumnReorderCommand(List<StringProperty> columnNameProperties, TableView<TopsoilDataEntry>
            tableView, ObservableList<TopsoilDataEntry> data, int fromIndex, int toIndex) {
        this.columnNameProperties = columnNameProperties;
        this.tableView = tableView;
        this.data = data;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Moves a TableColumn from the fromIndex, to the toIndex.
     *
     * @param fromIndex the column's starting index
     * @param toIndex   the column's target index
     */
    private void reorder(int fromIndex, int toIndex) {
        TableColumn<TopsoilDataEntry, ?> columnTemp = tableView.getColumns().get(fromIndex);
        StringProperty nameTemp = columnNameProperties.get(fromIndex);

        // Order TableColumns in TableView
        tableView.getColumns().remove(fromIndex);
        tableView.getColumns().add(toIndex, columnTemp);

        // Order columnNameProperties in TopsoilDataTable
        columnNameProperties.remove(fromIndex);
        columnNameProperties.add(toIndex, nameTemp);

        // Order data in TopsoilDataTable
        for (TopsoilDataEntry entry : data) {
            entry.swap(fromIndex, toIndex);
        }
    }

    /**
     * Called to execute the column reorder.
     */
    public void execute() {
        reorder(fromIndex, toIndex);
    }

    /**
     * Called to undo the column reorder.
     */
    public void undo() {
        reorder(toIndex, fromIndex);
    }

    /**
     * Called from the {@code UndoManager} to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Change column position";
    }
}
