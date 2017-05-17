package org.cirdles.topsoil.app.table.command;

import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that is added to a {@link TopsoilTab}'s {@link UndoManager} when a
 * {@link TopsoilTableCell} is edited in the {@link TableView}. This command stores the {@code TopsoilTableCell}
 * that was edited, the {@link TopsoilDataEntry} the cell's value is stored in, and both the former and new value of
 * the cell.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class TableCellEditCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TopsoilTableCell} that was edited.
     */
    private TopsoilTableCell cell;

    /**
     * The row of the cell that was edited.
     */
    private TopsoilDataEntry row;

    /**
     * The former value of the edited cell.
     */
    private Double formerValue;

    /**
     * The new value of the edited cell.
     */
    private Double newValue;

    //***********************
    // Constructors
    //***********************

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

    //***********************
    // Methods
    //***********************

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
     * Carries out a change in the data model of the {@code TableView} for the
     * cell, then updates the visible table.
     *
     * @param value the Double value to assign
     */
    private void changeCellValue(Double value) {

        this.row.setValue(cell.getColumnIndex(), value);
        this.cell.getTableColumn().setVisible(false);
        this.cell.getTableColumn().setVisible(true);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Change cell value";
    }
}