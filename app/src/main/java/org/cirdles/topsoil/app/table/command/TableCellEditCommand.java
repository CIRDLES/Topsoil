package org.cirdles.topsoil.app.table.command;

import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

/**
 * An undoable {@link Command} instance that is added to a {@link TopsoilTab}'s {@link UndoManager} when a
 * cell is edited in the table.
 *
 * @author marottajb
 *
 * @see Command
 * @see UndoManager
 */
public class TableCellEditCommand implements Command {

    /**
     * The {@code SpreadsheetCell} that was edited.
     */
    private SpreadsheetCell cell;

    /**
     * The former value of the edited cell.
     */
    private Double oldValue;

    /**
     * The new value of the edited cell.
     */
    private Double newValue;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a cell edit command for the specified cell, its former value,and its new value.
     *
     * @param   cell
     *          the SpreadsheetCell that was edited
     * @param   formerValue
     *          the former Double value of the cell
     * @param   newValue
     *          the new Double value of the cell
     */
    public TableCellEditCommand(SpreadsheetCell cell, Double formerValue, Double newValue) {
        this.cell = cell;
        this.oldValue = formerValue;
        this.newValue = newValue;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Called to execute the cell edit action.
     */
    public void execute() {
        cell.setItem(newValue);
    }

    /**
     * Called to undo the cell edit action.
     */
    public void undo() {
        cell.setItem(oldValue);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Change cell value";
    }
}
