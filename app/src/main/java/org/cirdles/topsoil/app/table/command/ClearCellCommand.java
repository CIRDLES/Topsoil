package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a
 * {@link TopsoilTableCell} in the {@link TableView} is cleared. This command stores the cell, the row it belongs to,
 * and the former value of the cell.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class ClearCellCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TopsoilTableCell} that was cleared, and that created this command.
     */
    private TopsoilTableCell cell;

    /**
     * The value of the cell before it was cleared.
     */
    private Double formerValue;

    /**
     * The row that the cell is in.
     */
    private TopsoilDataEntry row;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code ClearCellCommand} for the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public ClearCellCommand(TopsoilTableCell cell) {

        this.cell = cell;
        this.formerValue = cell.getItem();
        this.row = cell.getDataEntry();
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the cell clearing.
     */
    public void execute() {
        changeCellValue(0.0);
    }

    /**
     * Called to undo the cell clearing.
     */
    public void undo() {
        changeCellValue(formerValue);
    }

    /**
     * Carries out a change in the data model of the {@code TableView} for the
     * cell, then updates the visible table.
     *
     * @param value the Double value to assign
     */
    private void changeCellValue(Double value) {
        this.row.setValue(this.cell.getColumnIndex(), value);

        // This is a workaround to force the TableView to update the visible Node
        this.cell.getTableColumn().setVisible(false);
        this.cell.getTableColumn().setVisible(true);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Clear cell";
    }
}
