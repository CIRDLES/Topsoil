package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.Command;
import org.cirdles.topsoil.app.util.UndoManager;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a row in the <tt>TableView</tt> is cleared. This
 * class stores a copy of the row's <tt>TopsoilDataEntry</tt>, and the row's
 * index in the table view.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class ClearRowCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;
    private TopsoilDataEntry row;
    private int index;

    /**
     * Constructs a new clear row command from the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public ClearRowCommand(TopsoilTableCell cell) {
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
        for (int i = 0; i < this.tableView.getColumns().size(); i++) {
            dataEntry.addValues(this.row.getProperties().get(i).getValue());
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