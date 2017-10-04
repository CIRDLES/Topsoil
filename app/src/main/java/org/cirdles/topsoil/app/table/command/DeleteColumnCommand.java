package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.table.TopsoilTableController;
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
     * The {@code TopsoilTableController} for the table.
     */
    private TopsoilTableController tableController;

    /**
     * The {@code TopsoilDataColumn} that was deleted.
     */
    private TopsoilDataColumn dataColumn;

    /**
     * The index in {@code TableView.getColumns()} that the column occupied.
     */
    private int index;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new delete column command from the specified table controller and index.
     *
     * @param tableController  the TopsoilTableController that the command came from
     * @param index the int index of the column
     */
    public DeleteColumnCommand(TopsoilTableController tableController, int index) {
        this.tableController = tableController;
        this.dataColumn = tableController.getTable().getDataColumns().get(index);
        this.index = index;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the column deletion.
     */
    public void execute() {
        tableController.removeColumn(index);
    }

    /**
     * Called to undo the column deletion.
     */
    public void undo() {
        tableController.addColumn(index, dataColumn);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Delete column";
    }
}
