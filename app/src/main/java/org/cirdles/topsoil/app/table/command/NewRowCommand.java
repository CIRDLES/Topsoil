package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.util.Command;
import org.cirdles.topsoil.app.util.UndoManager;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a new empty row is added to the
 * <tt>TableView</tt>. This class only stores the table view, the end of which
 * the new row is added or removed.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class NewRowCommand implements Command {

    private TableView<TopsoilDataEntry> tableView;

    /**
     * Constructs a new new row command for the specified table view.
     *
     * @param tableView the TableView in question
     */
    public NewRowCommand(TableView<TopsoilDataEntry> tableView) {
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