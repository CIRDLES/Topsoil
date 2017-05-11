package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a {@link TopsoilTab}'s {@link UndoManager} when a row is
 * inserted into the {@link TableView}. This command creates an empty {@link TopsoilDataEntry} and inserts is at
 * the specified index.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class InsertRowCommand implements Command {

    //***********************
    // Attributes
    //***********************

    private TableView<TopsoilDataEntry> tableView;
    private int index;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code InsertRowCommand} for the selected cell. In this case, the row was inserted before or
     * after a specific row.
     *
     * @param cell  the cell from which the command was called
     * @param index the int index where the new row was added
     */
    public InsertRowCommand(TopsoilTableCell cell, int index) {
        this.tableView = cell.getTableView();
        this.index = index;
    }

    /**
     * Constructs a new {@code InsertRowCommand} for the specified {@code TableView}. In this case, a row was
     * inserted at the end of the table view.
     *
     * @param tableView the TableView that the row was inserted into
     */
    public InsertRowCommand(TableView<TopsoilDataEntry> tableView) {
        this.tableView = tableView;
        this.index = this.tableView.getItems().size();
    }

    //***********************
    // Methods
    //***********************

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

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Insert row";
    }

}