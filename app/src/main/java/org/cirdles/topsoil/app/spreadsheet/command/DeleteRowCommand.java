package org.cirdles.topsoil.app.spreadsheet.command;

import org.cirdles.topsoil.app.data.ObservableDataRow;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a {@link TopsoilTab}'s {@link UndoManager} when a row is
 * deleted from the data.
 *
 * @author marottajb
 *
 * @see Command
 * @see UndoManager
 */
public class DeleteRowCommand implements Command {

    private ObservableDataTable data;
    private int index;
    private ObservableDataRow row;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new {@code DeleteRowCommand} for the specified cell. In this case, the row containing the cell
     * is the row that was deleted.
     *
     * @param   data
     *          ObservableDataTable containing the row
     * @param   rowIndex
     *          the int index of the row
     */
    public DeleteRowCommand(ObservableDataTable data, int rowIndex ) {
        this.data = data;
        this.index = rowIndex;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Called to execute the row deletion.
     */
    public void execute() {
        row = data.removeRow(index);
    }

    /**
     * Called to undo the row deletion.
     */
    public void undo() {
        data.addRow(index, row);
    }

    /** {@inheritDoc} */
    public String getActionName() {
        return "Delete row";
    }
}