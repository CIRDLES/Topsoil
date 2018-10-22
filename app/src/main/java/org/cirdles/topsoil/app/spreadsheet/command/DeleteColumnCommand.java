package org.cirdles.topsoil.app.spreadsheet.command;

import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a column in the
 * data is deleted.
 *
 * @author marottajb
 *
 * @see Command
 * @see UndoManager
 */
public class DeleteColumnCommand implements Command {

    private ObservableDataTable data;
    private int index;
    private ObservableDataColumn column;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new delete column command from the specified data controller and index.
     *
     * @param   data
     *          ObservableDataTable containing the column
     * @param   colIndex
     *          the int index of the column
     */
    public DeleteColumnCommand(ObservableDataTable data, int colIndex ) {
        this.data = data;
        this.index = colIndex;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Called to execute the column deletion.
     */
    public void execute() {
        column = data.removeColumn(index);
    }

    /**
     * Called to undo the column deletion.
     */
    public void undo() {
        data.addColumn(index, column);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Delete column";
    }
}
