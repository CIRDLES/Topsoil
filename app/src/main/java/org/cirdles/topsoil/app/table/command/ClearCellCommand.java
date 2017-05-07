package org.cirdles.topsoil.app.table.command;

import org.cirdles.topsoil.app.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.Command;
import org.cirdles.topsoil.app.util.UndoManager;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TopsoilTableCell</tt> in the
 * <tt>TableView</tt> is cleared. This class stores the cell, the row it
 * belongs to, and the former value of the cell.
 *
 * @author marottajb
 * @see Command
 * @see UndoManager
 */
public class ClearCellCommand implements Command {

    private TopsoilTableCell cell;
    private Double formerValue;
    private TopsoilDataEntry row;

    /**
     * Constructs a new clear cell command for the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public ClearCellCommand(TopsoilTableCell cell) {

        this.cell = cell;
        this.formerValue = cell.getItem();
        this.row = cell.getDataEntry();
    }

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
     * Carries out a change in the data model of the <tt>TableView</tt> for the
     * cell, then updates the visible table.
     *
     * @param value the Double value to assign
     */
    private void changeCellValue(Double value) {
        this.row.setValue(this.cell.getColumnIndex(), value);
        this.cell.getTableColumn().setVisible(false);
        this.cell.getTableColumn().setVisible(true);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Clear cell";
    }
}