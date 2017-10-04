package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a row in the
 * {@link TableView} is cleared. This {@code Command} stores a copy of the row's {@link TopsoilDataEntry}, and the row's
 * index in the table view.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class ClearRowCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TopsoilDataEntry} that was cleared.
     */
    private TopsoilDataEntry row;

    /**
     * The former valued from the {@code TopsoilDataEntry}.
     */
    private Double[] rowValues;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code ClearRowCommand} for the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public ClearRowCommand(TopsoilTableCell cell) {
        this.row = cell.getDataEntry();
        this.rowValues = row.toArray();
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the row clearing.
     */
    public void execute() {
        for (int i = 0; i < rowValues.length; i++) {
            row.setValue(i, 0.0);
        }
    }

    /**
     * Called to undo the row clearing.
     */
    public void undo() {
        for (int i = 0; i < rowValues.length; i++) {
            row.setValue(i, rowValues[i]);
        }
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Clear row";
    }
}
