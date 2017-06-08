package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a
 * {@link TableColumn} in the {@code TableView} is deleted. This command stores a copy of the deleted column, and its
 * index in {@link TableView#getColumns()}.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class DeleteColumnCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TableView} that the column was deleted from.
     */
    private TableView<TopsoilDataEntry> tableView;

    /**
     * The {@code TableColumn} that was deleted.
     */
    private TableColumn<TopsoilDataEntry, Double> column;

    /**
     * The index in {@code TableView.getColumns()} that the column occupied.
     */
    private int index;

    //***********************
    // Constructors
    //***********************
    
    /**
     * Constructs a new delete column command from the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public DeleteColumnCommand(TopsoilTableCell cell) {
        this.tableView = cell.getTableView();
        this.column = cell.getTableColumn();
        this.index = cell.getColumnIndex();
    }

    //***********************
    // Methods
    //***********************
    
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

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Delete column";
    }
}