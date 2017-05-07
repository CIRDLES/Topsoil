package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.Command;
import org.cirdles.topsoil.app.util.UndoManager;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a row is deleted in the <tt>TableView</tt>. This
 * class stores the <tt>TopsoilDataEntry</tt> that was deleted and the index
 * of the <tt>TableView</tt> from which it was deleted.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class DeleteRowCommand implements Command {

    private int index;
    private TopsoilDataEntry dataEntry;
    private TableView<TopsoilDataEntry> tableView;

    /**
     * Constructs a new delete row command. Gets the row, its index, and the
     * table view from the specified cell.
     *
     * @param cell the TopsoilTableCell that the command came from
     */
    public DeleteRowCommand(TopsoilTableCell cell) {

        this.index = cell.getIndex();
        this.dataEntry = cell.getDataEntry();
        this.tableView = cell.getTableView();
    }

    public DeleteRowCommand(TableView<TopsoilDataEntry> tableView) {
        this.tableView = tableView;
        this.index = tableView.getItems().size() - 1;
        this.dataEntry = tableView.getItems().get(index);
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