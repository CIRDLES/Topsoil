package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableColumn;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a
 * {@link TableColumn} in the {@code TableView} is inserted.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class InsertColumnCommand implements Command {

    private TopsoilTableController tableController;
    private TopsoilDataColumn dataColumn;
    private int index;

    /**
     * Constructs a new insert column command from the specified table controller, column, and index.
     *
     * @param tableController  the TopsoilTableController that the command came from
     * @param index the int index of the column
     * @param dataColumn    the TopsoilDataColumn to be inserted
     */
    public InsertColumnCommand(TopsoilTableController tableController, int index, TopsoilDataColumn dataColumn) {
        this.tableController = tableController;
        this.dataColumn = dataColumn;
        this.index = index;
    }

    /**
     * Called to execute the column insertion.
     */
    public void execute() {
        tableController.addColumn(index, dataColumn);
    }

    /**
     * Called to undo the column insertion.
     */
    public void undo() {
        tableController.removeColumn(index);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Insert column";
    }

}
