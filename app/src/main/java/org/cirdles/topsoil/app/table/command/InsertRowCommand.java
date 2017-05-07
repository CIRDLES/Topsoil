package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.Command;
import org.cirdles.topsoil.app.util.UndoManager;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a row is inserted into the <tt>TableView</tt>.
 * This class creates an empty <tt>TopsoilDataEntry</tt> and inserts is above
 * the selected row.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class InsertRowCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;
    private int index;

    /**
     * Constructs a new insert row command from the selected cell.
     *
     * @param cell  the cell from which the command was called
     */
    public InsertRowCommand(TopsoilTableCell cell, int index) {
        this.tableView = cell.getTableView();
        this.index = index;
    }

    public InsertRowCommand(TableView<TopsoilDataEntry> tableView) {
        this.tableView = tableView;
        this.index = this.tableView.getItems().size();
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