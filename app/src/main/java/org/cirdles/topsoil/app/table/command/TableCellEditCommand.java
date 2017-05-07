package org.cirdles.topsoil.app.progress.table.command;

import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTableCell;
import org.cirdles.topsoil.app.progress.util.Command;

/**
 * An undoable <tt>Command</tt> instance that is added to a TopsoilTab's
 * <tt>UndoManager</tt> when a cell is edited in the <tt>TableView</tt>. This
 * class stores the <tt>TopsoilTableCell</tt> that was edited, the
 * <tt>TopsoilDataEntry</tt> the cell's value is stored in, the former value of
 * the cell, and the new value of the cell.
 *
 * @author marottajb
 * @see org.cirdles.topsoil.app.progress.util.Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
public class TableCellEditCommand implements Command {

    private TopsoilTableCell cell;
    private TopsoilDataEntry row;
    private Double formerValue;
    private Double newValue;

    /**
     * Constructs a cell edit command for the specified cell, its former value,
     * and its new value.
     *
     * @param cell  the TopsoilTableCell that was edited
     * @param formerValue   the former Double value of the cell
     * @param newValue  the new Double value of the cell
     */
    public TableCellEditCommand(TopsoilTableCell cell, Double formerValue,
                         Double newValue) {
        this.cell = cell;
        this.row = cell.getDataEntry();
        this.formerValue = formerValue;
        this.newValue = newValue;
    }

    /**
     * Called to execute the cell edit action.
     */
    public void execute() {
        changeCellValue(newValue);
    }

    /**
     * Called to undo the cell edit action.
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

        this.row.setValue(cell.getColumnIndex(), value);
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
        return "Change cell value";
    }
}